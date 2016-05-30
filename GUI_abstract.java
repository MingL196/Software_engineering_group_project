
public abstract class GUI_abstract{
        public abstract int get_n_players();

        public abstract String get_settings_file();

        public abstract void spin(String[] choices, int outcome);

        public abstract int[] pick_question(boolean[][] available_choices);

        public abstract boolean reveal_questions(String input_text);

        public abstract boolean use_token_prompt();

        public abstract void show_stats(int[] player_scores, int[] player_tokens, int rounds_counter);
}
