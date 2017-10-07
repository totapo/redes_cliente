package view;

import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class Board extends JPanel{
	//classe que representa o painel onde o tabuleiro é exibido na tela principal
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int size;
	private Casa[][] casas;
	private MouseListener ctrl;
	
	public Board(int s, MouseListener ctrl){
		this.size=s;
		this.ctrl=ctrl;
		casas=new Casa[size][size]; 
		
		this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		GridLayout g = new GridLayout(size,size);
		this.setLayout(g);
		
		for(int l=0; l<size; l++){
			for(int c=0; c<size;c++){
				casas[l][c]=new Casa(0,l,c);
				add(casas[l][c]);
			}
		}
		
	}
	
	
	
	public void updateBoard(int[][] newState, int[][] moves){
		for(int l=0; l<size; l++){
			for(int c=0; c<size;c++){
				casas[l][c].setTipo(newState[l][c]);
				casas[l][c].removeMouseListener(ctrl);
			}
		}
		
		if(moves!=null){
			for(int l=0; l<moves.length; l++){
				int x=moves[l][0];
				int y=moves[l][1];
				casas[x][y].setTipo(2); //movimentos possiveis
				casas[x][y].addMouseListener(ctrl);
			}
		}
	}



	public void clear() {
		for(int l=0; l<size; l++){
			for(int c=0; c<size;c++){
				casas[l][c].setTipo(0);
			}
		}
	}



	public void clearListeners() {
		for(int l=0; l<size; l++){
			for(int c=0; c<size;c++){
				if(casas[l][c].getTipo()==2){
					casas[l][c].setTipo(0);
					casas[l][c].removeMouseListener(ctrl);
				}
			}
		}
	}
	
}
