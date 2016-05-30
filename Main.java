import java.io.*;

public class Main {
	public static void main (String[] args) throws IOException {
		// init GUI
		GUI g = new GUI();

		// get number of players
		int n_players = g.get_n_players();
		System.out.println(n_players);
		
		// TO DO: check non-negative number of players

		String settings_file = g.get_settings_file();

		int[] player_scores = new int[n_players];
		int[] player_tokens = new int[n_players];
		int rounds_counter = 50;
	}
}


