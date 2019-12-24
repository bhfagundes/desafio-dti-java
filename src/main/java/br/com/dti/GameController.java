package br.com.dti;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
		File file = new File(id + ".txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.append(player);
		writer.append("Testando");
		writer.close();
		return null;
	}
	public String retornarJogador(ArrayList<String> conteudo) throws IOException {
		
		String data = null;
		return data;
	}
	public Void realizarJogada(String id,String player,Integer positionX,Integer positionY) throws IOException {
		FileWriter fw = new FileWriter(id + ".txt", true);
		BufferedWriter partida = new BufferedWriter(fw);
		partida.write(player);
		partida.newLine();
		partida.write(positionX+" " +positionY);
		partida.close();
		partida.flush();
		partida.close();
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
	@RequestMapping(value="/game/{id}/movement", method = RequestMethod.POST) 
	public Map<String, String> movement(@PathVariable("id") String id,@RequestParam("player") String player,@RequestParam("position[x]")  Integer positionX,@RequestParam("position[y]") Integer positionY) throws IOException {
        File file = new File(id+".txt");
        Map<String,String> resultado = new HashMap<String,String>();
       if(file.exists()) {
            ArrayList<String> conteudo = new ArrayList<String>();
            String jogador;
        	conteudo = conteudoArquivo(id);
        	jogador = retornarJogador(conteudo);
        	if((jogador != player && conteudo.size()==1) 
        			|| (jogador == player && conteudo.size()>1)){
        		resultado.put("msg", "Não é turno do jogador");
        	}
        	else if((jogador == player && conteudo.size()==1) 
        			||( jogador != player && conteudo.size()>1 && conteudo.size()<9)){
        		resultado.put("msg", "Jogada realizada");
        	}
        	else if( jogador != player && conteudo.size()>1 && conteudo.size()>=9){
        		
        	}
        }
        else {
        	resultado.put("msg", "Partida não encontrada");
        }
     	resultado.put("id", id.toString());
     	resultado.put("player",player);
		return resultado;
	} 
}
