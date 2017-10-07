package net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import control.Main;
import pattern.MessageBox;

public class NetConnection implements Runnable{
	//classe que � respons�vel pela comunica��o com o servidor
	//na sua constru��o, determinamos qual a mensagem que ser� enviada ao servidor e, se for necessario, qual o tempo 
	//de espera entre as mensagens (no caso das que devem se repetir como o keepAlive)
	
	//decidimos fazer com que cada mensagem trocada com o servidor fosse feita por uma conex�o TCP diferente
	//isso porque, como boa parte das requisi��es depende do usu�rio da m�quina, se utilizassemos uma �nica conex�o
	//ela ficaria a maior parte do tempo ociosa e ocuando recursos em ambos (cliente e servidor).
	
	//Assim sendo, cada inst�ncia de NetConnection manda somente uma requisi��o por conex�o tcp estabelecida
	//mesmo no caso da que deve ser repetida (keep alive)
	
	
	private InetAddress serverIp;
	private MessageBox entrada;
	//escreve resposta do servidor (se houver) na entrada
	
	private boolean repeat; //se deve repetir o request
	private int timer; //tempo de espera entre as repeti��es
	private String message;
	
	public NetConnection(InetAddress ip, MessageBox in,String message, int timer) { //assume que h� repeti��o de request - keepAlive
		this.serverIp = ip;
		this.entrada = in;
		this.message=message;
		repeat = true;
		this.timer=timer;
	}
	
	public NetConnection(InetAddress ip, MessageBox in, String message){ //assume que n�o h� repeti��o
		this.serverIp = ip;
		this.entrada = in;
		repeat = false;
		this.timer=0;
		this.message=message;
	}
	
	@Override
	public void run() {
		do{
			
			Socket clientSocket;
			try {
				//cria um novo sockcet TCP
				clientSocket = new Socket(serverIp, Main.PORTA);
				
				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				//envia a mensagem ao servidor
				outToServer.writeBytes(message+"\n");
				
				//le a resposta
				String response = inFromServer.readLine();
			
				outToServer.close();
				inFromServer.close();
				
				clientSocket.close();
				
				//se a resposta tiver alguma coisa
				if(response!=null && response.length()>0)
					entrada.addMessage(response); 
					//adiciona a mensagem na caixa de entrada (que dispara a notifica��o aos observadores da caixa)
					
			} catch (IOException ex) {
				System.out.println("erro! "+System.currentTimeMillis());
				ex.printStackTrace();
			}
			
			if(timer>0){
				try {
					Thread.sleep(timer);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
		}while(repeat);
	}
	
	

}
