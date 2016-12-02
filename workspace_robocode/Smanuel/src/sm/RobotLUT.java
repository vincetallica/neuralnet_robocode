package sm;

import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import static robocode.util.Utils.normalRelativeAngle;
import robocode.*;
import robocode.HitByBulletEvent;
import robocode.RobocodeFileOutputStream;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

public class RobotLUT extends AdvancedRobot {

	/**
	 * TrackFire's run method
	 */
	
	static LUT lookupTable = null; //static variable
	private int radarDirection=1;
	int actionPerforming;
	double gunTurnAmt; // How much to turn our gun when searching
	double [] s;
	double Q;
	double prevQ;
	int lutIndex;
	int prevLutIndex;
	
	public void run() {
		if(lookupTable == null) {
			lookupTable = new LUT(this);
			try{
				lookupTable.load(null);
			} catch (IOException e) {
				// Something went wrong reading the file, reset to 0.

			} catch (NumberFormatException e) {
				// Something went wrong converting to ints, reset to 0

			}
		} 
//		else {
//			lookupTable.printLUT();
//		}
		s = new double[3];
		//lookupTable = new LUT();
		//systematically create states-actions but with random Q values
		while (true) {
			turnGunRight(10); // Scans automatically
		}
	}

	/**
	 * onScannedRobot:  We have a target.  Go get it.
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		//1.0 Determine State
		
		//1.1 determine the coordinate
		double y  = this.getY();
		double x = this.getX();

		//defines the limits
		double x1 = 50;
		double x2 = 750;
		double y1 = 50;
		double y2 = 550;
		if ((x <= x1 && y <= y1) || (x <=x1 && y > y2) || (x >=x2 && y < y1) || (x >=x2 && y >= y2)) { //A corner
			s[0] = 0;
		} else if ((x > x1 && x < x2) && (y > y1 && y < y2)) { //center
			s[0] = 2;
		} else { //edges
			s[0] = 1;
		}
		//1.2 determine the coordinate
		double energy = this.getEnergy();
		if (energy >= 66.0) {
			s[1] = 2;
		} else if (energy < 33.0) {
			s[1] = 1;
		} else {
			s[1] = 0;
		}
		//1.3 determine the coordinate
		double distance = e.getDistance();
		if (distance > 300.0) {
			s[2] = 2;
		} else if (distance > 100.0) {
			s[2] = 1;
		} else {
			s[2] = 0;
		}
		//2.0 Look up table for action with Max
		lutIndex = lookupTable.indexFor(s);
		double action = lookupTable.lookuptable[lutIndex][3];
		
		Q = lookupTable.lookuptable[lutIndex][4];
		
		if (Math.random() <0.1) { //Choose a random action 10% of the time
			int randomNum = 0 + (int)(Math.random() * ((3 - 0) + 1));
			
			System.out.println("Choosing a random move" + randomNum + "Max Q action is "+action);
			action = randomNum;
		}
		//3.0 Do action with max Q.
		if(action == 0) {
			this.scan(e);
		} else if (action == 1) {
			this.retreat(e);
		} else if (action == 2) {
			this.shoot(e);
		} else if (action == 3) {
			this.chase(e);
		}
		
		prevLutIndex = lutIndex;
		//4.0 update reward
		
	}

	
	
	//Rewards
	public void onWin(WinEvent e) {
		// Victory dance
		lookupTable.updateQ(this.lutIndex, this.prevLutIndex, 20);
		turnRight(36000);
	}
	
    public void onHitByBullet(HitByBulletEvent event) {
    	lookupTable.updateQ(this.lutIndex, this.prevLutIndex, -1);
       out.println(event.getName() + " hit me!");
    }
	
    
    public void onHitRobot(HitRobotEvent event) {
    	lookupTable.updateQ(this.lutIndex, this.prevLutIndex, 10);
    }
    
	public void onDeath(DeathEvent event) {
		// Victory dance
		lookupTable.updateQ(this.lutIndex, this.prevLutIndex, -2);
		turnRight(36000);
	}
	
   public void onBattleEnded(BattleEndedEvent event) {
       out.println("The battle has ended");
       lookupTable.printLUT();
       lookupTable.save(null);
   }
	
	/// Actions ///
	public void chase(ScannedRobotEvent e) { //action 0
		gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));

		turnGunRight(gunTurnAmt); // Try changing these to setTurnGunRight,
		turnRight(e.getBearing()); // and see how much Tracker improves...
		ahead(e.getDistance() - 140);
		return;
	}
	
	public void retreat(ScannedRobotEvent e) { //action 1
		back(50);
	}
	
	public void shoot(ScannedRobotEvent e) { //action 2
		gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
		turnGunRight(gunTurnAmt);
		fire(3);
		
	}
	public void scan(ScannedRobotEvent e) { //action 3
		turnGunRight(10);
	}
}
