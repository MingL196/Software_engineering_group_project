/* 
	for testing purposes some features were hardcoded. For example... anytime a question is answered, the system will always 
	detect the question was answered correctly. To turn on all features of the code see following instructions.
	
	line 228: turn off auto answer. allow user to input answer
	line 239: turn off auto answer was correctly answer. allow user to input whether answer was correct.

*/



import java.io.*;
import java.util.*;

class GameCode 
{
	//GUI_abstract gui;

	private String[] categoryNames;  // names of categories
	private String[][][][] categories; //array storing the questions. two dimension array for two rounds. stored as [category number, question number, round #, answer/question]
	private int[][] questions;  // indicator saying which questions have been answered. two dimension array for two rounds. stored as [category, round]

	public static Scanner input = new Scanner(System.in);
	public boolean endGame;
	private boolean endTurn; // detects if player gets to spin again.
	public  int spin_counter;
	private int numberPlayers;
	private int currentPlayer;  // stored 1 through n.
	private int round;  //indicates current round. stored 0-1.
	private int[][] points; //indicates points of each player.  stored. 0-numberPlayers-1, 0-1
	private int[] free; // Question: Does free tokens reset each round? stored 0-numPlayers-1.
	
	
	public void GameCode()
	{
		//gui = new GUI_text_version();
		//gui = new GUI_interface();
		
	}// end GameCode

	
	
	public void start()
	{
		init(); // initialize the game
		
		while(!endGame) //plays the game until the game is over
		{
			
			endTurn = false; //detects if the player's turn is over
			while(!endTurn) //allows player to keep playing until turn ends.
			{
				spin_counter--;
				System.out.println("							"+"turn, "+ currentPlayer + " " +round + " "+ spin_counter);
				turn();
				boolean a = check_round_finished();
				if( !a && endTurn) // if round did not end allow player to use free.
				{	
					use_free();
				}
			}// end while
			
			check_game_over();  // checks if game is over
			end_turn(); //increments the player
			
		}// end while
		
		end();
		
	}// end start

	
	
	private void init() //done
	{
		//request num players from console (used for testing purposes)
		numberPlayers = 0;
		int num = 0;
		while(num <= 0)
		{
			System.out.print("how many players: ");
			num = input.nextInt();
		}
		numberPlayers = num;
		System.out.println("The numer of players is: " + numberPlayers);
		
		
		// initialize class variables
		points = new int[numberPlayers][2]; //use two dimension array because points reset per round.
		questions = new int[6][2];
		int i;
		for(i = 0; i<6;i++)
		{
			questions[i][0] = 0;
			questions[i][1] = 0;
		}
		free = new int[numberPlayers];
		currentPlayer = 1;
		round = 0;
		spin_counter = 50;
		endGame = false;
		endTurn = false;
		load_questions();
		
		
		
		//while(numberPlayers <=0){ numberPlayers = gui.get_n_players(); }
		// ...
		
		
	}// end init()
	
	
	private void load_questions() //done
	{
		categories = new String[6][5][2][2];
		categoryNames = new String[6];
		String temp;
		try
		{
			int i,j, k;
			Scanner in = new Scanner(new FileReader("questions.txt"));
			
			
			for(i = 0; i<6; i++)
			{
				temp = in.nextLine();
				categoryNames[i] = temp;
				
				for(k = 0; k<2; k++) // loads 2 rounds
				{
					for(j = 0; j<5; j++)
					{
						in.useDelimiter("	");
						temp = in.next();
						categories[i][j][k][0] = temp; // question
						
						in.skip("	");
						in.useDelimiter("\n");
						temp = in.next();
						categories[i][j][k][1] = temp; // answer
						in.skip("\n");
						
					
					}//end for(j)
					if( !( (i ==5) && (k ==1)) )
					{
						temp = in.next();
						in.skip("\n");
					}// end if
				}//end for (k)
				
			}// end for (i)
			
			
		}// end try
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		
	}// end load_questions
	
	// take a turn in the game.
	private void turn() 
	{
		int outcome = spin();
		switch(outcome)
		{
				
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
				
		}// end switch
		
	}// end turn() 
	
	
	
	private void end_turn() // done 
	{
		
		// System.out.println("Points for player " + currentPlayer + " :" + points[currentPlayer-1][round]);
		
		currentPlayer++;
		if(currentPlayer > numberPlayers)
		{
			currentPlayer = 1;
		}
		
	}// end endTurn
	
	
	private int spin() //done
	{ 
		int result;
		result = (int) (Math.random()*12);
		return result; 
	}// end spin

	
	private void category(int a)
	{
		
		//displays the category selected
		System.out.println("Admin: You have selected category: " + categoryNames[a]);
		
		//checks if questions remain.
		if( questions[a][round] >4  )
		{
			System.out.println("There are no more questions in this category. Please spin again.");
			return;
		}
		
		//prints the question/prompts answer
		System.out.println("The question is: " + categories[a][ (questions[a][round]) ][ round ][0]);
		System.out.print("Your answer: ");
			/* auto answer instead. otherwise prompt user for response.
			String k = input.next();
			*/
		System.out.println();
		System.out.println("Player: Answer!");
		
		//displays the answer for the question/asks if the question was answered correctly
		System.out.println("Admin: The answer was: " + categories[a][ (questions[a][round]) ][ round ][1]);
		System.out.print("Did you answer correctly?(y/n): ");
		//char y = input.next().charAt(0); 
		System.out.println();
		char y = 'y';
		
		
		//awards points or ends turn based on correct answer
		if( ( y =='y')|| (y == 'Y')  )
		{
			//award points
			points[currentPlayer -1][round] = points[currentPlayer -1][round] +(   (questions[a][round]+1)  *100 *(round+1)  ) ;
			System.out.println("Admin: you answered correctly. Go again!");
			
		}
		else
		{
			System.out.println("You answere incorrectly. You're turn is over.");
			endTurn = true;
		}
		
		
		// increment the next question.
		questions[a][round]++; 
		
		
	}// end category()
	
	
	private void lose()  //done
	{	
		endTurn = true;
		System.out.println("Lose Turn");
	}//end endTurn
	
	
	private void free()  // done
	{
		free[currentPlayer-1]++;
		System.out.println("Free token. Spin again.");
		
	}// end free.
	
	
	private void bankrupt()  // done
	{
		points[currentPlayer -1][round] = 0;
		System.out.println("Bankrupt. lose turn");
		endTurn = true;
		
	}//end bankrupt
	
	
	private void player_choice() //done
	{
		int k = 0;
		while(   !(  k<7 && k>0     )    )
		{
			System.out.print("Admin: Player's Choice! Please pick a category: " );
			k = input.nextInt();
		}

		category(k-1);
		
	}// end player_choice() 
	
	private void opponent_choice() //done
	{
		int k = 0;
		int opponent = currentPlayer +1;
		if(opponent > numberPlayers)
		{
			opponent = 1;
		}
		while(   !(  k<7 && k>0     )    )
		{
			System.out.println("Admin: Opponent's Choice! Player " + opponent + ". Please pick a category for your opponent."   );
			System.out.print("Admin: Enter selection: ");
			k = input.nextInt();
		}
		category(k-1);
	}//end opponent_choice()
	
	private void spin_again() //done
	{
		System.out.println("Admin: Spin again!");
	}// end spin_again()
	

	private void use_free() //done
	{
		
		//only activates if player has enough free tokens
		if( free[currentPlayer -1] >0  )
		{
				System.out.println("Admin: You have free tokens; Do you want to use a free token: ");
				//char k = input.next().charAt(0);
				
				char k = 'y';
				System.out.println("Player: yes!");
		
				if((k == 'y') || (k == 'Y'))
				{
					System.out.println("Admin: you used a free token.");
					free[currentPlayer -1]--;
					endTurn = false;
				}

		}// end if
		
	}//end use_free
	
	
	private boolean check_round_finished() //done
	{ 
		boolean over = false;
		
		//detects is spin counter is exausted
		if(spin_counter <= 0)
		{
			over = true;
			System.out.println("spin exausted");
		}
		
		//detects if questions have been answered
		int i = 0;
		boolean indicator = true;
		for(i = 0; i< 6; i++)
		{
			if(  questions[i][round] < 5 )
			{
				indicator = false;
			}
		}
		if(indicator)
		{
			System.out.println("questions exausted");
			over = true;
		}
		
		
		
		//changes the round and resets spin counter if previous round ended;
		if(over == true)
		{
			 //increments the rounds
			round++;
			System.out.println(round);
			if(round <2)
			{
				int sum = 0;
				System.out.println("Start next round");
				
				//displays the scores for round 1.
				System.out.println("Scores for round 1:");
				for(i = 0; i<numberPlayers; i++)
				{
					sum = points[i][0];
					System.out.println("Points for player "+ (i+1) +" : " + sum);
				}
				
				
			}
			
			
			//resets spin counter
			spin_counter = 50;
			
			
		}// end if
		
		if(over == true)
		{
			endTurn = true;
		}
		return over;
		
	}// end check_round_finished
	

	private void check_game_over()  //done
	{
		if(round > 1 )
		{
			System.out.println("game over");
			endGame = true;
			
		}
	}// end check_game_over

	
	private void end() //done
	{ 
		//prints scores for each player at end of game
		int i, sum;
		System.out.println();
		System.out.println("Score for each player:");
		for(i = 0; i<numberPlayers; i++)
		{
			sum = points[i][0]+points[i][1];
			System.out.println("Points for player "+ (i+1) +" : " + sum);
		}
	
	}// end end()  
	
	
}// end class
