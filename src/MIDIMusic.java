import java.io.File;
import java.io.IOException;

import javax.sound.midi.*;

public class MIDIMusic {

	Sequence  sequence;
	Sequencer sequencer;
	Synthesizer synthesizer;
	
	public MIDIMusic(String filename) throws InvalidMidiDataException, IOException, MidiUnavailableException {
//		sequence    = MidiSystem.getSequence(new File("tnfdm.mid"));
		sequence    = MidiSystem.getSequence(new File(filename));
		sequencer   = MidiSystem.getSequencer(true);
		sequencer.setSequence(sequence);
		synthesizer = MidiSystem.getSynthesizer();
	}
	
	public void play() {
		sequencer.start();
	}
	
	public void pause() {
		sequencer.stop();
	}
	
	public void setSpeed(float scalar) {
		sequencer.setTempoFactor(scalar);
	}
	
}
