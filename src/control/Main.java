package control;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main {
	public static final int PORTA=12345;
	public static final int KEEPALIVETIMER=10000; //10 segundos
	
	public static void main(String[] args) {
		

		/*// a jframe here isn't strictly necessary, but it makes the example a little more real
	    JFrame frame = new JFrame("InputDialog Example #1");

	    // prompt the user to enter their name
	    //String name = JOptionPane.showInputDialog(frame, "What's your name?");

	    // get the user's input. note that if they press Cancel, 'name' will be null
	    
	    try {
			InetAddress server = InetAddress.getLocalHost();
			Socket clientSocket;
			try {
				clientSocket = new Socket(server, 12345);
			
				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				outToServer.writeBytes("/biatch" +'\n');
				String modifiedSentence = inFromServer.readLine();
				System.out.println("FROM SERVER: " + modifiedSentence);
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
	
	
	

}
