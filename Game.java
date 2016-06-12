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
	private int[] free; // Question: Does free tokens reset each round?

	public Game(){
//		gui = new GUI_text_version();
		gui = new GUI_interface();
	}

	public void start(){
		init();
		while(!done){
			int outcome = spin();
			switch(outcome){
				case 0: lose(); break;
				case 1: free(); break;
				case 2: bankrupt(); break;
				case 3: player_choice(); break;
				case 4: opponent_choice(); break;
				case 5: spin_again(); break;
				default: category(outcome - 6); break;
			}
			check_round_finished();
		}
		end();
	}

	private void init(){
		numberPlayers = 0;
		while(numberPlayers <=0){ numberPlayers = gui.get_n_players(); }
		System.out.println(numberPlayers);
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
