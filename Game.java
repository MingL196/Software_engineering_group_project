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
				case 0: category(0); break;
				case 1: category(1); break;
				case 2: category(2); break;
				case 3: category(3); break;
				case 4: category(4); break;
				case 5: category(5); break;
				case 6: lose(); break;
				case 7: free(); break;
				case 8: bankrupt(); break;
				case 9: player_choice(); break;
				case 10: opponent_choice(); break;
				case 11: spin_again(); break;
			}
			check_round_finished();
		}
		end();
	}

	private void init(){
		numberPlayers = 0;
		while(numberPlayers <=0){ numberPlayers = gui.get_n_players(); }
		// ...
	}

	private int spin(){ return 1; }

	private void category(int i){}
	private void lose(){}
	private void free(){}
	private void bankrupt(){}
	private void player_choice(){}
	private void opponent_choice(){}
	private void spin_again(){}

	private void check_round_finished(){ done=true; }

	private void end(){ }
}
