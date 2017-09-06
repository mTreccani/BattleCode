package examplefuncsplayer;

import battlecode.common.*;


public class ScoutLogic extends RobotLogic{
	
	Team enemy = rc.getTeam().opponent();
	
	public ScoutLogic (RobotController rc){
		super(rc);
	}
	
	@Override
	public void run() throws GameActionException {
		
		while(true){
			
			int birthRound=rc.getRoundNum();
	        // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
	        try {
	
	        	if(getNumScout()<=2 && rc.getRoundNum()-birthRound < 200){
	        		exploreStrategy();
	        		if(rc.getHealth()< RobotType.SCOUT.maxHealth/5 && rc.getRoundNum() - birthRound >= 20) setNumScout(-1);
	        		Clock.yield();
	        	}
	        	if(getNumScout()>2 && rc.getRoundNum()-birthRound < 400 ){
	        		runnerStrategy();
	        		if(rc.getHealth()< RobotType.SCOUT.maxHealth/5 && rc.getRoundNum() - birthRound >= 20) setNumScout(-1);
	        		Clock.yield();
	        	}
	        	else{
	        		basicScout();
	        		if(rc.getHealth()< RobotType.SCOUT.maxHealth/5 && rc.getRoundNum() - birthRound >= 20) setNumScout(-1);
	        	}
	
	        } catch (Exception e) {
	            System.out.println("Soldier Exception");
	            e.printStackTrace();
	            Clock.yield();
	        }
		}
	}

	public void basicScout() throws GameActionException {
		// See if there are any nearby enemy robots
		RobotInfo[] robots = rc.senseNearbyRobots(RobotType.SCOUT.sensorRadius, enemy);
		TreeInfo[] trees = rc.senseNearbyTrees(RobotType.SCOUT.sensorRadius);
		
		
		// If there is one...
		if (robots.length>0) {
		    // And we have enough bullets, and haven't attacked yet this turn...
		    if (rc.canFireSingleShot()) {
		        // ...Then fire a bullet in the direction of the enemy.
		        rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
		    }
		}
		
		if(robots.length > 0 || trees.length>0) {
		    //MapLocation myLocation = rc.getLocation();
		    MapLocation enemyLocation = robots[0].getLocation();
		    MapLocation treeLocation = trees[0].getLocation();
		    
		    //broadcast enemy
		    rc.broadcast(0,(int)enemyLocation.x);
		    rc.broadcast(1,(int)enemyLocation.y);  
		    //broadcast tree
		    rc.broadcast(2,(int)treeLocation.x);
		    rc.broadcast(3,(int)treeLocation.y); 
		}
		
		// Move randomly
		tryMove(randomDirection());
	}
}

