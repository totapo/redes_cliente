package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
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
	private String playerId,otherPlayerId;
	private long matchId;
	private final ExecutorService pool;
	private long lastRefresh=0;
	private String selectedPlayer;
	
	public MainScreenController(MainScreen s, MessageBox inServer, String nick, InetAddress server){
		this.tela=s;
		playerId=nick;
		inFromServer=inServer;
		this.server=server;
		
		inFromServer.registerObserver(this);
		
		tela.getBtnEncerrar().addActionListener(this);
		tela.getBtnInvite().addActionListener(this);
		tela.getPlayerList().addListSelectionListener(this);
		
		tela.getFrmReversi().setVisible(true);
		tela.getFrmReversi().validate();
		
		pool = Executors.newCachedThreadPool();
		pool.execute(new NetConnection(this.server,inFromServer,"2;"+playerId+"\n",Main.KEEPALIVETIMER)); //inicializa a thread que roda o keepAlive
		
		this.isPlayersTurn=false;
		matchId=-1;
	}
	
	@Override
	public void valueChanged(ListSelectionEvent a) {
		selectedPlayer = tela.getPlayerList().getSelectedValue();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(this.tela.getBtnEncerrar().getActionCommand())){
			//encerrar partida
		} else if(e.getActionCommand().equals(this.tela.getBtnInvite().getActionCommand())) {
			//challenge
			if(selectedPlayer.length()>0) {
				pool.execute(new NetConnection(this.server,inFromServer,"3;"+playerId+";"+selectedPlayer+"\n"));
				tela.getPlayerList().setSelectedIndex(-1);
				tela.getBtnInvite().setEnabled(false);
				tela.refresh();
			}
		}
	}

	@Override
	synchronized public void onObservableChanged() {
		String msg;
		while((msg=inFromServer.getFirsMessage())!=null){
			String[] params = msg.split(";");
			if(params.length>0){
				switch(Integer.parseInt(params[0])){
				case 2: //keepAlive-formato resposta: "2;[mensagens];[listajogadores]"
					if(params.length==3)
						tratarKeepAlive(params);
					break;
				case 3: //challenge-formato resposta: "3;[playerId];[status];[matchid?]" 0 = convite enviado; 1 = convite não enviado ; matchid só se status=0
					tratarChallenge(params);
					break;
				case 4: //challResponse-formato: "4;[iniciar]" 0= iniciar partida, 1=cancelar
					tratarChallengeResponse(params);
					break;
				case 5: //getBoard-formato: "5;[playerId];[matchId]"
					break;
				case 6: //makeMove-formato: "6;[playerId];[matchId];[xCoordinate];[yCoordinate]"
					break;
				case 7: //endMatch-formato: "7:[playerId]:[matchId]"
					break;
				}
		}
	}

}
	private void tratarChallengeResponse(String[] params) {
		
	}

	private void tratarChallenge(String[] params) {
		String msg="";
		if(matchId==-1) { //se uma partida não estiver rolando
			if(Integer.parseInt(params[1])==0) {
				msg="Desafio enviado com sucesso! ("+params[3]+")";
				this.matchId=Integer.parseInt(params[2]);
			} else {
				msg="Desafio não foi concluído!";
				tela.getBtnInvite().setEnabled(true);
				tela.refresh();
			}
			JOptionPane.showMessageDialog(tela.getFrmReversi(), msg);
		}
	}

	//"," separa parametros internos e "|" separa as mensagens
	private void tratarKeepAlive(String[] params) {
		String[] mensagens = params[1].split("\\|");
		String[] jogadores = params[2].split("\\|");
		
		DefaultListModel<String> modelo = (DefaultListModel<String>)tela.getPlayerList().getModel();
		if(modelo.isEmpty() || System.currentTimeMillis()-lastRefresh>Main.KEEPALIVETIMER){
			modelo.clear();
			for(String jogador : jogadores)
				modelo.addElement(jogador);
			tela.getPlayerList().setModel(modelo);
			
			tela.refresh();
			selectedPlayer="";
			lastRefresh = System.currentTimeMillis();
		}
		
		String[] mParams;
		for(String s:mensagens) {
			mParams=s.split(",");
			switch(Integer.parseInt(mParams[0])) {
			case 3: 
				int r = JOptionPane.showConfirmDialog(tela.getFrmReversi(), "Desafio enviado por "+mParams[1],"Desafio",JOptionPane.YES_NO_OPTION);
				
				if(r==JOptionPane.YES_OPTION) {
					r=0;
					matchId = Long.parseLong(mParams[2]);
				} else {
					r=1;
				}
				pool.execute(new NetConnection(this.server,inFromServer,"4;"+matchId+";"+r+"\n")); //inicializa a thread que roda o keepAlive
				break;
			}
		}
		
	}
}
