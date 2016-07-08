import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.*;
import static javax.swing.GroupLayout.Alignment.*;
import java.util.concurrent.locks.*;
import java.awt.Dimension;
import java.util.Random;

//		try { Thread.sleep(1000); } catch (Exception e) {}
public class GUI extends GUI_abstract {
	GUI_worker g = new GUI_worker();

	String m;
	int round;
	int currentPlayer;
	int[][] points;
	int[] free;
	int spin_counter;
	public int get_n(String message){
		m = message;
		int int_return=0;

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { g.get_n(m); }
		});

		g.lock.lock(); try{
			g.returned = false;
			while(!g.returned){g.interface_wait.await();}
			int_return = g.int_share;
		} catch(InterruptedException e){
		} finally{g.lock.unlock();}

		return int_return;
	}

	public int yes_no_prompt(String message){
		m = message;
		int int_return=0;

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { g.yes_no_prompt(m); }
		});

		g.lock.lock(); try{
			g.returned = false;
			while(!g.returned){g.interface_wait.await();}
			int_return = g.int_share;
		} catch(InterruptedException e){
		} finally{g.lock.unlock();}

		return int_return;
	}

	public String get_settings_file(){ 
		String string_return="";

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { g.get_settings_file(); }
		});

		g.lock.lock(); try{
			g.returned = false;
			while(!g.returned){g.interface_wait.await();}
			string_return = g.string_share;
		} catch(InterruptedException e){
		} finally{g.lock.unlock();}

		return string_return;
	}

	public void message(String message){
		m = message;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { g.message(m); }
		});

		g.lock.lock(); try{
			g.returned = false;
			while(!g.returned){g.interface_wait.await();}
		} catch(InterruptedException e){
		} finally{g.lock.unlock();}
	}

	public void show_info(int r, int c, String message, int[][] p, int[] f, int s){
		round = r; currentPlayer = c; m = message; points = p; free = f; spin_counter = s;
		m = message;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { g.show_info(round,currentPlayer,m,points,free,spin_counter); }
		});

		g.lock.lock(); try{
			g.returned = false;
			while(!g.returned){g.interface_wait.await();}
		} catch(InterruptedException e){
		} finally{g.lock.unlock();}
	}

	private String[] choices; private int outcome;
	public void spin(String[] c, int o){
		choices=c; outcome=o;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { g.spin(choices,outcome); }
		});
		g.lock.lock(); try{
			g.returned = false;
			while(!g.returned){g.interface_wait.await();}
		} catch(InterruptedException e){
		} finally{g.lock.unlock();}
		return;
	}

	public void show_board(int round, int category, String[] categories, int[] questions){}
	public void ask_question(int timer, String question){}
	public int check_answer(String answer){return 0;}
	public int choose_category(String message, String [] categories){return 0;}
}

class GUI_worker extends JFrame {
	public ReentrantLock lock = new ReentrantLock();
	public Condition interface_wait = lock.newCondition();
	public boolean returned = false;
	public int int_share;
	public String string_share;

	private JFrame frame;
	private Container pane;
	private int width=16*60;
	private int height=9*60;

	private JLabel message_label = new JLabel("<html><h1>Number of Players: </h1></html>");
	private JLabel message_label2 = new JLabel("<html><h1> </h1></html>");
	private JTextField get_n_text_filed = new JTextField("1");
	private JButton continue_button = new JButton("Continue");

	private JButton y_button = new JButton("Yes");
	private JButton n_button = new JButton("No");

	private JButton file_chooser = new JButton("Open file");

	private JTable table;
	private JScrollPane scrollPane;

	public GUI_worker(){
		javax.swing.SwingUtilities.invokeLater(new Runnable() { public void run() { init(); } });
	}

	private void init(){
		frame = this;
		pane = this.getContentPane();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Triviopoly");

		//Display the window.
		frame.setSize(width, height);
		frame.setVisible(true);

		continue_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				lock.lock(); try{
					int_share = Integer.parseInt(get_n_text_filed.getText());
					returned = true;
					interface_wait.signal();
				} catch(NumberFormatException n){
				} finally{lock.unlock();}
			}
		});

		y_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				lock.lock();
				int_share = 1;
				returned = true;
				interface_wait.signal();
				lock.unlock();
			}
		});

		n_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				lock.lock();
				int_share = 0;
				returned = true;
				interface_wait.signal();
				lock.unlock();
			}
		});

		file_chooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser fileChooser = new JFileChooser();
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					lock.lock();
					File f = fileChooser.getSelectedFile();
					string_share = f.getAbsolutePath();
					returned = true;
					interface_wait.signal();
					lock.unlock();
				}
			}
		});

		return;
	}

	public void get_n(String message){
		message = message.replace("\n", "<p>");
		message_label.setText("<html><h1>"+message+"</h1></html>");
		frame.setContentPane(pane);
		pane.removeAll();

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(message_label, c);
		c.gridx = 0;
		c.gridy = 1;
		pane.add(get_n_text_filed, c);
		c.gridx = 0;
		c.gridy = 2;
		pane.add(continue_button, c);

		pane.repaint();
		return;
	}

	public void yes_no_prompt(String message){
		message = message.replace("\n", "<p>");
		message_label.setText("<html><h1>"+message+"</h1></html>");
		frame.setContentPane(pane);
		pane.removeAll();

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		pane.add(message_label, c);
		c.gridx = 0;
		c.gridy = 1;
		pane.add(y_button, c);
		c.gridx = 2;
		c.gridy = 1;
		pane.add(n_button, c);

		pane.repaint();
		return;
	}

	public void get_settings_file(){
		message_label.setText("<html><h1>Choose file</h1></html>");
		frame.setContentPane(pane);
		pane.removeAll();

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(message_label, c);
		c.gridx = 0;
		c.gridy = 1;
		pane.add(file_chooser, c);

		pane.repaint();
		return;
	}
	public void message(String message){
		message = message.replace("\n", "<p>");
		message_label.setText("<html><h1>"+message+"</h1></html>");
		frame.setContentPane(pane);
		pane.removeAll();

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(message_label, c);
		c.gridx = 0;
		c.gridy = 1;
		pane.add(continue_button, c);

		pane.repaint();
		return;
	}
	public void show_info(int round, int currentPlayer, String message, int[][] points, int[] free, int spin_counter){
		message = message.replace("\n", "<p>");
		message_label.setText("<html><h1>"+message+"</h1></html>");

		String m2 = "Round: "+(round+1)+"  Spins left: "+spin_counter+"  Player "+(currentPlayer+1)+"'s turn";
		message_label2.setText("<html>"+m2+"</html>");

		String[] columnNames = {"Player ","#", "Round 1 Points", "Round 2 Points", "Free tokens"};
		Object[][] data = new Object[(points[0].length)][columnNames.length];
		for(int i=0; i<points[0].length; i++){
			data[i][0] = "Player";
			data[i][1] = i;
			data[i][2] = points[0][i];
			data[i][3] = points[1][i];
			data[i][4] = free[i];
		}
		table = new JTable(data, columnNames);
		scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		frame.setContentPane(pane);
		pane.removeAll();

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(message_label, c);
		c.gridx = 0;
		c.gridy = 1;
		pane.add(message_label2, c);
		c.gridx = 0;
		c.gridy = 2;
		pane.add(scrollPane, c);
		c.gridx = 0;
		c.gridy = 3;
		pane.add(continue_button, c);

		pane.repaint();
	}

	private SpinPanel s_panel = new SpinPanel();
	public void spin(String[] choices, int outcome){
		pane.removeAll();
		frame.setContentPane(s_panel);
		s_panel.revalidate();

		Color[] colors = new Color[choices.length];
		Random gen = new Random(30);
		for(int i=0; i<colors.length; i++){ colors[i] = new Color(gen.nextInt(255), gen.nextInt(255), gen.nextInt(255)); }
		s_panel.init(choices, colors, outcome, frame.getWidth(), frame.getHeight());

		Thread animation = new Thread(s_panel);
		animation.start();

		return;
	}
	class SpinPanel extends JComponent implements Runnable {
		private Image imageOffScreen;
		private Graphics graphicsOffScreen;

		Font font = new Font("TimesRoman", Font.BOLD, 40);
		int height; int width;
		String[] choices;
		Color[] colors;
		int outcome; int loc=0; int pos=0; int div=50;

		public void init(String[] ch, Color[] co, int o, int w, int h){ 
			height = h; width = w;
			imageOffScreen = createImage(width, height);
			graphicsOffScreen = imageOffScreen.getGraphics();
			choices = ch; colors = co; outcome = o;
			loc = outcome-1; if(loc < 0){loc = loc + ch.length;}
		}
		public void run(){
			for(int i=0; i<51; i++){
				pos = i;
				repaint();
				try { Thread.sleep( 20 ); } catch (InterruptedException e) { }
			}
			lock.lock(); try{
				returned = true;
				interface_wait.signal();
			} finally{lock.unlock();}
		}

		public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
			FontMetrics metrics = g.getFontMetrics(font);
			int x = rect.x + rect.width/2 - metrics.stringWidth(text)/2;
			int y = rect.y + rect.height/2 + metrics.getHeight()/4;
			g.setFont(font);
			g.drawString(text, x, y);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			graphicsOffScreen.setColor(colors[loc]);
			graphicsOffScreen.fillRect(0, height*pos/div-height, width,height);
			graphicsOffScreen.setColor(colors[(loc+1)%choices.length]);
			graphicsOffScreen.fillRect(0, height*pos/div, width, height);
			graphicsOffScreen.setColor(new Color(0,0,0));

			drawCenteredString(graphicsOffScreen,choices[loc],new Rectangle(0, height*pos/div-height, width, height), font);
			drawCenteredString(graphicsOffScreen,choices[(loc+1)%choices.length],new Rectangle(0, height*pos/div, width, height), font);
			graphicsOffScreen.setColor(new Color(255,255,255));
			graphicsOffScreen.fillRect(width/4, 0, width/2, height/10);
			graphicsOffScreen.setColor(new Color(0,0,0));

			drawCenteredString(graphicsOffScreen,"Spin:",new Rectangle(width/4, 0, width/2, height/10), font);

			g.drawImage( imageOffScreen, 0, 0, this);
		}
	}
}


