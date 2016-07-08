import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.*;
import static javax.swing.GroupLayout.Alignment.*;
import java.util.concurrent.locks.*;
import java.awt.Dimension;
import java.util.Random;

public class GUI extends GUI_abstract {
	GUI_worker g = new GUI_worker();

	String m;
	int round;
	int currentPlayer;
	int[][] points;
	int[] free;
	int spin_counter;
	int category;
	String[] categories;
	int[] questions;
	int timer;

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

	public int check_answer(String message){
		m = message;
		int int_return=0;

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { g.check_answer(m); }
		});

		g.lock.lock(); try{
			g.returned = false;
			while(!g.returned){g.interface_wait.await();}
			int_return = g.int_share;
		} catch(InterruptedException e){
		} finally{g.lock.unlock();}

		return int_return;
	}
	public int choose_category(String message, String [] c){
		categories = c;
		m = message;
		int int_return=0;

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { g.choose_category(m,categories); }
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
			g.s_panel.stop();
		} catch(InterruptedException e){
		} finally{g.lock.unlock();}
		
		return;
	}

	public void show_board(int r, int c, String[] ca, int[] q){
		round = r; category = c; categories = ca; questions = q;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { g.show_board(round, category, categories, questions); }
		});
		g.lock.lock(); try{
			g.returned = false;
			while(!g.returned){g.interface_wait.await();}
		} catch(InterruptedException e){
		} finally{g.lock.unlock();}
		g.s_panel.stop();
		return;
	}
	public boolean timer_returned = false;
	public void ask_question(int t, String question){
		m = question; timer = t;

		g.lock.lock(); try{
			g.returned = false;
			while(!g.returned){
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() { g.ask_question(timer,m); }
				});
				timer_returned = false;
				Thread sec_timer = new Thread(new GUI_Timer(this));
				while(!(timer_returned || g.returned)){
					sec_timer.start();
					g.interface_wait.await();
				}
				timer = timer - 1; if(timer < 0){timer = 0;}
			}
		} catch(InterruptedException e){
		} finally{g.lock.unlock();}
	}
}
class GUI_Timer implements Runnable {
	GUI gui;
	public GUI_Timer(GUI g){gui = g;}
	public void run(){try { 
		Thread.sleep( 1000 ); 
		gui.g.lock.lock();
		gui.timer_returned = true; 
		gui.g.interface_wait.signal();
		gui.g.lock.unlock();
	} catch (InterruptedException e) { };}
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
	private JButton timeout_button = new JButton("Timeout");

	private JButton cat1_button = new JButton("Category 1");
	private JButton cat2_button = new JButton("Category 2");
	private JButton cat3_button = new JButton("Category 3");
	private JButton cat4_button = new JButton("Category 4");
	private JButton cat5_button = new JButton("Category 5");
	private JButton cat6_button = new JButton("Category 6");

	private JButton file_chooser = new JButton("Open file");

	private JTable table;
	private JScrollPane scrollPane;

	public SpinPanel s_panel;

	private JButton answer_button = new JButton("Answer!");

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

		answer_button.addActionListener(new ActionListener() {
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

		timeout_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				lock.lock();
				int_share = 2;
				returned = true;
				interface_wait.signal();
				lock.unlock();
			}
		});

		cat1_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				lock.lock();
				int_share = 1;
				returned = true;
				interface_wait.signal();
				lock.unlock();
			}
		});
		cat2_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				lock.lock();
				int_share = 2;
				returned = true;
				interface_wait.signal();
				lock.unlock();
			}
		});
		cat3_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				lock.lock();
				int_share = 3;
				returned = true;
				interface_wait.signal();
				lock.unlock();
			}
		});
		cat4_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				lock.lock();
				int_share = 4;
				returned = true;
				interface_wait.signal();
				lock.unlock();
			}
		});
		cat5_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				lock.lock();
				int_share = 5;
				returned = true;
				interface_wait.signal();
				lock.unlock();
			}
		});
		cat6_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				lock.lock();
				int_share = 6;
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
		pane.revalidate();
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
		pane.revalidate();
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
	public void check_answer(String message){
		message = message.replace("\n", "<p>");
		message_label.setText("<html><h1>Answer: "+message+"</h1></html>");
		message_label2.setText("<html><h1>Did the Player answer correctly?</h1></html>");
		frame.setContentPane(pane);
		pane.revalidate();
		pane.removeAll();

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(message_label, c);
		c.gridx = 0;
		c.gridy = 1;
		pane.add(message_label2, c);
		c.gridx = 0;
		c.gridy = 2;
		pane.add(y_button, c);
		c.gridx = 0;
		c.gridy = 3;
		pane.add(n_button, c);
		c.gridx = 0;
		c.gridy = 4;
		pane.add(timeout_button, c);

		pane.repaint();
		return;
	}
	public void choose_category(String message, String[] categories){
		message = message.replace("\n", "<p>");
		message_label.setText("<html><h1>"+message+"</h1></html>");
		cat1_button.setText(categories[0]);
		cat2_button.setText(categories[1]);
		cat3_button.setText(categories[2]);
		cat4_button.setText(categories[3]);
		cat5_button.setText(categories[4]);
		cat6_button.setText(categories[5]);

		frame.setContentPane(pane);
		pane.revalidate();
		pane.removeAll();

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(message_label, c);
		c.gridx = 0;
		c.gridy = 1;
		pane.add(cat1_button, c);
		c.gridx = 0;
		c.gridy = 2;
		pane.add(cat2_button, c);
		c.gridx = 0;
		c.gridy = 3;
		pane.add(cat3_button, c);
		c.gridx = 0;
		c.gridy = 4;
		pane.add(cat4_button, c);
		c.gridx = 0;
		c.gridy = 5;
		pane.add(cat5_button, c);
		c.gridx = 0;
		c.gridy = 6;
		pane.add(cat6_button, c);

		pane.repaint();
		return;
	}

	public void get_settings_file(){
		message_label.setText("<html><h1>Choose file</h1></html>");
		frame.setContentPane(pane);
		pane.revalidate();
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
		pane.revalidate();
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
	public void ask_question(int timer, String message){
		message = message.replace("\n", "<p>");
		message_label.setText("<html><h1>"+message+"</h1></html>");
		message_label2.setText("<html><h1>Time left:"+timer+"</h1></html>");
		frame.setContentPane(pane);
		pane.revalidate();
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
		pane.add(answer_button, c);

		pane.repaint();
		return;
	}
	public void show_info(int round, int currentPlayer, String message, int[][] points, int[] free, int spin_counter){
		message = message.replace("\n", "<p>");
		message_label.setText("<html><h1>"+message+"</h1></html>");

		String m2 = "Round: "+(round+1)+"&nbsp;&nbsp;&nbsp;&nbsp;Spins left: "+spin_counter+"&nbsp;&nbsp;&nbsp;&nbsp;Player "+(currentPlayer+1)+"'s turn";
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
		pane.revalidate();
		pane.removeAll();

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = c.weighty = 0.8;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(message_label, c);
		c.gridx = 0;
		c.gridy = 1;
		pane.add(message_label2, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 10;
		pane.add(scrollPane, c);
		c.gridx = 0;
		c.gridy = 3;
		pane.add(continue_button, c);

		pane.repaint();
		return;
	}

	public void show_board(int round, int category, String[] categories, int[] questions){
		message_label.setText("<html><h1>Board:</h1></html>");
		String[] p = new String[6];
		for(int i=0; i<p.length; i++){p[i]="_";}
		p[category] = "<html><b>Category Picked</b></html>";

		Object[][] data = new Object[6][p.length];
		for(int i=0; i<p.length; i++){data[0][i] = categories[i];}
		for(int i=0; i<5; i++){
		for(int j=0; j<6; j++){
			if(questions[j]<=i){data[i+1][j]=((i+1)*(round+1)*100)+"";}
			else{data[i+1][j]="";}
		}}

		table = new JTable(data, p);
		scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		frame.setContentPane(pane);
		pane.revalidate();
		pane.removeAll();

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = c.weighty = 0.8;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(message_label, c);
		c.gridx = 0;
		c.gridy = 1;
		pane.add(scrollPane, c);
		c.gridx = 0;
		c.gridy = 2;
		pane.add(continue_button, c);

		pane.repaint();
		return;
	}

	public void spin(String[] choices, int outcome){
		pane.removeAll();
		s_panel = new SpinPanel();
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
	class SpinPanel extends JPanel implements Runnable {
		public ReentrantLock lock2 = new ReentrantLock();
		private Image imageOffScreen;
		private Graphics graphicsOffScreen;

		Font font = new Font("TimesRoman", Font.BOLD, 40);
		int height; int width;
		String[] choices;
		Color[] colors;
		int current = 0;
		int outcome;
		int loc=0; int pos=0; int div=20;
		boolean end = false;

		public void init(String[] ch, Color[] co, int o, int w, int h){ 
			height = h; width = w;
			this.setLayout(new FlowLayout(FlowLayout.LEFT));
			this.add(continue_button);
			imageOffScreen = createImage(width, height);
			graphicsOffScreen = imageOffScreen.getGraphics();
			choices = ch; colors = co; outcome = o;
			loc = outcome-1; if(loc < 0){loc = loc + ch.length;}
		}
		public void run(){
			boolean atleast1 = false;
			for(loc = current; !(loc == outcome && atleast1); loc = (loc+1) % choices.length){
				spin1sector();
				atleast1=true;
				lock2.lock(); if(end){lock2.unlock(); break;} lock2.unlock();
			}
			current = outcome;
		}
		public void spin1sector(){
			for(int i=0; i<(div+1); i++){
				pos = i;
				repaint();
				try { Thread.sleep( 20 ); } catch (InterruptedException e) { }
				lock2.lock(); if(end){lock2.unlock(); break;} lock2.unlock();
			}
		}
		public void stop(){lock2.lock(); end = true; lock2.unlock();}

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
			graphicsOffScreen.setColor(colors[(loc+1)%choices.length]);
			graphicsOffScreen.fillRect(0, height*pos/div-height, width,height);
			graphicsOffScreen.setColor(colors[loc]);
			graphicsOffScreen.fillRect(0, height*pos/div, width, height);
			graphicsOffScreen.setColor(new Color(0,0,0));

			drawCenteredString(graphicsOffScreen,choices[(loc+1)%choices.length],new Rectangle(0, height*pos/div-height, width, height), font);
			drawCenteredString(graphicsOffScreen,choices[loc],new Rectangle(0, height*pos/div, width, height), font);
			graphicsOffScreen.setColor(new Color(255,255,255));
			graphicsOffScreen.fillRect(width/4, 0, width/2, height/10);
			graphicsOffScreen.setColor(new Color(0,0,0));

			drawCenteredString(graphicsOffScreen,"Spin:",new Rectangle(width/4, 0, width/2, height/10), font);

			g.drawImage( imageOffScreen, 0, 0, this);
		}
	}
}


