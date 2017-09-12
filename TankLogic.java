package examplefuncsplayer;

import battlecode.common.*;

public class TankLogic extends RobotLogic{

	Team enemy = rc.getTeam().opponent();
	boolean isAlive;
	int birthRound;
	
	public TankLogic (RobotController rc){
		super(rc);
		birthRound=rc.getRoundNum();
		isAlive = true;
	}
	
	@Override
	public void run() throws GameActionException{
		
		// Position of Archon
        
        while(true){
        	
        	
		    // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
	        try {
	            MapLocation myLocation = rc.getLocation();
	
	            // See if there are any nearby enemy robots
	            RobotInfo[] robots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemy);
	
	            shoot();
	            
	            
	            if(isAlive){
	            	if(isDead(birthRound)){
		            	setNumTank(-1);
		            	isAlive = false;
		            }	
	            }
	            // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
	            Clock.yield();
	
	        } catch (Exception e) {
	            System.out.println("Soldier Exception");
	            e.printStackTrace();
	        }
	    }
	}
}
