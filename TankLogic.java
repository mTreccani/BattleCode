package examplefuncsplayer;

import battlecode.common.*;

public class TankLogic extends RobotLogic{

	public TankLogic (RobotController rc) {
		super(rc);
	}
	
	@Override
	public void run() throws GameActionException{
		
		int birthRound=rc.getRoundNum();
		boolean isDead=false;
		setNumTank(+1);
        
        while(true){
        		
		    // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
	        try {
	            
	            // See if there are any nearby enemy robots
	            RobotInfo[] robots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemy);
	
	            // If there is one...
	            if (robots.length==1) {
	                // And we have enough bullets, and haven't attacked yet this turn...
	                if (rc.canFireSingleShot()) {
	                    // ...Then fire a bullet in the direction of the enemy.
	                    rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
	                }
	            }
	            
	            // If there are some....
	            if (robots.length>1 && robots.length<=3) {
	                // And we have enough bullets, and haven't attacked yet this turn...
	                if (rc.canFireTriadShot()) {
	                    // ...Then fire a bullet in the direction of the enemy.
	                    rc.fireTriadShot(rc.getLocation().directionTo(robots[0].location));
	                }
	            }
	            
	            // If there are more than 3 enemies
	            if (robots.length>3) {
	                // And we have enough bullets, and haven't attacked yet this turn...
	                if (rc.canFirePentadShot()) {
	                    // ...Then fire a bullet in the direction of the enemy.
	                    rc.firePentadShot(rc.getLocation().directionTo(robots[0].location));
	                }
	            }
	            
	            if(robots.length > 0) {
	                MapLocation miaposizione = rc.getLocation();
	                MapLocation enemyLocation = robots[0].getLocation();
	                Direction toEnemy = miaposizione.directionTo(enemyLocation);
	
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
