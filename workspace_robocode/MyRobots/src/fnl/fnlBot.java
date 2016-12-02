package fnl;

import robocode.*;
import java.awt.*;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.HitByBulletEvent;

public class fnlBot extends AdvancedRobot {
	// This area should contain the initialization of the neural network
	// static neuralNet Q = 
	//length of input vector
	//num of hidden neurons
	//learning rate used in BP
	//alpha = momentum term in BP
    //lower bound of Q
	//upper bound of Q
    //use bipolar hidden neurons?
	
	public void run() {
		while (true) {
			ahead (100);
			turnRight(90);
			turnGunRight(360);
			back(100);
		}
	}
	public void onScannedRobot(ScannedRobotEvent e) {
		fire(1);
	}
	public void onHitByBullet(HitByBulletEvent e) {
		turnLeft(90 - e.getBearing());
	}
	
}
