package examplefuncsplayer;

import battlecode.common.*;

/**
 * classe che richiama le strategie e i metodi di movimento utili per i robot di tipo 
 * soldier
 *
 */
public class SoldierLogic extends RobotLogic{
	
	public SoldierLogic (RobotController rc) throws GameActionException{
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
	   		 	boolean roman = rc.readBroadcast(ROMAN_EMPIRE_1) == rc.getID() || rc.readBroadcast(ROMAN_EMPIRE_2)== rc.getID() || rc.readBroadcast(ROMAN_EMPIRE_3) == rc.getID();
	   		 	boolean tank = rc.readBroadcast(SQUAD_1) == rc.getID() || rc.readBroadcast(SQUAD_2)== rc.getID() || rc.readBroadcast(SQUAD_3) == rc.getID();

	            romanEmpireStrategy();
	            
	            if(!roman && !tank){
		            totalHelpStrategy();
	            }
		   		
		   		if(!rc.readBroadcastBoolean(ENEMY_ARCHON_KILLED)){
		   			
		   			
	            	if(getNumTank()!=0){
		            	tankSquadStrategy();
		            }
		            else if(!rc.canSenseRobot(SQUAD_0) || !rc.canSenseRobot(SQUAD_1) || !rc.canSenseRobot(SQUAD_2) || !rc.canSenseRobot(SQUAD_3)){
	            		rc.broadcast(SQUAD_0, 0);
		        		rc.broadcast(SQUAD_1, 0);
		        		rc.broadcast(SQUAD_2, 0);	
		        		rc.broadcast(SQUAD_3, 0);	
	            	}
	            	
		            if((rc.getRoundNum()>1500 && rc.getRoundNum()<2000) || rc.getRoundNum()>2500) {
		            	vikingStrategy();
		            }
		            else if(!rc.canSenseRobot(VIKING_1) && !rc.canSenseRobot(VIKING_2) && !rc.canSenseRobot(VIKING_3)){
	            		rc.broadcast(VIKING_1, 0);
		        		rc.broadcast(VIKING_2, 0);
		        		rc.broadcast(VIKING_3, 0);	
	            	}
		            
		            if(enemyRobots.length > 0) {
		            	if(!rc.hasAttacked()) tryShoot();
		                MapLocation enemyLocation = enemyRobots[0].getLocation();
		                Direction toEnemy = myLocation.directionTo(enemyLocation);
		                if(!rc.hasMoved()) tryMove(toEnemy);
		            }
	            }
	            else{
	            	killThemAll();
	            }
	            
	            if(!rc.hasMoved()){
	            	tryMove(randomDirection());
	            }

	            if(!isDead){
	            	
	            	if(isDead()){
	            		setNumSoldier(-1);
	            		isDead=true;
	            	}
	            }
	            Clock.yield();
	
	        } catch (Exception e) {
	            System.out.println("Soldier Exception");
	            e.printStackTrace();
	        }
	    }
	}
	
}

