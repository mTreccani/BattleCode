package examplefuncsplayer;

import battlecode.common.*;

public class SoldierLogic extends RobotLogic{
	Team enemy = rc.getTeam().opponent();
	
	public SoldierLogic (RobotController rc){
		super(rc);
	}
	
	@Override
	public void run() throws GameActionException{
		
		while(true){
			
			int birthRound=rc.getRoundNum();
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
	           /* if (robots.length>1) {
	                // And we have enough bullets, and haven't attacked yet this turn...
	                if (rc.canFireTriadShot()) {
	                    // ...Then fire a bullet in the direction of the enemy.
	                    rc.fireTriadShot(rc.getLocation().directionTo(robots[0].location));
	                }
	            } */
	            
	            if(robots.length > 0) {
	                MapLocation myLocation = rc.getLocation();
	                MapLocation enemyLocation = robots[0].getLocation();
	                Direction toEnemy = myLocation.directionTo(enemyLocation);
	
	                tryMove(toEnemy);
	            }
	            else{
	            // Move randomly
	                	tryMove(randomDirection());
	            }
	            
	            if(rc.getHealth()<RobotType.SOLDIER.maxHealth/DEATHDIVIDER && rc.getRoundNum()-birthRound< REGENERATIONROUNDS) setNumSoldier(-1);
	            // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
	            Clock.yield();
	
	        } catch (Exception e) {
	            System.out.println("Soldier Exception");
	            e.printStackTrace();
	        }
	    }
	}
}
