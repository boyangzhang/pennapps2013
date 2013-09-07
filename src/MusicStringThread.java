import org.jfugue.*;


public class MusicStringThread extends Thread {
	
	Player player;
	Pattern song;
	
	public MusicStringThread() {
		player = new Player();

		// "Frere Jacques"
		Pattern pattern1 = new Pattern("C5q D5q E5q C5q");

		// "Dormez-vous?"
		Pattern pattern2 = new Pattern("E5q F5q G5h");

		// "Sonnez les matines"
		Pattern pattern3 = new Pattern("G5i A5i G5i F5i E5q C5q");

		// "Ding ding dong"
		Pattern pattern4 = new Pattern("C5q G4q C5h");

		// Put it all together
		song = new Pattern();
		song.add(pattern1);
		song.add(pattern1);
		song.add(pattern2);
		song.add(pattern2);
		song.add(pattern3);
		song.add(pattern3);
		song.add(pattern4);
		song.add(pattern4);

	}
	
	  public void run() {
	        player.play(song);
	    }

}
