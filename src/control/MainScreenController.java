package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetAddress;
import java.util.Arrays;
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

public class MainScreenController implements Observer, ActionListener, ListSelectionListener, MouseListener{

	private MainScreen tela;
	private MessageBox inFromServer;
	private InetAddress server;
	private String playerId,otherPlayerId;
	private long matchId;
	private final ExecutorService pool;
	private long lastRefresh=0;
	private String selectedPlayer;
	private boolean inGame;
	
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
		
		matchId=-1;
		inGame=false;
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
				habilitarInvite(false);
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
				case 3: //challenge-formato resposta: "3;[playerId];[status];[matchid?]" 0 = convite enviado; 1 = convite n�o enviado ; matchid s� se status=0
					tratarChallenge(params);
					break;
				case 4: //challResponse-formato: "4;[iniciar]" 0= iniciar partida, 1=cancelar
					tratarChallengeResponse(params);
					break;
				case 5: //getBoard-formato: "5;[blackId];[whiteId];[board];[possibleMoves];[blackCount];[whiteCount];[turn]" turn=0 black; turn=1 white; turn=-1 encerrado
					System.out.println(msg);
					updateBoard(params);
					break;
				case 6: //makeMove-formato: "6;[playerId];[matchId];[xCoordinate];[yCoordinate]"
					break;
				case 7: //endMatch-formato: "7;[playerId];[matchId]"
					break;
				}
		}
	}

}
	private void updateBoard(String[] params) {
		int[][]boardState,moves;
		
		String b = params[3];
		String m = params[4];
		
		int blCount,wCount;
		wCount = Integer.parseInt(params[6]);
		blCount = Integer.parseInt(params[5]);
		
		String[] cols, rows=b.split("\\|");
		
		boardState = new int[rows.length][rows.length];
		for(int l=0; l<boardState.length; l++){
			cols = rows[l].split(",");
			for(int c=0; c<boardState.length; c++){
				boardState[l][c]=Integer.parseInt(cols[c]);
			}
		}
		
		moves=null;
		
		if(params[4].length()>0){
			rows = m.split("\\|");
			moves = new int[rows.length][2];
			for(int l=0; l<moves.length; l++){
				cols = rows[l].split(",");
				for(int c=0; c<moves.length; c++){
					moves[l][c]=Integer.parseInt(cols[c]);
				}
			}
		}
		for(int[] a:boardState)
			System.out.println(Arrays.toString(a));
		if(inGame){
			//nao precisa criar o tabuleiro, s� faz o update msm
			tela.drawBoard(this, boardState, moves);
			
			tela.setPlayerColor(this.playerId.equals(params[2]));
		} else{
			tela.startGame(boardState.length);
			tela.drawBoard(this, boardState, moves);
			inGame=true;
		}
		
		int turn = Integer.parseInt(params[7]);
		
		if(turn>=0){
			tela.getGameLabel().setText("Turno atual: "+((turn==0)?"Pretas":"Brancas"));
			tela.setTurn((turn==0));
		}else{
			tela.getGameLabel().setText("Jogo encerrado");
		}
		
		tela.setCounts(wCount,blCount);
	
		tela.refresh();
	}

	private void tratarChallengeResponse(String[] params) {
		if(Integer.parseInt(params[1])==0){
			//start match
			JOptionPane.showMessageDialog(tela.getFrmReversi(), "Partida com "+otherPlayerId+" iniciar� em instantes.");
		} else {
			//close match
			JOptionPane.showMessageDialog(tela.getFrmReversi(), "Partida com "+otherPlayerId+" cancelada.");
			matchId=-1;
			otherPlayerId="";
			habilitarInvite(true);
		}
	}

	private void tratarChallenge(String[] params) {
		String msg="";
		if(matchId==-1) { //se uma partida n�o estiver rolando
			if(Integer.parseInt(params[1])==0) {
				msg="Desafio enviado com sucesso! ("+params[3]+")";
				this.matchId=Integer.parseInt(params[2]);
			} else {
				msg="Desafio n�o foi conclu�do!";
				tela.getBtnInvite().setEnabled(true);
			}
			JOptionPane.showMessageDialog(tela.getFrmReversi(), msg);
			tela.refresh();
		}
	}
	
	public void habilitarInvite(boolean hab){
		//tela.getPlayerList().setSelectedIndex(-1);
		tela.getBtnInvite().setEnabled(hab);
		tela.refresh();
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
			if(s.length()>0){
				System.out.println(s);
				mParams=s.split(",");
				switch(Integer.parseInt(mParams[0])) {
				case 3: 
					if(matchId==-1){ //se o jogador nao estiver no meio de um jogo ele pode responder ao desafio
						int r = JOptionPane.showConfirmDialog(tela.getFrmReversi(), "Desafio enviado por "+mParams[1],"Desafio",JOptionPane.YES_NO_OPTION);
						
						if(r==JOptionPane.YES_OPTION) {
							r=0;
							this.otherPlayerId=mParams[1];
							matchId = Long.parseLong(mParams[2]);
							habilitarInvite(false);
						} else {
							r=1;
						}
						
						pool.execute(new NetConnection(this.server,inFromServer,"4;"+matchId+";"+r+"\n")); //thread que envia a resposta do desafio 
						break;
					}
				case 5:
					int ok = Integer.parseInt(mParams[1]);
					if(ok==0)
						pool.execute(new NetConnection(this.server,inFromServer,"5;"+playerId+";"+matchId+"\n")); //pede o tabuleiro e as informa��es do jogo ao servidor
					else
						;//TODO cancelar partida
				}
			}
		}
		
	}

	
	//ESCUTA TABULEIRO
	@Override
	public void mouseClicked(MouseEvent arg) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
