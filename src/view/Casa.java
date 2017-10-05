package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class Casa extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int x,y;
	private int tipo;
	
	public Casa(int tipo, int x, int y){
		this.x=x;
		this.y=y;
		this.tipo=tipo;
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public void setTipo(int tipo){
		this.tipo=tipo;
	}
	
	@Override
    public void paintComponent(Graphics g) {
		Dimension d = this.getSize();
		g.setColor(Color.GREEN);
		g.fillRect(0, 0, d.width, d.height);
		if(tipo==2){
			g.setColor(Color.yellow);
			g.fillOval(15, 15, d.width-30, d.height-30);
		} else if(tipo!=0){
			g.setColor((tipo==-1)?Color.black:Color.white);
			g.fillOval(5, 5, d.width-10, d.height-10);
		}
    }

}
