package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import javax.swing.JButton;

public class ConnectScreen extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public ConnectScreen() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 222, 144);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		
		JLabel lblNewLabel = new JLabel("IP Servidor: ");
		
		textField = new JTextField();
		textField.setColumns(10);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		panel.add(lblNewLabel);
		panel.add(textField);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1);
		panel_1.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblNome = new JLabel("Nome:");
		panel_1.add(lblNome);
		
		textField_1 = new JTextField();
		panel_1.add(textField_1);
		textField_1.setColumns(15);
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2);
		
		JButton btnConectar = new JButton("Conectar");
		panel_2.add(btnConectar);
	}

}
