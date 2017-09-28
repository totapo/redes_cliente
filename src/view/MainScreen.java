package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.JList;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import net.miginfocom.swing.MigLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.ListSelectionModel;
import javax.swing.AbstractListModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.JButton;

public class MainScreen {

	private JFrame frmReversi;

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
		
		JMenuBar menuBar = new JMenuBar();
		frmReversi.setJMenuBar(menuBar);
		frmReversi.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Jogo", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBackground(UIManager.getColor("Panel.background"));
		panel.setBounds(0, 0, 589, 495);
		frmReversi.getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JPanel gamePanel = new JPanel();
		panel.add(gamePanel);
		gamePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
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
		
		JButton btnEncerrar = new JButton("Encerrar");
		panel_6.add(btnEncerrar);
		
		JPanel panel_7 = new JPanel();
		panel_7.setBounds(10, 21, 243, 129);
		panel_2.add(panel_7);
		
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
		
		JList list = new JList();
		scrollPane.setViewportView(list);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBounds(10, 240, 243, 33);
		panel_3.add(panel_5);
		
		JButton btnConvidarParaPartida = new JButton("Convidar");
		panel_5.add(btnConvidarParaPartida);
	}
}
