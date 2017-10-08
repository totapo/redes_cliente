package control;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import view.Casa;
import view.MainScreen;

public class MainScreenController implements Observer, ActionListener, ListSelectionListener, MouseListener, WindowListener{
	//classe responsavel por controlar o que acontece na tela principal (e por iniciar as threads de comunicação com o servidor)
	private MainScreen tela;
	private MessageBox inFromServer;
	private InetAddress server;
	private String playerId,otherPlayerId;
	private long matchId;
	private final ExecutorService pool;
	private long lastRefresh=0;
	private String selectedPlayer;
	private boolean inGame;
	private int lastX;
	private int lastY;
	
	//recebe a tela, uma caixa de mensagem que guarda as mensagens do servidor, o apelido do jogador e o ip do servidor
	public MainScreenController(MainScreen s, MessageBox inServer, String nick, InetAddress server){
		this.tela=s;
		playerId=nick;
		inFromServer=inServer;
		this.server=server;
		
		inFromServer.registerObserver(this); //se registra como observador da caixa de entrada
		
		//se registra como Listener dos botoes/lista de nomes da tela principal
		tela.getBtnEncerrar().addActionListener(this);
		tela.getBtnInvite().addActionListener(this);
		tela.getPlayerList().addListSelectionListener(this);
		
		//inicia a o display da tela principal
		tela.getFrmReversi().setVisible(true);
		tela.getFrmReversi().validate();
		
		//inicia o executor das threads responsaveis por fazer as requisições tcp ao servidor
		pool = Executors.newCachedThreadPool();
		
		 //inicializa a thread que roda o keepAlive
		pool.execute(new NetConnection(this.server,inFromServer,"2;"+playerId+"\n",Main.KEEPALIVETIMER));
		
		//inicializa as variaveis para o controle do andamento do jogo
		matchId=-1;
		inGame=false;
		lastX=lastY=-1;
		
		//inicializa o tabuleiro vazio na tela principal
		tela.startGame(8,this);
		habilitarInvite(true);
		
		//da um titulo para a tela principal
		tela.getFrmReversi().setTitle(tela.getFrmReversi().getTitle()+" "+playerId);
		
		tela.limparJogo();
	}
	
	@Override
	public void valueChanged(ListSelectionEvent a) { 
		//metodo executado quando o usuario seleciona um item na lista de usuarios online
		//apenas seta a variavel selectedPlayer
		selectedPlayer = tela.getPlayerList().getSelectedValue();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//metodo executado quando o usuario clica em algum dos botoes
		if(e.getActionCommand().equals(this.tela.getBtnEncerrar().getActionCommand())){
			//envia uma requisição para encerrar a partida
			pool.execute(new NetConnection(this.server,inFromServer,"7;"+playerId+";"+matchId+"\n"));
		} else if(e.getActionCommand().equals(this.tela.getBtnInvite().getActionCommand())) {
			//se houver um jogador selecionado envia a requisição de desafio ao servidor, e desabilita o envio de novos desafios
			if(selectedPlayer.length()>0) {
				pool.execute(new NetConnection(this.server,inFromServer,"3;"+playerId+";"+selectedPlayer+"\n"));
				habilitarInvite(false);
			}
		}
	}

	@Override
	synchronized public void onObservableChanged() {//executado quando alguma mensagem é inserida na caixa de entrada
		String msg;
		while((msg=inFromServer.getFirsMessage())!=null){
			String[] params = msg.split(";");
			if(params.length>0){
				switch(Integer.parseInt(params[0].trim())){
				case 2: //keepAlive-formato resposta: "2;[mensagens];[listajogadores]"
					tratarKeepAlive(params);
					break;
				case 3: //challenge-formato resposta: "3;[playerId];[status];[matchid?]" status 0 = convite enviado; 1 = convite não enviado ; matchid só se status=0
					tratarChallenge(params);
					break;
				case 4: //challResponse-formato: "4;[iniciar]" 0= iniciar partida, 1=cancelar
					tratarChallengeResponse(params);
					break;
				case 5: //getBoard-formato: "5;[blackId];[whiteId];[board];[possibleMoves];[blackCount];[whiteCount];[turn]" turn=0 black; turn=1 white; turn=-1 encerrado
					updateBoard(params);
					break;
				case 6: //makeMove-formato resposta: "6;[isOk];[5params]" isOk=0 deu certo, isOk =1 fail tem q repetir
					makeMoveResponse(params);
					break;
				case 8: //getwinner response formato: "8;[winnerId]"
					showWinner(params);
					break;
				}
		}
	}

}
	private void showWinner(String[] params) {//exibe um alerta indicando quem venceu a partida
		String winnerName = params[1];
		if(winnerName.trim().length()>0){
			JOptionPane.showMessageDialog(tela.getFrmReversi(), winnerName+" venceu a partida!");
		} else {
			JOptionPane.showMessageDialog(tela.getFrmReversi(), "A partida terminou em empate!");
		}
		clearGame();
	}

	private void makeMoveResponse(String[] params) {//verifica se o movimento foi registrado no servidor, caso não tenha sido, pede ao usuario apra tentar novamente
		if(Integer.parseInt(params[1])==0){
			params = Arrays.copyOfRange(params, 1, params.length);
			updateBoard(params);
		} else {
			JOptionPane.showMessageDialog(tela.getFrmReversi(), "Movimento não foi registrado, tente novamente");
		}
		this.lastX=lastY=-1;
	}

	private void updateBoard(String[] params) {//atualiza o tabuleiro conforme os parametros recebidos
		int[][]boardState,moves;
		
		String b = params[3].trim();
		String m = params[4].trim();
		
		int blCount,wCount;
		wCount = Integer.parseInt(params[6].trim());
		blCount = Integer.parseInt(params[5].trim());
		
		String[] cols, rows=b.split("\\|");
		
		//faz o parse do tabuleiro em String para int[][]
		boardState = new int[rows.length][rows.length];
		for(int l=0; l<boardState.length; l++){
			cols = rows[l].split(",");
			for(int c=0; c<boardState.length; c++){
				boardState[l][c]=Integer.parseInt(cols[c]);
			}
		}
		
		moves=null;
		//adiciona os movimentos possiveis na lista, se existir algum movimento
		if(params[4].trim().length()>0){
			rows = m.split("\\|");
			moves = new int[rows.length][2];
			for(int l=0; l<moves.length; l++){
				cols = rows[l].split(",");
				moves[l][0]=Integer.parseInt(cols[0]);
				moves[l][1]=Integer.parseInt(cols[1]);
			}
		}
		
		if(inGame){
			//nao precisa criar o tabuleiro, só faz o update msm
			tela.drawBoard(boardState, moves);
			
		} else{ //se o jogo ainda não começou, limpa o tabuleiro antigo e desenha o novo
			tela.resetBoard();	
			this.tela.getBtnEncerrar().setEnabled(true);
			tela.setPlayerColor(this.playerId.equals(params[2].trim()));

			
			tela.drawBoard(boardState, moves);
			inGame=true;
		}
		
		int turn = Integer.parseInt(params[7].trim());
		//se o turno for <0 quer dizer que o jogo acabou
		if(turn>=0){//se o jogo continua, atualiza a marcação de turno(borda) na tela principal
			tela.getGameLabel().setText(this.playerId+" versus "+this.otherPlayerId);
			tela.setTurn((turn==0));
		}else{//caso contrario verifica quem foi o vencedor, enviando um request para o servidor
			tela.getGameLabel().setText("Jogo encerrado");
			pool.execute(new NetConnection(this.server,inFromServer,"8;"+this.playerId+";"+this.matchId+"\n")); //pede o vencedor do jogo
		}
		
		//atualiza a contagem das peças
		tela.setCounts(wCount,blCount);
		
		tela.refresh();
	}

	private void tratarChallengeResponse(String[] params) {
		//este jogador recebeu um desafio e enviou a resposta ao servidor
		//o servidor envia uma mensagem de volta indicando se a partida iniciará ou não
		if(Integer.parseInt(params[1].trim())==0){
			//start match
			JOptionPane.showMessageDialog(tela.getFrmReversi(), "Partida com "+otherPlayerId+" iniciará em instantes.");
		} else {
			//close match
			JOptionPane.showMessageDialog(tela.getFrmReversi(), "Partida com "+otherPlayerId+" cancelada.");
			matchId=-1;
			otherPlayerId="";
			habilitarInvite(true);
		}
	}

	private void tratarChallenge(String[] params) {
		//este jogador enviou um desafio a outro
		//o servidor envia uma mensagem de volta indicando se conseguiu adicionar o pedido na caixa de mensagens do outro ou não
		String msg="";
		if(matchId==-1) { //se uma partida não estiver rolando (se alguma outra partida começou antes da resposta do servidor, o cliente ignora e continua a partida já iniciada
			if(Integer.parseInt(params[1].trim())==0) {
				msg="Desafio enviado com sucesso! ("+params[3].trim()+")";
				this.otherPlayerId=params[3].trim();
				this.matchId=Integer.parseInt(params[2].trim());
			} else {
				msg="Desafio não foi concluído!";
				tela.getBtnInvite().setEnabled(true);
			}
			JOptionPane.showMessageDialog(tela.getFrmReversi(), msg);
			tela.refresh();
		}
	}
	
	//habilita o botao para envio de convites aos jogadores
	public void habilitarInvite(boolean hab){
		tela.getBtnInvite().setEnabled(hab);
		tela.refresh();
	}

	//"," separa parametros internos e "|" separa as mensagens
	private void tratarKeepAlive(String[] params) { //responsável por tratar as mensagens recebidas como resposta do keepAlive
		String[] mensagens = params[1].trim().split("\\|");
		String[] jogadores = params[2].trim().split("\\|");
		//keep alive tem dois parametros, um deles é a lista de jogadores online e o outro é a lista de mensagens na "caixa" do usuário
		
		//atualiza  a lista de jogadores online, de acordo com o timer (keep alive roda a cada 5 segundos,
		//se a lista atualizasse tao rapido não seria fácil selecionar um jogador para desafiar)
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
		
		//le as mensagens enfileiradas para o jogaodr
		String[] mParams;
		for(String s:mensagens) {
			if(s.length()>0){
				System.out.println(s);
				mParams=s.split(",");
				switch(Integer.parseInt(mParams[0])) {
				case 3: //desafio recebido
					if(matchId==-1){ //se o jogador nao estiver no meio de um jogo ele pode responder ao desafio
						int r = JOptionPane.showConfirmDialog(tela.getFrmReversi(), "Desafio enviado por "+mParams[1],"Desafio",JOptionPane.YES_NO_OPTION);

						matchId = Long.parseLong(mParams[2]);
						this.otherPlayerId=mParams[1];
						if(r==JOptionPane.YES_OPTION) {
							r=0;
							habilitarInvite(false);
						} else {
							r=1;
						}
						
						pool.execute(new NetConnection(this.server,inFromServer,"4;"+matchId+";"+r+"\n")); //thread que envia a resposta do desafio 
						matchId=(r==1)?-1:matchId;
					}
					break;
				case 5: //deve atualizar o tabuleiro
					if(this.matchId==Integer.parseInt(mParams[2])){
						int ok = Integer.parseInt(mParams[1]);
						if(ok==0)
							//faz a requisição que traz os dados atualizados
							pool.execute(new NetConnection(this.server,inFromServer,"5;"+playerId+";"+matchId+"\n")); //pede o tabuleiro e as informações do jogo ao servidor
						else
							//caso haja um problema, limpa o estado do jogo atual
							clearGame();//
						break;
					}
				case 7: //caso a partida tenha sido encerrada, exibe um alerta com o motivo e limpa o estado do jogo atual
					JOptionPane.showMessageDialog(this.tela.getFrmReversi(), mParams[1]);
					clearGame();
				}
			}
		}
		
	}

	
	private void clearGame() { //limpa o estado do jogo atual, para que o usuario possa começar um novo se quiser
		this.inGame=false;
		this.lastX=lastY=-1;
		this.matchId=-1;
		this.otherPlayerId="";
		tela.limparJogo();
		tela.refresh();
	}

	//ESCUTA TABULEIRO
	@Override
	public void mouseClicked(MouseEvent arg) {
		Component source = arg.getComponent();
		if(source.getClass().equals(Casa.class)){
			Casa src = (Casa) source;
			if(lastX==-1 && lastY==-1){ //se já não enviou um request para fazer um movimento ao servidor
				lastX = src.getX();
				lastY=src.getY();
				pool.execute(new NetConnection(this.server,inFromServer,"6;"+playerId+";"+matchId+";"+src.getXCord()+";"+src.getYCord()+"\n"));	
			}
		}
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

	@Override
	public void windowActivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		pool.shutdownNow(); //tenta encerrar todas as threads ainda vivas
	}

	@Override
	public void windowClosing(WindowEvent e) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		
	}
}
