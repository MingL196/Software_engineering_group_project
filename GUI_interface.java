import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.*;
import static javax.swing.GroupLayout.Alignment.*;
import java.util.concurrent.locks.*;

//		try { Thread.sleep(1000); } catch (Exception e) {}
public class GUI_interface extends GUI_abstract {
	GUI_worker g = new GUI_worker();

	public int get_n_players(){
		int n_players=0;

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { g.get_n_players(); }
		});

		g.lock.lock(); try{
			g.returned = false;
			while(!g.returned){g.interface_wait.await();}
			n_players = g.int_share;
		} catch(InterruptedException e){
		} finally{g.lock.unlock();}

		return n_players;
	}

	public String get_settings_file(){
		return "";
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

}

class GUI_worker extends JFrame {
	public ReentrantLock lock = new ReentrantLock();
	public Condition interface_wait = lock.newCondition();
	public boolean returned = false;
	public int int_share;

	private JFrame frame;
	private Container pane;
	private int width=700;
	private int height=300;

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

	private JLabel n_players_label = new JLabel("<html><h1>Number of Players: </h1></html>");
	private JTextField n_text_field = new JTextField("1");
	private JButton continue_button = new JButton("Continue");
	public void get_n_players(){
		frame.setContentPane(pane);
		pane.removeAll();
		continue_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				lock.lock(); try{
					int_share = Integer.parseInt(n_text_field.getText());
					returned = true;
					interface_wait.signal();
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

	private SpinPanel s_panel = new SpinPanel();
	private int lastoutcome = 0;
	public void spin(String[] choices, int outcome){
		pane.removeAll();
		frame.setContentPane(s_panel);
		Rectangle r = frame.getBounds();
		s_panel.set_info( r.height, r.width);

		s_panel.revalidate();
		lock.lock(); try{
			returned = true;
			interface_wait.signal();
		} finally{lock.unlock();}
		return;
	}
	class SpinPanel extends JPanel {
		int height; int width;
		SpinPanel() {}
		public void set_info(int w, int h){ 
			height=h; width=w; 
			// repaint();
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			g.drawRect(25, 25, 200, 200);
			g.drawString("BLAH", 100, 100);
		}
	}
}


