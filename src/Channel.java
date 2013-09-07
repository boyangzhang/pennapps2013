
public class Channel {

	public Note[] notes;
	
	public Channel(Note[] notes) {
		this.notes = notes;
	}
	
	public Note[] getNotes() {
		return notes;
	}
	
	public int size() {
		return notes.length;
	}
}
