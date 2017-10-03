package control;

import view.ConnectScreen;

public class Main {
	public static final int PORTA=12345;
	public static final int KEEPALIVETIMER=10000; //10 segundos
	public static final int TimerRefreshListaJogadores=40000; //40 segundos
	
	public static void main(String[] args) {
		new ConnectScreenController(new ConnectScreen());
	}

}
