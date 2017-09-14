package examplefuncsplayer;

import battlecode.common.*;

public class TankLogic extends RobotLogic{

	public TankLogic (RobotController rc) throws GameActionException{
		super(rc);
	}
	
	@Override
	public void run() throws GameActionException{
		
		boolean isDead=false;
	
        while(true){
        		
	        try {
	            
	            gameInfo();
	            
                tankSquadStrategy();
                
	            if(enemyRobots.length > 0) {
	            	tryShoot();
	                MapLocation enemyLocation = enemyRobots[0].getLocation();
	                Direction toEnemy = myLocation.directionTo(enemyLocation);
                	if(!moved) tryMove(toEnemy);
	            }
	            else{
	            	totalHelpStrategy();
	            }
	            
	            if(rc.getRoundNum()>2000){
                	MapLocation[] enemyArchon = rc.getInitialArchonLocations(enemy);
                	Direction toEnemyArchon = myLocation.directionTo(enemyArchon[0]);
                	if(!moved) tryMove(toEnemyArchon);
                }
	            
	            if(!moved) tryMove(randomDirection());
	            
	            if(!isDead){
	            	if(isDead()) {
	            		setNumTank(-1);
	            		isDead=true;
	            		if(rc.readBroadcast(SQUAD_0)==rc.getID()) rc.broadcast(SQUAD_0, 0);
	            	}
	            }
	            
	            Clock.yield();
	
	        } catch (Exception e) {
	            System.out.println("Tank Exception");
	            e.printStackTrace();
	        }
	    }
	}
}
