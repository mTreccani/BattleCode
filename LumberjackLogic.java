package examplefuncsplayer;

import battlecode.common.*;


public class LumberjackLogic extends RobotLogic {

	public LumberjackLogic (RobotController rc){
		super(rc);
	}
	
	@Override
	public void run() throws GameActionException{
	
		int birthRound=rc.getRoundNum();
    	boolean isDead=false;
    	setNumLumberjack(+1);
	       
        // The code you want your robot to perform every round should be in this loop
        
            while(true){
            	
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
                RobotInfo[] robots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);
                TreeInfo[] trees = rc.senseNearbyTrees(RobotType.LUMBERJACK.sensorRadius);
                
                
                if(rc.readBroadcast(FARMING_LUMBERJACK)==0 || rc.readBroadcast(FARMING_LUMBERJACK)==rc.getID()){
                	rc.broadcast(FARMING_LUMBERJACK, rc.getID());
                	farmStrategy();
                }
                
                if(robots.length > 0 && !rc.hasAttacked()) {
                    // Use strike() to hit all nearby robots!
                    rc.strike();
                } else {
                    // No close robots, so search for robots within sight radius
                    robots = rc.senseNearbyRobots(-1,enemy);

                    // If there is a robot, move towards it
                    if(robots.length > 0) {
                        MapLocation myLocation = rc.getLocation();
                        MapLocation enemyLocation = robots[0].getLocation();
                        Direction toEnemy = myLocation.directionTo(enemyLocation);

                        tryMove(toEnemy);
                    } else if(trees.length > 0 && robots.length == 0){
                    	MapLocation myLocation = rc.getLocation();
                        MapLocation treeLocation = trees[0].getLocation();
                        Direction toTree = myLocation.directionTo(treeLocation);

                        tryMove(toTree);
                        if(rc.canChop(treeLocation)){
                        	rc.chop(treeLocation);
                        }
                    } else {
                        // Move Randomly
                        tryMove(randomDirection());
                    }
                }

                if(!isDead){
                	if(isDead(birthRound)) setNumLumberjack(-1);
                	if(rc.readBroadcast(FARMING_LUMBERJACK)==rc.getID()) rc.broadcast(FARMING_LUMBERJACK, 0);
                	isDead=true;
                }
                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
	}
}
