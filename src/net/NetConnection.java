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
	
	private InetAddress serverIp;
	private MessageBox entrada;
	//escreve resposta do servidor (se houver) na entrada
	
	private boolean repeat; //se deve repetir o request
	private int timer; //tempo de espera entre as repetições
	private String message;
	
	
	public NetConnection(InetAddress ip, MessageBox in, MessageBox out) {
		this.serverIp = ip;
		this.entrada = in;
		repeat = false;
		timer=0;
	}
	
	public NetConnection(InetAddress ip, MessageBox in,String message, int timer) { //assume que há repetição de request - keepAlive
		this.serverIp = ip;
		this.entrada = in;
		this.message=message;
		repeat = true;
		this.timer=timer;
	}
	
	public NetConnection(InetAddress ip, MessageBox in, String message){ //assume que não há repetição
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
				clientSocket = new Socket(serverIp, Main.PORTA);
				System.out.println("socket: "+clientSocket.getLocalPort()+" server: "+clientSocket.getPort());
				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				outToServer.writeBytes(message+"\n");
				String response = inFromServer.readLine();
			
				outToServer.close();
				inFromServer.close();
				
				clientSocket.close();
				if(response!=null && response.length()>0)
					entrada.addMessage(response);
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
