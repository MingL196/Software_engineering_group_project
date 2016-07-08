import java.util.*;
import java.io.*;

public class DB_simple extends DB_abstract {
	String[][]   categories = new String[2][6]; // [round][category]
	String[][][] questions  = new String[2][6][5];  // [round][category][row]
	String[][][] answers    = new String[2][6][5];    // [round][category][row]

	public String init(int mode, String file){
		BufferedReader buffer = null; 
		// get stream
		if(mode == 1){
			String defaultfile = "/default_settings.txt";
			InputStream stream = DB_simple.class.getResourceAsStream(defaultfile);
			buffer =new BufferedReader(new InputStreamReader(stream));
		} else {
			try{ buffer = new BufferedReader(new FileReader(file));}
			catch (FileNotFoundException e){return "File not found";}
		}
		// parse stream
		String line;
		int count = 0;
		boolean toomany = false;
		try{ while((line = buffer.readLine()) != null){
			if(count >= 12*6){toomany = true; break;}
			line = line.trim();
			if(line.equals("")){continue;}
			if(count % 6 == 0){
				categories[count/36][(count/6)%6] = line;
			} else{
				String[] parts = line.split("\t");
				questions[count/36][(count/6)%6][(count%6)-1] = parts[0];
				if(parts.length > 1){
					answers[count/36][(count/6)%6][(count%6)-1] = parts[1];
				} else {
					answers[count/36][(count/6)%6][(count%6)-1] = "";
				}
			}
			count++; 
		}
		if(toomany || count != 12*6){return "The Settings file was not fomatted correctly.\n"
			+ "The file should have 12 groups of 6 non-empty lines.\n\n"
			+ "The first 6 groups specify the categories, questions and answers for round 1\n"
			+ "The second 6 groups specify the categories, questions and answers for round 2\n\n"
			+ "The first line of each group specifies the category name, and \n"
			+ "each of the other 5 lines consists of a question, a tab, and the corresponding answer.\n"
		;}
		buffer.close();
		} catch (IOException e){return "Unable to read file";}
		return "";
	}

	public String[] get_categories(int round){                    return categories[round]; }
	public String get_category(int round, int category){          return categories[round][category]; }
	public String get_question(int round, int category, int row){ return questions [round][category][row]; }
	public String get_answer  (int round, int category, int row){ return answers   [round][category][row]; }
}

