import java.util.Scanner;
import java.util.*;
import java.io.*;

public class GUI_text_version extends GUI_abstract {
	public static Scanner input = new Scanner(System.in);

	public int get_n_players(){
		System.out.println("Please enter the number of players:");
		int k = input.nextInt();
		return k;
	}

	public String get_settings_file(){
		System.out.println("Please enter the settings file:");
		return "";
	}

	public void spin(String[] choices, int outcome){
		return;
	}

}

