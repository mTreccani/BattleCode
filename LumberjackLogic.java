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
	       
        //codice che viene eseguito ogni round
        while(true){
            	
            //il try/catch gestisce le eccezioni che altrimenti farebbero scomparire il robot
        	try {

                gameInfo();
       
                if(rc.readBroadcastBoolean(FARM_ZONE) && (rc.readBroadcast(FARMING_LUMBERJACK)==0 || rc.readBroadcast(FARMING_LUMBERJACK)==rc.getID())){
                	rc.broadcast(FARMING_LUMBERJACK, rc.getID());
                	farmStrategy();
                }
                
                if(enemyRobots.length > 0) {
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
                // Clock.yield() fa terminare il round
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
	}
}
