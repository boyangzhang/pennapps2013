import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class GraphicsFrontend {

	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("LeapMotion Music");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/*
		 * TODO: Need a way to properly load all channel data
		 */
		
		Note[] notes = { new Note(0,10,30), new Note(10,20,5), new Note(20,50,10) };
		Channel[] channels = { new Channel(notes) };
		frame.add(new TrackPanel(channels));

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}
	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}

@SuppressWarnings("serial")
class TrackPanel extends JPanel {

	int netOffset = 0;
	Channel[] channels;
	
	public TrackPanel(Channel[] channels) {
		this.channels = channels;
		// Keystroke listener
		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == e.VK_LEFT) {
					if (netOffset > 0) {
						netOffset--;
					}
				}
				else if (e.getKeyCode() == e.VK_RIGHT) {
					netOffset++;
				}
				repaintAll();
			}
			public void keyPressed(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
			}
		});
	}

	public Dimension getPreferredSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	public Color getBackground() {
		return new Color(0,0,0);
	}
	
	public boolean getFocusable() {
		return true;
	}

	private void repaintAll() {
		int channelCount = channels.length;
		int channelHeight = this.getHeight()/channelCount;
		for (int i = 0; i < channelCount; i++) {
			Note[] notes = channels[i].getNotes();
			for (int j = 0; j < notes.length; j++) {
				Note note = notes[j];
				repaint(note.getStart() + netOffset, note.getPitch(), 
						note.getDuration(), channelHeight/note.getPitch());
			}
		}
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int channelCount = channels.length;
		int channelHeight = this.getHeight()/channelCount;
		for (int i = 0; i < channelCount; i++) {
			Note[] notes = channels[i].getNotes();
			for (int j = 0; j < notes.length; j++) {
				Note note = notes[j];
				g.setColor(new Color(254,0,0));
				g.fillRect(note.getStart() + netOffset, note.getPitch(), 
						note.getDuration(), channelHeight/note.getPitch());
			}
		}
	}

}

