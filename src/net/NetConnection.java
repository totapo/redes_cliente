package net;

import java.net.InetAddress;

import pattern.MessageBox;

public class NetConnection extends Thread{
	
	private InetAddress serverIp;
	private MessageBox entrada, saida;
	//le da entrada e manda para o server
	//escreve resposta (se houver) na saida
	
	private boolean tcp,repeat; //se deve repetir o request
	private int timer; //tempo de espera entre as repetições
	
	
	public NetConnection(InetAddress ip, MessageBox in, MessageBox out, boolean isTcp) {
		this.serverIp = ip;
		this.entrada = in;
		this.saida = out;
		repeat = false;
		timer=0;
		tcp = isTcp;
	}
	
	public NetConnection(InetAddress ip, MessageBox in, MessageBox out,boolean isTcp, int timer) { //assume que há repetição de request
		this.serverIp = ip;
		this.entrada = in;
		this.saida = out;
		repeat = true;
		this.timer=timer;
		tcp = isTcp;
	}
	
	@Override
	public void run() {
		do{
			if(tcp){
				
			} else {
				
			}
			
			try {
				Thread.sleep(timer);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}while(repeat);
	}
	
	

}
