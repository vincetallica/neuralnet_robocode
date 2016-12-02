package sm;

import robocode.ScannedRobotEvent;
//Class from https://code.google.com/a/eclipselabs.org/p/robocode-ai-team/source/browse/trunk/src/RobocodeAITeam/DogPoundTerror.java?r=13

public class Enemy {
        
        private RobotLUT robot; 
        private double lastKnownX, lastKnownY, absoluteBearing; 
        private ScannedRobotEvent lastScanEvent; 
        private String name;
        
        public Enemy(RobotLUT pRobot){
                robot = pRobot;
        }
        
        public void setInfoFromScanEvent(ScannedRobotEvent e) {
                lastScanEvent = e; 
                absoluteBearing = robot.getHeadingRadians() + e.getBearingRadians();
                lastKnownX = robot.getX() + e.getDistance() * Math.sin(absoluteBearing);
                lastKnownY = robot.getY() + e.getDistance() * Math.cos(absoluteBearing);
                name = e.getName();
        }
        
        public String getName(){
                return name;
        }

        public ScannedRobotEvent getLastScanEvent() {
                return lastScanEvent;
        }

        public double getLastKnownX(){
                return lastKnownX;
        }
        
        public double getLastKnownY(){
                return lastKnownY; 
        }
        
        public double getAbsoluteBearing(){
                return absoluteBearing; 
        }
        
        public double getEnergyLevel(){
                return lastScanEvent.getEnergy(); 
        }
}