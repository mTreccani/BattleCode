package examplefuncsplayer;

import battlecode.common.*;


public class LumberjackLogic extends RobotLogic {

	public LumberjackLogic (RobotController rc)throws GameActionException{
		super(rc);
	}
	
	@Override
	public void run() throws GameActionException{
	
    	boolean isDead=false;
	       
            while(true){
            	
            try {

                gameInfo();
                trySenseEnemyArchon();
       
                if(rc.readBroadcastBoolean(FARM_ZONE) && (rc.readBroadcast(FARMING_LUMBERJACK)==0 || rc.readBroadcast(FARMING_LUMBERJACK)==rc.getID())){
                	rc.broadcast(FARMING_LUMBERJACK, rc.getID());
                	farmStrategy();
                }
                
                if(enemyRobots.length > 0) {
                	MapLocation enemyLocation = enemyRobots[0].getLocation();
                	Direction toEnemy = myLocation.directionTo(enemyLocation);
                	if(!moved) tryMove(toEnemy);
                    rc.strike();  
                }
                else if(trees.length > 0){
            
                    MapLocation treeLocation = trees[0].getLocation();
                    Direction toTree = myLocation.directionTo(treeLocation);
                    if(!moved) tryMove(toTree);
                    if(rc.canChop(treeLocation)){
                    	rc.chop(treeLocation);
                    }
                }
                if(!moved) tryMove(randomDirection());

                if(!isDead){
                	if(isDead()){
                		setNumLumberjack(-1);
                		if(rc.readBroadcast(FARMING_LUMBERJACK)==rc.getID()) rc.broadcast(FARMING_LUMBERJACK, 0);
                		isDead=true;
                	}
                }
                
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
	}
}
