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

public class RobotLUT2 extends AdvancedRobot {

	/**
	 * TrackFire's run method
	 */
	
	static LUT lookupTable = null; //static variable
	double gunTurnAmt; // How much to turn our gun when searching
	double [] s; //present state
	double action; //Action
	int lutIndex;
	int prevLutIndex;
	double lastReward;
	
	public void run() {
		s = new double[3];
		this.loadLUT();
		while (true) {
			this.getState(null);
			this.determineAction();
			this.doAction(null);
		}
	}

	/**
	 * onScannedRobot:  We have a target.  Go get it.
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		this.getState(e);
		this.determineAction();
		this.doAction(e);
		return;
	}
	
	public void loadLUT() {
		if(lookupTable == null) {
			lookupTable = new LUT(this);
			try{
				if (1 == 1) {
					lookupTable.load(null);
				}
			} catch (IOException e) {
			} catch (NumberFormatException e) {
			}
		}
		return;
	}

	//get the state
	public void getState(ScannedRobotEvent e) {
		//1.1 determine the coordinate
		double y  = this.getY();
		double x = this.getX();

		//defines the limits
		double sentrySize = 75;
		double x1 = sentrySize;
		double x2 = 800-sentrySize;
		double y1 = sentrySize;
		double y2 = 600-sentrySize;
		
		if ((x <= x1 && y <= y1) || (x <=x1 && y > y2) || (x >=x2 && y < y1) || (x >=x2 && y >= y2)) { //A corner
			s[0] = 0;
		} else if ((x > x1 && x < x2) && (y > y1 && y < y2)) { //center
			s[0] = 2;
		} else { //edges
			s[0] = 1;
		}
		//1.2 determine the energy
		double energy = this.getEnergy();
		if (energy >= 66.0) {
			s[1] = 2;
		} else if (energy < 33.0) {
			s[1] = 1;
		} else {
			s[1] = 0;
		}
		//1.3 determine the distance to enemy
		s[2] = 3; //Unknown
		if(e != null) {
			double distance = e.getDistance();
			if (distance > 300.0) {
				s[2] = 2;
			} else if (distance > 100.0) {
				s[2] = 1;
			} else {
				s[2] = 0;
			}
		}
		return;
	}
	
	//Determine the action
	public void determineAction() {
		lutIndex = lookupTable.indexFor(s);
		action = lookupTable.lookuptable[lutIndex][3];
		
		if (Math.random() < 0.05) { //Choose a random action 10% of the time
			int randomNum = 0 + (int)(Math.random() * ((6 - 0) + 1));
			System.out.println("Choosing a random move " + randomNum + " Max Q action is "+action);
			action = randomNum;
		}
		prevLutIndex = lutIndex;
		return;
	}
	
	public void doAction(ScannedRobotEvent e) {
		switch ((int) action) {
			case 0:
				this.chase(e);
				break;
			case 1:
				this.shoot(e);
				break;
			case 2:
				this.left();
				break;
			case 3:
				this.right();
				break;
			case 4:
				this.back();
				break;
			case 5:
				this.ahead();
				break;	
			case 6:
				this.scan();
				break;
		}
		return;
	}
	
	public void updateQ() { //not used now
		lookupTable.updateQ(this.lutIndex, this.prevLutIndex, lastReward);
	}
	
	/// REWARDS ///
	
	public void onWin(WinEvent e) {
		// Victory dance
		lastReward = 20;
		lookupTable.updateQ(this.lutIndex, this.prevLutIndex, lastReward);
		lookupTable.save(null);
		return;
	}
	
    public void onHitByBullet(HitByBulletEvent event) {
    	lastReward= -1;
    	lookupTable.updateQ(this.lutIndex, this.prevLutIndex, lastReward);
        out.println(event.getName() + " hit me!");
        return;
    }
	  
    public void onHitRobot(HitRobotEvent event) {
    	lastReward = 10;
    	lookupTable.updateQ(this.lutIndex, this.prevLutIndex, lastReward);
    	return;
    }
    
	public void onDeath(DeathEvent event) {
		lastReward = -1;
		lookupTable.updateQ(this.lutIndex, this.prevLutIndex, lastReward);
		lookupTable.save(null);
		return;
	}
	
   public void onBattleEnded(BattleEndedEvent event, BattleResults results) {
	   //results see: http://robocode.sourceforge.net/docs/robocode/robocode/BattleResults.html
       out.println("The battle has ended");
       lookupTable.printLUT();
       lookupTable.save(null);
       out.println("The battle has ended");
       return;
   }
	
	/// ACTIONS ///
   
   public void chase(ScannedRobotEvent e) { //action 0
		if (e != null) {
			gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
	
			this.setTurnGunRight(gunTurnAmt); // Try changing these to setTurnGunRight,
			this.turnRight(e.getBearing()); // and see how much Tracker improves...
			this.ahead(e.getDistance() - 140);
		}
		return;
	}
	
	public void shoot(ScannedRobotEvent e) { //action 1
		if (e != null) {
			gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
			this.turnGunRight(gunTurnAmt);
			this.fire(2);
		}
		return;
	}

	public void left() { //action 2
		this.setTurnLeft(15);
		return;
	}
	
	public void right() { //action 3
		this.setTurnRight(15);
		return;
	}
	
	public void back() { //action 4
		this.back(40);
		return;
	}
	
	public void ahead() { //action 5
		this.ahead(40.0);
		return;
	}
	
	public void scan() { //action scan all the around 6
		this.setTurnGunRight(360);
		return;
	}
}
