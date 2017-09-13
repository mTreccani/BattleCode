package examplefuncsplayer;

import battlecode.common.*;

public class TankLogic extends RobotLogic{

	public TankLogic (RobotController rc) throws GameActionException{
		super(rc);
		//setNumTank(+1);
	}
	
	@Override
	public void run() throws GameActionException{
		
		int birthRound=rc.getRoundNum();
		boolean isDead=false;
	
        while(true){
        		
		    // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
	        try {
	            
	            // See if there are any nearby enemy robots
	            gameInfo();
	
	            tryShoot();
	            
	            if(enemyRobots.length > 0) {
	                MapLocation enemyLocation = enemyRobots[0].getLocation();
	                Direction toEnemy = myLocation.directionTo(enemyLocation);
                	tryMove(toEnemy);
	            }
	            else{
		            // Move randomly
		            tryMove(randomDirection());
	            }
	            
	            if(!isDead){
	            	if(isDead(birthRound)) setNumTank(-1);
	            	isDead=true;
	            }
	            
	            // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
	            Clock.yield();
	
	        } catch (Exception e) {
	            System.out.println("Tank Exception");
	            e.printStackTrace();
	        }
	    }
	}
}
