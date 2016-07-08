
public abstract class DB_abstract {

	// init will return a non-empty string if the initialization fails.
	public abstract String init(int mode, String file);

	public abstract String[] get_categories(int round);

	public abstract String get_category(int round, int column);

	public abstract String get_question(int round, int column, int row);

	public abstract String get_answer  (int round, int column, int row);

}
