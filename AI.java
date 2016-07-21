
public class AI extends Player_type_abstract{
	GUI_abstract gui;
	public AI(GUI_abstract g){gui=g;}

	public void message(String message){gui.message(message);}
	public void show_info(String[] player_names, int round, int currentPlayer, String message, int[][] points, int[] free, int spin_counter){gui.show_info(player_names,round,currentPlayer,message,points,free,spin_counter);}
	public void spin(String[] choices, int outcome){gui.spin(choices,outcome);}
	public void show_board(int round, int category, String[] categories, int[] questions){gui.show_board(round,category,categories,questions);}

	public int get_n(String message, int lowerbound, int upperbound){
		int range = upperbound-lowerbound;
		int r=(int) (Math.random()*(range))%(range)+lowerbound;
		gui.message(message+"\nAI chose "+r);
		return r;
	}

	public int yes_no_prompt(String message){
		int r=(int) (Math.random()*(2))%(2);
		if(r==1){	gui.message(message+"\nAI chose yes"); }
		else {		gui.message(message+"\nAI chose no");}
		return r;
	}
	public void ask_question(int timer, String question){
		gui.message(question+"\nAI is considering the question");
	}
	public int check_answer(String answer){
		int r=(int) (Math.random()*(3))%(3);
		if(r==0){	gui.message(answer+"\nAI answered incorrectly"); }
		else if(r==1){	gui.message(answer+"\nAI answered correctly"); }
		else {		gui.message(answer+"\nAI timed out"); }
		return r;
	}
	public int choose_category(String message, String [] categories, int[] questions){ 
		int n = (int) (Math.random()*(categories.length))%(categories.length)+1;
		gui.message(message+"\nAI chose "+categories[n-1]);
		return n;
	}
}
