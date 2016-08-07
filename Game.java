import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;

class Game {
	public int timer = 30; // seconds
	public int spins_per_round = 50;
	private String[] spin_sectors_header = {"LOSE TURN","FREE TURN","BANKRUPT","PLAYER'S CHOICE","OPPONENT'S CHOICE", "SPIN AGAIN"};

	GUI_abstract gui;
	DB_abstract db = new DB_simple();

	boolean endGame = false;
	private int spin_counter = spins_per_round;
	private int currentPlayer = 0;
	private int round = 0;
	private int[] questions = new int[6]; // number of questions answered in each category
	private int[][] daily = new int[3][2]; // [0 = round 1   1,2 = round 2][0 = category  1 = row]
	String message = "";
	String[] current_spin_sectors;

	private int numberPlayers;
	private int numberAI;
	private Player_type_abstract[] is_AI; //[player]
	private String[] player_names; //[player]
	private int[][] points; //[round][player]
	private int[] free; // [player]

	public Game(int i){
		if(i==0){ gui = new GUI();}
		else {gui = new UI();}
	}

	public void start(){
		init();
		while(!endGame){
			gui.show_info(player_names, round, currentPlayer, message, points, free, spin_counter);
			message = "";
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
			update();
		}
		end();
	}

	private void init(){
		// init daily
		for(int i=0; i<daily.length; i++){
			daily[i][0] = (int) (Math.random()*(6))%(6);
			daily[i][1] = (int) (Math.random()*(5))%(5);
		}
		// get spins per round
		spins_per_round = gui.get_n("Please enter the number of spins per round: ");
		while(spins_per_round <=0){ spins_per_round = gui.get_n("Please enter the number of spins per round: "); }
		spin_counter = spins_per_round;
		// get number of players
		numberPlayers = 0;
		while(numberPlayers <=0){ numberPlayers = gui.get_n("Please enter the number of human controlled players: "); }
		// get number of AI
		numberAI = -1;
		while(numberAI <0){ numberAI = gui.get_n("Please enter the number of AI controlled players: "); }
		numberPlayers += numberAI;
		// initialize class variables
		is_AI = new Player_type_abstract[numberPlayers]; 
		player_names = new String[numberPlayers]; 
		for(int i=0; i<is_AI.length; i++){
			if(i<numberAI){ is_AI[i]=new AI(gui); player_names[i]="AI"; } 
			else { is_AI[i]=new Human(gui);player_names[i]="Player"; }
		}
		points = new int[2][numberPlayers];
		free = new int[numberPlayers];
		// option: rename players
		if(gui.yes_no_prompt("Do you want to rename the players?")==1){
			for(int i=0; i<player_names.length;i++){player_names[i]=gui.get_s("Please rename "+player_names[i]+" "+(i+1));}
		}
		// option: default or custom settings file
		int mode = gui.yes_no_prompt("Do you want to use the default settings file?");
		boolean ok = false; while(!ok){
			// get file
			String file = "";
			if(mode==0){ file = gui.get_settings_file(); }
			// init DB
			String errors = db.init(mode,file);
			if(errors.equals("")){break;}
			gui.message(errors);
		}
		current_spin_sectors = concatenate(spin_sectors_header, db.get_categories(round));
		return;
	}

//	int result = 0;
	private int spin(){
		int result = (int) (Math.random()*current_spin_sectors.length) % current_spin_sectors.length; 
		is_AI[currentPlayer].spin(current_spin_sectors,result);
		is_AI[currentPlayer].message(current_spin_sectors[result]);
		spin_counter--;
//		result = (result+1)% current_spin_sectors.length;
		return result;
	}

	private void category(int cat){
		// show board
		is_AI[currentPlayer].show_board(round, cat, db.get_categories(round), questions);
		// check questions left
		if( questions[cat] > 4){is_AI[currentPlayer].message("There are no more questions in this category. Please spin again."); return;}
		// check daily
		boolean daily_double = false;
		if(round == 0 && cat==daily[0][0] && questions[cat]==daily[0][1]){
			daily_double = true;
		} else if( round == 1 && ((cat==daily[1][0] && questions[cat]==daily[1][1]) || (cat==daily[2][0] && questions[cat]==daily[2][1]))){
			daily_double = true;
		}
		int daily_n = 0;
		// get daily wager
		if(daily_double){
			if(points[round][currentPlayer] == 0){
				int pointsleft = 0;
				for(int i=0; i<questions.length; i++){
				for(int j=questions[i]; j<5; j++){
					pointsleft += 100*(round+1)*(questions[cat]+1);
				}}
				daily_n = is_AI[currentPlayer].get_n("Daily double! Please enter your wager (1-"+(pointsleft)+"):", 0, pointsleft); 
			} else {
				daily_n = is_AI[currentPlayer].get_n("Daily double! Please enter your wager (1-"+(points[round][currentPlayer])+"):", 0, points[round][currentPlayer]); 
			}
		}
		// ask question
		is_AI[currentPlayer].ask_question(timer, db.get_question(round, cat, questions[cat]));
		// check answer
		int response = is_AI[currentPlayer].check_answer(db.get_answer(round, cat, questions[cat]));
		if(response == 1){
			// correct answer
			message = player_names[currentPlayer]+" ("+(currentPlayer+1)+") answered correctly. Go again!";
			if(daily_double){points[round][currentPlayer]+=daily_n;}
			else{ points[round][currentPlayer]+=100*(round+1)*(questions[cat]+1); }
		} else if(response == 0){
			// incorrect
			message=player_names[currentPlayer]+" ("+(currentPlayer+1)+") answered incorrectly. "+player_names[currentPlayer]+" ("+(currentPlayer+1)+") turn is over.";
			if(daily_double){points[round][currentPlayer]-=daily_n;}
			else{ points[round][currentPlayer]-=100*(round+1)*(questions[cat]+1); }
			nextplayer();
		} else {
			// timeout
			message = player_names[currentPlayer]+" ("+(currentPlayer+1)+") timed out. "+player_names[currentPlayer]+" ("+(currentPlayer+1)+") turn is over.";
			nextplayer();
		}
		questions[cat]++;
	}

	private void lose(){
		if(free[currentPlayer] == 0){ is_AI[currentPlayer].message("No token. Lose a turn"); nextplayer(); return; } 
		if(is_AI[currentPlayer].yes_no_prompt("Do you want to use a token?") == 0){
			is_AI[currentPlayer].message("You chose not to use a token. Lose a turn"); nextplayer(); return;
		}
		is_AI[currentPlayer].message("Spin again");
		return;
	}

	private void free(){ 
		is_AI[currentPlayer].message("Free token. Spin again."); 
		free[currentPlayer]++; 
	}

	private void bankrupt(){ 
		points[round][currentPlayer] = 0; 
		nextplayer(); 
	}

	private void player_choice(){
		int n = 0;
		boolean invalid = true;
		while(invalid){
			n=is_AI[currentPlayer].choose_category(player_names[currentPlayer]+" ("+(currentPlayer+1)+") Choose category: ", db.get_categories(round), questions);
			if(1 <= n && n <= 6){invalid = false;}
			else{continue;}
			if(questions[n-1]>4){invalid = true;}
			if(invalid){is_AI[currentPlayer].message("Please choose a different category");}
		}
		category(n-1);
	}

	private void opponent_choice(){
		int n = 0;
		n=is_AI[((currentPlayer+1)%numberPlayers)].choose_category((player_names[((currentPlayer+1)%numberPlayers)]+" ("+((currentPlayer+1)%numberPlayers+1)+") Choose category for "+player_names[currentPlayer]+" ("+(currentPlayer+1)+"): "), db.get_categories(round), questions);
		category(n-1);
	}

	private void spin_again(){}

	private void update(){ 
		// check end round
		boolean endround = false;
		if(spin_counter==0){endround=true;}
		boolean allanswered = true;
		for(int i=0; i<questions.length; i++){
			allanswered = allanswered && (questions[i]>4);
		}
		if(allanswered){endround=true;}
		if(endround && round == 0){
			// new round, reset variables
			spin_counter = spins_per_round;
			round++; 
			for(int i=0; i<questions.length; i++){questions[i]=0;};
			current_spin_sectors = concatenate(spin_sectors_header, db.get_categories(round));
			is_AI[currentPlayer].message("\n\tNEW ROUND!\n");
		} else if(endround && round == 1){
			endGame=true;
		}
	}

	private void end(){
		// get max points
		int sum = 0;
		for(int i=0;i<points[0].length; i++){
			if(sum < (points[0][i]+points[1][i])){sum = (points[0][i]+points[1][i]);}
		}
		// get list of players that won
		ArrayList<Integer> maxplayers = new ArrayList<Integer>();
		for(int i=0;i<points[0].length; i++){
			if(sum == (points[0][i]+points[1][i])){maxplayers.add(i);}
		}
		// make winning message
		String m = "";
		if(maxplayers.size()==1){
			m=player_names[maxplayers.get(0)]+" ("+(maxplayers.get(0)+1)+") won with "+sum+" points!";
		} else{
			m="Players ";
			for(int i=0; i<(maxplayers.size()-1);i++){m+= player_names[maxplayers.get(i)]+" ("+(maxplayers.get(i)+1)+"), ";}
			m+= "and "+player_names[maxplayers.get(maxplayers.size()-1)]+" ("+(maxplayers.get(maxplayers.size()-1)+1)+") won with "+sum+"points!";
		}
		is_AI[currentPlayer].show_info(player_names, round, currentPlayer, m, points, free, spin_counter);
	}

	/////////////////////////////////////////////

	private void nextplayer(){ currentPlayer=(currentPlayer+1)%numberPlayers; }

	private String[] concatenate (String[] a, String[] b) {
		int aLen = a.length;
		int bLen = b.length;

		String[] c = new String[aLen+bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}
}
