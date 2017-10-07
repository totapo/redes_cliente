package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import pattern.MessageBox;
import view.ConnectScreen;
import view.MainScreen;

public class ConnectScreenController implements ActionListener {
	//controladora respons�vel pela tela de "login" da aplica��o
	private ConnectScreen tela;
	
	public ConnectScreenController(ConnectScreen tela){
		this.tela = tela;
		tela.getCon().addActionListener(this);
		tela.pack();
	    tela.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {//executado quando o usuario clica em conectar
		if(e.getActionCommand().equals(tela.getCon().getActionCommand())){
			String ip = tela.getIp().getText();
			String nick = tela.getNick().getText();
			
			nick = nick.trim();
			ip = ip.trim();
			//o nome do usu�rio n�o pode conter os caracteres abaixo pois eles s�o importantes no nosso protocolo
			if(nick.matches(";") || nick.matches("|") || nick.matches(",")) {
				JOptionPane.showConfirmDialog(tela,"Nickname inv�lido","Erro",JOptionPane.ERROR_MESSAGE);
				nick="";
			}
			if(ip.length()>0 && nick.length()>0){
				//esse request n tem problema ser bloqueante (tipo o login msm)
				InetAddress add=null;
				try {
					add = InetAddress.getByName(ip);
				} catch (UnknownHostException e1) {
					JOptionPane.showConfirmDialog(tela,"Ip inv�lido","Erro",JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
					
				}
				
				//se o endere�o do servidor existe
				if(add!=null){
					Socket clientSocket;
					try {
						clientSocket = new Socket(add, Main.PORTA);
					
						DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
						BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						
						outToServer.writeBytes("1;"+nick+"\n");//formato request login "1;[nomejogador]" - resposta servidor "1;[0 ou 1]", onde 0 = ok e 1 = fail
						String response = inFromServer.readLine();
						
						outToServer.close();
						inFromServer.close();
						clientSocket.close();
						if(response.equals("1;0")){
							//login success, destroi a tela atual e inicia a tela principal, sua controladora e a caixa de entrada
							tela.dispose();
							
							MessageBox inServer;
							
							inServer = new MessageBox();
							
							MainScreen main = new MainScreen();
							new MainScreenController(main,inServer,nick,add);
						} else {
							JOptionPane.showConfirmDialog(tela,"Nickname inv�lido","Erro",JOptionPane.ERROR_MESSAGE);
						}
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(tela, ex.getMessage());
					}
				}
				
			}
		}
		// TODO Auto-generated method stub
		
	}

}
