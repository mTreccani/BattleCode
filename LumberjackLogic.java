package examplefuncsplayer;

import battlecode.common.*;


public class LumberjackLogic extends RobotLogic {

	public LumberjackLogic (RobotController rc)throws GameActionException{
		super(rc);
		//setNumLumberjack(+1);
	}
	
	@Override
	public void run() throws GameActionException{
	
		int birthRound=rc.getRoundNum();
    	boolean isDead=false;
	       
        // The code you want your robot to perform every round should be in this loop
        
            while(true){
            	
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
                gameInfo();
       
                if(rc.readBroadcastBoolean(FARM_ZONE) && (rc.readBroadcast(FARMING_LUMBERJACK)==0 || rc.readBroadcast(FARMING_LUMBERJACK)==rc.getID())){
                	rc.broadcast(FARMING_LUMBERJACK, rc.getID());
                	farmStrategy();
                }
                
                if(enemyRobots.length > 0) {
                    // Use strike() to hit all nearby robots!
                	MapLocation enemyLocation = enemyRobots[0].getLocation();
                	Direction toEnemy = myLocation.directionTo(enemyLocation);
                	tryMove(toEnemy);
                    rc.strike();
                    
                }
                else if(trees.length > 0 && enemyRobots.length == 0){
            
                    MapLocation treeLocation = trees[0].getLocation();
                    Direction toTree = myLocation.directionTo(treeLocation);
                    tryMove(toTree);
                    if(rc.canChop(treeLocation)){
                    	rc.chop(treeLocation);
                    }
                }
                if(!rc.hasMoved()) tryMove(randomDirection());

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
