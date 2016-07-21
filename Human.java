
public class Human extends Player_type_abstract{
	GUI_abstract gui;
	public Human(GUI_abstract g){gui=g;}

	public void message(String message){gui.message(message);}
	public void show_info(String[] player_names, int round, int currentPlayer, String message, int[][] points, int[] free, int spin_counter){gui.show_info(player_names,round,currentPlayer,message,points,free,spin_counter);}
	public void spin(String[] choices, int outcome){gui.spin(choices,outcome);}
	public void show_board(int round, int category, String[] categories, int[] questions){gui.show_board(round,category,categories,questions);}

	public int get_n(String message, int lowerbound, int upperbound){
		int r=gui.get_n(message);
		while(r < lowerbound || r > upperbound){
			r=gui.get_n(message);
		}
		return r;
	}


	public int yes_no_prompt(String message){return gui.yes_no_prompt(message);}
	public void ask_question(int timer, String question){gui.ask_question(timer,question);}
	public int check_answer(String answer){return gui.check_answer(answer);}
	public int choose_category(String message, String [] categories, int[] questions){
		int n = 0;
		boolean invalid = true;
		while(invalid){
			n=gui.choose_category(message,categories);
			if(! (1 <= n && n <= 6)){continue;}
			if(questions[n-1]>4){gui.message("Please choose a different category"); continue;} 
			break;
		}
		return n;
	}
}
