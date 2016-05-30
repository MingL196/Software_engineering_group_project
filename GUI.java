import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.*;
import static javax.swing.GroupLayout.Alignment.*;
import java.util.concurrent.locks.*;

//		try { Thread.sleep(1000); } catch (Exception e) {}
public class GUI extends GUI_abstract {
	GUI_worker g = new GUI_worker();

	public int get_n_players(){
		int ret=0;

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { g.get_n_players(); }
		});

		g.lock.lock(); try{
			g.ret = 0;
			while(g.ret ==0 ){g.returned.await();}
			ret = g.int_ret;
		} catch(InterruptedException e){
		} finally{g.lock.unlock();}

		return ret;
	}

	public String get_settings_file(){return "";}

	public void spin(String[] choices, int outcome){return;}

	public int[] pick_question(boolean[][] available_choices){
		int[] choice = new int[2];
		return choice;
	}

	public boolean reveal_questions(String input_text){return true;}

	public boolean use_token_prompt(){return true;}

	public void show_stats(int[] player_scores, int[] player_tokens, int rounds_counter){}
}

class GUI_worker extends JFrame {
	public ReentrantLock lock = new ReentrantLock();
	public Condition returned = lock.newCondition();
	public int ret = 0;

	public int int_ret;

	private JFrame frame;
	private Container pane;
	private int width=700;
	private int height=300;

	private JLabel n_players_label = new JLabel("<html><h1>Enter number of Players: </h1></html>");
	private JTextField n_text_field = new JTextField("0");
	private JButton continue_button = new JButton("Continue");

	public GUI_worker(){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { init(); }
		});
	}

	private void init(){
		frame = this;
		pane = this.getContentPane();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Triviopoly");

		//Display the window.
		frame.setSize(width, height);
		frame.setVisible(true);
		return;
	}

	public void get_n_players(){
		pane.removeAll();
		continue_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				lock.lock(); try{
					ret = 1;
					int_ret = Integer.parseInt(n_text_field.getText());
					returned.signal();
				} catch(NumberFormatException n){
				} finally{lock.unlock();}
			}
		});

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(n_players_label, c);
		c.gridx = 0;
		c.gridy = 1;
		pane.add(n_text_field, c);
		c.gridx = 0;
		c.gridy = 2;
		pane.add(continue_button, c);

		pane.repaint();
		return;
	}

}



