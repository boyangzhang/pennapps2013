
public class Note {

	private int start;
	private int duration;
	private int pitch;
	
	public Note(int start, int duration, int pitch) {
		this.duration = duration;
		this.pitch = pitch;
		this.start = start;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public int getPitch() {
		return pitch;
	}

	public int getStart() {
		return start;
	}
	
}
