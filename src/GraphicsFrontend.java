import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class GraphicsFrontend {

	public static Timer timer;
	private static long timeInterval = 20;		// in milliseconds
	final static String songFileName = "tnfdm.mid";

	private static void createAndShowGUI() {
		
		// parse data for GUI
		Sequence midi = null;
		try {
			midi = MidiSystem.getSequence(new File(songFileName));
			timeInterval = midi.getMicrosecondLength() / 1000 / 1440;
		} catch (InvalidMidiDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		MIDIParser parser = new MIDIParser();
		Channel[] ch = parser.parseMIDIData(midi);
		
		//Create and set up the window.
		JFrame frame = new JFrame("LeapMotion Music");
	
		Channel[] channels = parser.makeMIDIDataCompatibleForGUI(ch, 48);
		
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
					player = new MIDIMusic(songFileName);
					player.sequencer.open();
					player.play();
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
				if (note == null) continue;
				repaint((int)note.getStart(), (int)note.getPitch(), 
						(int)note.getDuration(), (int)(channelHeight/note.getPitch()));
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
				if (note == null) continue;
				g.setColor(Color.red);
//				System.out.println("note " + j + " stuff: " + note.getStart() + ", " + note.getPitch() + ", " + note.getDuration() + ", " + note.isRest());
				g.fillRect((int)note.getStart(), (int)note.getPitch(), 
						(int)note.getDuration(), (int)(channelHeight/note.getPitch()));
			}
		}
		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
		windowHeight = topFrame.getHeight();
		g.setColor(Color.green);
		g.fillRect(cursorXPos, 0, 2, windowHeight);
	}

}

