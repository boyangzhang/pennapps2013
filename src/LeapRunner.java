import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;


public class LeapRunner {
	
	static MIDIMusic player;
	
	static Frame frame = null, prevFrame = null;
	
	static int timeInterval = 10;		// in milliseconds
	
	public static void main(String[] args) {

		try {
			player = new MIDIMusic("tnfdm.mid");
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
		
		// create Controller object
        final Controller controller = new Controller();
        final MotionHandler motionHandler = new MotionHandler();
        
        new Timer().schedule(new TimerTask() {
        	public void run()  {
        	  // do stuff
        		if (frame == null) {
        			frame		= controller.frame();
        			prevFrame	= controller.frame();
        		}
        		else {
        			prevFrame = frame;
        			frame = controller.frame();
        		}
        		
        		String event = motionHandler.processFrame(frame, prevFrame);
        		
        		boolean eventWasUnimportant = event == null;
        		if (eventWasUnimportant)
        			return;
        		
        		if (event.equals("PAUSE")) {
        			player.pause();
        		}
        		else if (event.equals("PLAY")) {
        			player.play();
        		}
        	}
        	}, 1, timeInterval);

        // Have the listener receive events from the controller
//        controller.addListener(listener);

        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove the listener when done
//        controller.removeListener(listener);
    }

}
