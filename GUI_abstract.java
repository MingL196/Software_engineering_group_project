
public abstract class GUI_abstract{
	public abstract int get_n(String message);
	public abstract int yes_no_prompt(String message);
	public abstract String get_settings_file();
	public abstract void message(String message);
	public abstract void show_info(int round, int currentPlayer, String message, int[][] points, int[] free, int spin_counter);
	public abstract void spin(String[] choices, int outcome);
	public abstract void show_board(int round, int category, String[] categories, int[] questions);
	public abstract void ask_question(int timer, String question);
	public abstract int check_answer(String answer); // return 0 = incorrect, 1 = correct, 2 = timer ran out
	public abstract int choose_category(String message, String [] categories);
}
