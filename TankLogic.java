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
	            trySenseEnemyArchon();
	            enemyArchonKilled();
	            
	            if(!rc.readBroadcastBoolean(ENEMY_ARCHON_KILLED)){
	                tankSquadStrategy();
	                
		            if(enemyRobots.length > 0) {
		            	if(!attacked) tryShoot();
		                MapLocation enemyLocation = enemyRobots[0].getLocation();
		                Direction toEnemy = myLocation.directionTo(enemyLocation);
	                	if(!moved) tryMove(toEnemy);
		            }
		            else{
		            	totalHelpStrategy();
		            }
		            
		            if(rc.getRoundNum()>2000){
	                	if(!moved) tryMove(toEnemyArchon);
	                }
		            
		            if(!moved) tryMove(randomDirection());
	            }
	            else{
	            	killThemAll();
	            }
	            
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
