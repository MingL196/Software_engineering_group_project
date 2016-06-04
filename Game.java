import java.io.*;

class Game {
	GUI_abstract gui;

	private String[] categories;
	private int[] questions;

	boolean done = false;
	public  int spin_counter;
	private int numberPlayers;
	private int currentPlayer;
	private int round;
	private int[] points; // Note: I don't think we need to reset points after each round, so one array is enough
	private int[] free;

	public Game(){
		gui = new GUI_text_version();
//		gui = new GUI_interface();
	}

	public void start(){
		init();
		while(!done){
			int outcome = spin();
			switch(outcome){
				case 0: category1(); break;
				case 1: category2(); break;
				// ...
			}
			check_round_finished();
		}
		end();
	}

	private void init(){
		numberPlayers = gui.get_n_players();
		// ...
	}

	private int spin(){ return 1; }

	private void category1(){}
	private void category2(){}
	// ...

	private void check_round_finished(){ done=true; }

	private void end(){ }
}
