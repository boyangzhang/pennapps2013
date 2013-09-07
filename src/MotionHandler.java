import java.util.LinkedList;
import java.util.List;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.KeyTapGesture;
import com.leapmotion.leap.ScreenTapGesture;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;


public class MotionHandler {
	
	private static LinkedList<Integer> framesWithDecreasingFingerNumbers, framesWithIncreasingFingerNumbers;
	
	public MotionHandler() {
		framesWithDecreasingFingerNumbers = new LinkedList<Integer>();
		framesWithIncreasingFingerNumbers = new LinkedList<Integer>();
	}
	
	private static boolean checkThatPalmIsFaceDown(Hand hand) {
		Vector normal = hand.palmNormal();
		return Math.toDegrees(normal.pitch()) <= -45 && 
				Math.toDegrees(normal.roll()) <= 10 &&
				Math.toDegrees(normal.roll()) >= -10;
	}

	private static boolean checkIfUserHasMadeFist(Hand hand) {
				
		int numberOfFingers = hand.fingers().count();
		
		// ensure that fist is closing --> # fingers is decreasing
		boolean fingersAreIncreasing = true;
		if (!framesWithDecreasingFingerNumbers.isEmpty()) {
			int lastNumberOfFingers = framesWithDecreasingFingerNumbers.getLast();
			
			fingersAreIncreasing = lastNumberOfFingers < numberOfFingers;
			if (fingersAreIncreasing) {
				// if our hand is opening again --> reset the list
				framesWithDecreasingFingerNumbers = new LinkedList<Integer>();
			}
		}
		framesWithDecreasingFingerNumbers.add(numberOfFingers);
		
		// ensure that palm is face down
		boolean palmIsFaceDown = checkThatPalmIsFaceDown(hand);
		
		
		boolean userHasMadeFist = (numberOfFingers <= 1) && !fingersAreIncreasing && palmIsFaceDown;
		return userHasMadeFist;
	}
	
	private static boolean checkIfUserOpenedFist(Hand hand) {
		int numberOfFingers = hand.fingers().count();
		
		// ensure that fist is OPENING --> # fingers is INCREASING
		boolean fingersAreDecreasing = true;
		if (!framesWithIncreasingFingerNumbers.isEmpty()) {
			int lastNumberOfFingers = framesWithIncreasingFingerNumbers.getLast();
			
			fingersAreDecreasing = lastNumberOfFingers > numberOfFingers;
			if (fingersAreDecreasing) {
				// if our hand is opening again --> reset the list
				framesWithIncreasingFingerNumbers = new LinkedList<Integer>();
			}
		}
		framesWithIncreasingFingerNumbers.add(numberOfFingers);
		
		// ensure that palm is face down
		boolean palmIsFaceDown = checkThatPalmIsFaceDown(hand);		
		
		boolean userHasOpenedFist = (numberOfFingers == 5) && !fingersAreDecreasing && palmIsFaceDown;
		return userHasOpenedFist;
	}

	public String processFrame(Frame frame, Frame prev) {

		System.out.println("Frame id: " + frame.id()
				+ ", prev: " + prev.id()
				+ ", timestamp: " + frame.timestamp()
				+ ", hands: " + frame.hands().count()
				+ ", fingers: " + frame.fingers().count()
				+ ", tools: " + frame.tools().count()
				+ ", gestures " + frame.gestures().count());

		if (!frame.hands().isEmpty()) {
			// Get the first hand
			Hand hand = frame.hands().get(0);

			// Check if the hand has any fingers
			FingerList fingers = hand.fingers();
			if (!fingers.isEmpty()) {
				// Calculate the hand's average finger tip position
				Vector avgPos = Vector.zero();
				for (Finger finger : fingers) {
					avgPos = avgPos.plus(finger.tipPosition());
				}
				avgPos = avgPos.divide(fingers.count());
				System.out.println("Hand has " + fingers.count()
						+ " fingers, average finger tip position: " + avgPos);
				
				boolean shouldPause = checkIfUserHasMadeFist(hand);
				if (shouldPause) {
					// pause song!!!
					return "PAUSE";
				}
				
				boolean shouldPlay = checkIfUserOpenedFist(hand);
				if (shouldPlay) {
					return "PLAY";
				}
			}
			else {
				framesWithDecreasingFingerNumbers = new LinkedList<Integer>();
				framesWithIncreasingFingerNumbers = new LinkedList<Integer>();
			}

			// Get the hand's sphere radius and palm position
//			System.out.println("Hand sphere radius: " + hand.sphereRadius()
//					+ " mm, palm position: " + hand.palmPosition());

			// Get the hand's normal vector and direction
//			Vector normal = hand.palmNormal();
//			Vector direction = hand.direction();

			// Calculate the hand's pitch, roll, and yaw angles
//			System.out.println("Hand pitch: " + Math.toDegrees(direction.pitch()) + " degrees, "
//					+ "roll: " + Math.toDegrees(normal.roll()) + " degrees, "
//					+ "yaw: " + Math.toDegrees(direction.yaw()) + " degrees");
		}

		GestureList gestures = frame.gestures();
		for (int i = 0; i < gestures.count(); i++) {
			Gesture gesture = gestures.get(i);

			switch (gesture.type()) {
			case TYPE_CIRCLE:
				CircleGesture circle = new CircleGesture(gesture);

				// Calculate clock direction using the angle between circle normal and pointable
				String clockwiseness;
				if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/4) {
					// Clockwise if angle is less than 90 degrees
					clockwiseness = "clockwise";
				} else {
					clockwiseness = "counterclockwise";
				}

				// Calculate angle swept since last frame
				double sweptAngle = 0;
				if (circle.state() != State.STATE_START) {
					CircleGesture previousUpdate = new CircleGesture(prev.gesture(circle.id()));
					sweptAngle = (circle.progress() - previousUpdate.progress()) * 2 * Math.PI;
				}

				System.out.println("Circle id: " + circle.id()
						+ ", " + circle.state()
						+ ", progress: " + circle.progress()
						+ ", radius: " + circle.radius()
						+ ", angle: " + Math.toDegrees(sweptAngle)
						+ ", " + clockwiseness);
				break;
			case TYPE_SWIPE:
				SwipeGesture swipe = new SwipeGesture(gesture);
				System.out.println("Swipe id: " + swipe.id()
						+ ", " + swipe.state()
						+ ", position: " + swipe.position()
						+ ", direction: " + swipe.direction()
						+ ", speed: " + swipe.speed());
				break;
			case TYPE_SCREEN_TAP:
				ScreenTapGesture screenTap = new ScreenTapGesture(gesture);
				System.out.println("Screen Tap id: " + screenTap.id()
						+ ", " + screenTap.state()
						+ ", position: " + screenTap.position()
						+ ", direction: " + screenTap.direction());
				break;
			case TYPE_KEY_TAP:
				KeyTapGesture keyTap = new KeyTapGesture(gesture);
				System.out.println("Key Tap id: " + keyTap.id()
						+ ", " + keyTap.state()
						+ ", position: " + keyTap.position()
						+ ", direction: " + keyTap.direction());
				break;
			default:
				System.out.println("Unknown gesture type.");
				break;
			}
		}

		if (!frame.hands().isEmpty() || !gestures.isEmpty()) {
			System.out.println();
		}
		return null;
	}
}
