import java.util.Scanner;
import java.util.*;
import java.io.*;
import java.lang.Thread;
import java.util.concurrent.locks.*;
import java.util.concurrent.TimeUnit;

public class UI extends GUI_abstract {
	public static Scanner input = new Scanner(System.in);

	public int get_n(String message){
		int k = 0;
		boolean correctinput = false; while(!correctinput){
			try {
			System.out.println("\n"+message);
			k = input.nextInt(); input.nextLine();
			correctinput = true;
			} catch (Exception e) { correctinput = false; input.next();}
		}
		return k;
	}

	public int yes_no_prompt(String message){
		System.out.println("\n"+message+" (y/n)");
		char k = ' ';
		boolean correctinput = false;
		while(!correctinput){
			k = input.next().charAt(0); input.nextLine();
			correctinput = true;
			if(k!='y' && k!='n'){correctinput=false; System.out.println("Please enter y/n");}
		}
		if(k=='y'){return 1;}
		return 0;
	}

	public String get_settings_file(){
		System.out.println("\nPlease specify the settings file:");
		String k = input.nextLine();
		return k;
	}

	public void message(String message){
		System.out.println("\n"+message);
		System.out.println("Press Enter to continue.");
		String k = input.nextLine();
		return;
	}

	public void show_info(int round, int currentPlayer, String message, int[][] points, int[] free, int spin_counter){
		System.out.println("\n==============================================");
		System.out.println(message);
		System.out.println("Round: "+(round+1)+"  Spins left: "+spin_counter+"  Player "+(currentPlayer+1)+"'s turn");
		System.out.printf("%10s %5s: %15s %15s %15s\n", "Player ","#", "Round 1 Points", "Round 2 Points", "Free tokens");
		for(int i=0; i<points[0].length; i++){
			System.out.printf("%10s %5d: %15d %15d %15d\n", "Player ",(i+1), points[0][i], points[1][i], free[i]);
		}
	}

	public void spin(String[] choices, int outcome){
		System.out.println("\nSpinning...");
		System.out.println("Landed on: "+choices[outcome]);
	}
	
	public void show_board(int round, int category, String[] categories, int[] questions){
		String[] p = new String[6];
		for(int i=0; i<p.length; i++){p[i]="";}
		p[category] = "vvvvvvvvvvvvvvv";
		System.out.printf("%15s %15s %15s %15s %15s %15s\n",p[0],p[1],p[2],p[3],p[4],p[5]);
		System.out.printf("%15s %15s %15s %15s %15s %15s\n",categories[0],categories[1],categories[2],categories[3],categories[4],categories[5]);
		for(int i=0; i<5; i++){
			for(int j=0; j<6; j++){
				if(questions[j]<=i){p[j]=((i+1)*(round+1)*100)+"";}
				else{p[j]="";}
			}
			System.out.printf("%15s %15s %15s %15s %15s %15s\n",p[0],p[1],p[2],p[3],p[4],p[5]);
		}
	}

	public void ask_question(int timer, String question){
		Timer t = new Timer(timer, question);
		t.start();
		input.nextLine();
		t.end();
	}

	public int check_answer(String answer){
		System.out.println("\nThe answer was: ");
		System.out.println(answer);
		System.out.println("Did the Player answer correctly? (Enter 0 for an incorrect answer, 1 for a correct answer, or 2 for timeout)");
		int k = 100;
		while(!(k==0 || k==1 || k==2)){
			try{k = input.nextInt(); input.nextLine();} 
			catch (Exception e){input.next();}
		}
		return k;
	}

	public int choose_category(String message, String [] p){
		int k = 0;
		boolean correctinput = false; while(!correctinput){
			try {
			System.out.println("\n"+message);
			System.out.printf("%15s %15s %15s %15s %15s %15s\n","1","2","3","4","5","6");
			System.out.printf("%15s %15s %15s %15s %15s %15s\n",p[0],p[1],p[2],p[3],p[4],p[5]);
		
			k = input.nextInt(); input.nextLine();
			correctinput = true;
			} catch (Exception e) { correctinput = false; input.next();}
		}
		return k;

	}
}

class Timer extends Thread {
	private String question = "";
	private int timer = 10;

	private Lock lock = new ReentrantLock();
	private Condition checkdone = lock.newCondition();
	private boolean done = false;

	public Timer(int i, String q){timer = i; question = q;}
	public void run() {
		System.out.println("\n"+question+"\tTime left: "+timer);
		for(timer=timer; timer>0; timer--){
			lock.lock();
			try{ 
			checkdone.await(1L, TimeUnit.SECONDS); 
			if(done){break;}
			System.out.print(String.format("\033[%dA",1)); System.out.print("\033[2K"); 
			System.out.println(question+"\tTime left: "+(timer-1));
			} catch (InterruptedException e){}
			lock.unlock();
		}
		if(!done){System.out.println("Timeout");}
	}
	public void end(){
		lock.lock();
		done = true;
		checkdone.signal();
		lock.unlock();
	}
}
