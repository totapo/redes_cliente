package view;


import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JList;

import java.awt.GridLayout;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import java.awt.Color;

import javax.swing.border.TitledBorder;


import javax.swing.UIManager;
import javax.swing.JButton;

public class MainScreen {
	//tela principal
	private JFrame frmReversi;
	private JLabel gameLabel;
	private JPanel playerPiece;
	private JPanel otherPiece;
	private JButton btnEncerrar;
	private JPanel gamePanel;
	private JLabel playerCount;
	private JLabel otherCount;
	private Board b;

	/**
	 * Create the application.
	 */
	public MainScreen() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmReversi = new JFrame();
		frmReversi.setTitle("Reversi");
		frmReversi.setResizable(false);
		frmReversi.setBounds(100, 100, 859, 546);
		frmReversi.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmReversi.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Jogo", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBackground(UIManager.getColor("Panel.background"));
		panel.setBounds(0, 0, 589, 495);
		frmReversi.getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		gamePanel = new JPanel();
		gamePanel.setLayout(new GridLayout(1,1));
		panel.add(gamePanel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(588, 0, 263, 495);
		frmReversi.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Status Jogo", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_2.setBounds(0, 0, 263, 205);
		panel_1.add(panel_2);
		panel_2.setLayout(null);
		
		JPanel panel_6 = new JPanel();
		panel_6.setBounds(10, 161, 243, 33);
		panel_2.add(panel_6);
		
		btnEncerrar = new JButton("Encerrar");
		btnEncerrar.setActionCommand("encerrarPartida");
		panel_6.add(btnEncerrar);
		
		JPanel panel_7 = new JPanel();
		panel_7.setBounds(10, 21, 243, 129);
		panel_2.add(panel_7);
		panel_7.setLayout(null);
		
		JPanel panel_8 = new JPanel();
		panel_8.setBounds(10, 11, 223, 29);
		panel_7.add(panel_8);
		panel_8.setLayout(new BorderLayout(0, 0));
		
		gameLabel = new JLabel("");
		panel_8.add(gameLabel, BorderLayout.CENTER);
		
		playerPiece = new JPanel();
		playerPiece.setBounds(10, 51, 52, 50);
		panel_7.add(playerPiece);
		
		otherPiece = new JPanel();
		otherPiece.setBounds(124, 51, 52, 50);
		panel_7.add(otherPiece);
		
		playerCount = new JLabel("");
		playerCount.setBounds(72, 71, 42, 14);
		panel_7.add(playerCount);
		
		otherCount = new JLabel("");
		otherCount.setBounds(186, 71, 42, 14);
		panel_7.add(otherCount);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Jogadores Online", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_3.setBounds(0, 216, 263, 279);
		panel_1.add(panel_3);
		panel_3.setLayout(null);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(10, 24, 243, 213);
		panel_3.add(panel_4);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_4.add(scrollPane);
		
		playerList = new JList<String>();
		playerList.setModel(new DefaultListModel<String>());
		scrollPane.setViewportView(playerList);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBounds(10, 240, 243, 33);
		panel_3.add(panel_5);
		
		btnInvite = new JButton("Convidar");
		btnInvite.setActionCommand("convidar");
		panel_5.add(btnInvite);
	}
	
	private JList<String> playerList;
	private JButton btnInvite;
	private boolean isWhite;

	public JLabel getGameLabel() {
		return gameLabel;
	}

	public JButton getBtnEncerrar() {
		return btnEncerrar;
	}

	public JList<String> getPlayerList() {
		return playerList;
	}

	public JButton getBtnInvite() {
		return btnInvite;
	}
	
	public JLabel getPlayerCount() {
		return playerCount;
	}

	public JLabel getOtherCount() {
		return otherCount;
	}

	public void setTurn(boolean isBlackTurn){
		if(!isWhite){
			if(isBlackTurn){
				playerPiece.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				otherPiece.setBorder(BorderFactory.createEmptyBorder());	
			} else {
				otherPiece.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				playerPiece.setBorder(BorderFactory.createEmptyBorder());
			}
		} else {
			if(isBlackTurn){
				otherPiece.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				playerPiece.setBorder(BorderFactory.createEmptyBorder());
			} else {
				playerPiece.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				otherPiece.setBorder(BorderFactory.createEmptyBorder());
			}
		}
	}
	
	public void startGame(int bSize, MouseListener ctrl){
		this.b = new Board(bSize,ctrl);
		this.gamePanel.setLayout(new BorderLayout());
		this.gamePanel.add(b,BorderLayout.CENTER);
	}
	
	public void resetBoard(){
		this.b.clear();
	}
	
	public void drawBoard(int[][] board, int[][] moves){
		b.updateBoard(board, moves);
	}
	
	public void setPlayerColor(boolean isWhite){
		this.isWhite=isWhite;
		
		this.playerPiece.removeAll();
		this.otherPiece.removeAll();
		
		this.playerPiece.setLayout(new GridLayout(1,1));
		this.otherPiece.setLayout(new GridLayout(1,1));
		
		this.playerPiece.add(new Casa((isWhite)?-1:1,0,0));
		this.otherPiece.add(new Casa((isWhite)?1:-1,0,0));
	}

	public JFrame getFrmReversi() {
		return frmReversi;
	}

	public void refresh() {
		frmReversi.repaint();
		frmReversi.revalidate();
	}

	public void setCounts(int wCount, int blCount) {
		if(isWhite){
			this.playerCount.setText(""+wCount);
			this.otherCount.setText(""+blCount);
		} else {
			this.playerCount.setText(""+blCount);
			this.otherCount.setText(""+wCount);
		}
	}

	public void limparJogo() {
		this.btnEncerrar.setEnabled(false);
		this.gameLabel.setText("");
		this.playerCount.setText("");
		this.playerPiece.removeAll();
		this.otherPiece.removeAll();
		this.b.clearListeners();
		this.playerPiece.setBorder(BorderFactory.createEmptyBorder());
		this.otherPiece.setBorder(BorderFactory.createEmptyBorder());
		
		this.otherCount.setText("");
		this.btnInvite.setEnabled(true);
	}
	
}
