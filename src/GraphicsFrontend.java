import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class GraphicsFrontend {

	public static Timer timer;
	private static int timeInterval = 20;		// in milliseconds

	private static void createAndShowGUI() {
		
		
		
		//Create and set up the window.
		JFrame frame = new JFrame("LeapMotion Music");

		Note[] notes = { new Note(0,10,30), new Note(10,20,5), new Note(30,50,10), new Note(80, 500, 100) };
		Channel[] channels = { new Channel(notes) };

		final TrackPanel tp = new TrackPanel(channels);
		frame.add(tp);
		
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run()  {
				// move cursor
				tp.cursorXPos++;
				tp.repaintAll();
			}
		}, 1, timeInterval);

		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					if (timer != null){
						timer.cancel();
						timer = null;
					}
					else {
						timer = new Timer();
						timer.schedule(new TimerTask() {
							public void run()  {
								// move cursor
								tp.cursorXPos++;
								tp.repaintAll();
							}
						}, 1, timeInterval);
					}
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {}

			@Override
			public void keyTyped(KeyEvent e) {}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/*
		 * TODO: Need a way to properly load all channel data
		 */

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

				//				LeapRunner runner = new LeapRunner();
				//				runner.playSong();

				MIDIMusic player;
				try {
					player = new MIDIMusic("tnfdm.mid");
					//					player.sequencer.open();
					//					player.play();
				} catch (InvalidMidiDataException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (MidiUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
	}
}

@SuppressWarnings("serial")
class TrackPanel extends JPanel {

	public Channel[] channels;
	public int cursorXPos;
	private int windowHeight;

	public TrackPanel(Channel[] channels) {
		this.channels = channels;
		cursorXPos = 0;

	}

	public Dimension getPreferredSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	public Color getBackground() {
		return Color.black;
	}

	public boolean getFocusable() {
		return true;
	}

	public void repaintAll() {
		int channelCount = channels.length;
		int channelHeight = this.getHeight()/channelCount;
		for (int i = 0; i < channelCount; i++) {
			Note[] notes = channels[i].getNotes();
			for (int j = 0; j < notes.length; j++) {
				Note note = notes[j];
				repaint(note.getStart(), note.getPitch(), 
						note.getDuration(), channelHeight/note.getPitch());
			}
		}
		repaint(cursorXPos, 0, 2, windowHeight);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int channelCount = channels.length;
		int channelHeight = this.getHeight()/channelCount;
		for (int i = 0; i < channelCount; i++) {
			Note[] notes = channels[i].getNotes();
			for (int j = 0; j < notes.length; j++) {
				Note note = notes[j];
				g.setColor(new Color((j+1) * 50,0,0));
				g.fillRect(note.getStart(), note.getPitch(), 
						note.getDuration(), channelHeight/note.getPitch());
			}
		}
		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
		windowHeight = topFrame.getHeight();
		g.setColor(Color.green);
		g.fillRect(cursorXPos, 0, 2, windowHeight);
	}

}

