
public class Note {

	private long start;
	private long duration;
	private long pitch;
	private boolean isRest;
	
	public Note(long start, long duration, long pitch, boolean isRest) {
		this.start = start;
		this.duration = duration;
		this.pitch = pitch;
		this.start = start;
		this.isRest = isRest;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public long getPitch() {
		return pitch;
	}

	public long getStart() {
		return start;
	}

	public boolean isRest() {
		return isRest;
	}
	
}
