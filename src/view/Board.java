package view;

import java.awt.ComponentOrientation;
import java.awt.GridLayout;

import javax.swing.JPanel;

import control.MainScreenController;

public class Board extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int size;
	private Casa[][] casas;
	
	public Board(int s){
		this.size=s;
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
	
	
	
	public void updateBoard(int[][] newState, int[][] moves,MainScreenController listener){
		for(int l=0; l<size; l++){
			for(int c=0; c<size;c++){
				casas[l][c].setTipo(newState[l][c]);
				casas[l][c].removeMouseListener(listener);
			}
		}
		
		if(moves!=null){
			for(int l=0; l<moves.length; l++){
				int x=moves[l][0];
				int y=moves[l][1];
				casas[x][y].setTipo(2); //movimentos possiveis
				casas[x][y].addMouseListener(listener);
			}
		}
	}
	
}
