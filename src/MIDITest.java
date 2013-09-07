import javax.sound.midi.*;


public class MIDITest {

	public static void main(String[] args) throws InvalidMidiDataException, MidiUnavailableException {
		 ShortMessage myMsg = new ShortMessage();
		    // Play the note Middle C (60) moderately loud
		    // (velocity = 93)on channel 4 (zero-based).
		    myMsg.setMessage(ShortMessage.NOTE_ON, 4, 60, 93); 
		    Synthesizer synth = MidiSystem.getSynthesizer();
		    Receiver synthRcvr = MidiSystem.getReceiver();
		    synthRcvr.send(myMsg, -1); // -1 means no time stamp
	}
}
