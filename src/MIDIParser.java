import java.io.File;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;


public class MIDIParser {

	public Channel[] parseMIDIData(Sequence midi) {
		Channel[] channels = new Channel[midi.getTracks().length];
		long length = midi.getTickLength();
		// TODO Refer to these for synchronizing with system clock
		// midi.getResolution();
		// midi.getMicrosecondLength();
		Track[] tracks = midi.getTracks();
		for (int i = 0; i < midi.getTracks().length; i++) {
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("TRACK " + i);
			System.out.println();
			System.out.println();
			System.out.println();
			Track track = tracks[i];
			Note[] notes = new Note[track.size()];
			for (int j = 0; j < track.size(); j++) {
				MidiEvent event = track.get(j);
				// Start time
				long tick = event.getTick();
				MidiMessage msg = event.getMessage();
				if (!(msg instanceof ShortMessage)) continue;
				ShortMessage noteCmd = (ShortMessage) msg;
				// Pitch
				int note = noteCmd.getData1();
				// Rest
				boolean isRest;
				if (noteCmd.getCommand() == ShortMessage.NOTE_ON) {
					isRest = false;
				}
				else {
					isRest = true;
				}
				long duration;
				// Reference point is time of next tick
				if (j < track.size() - 1) {
					long nextTick = track.get(j+1).getTick();
					duration = nextTick - tick;
				}
				// Reference point is final tick of track
				else { 
					duration = length - tick;
				}
				notes[j] = new Note(tick, duration, note, isRest);
				System.out.println("NOTE: " + notes[j] + "@" + tick + " note " + note + " with dur " + duration + " and rest is " + isRest);
			}
			channels[i] = new Channel(notes);
		}
		return channels;
	}
	
	public static void main(String[] args) throws Exception {
		Sequence midi = MidiSystem.getSequence(new File("tnfdm.mid"));
		MIDIParser parser = new MIDIParser();
		parser.parseMIDIData(midi);
	}

}
