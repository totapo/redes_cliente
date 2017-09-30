package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.NetConnection;
import pattern.MessageBox;
import pattern.Observer;
import view.MainScreen;

public class MainScreenController implements Observer, ActionListener, ListSelectionListener{

	private MainScreen tela;
	private MessageBox inFromServer;
	private InetAddress server;
	private boolean isPlayersTurn;
	private String playerId,matchId,otherPlayerId;
	private final ExecutorService pool;
	
	public MainScreenController(MainScreen s, MessageBox inServer, String nick, InetAddress server){
		this.tela=s;
		playerId=nick;
		inFromServer=inServer;
		this.server=server;
		
		tela.getBtnEncerrar().addActionListener(this);
		tela.getBtnInvite().addActionListener(this);
		tela.getPlayerList().addListSelectionListener(this);
		
		tela.getFrmReversi().setVisible(true);
		tela.getFrmReversi().validate();
		
		pool = Executors.newCachedThreadPool();
		pool.execute(new NetConnection(this.server,inFromServer,"2;"+playerId+"\n",Main.KEEPALIVETIMER)); //inicializa a thread que roda o keepAlive
	
		this.isPlayersTurn=false;
	}
	
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onObservableChanged() {
		String msg;
		while((msg=inFromServer.getFirsMessage())!=null){
			
		}
	}

}
