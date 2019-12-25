package br.com.dti;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GameController {
	public String defineJogador(){
		String player;
		Random sorteador = new Random();
		if(sorteador.nextBoolean() == true){
			player = "X";
		}
		else {
			player = "O";
		}	
		return player;
	}
	public Integer criaPartida(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String idDate  = dateFormat.format(new Date());
		Integer id = idDate.hashCode();
		if (id < 0){
			id = id*(-1);
		}
		return id;
	}
	public Void criarArquivo(String id, String player) throws IOException {
		FileOutputStream file = new FileOutputStream(id + ".txt", true);
		OutputStreamWriter osw = new OutputStreamWriter(file);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write(player);
		bw.close();
		
		return null;
	}
	public String retornarJogador(ArrayList<String> conteudo) throws IOException {
		
		String jogador = null;
		if(conteudo.size() <2){
			jogador=conteudo.get(conteudo.size()-1);
		}
		else {
			jogador=conteudo.get(conteudo.size()-2);
		}
		return jogador;
	}
	public Void realizarJogada(String id,String player,Integer positionX,Integer positionY) throws IOException {
		FileOutputStream file = new FileOutputStream(id + ".txt", true);
		OutputStreamWriter osw = new OutputStreamWriter(file);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.newLine();
		bw.write(player);
		bw.newLine();
		bw.write(positionX + " " +positionY);
		bw.close();
		return null;
	}
	public ArrayList<String> conteudoArquivo(String id) throws IOException{
	    ArrayList<String> conteudo = new ArrayList<String>();
	    File file = new File(id + ".txt");
		FileReader fileReader = new FileReader(file);
		BufferedReader reader = new BufferedReader(fileReader);
		String data = null;
		while((data = reader.readLine()) != null){
			conteudo.add(data);
		}
		fileReader.close();
		reader.close();
		return conteudo;   
	}
	@RequestMapping(value="/game", method = RequestMethod.POST)
	public Map<String, String> start() throws IOException {
		String player;
		Integer id;
		player = defineJogador();
		id = criaPartida();
		criarArquivo(id.toString(),player);
		Map<String,String> resultado = new HashMap<String,String>();
		resultado.put("id", id.toString());
		resultado.put("player", player);
		return resultado;
	}
	public String checarLinhas(String m[][]) {	
		String resultado = ".";
		for(int i =0; i<3;i++){
			if(m[0][i].equals("X") && m[1][i].equals("X") && m[2][i].equals("X")) {
				resultado = "X";
				i=3;				
			}
			else if(m[0][i].equals("O") && m[1][i].equals("O") && m[2][i].equals("O")) {
				resultado = "O";
				i=3;
			}
		}
		return resultado;
	}
	public String checarColunas(String m[][]) {	
		String resultado = ".";
		for(int i =0; i<3;i++){
			if(m[i][0].equals("X") && m[i][1].equals("X") && m[i][2].equals("X")) {
				resultado = "X";
				i=3;				
			}
			else if(m[i][0].equals("O") && m[i][1].equals("O") && m[i][2].equals("O")) {
				resultado = "O";
				i=3;
			}
		}
		return resultado;
	}
	public String checarDiagonais(String m[][]) {	
		String resultado = ".";
		if((m[0][2].equals("X") && m[1][1].equals("X") && m[2][0].equals("X"))
				|| (m[0][0].equals("X") && m[1][1].equals("X") && m[2][2].equals("X"))) {
			resultado = "X";			
		}
		else if((m[0][2].equals("O") && m[1][1].equals("O") && m[2][0].equals("O"))
				|| (m[0][0].equals("O") && m[1][1].equals("O") && m[2][2].equals("O"))) {
			resultado = "O";
		}
		return resultado;
	}
	public String[][] montarMatriz(ArrayList<String> conteudo) {	
		String m[][] = new String[3][3];
		String player;
		String[] position;
		for(int i =0; i< 3; i++){
			for (int j =0; j<3; j++){
				m[i][j]=".";
			}
		}
		for(int i=1; i< conteudo.size();i++) {
			player=conteudo.get(i);
			position=conteudo.get(i+1).split(" ");
			m[Integer.parseInt(position[0])][Integer.parseInt(position[1])]=player;
			i++;
		}
		return m;
	}
	public Map<String,String> checarResultado(String id) throws IOException{
		ArrayList<String> conteudo = new ArrayList<String>();
		Map<String,String> resultado = new HashMap<String,String>();
		String m[][] = new String[3][3];
		String colunas,linhas,diagonais;
		conteudo = conteudoArquivo(id);
		m=montarMatriz(conteudo);
		colunas = checarColunas(m);
		linhas=checarLinhas(m);
		diagonais=checarDiagonais(m);
		if(colunas.equals("X")||linhas.equals("X")||diagonais.equals("X")){
			resultado.put("msg", "Partida finalizada");
			resultado.put("winner", "X");
		}
		else if(colunas.equals("O")||linhas.equals("O")||diagonais.equals("O")){
			resultado.put("msg", "Partida finalizada");
			resultado.put("winner", "O");
		}
		else {
			if(conteudo.size()>18) {
				resultado.put("msg", "Partida finalizada");
				resultado.put("winner", "Draw");
			}
			else {
				resultado.put("msg", "Jogada realizada");
			}
		}
		return resultado;
	}
	@RequestMapping(value="/game/{id}/movement", method = RequestMethod.POST) 
	public Map<String, String> movement(@PathVariable("id") String id,@RequestParam("player") String player,@RequestParam("position[x]")  Integer positionX,@RequestParam("position[y]") Integer positionY) throws IOException {
        File file = new File(id+".txt");
        Map<String,String> resultado = new HashMap<String,String>();
       if(file.exists()) {
            ArrayList<String> conteudo = new ArrayList<String>();
            String jogador;
        	conteudo = conteudoArquivo(id);
        	jogador = retornarJogador(conteudo);
        	if((!jogador.equals(player)   && conteudo.size()==1) 
        			|| (!jogador.equals(player) && conteudo.size()>1)){
        		resultado.put("msg", "Não é turno do jogador" +  conteudo.size());
        	}
        	else if((jogador.equals(player) && conteudo.size()==1) 
        			||( !jogador.equals(player) && conteudo.size()>1 && conteudo.size()<9)){
        		realizarJogada(id,player,positionX,positionY);
        		resultado.put("msg", "Jogada realizada");
        	}
        	else if( !jogador.equals(player) && conteudo.size()>=9){
        		realizarJogada(id,player,positionX,positionY);
        		resultado= checarResultado(id);
        	}
        }
        else {
        	resultado.put("msg", "Partida não encontrada");
        }
 
		return resultado;
	} 
}
