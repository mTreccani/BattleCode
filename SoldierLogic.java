package examplefuncsplayer;

import battlecode.common.*;

public class SoldierLogic extends RobotLogic{
	
	public SoldierLogic (RobotController rc) throws GameActionException{
		super(rc);
	}
	
	@Override
	public void run() throws GameActionException{
		
		boolean isDead=false;
		boolean timeForVikings=(rc.getRoundNum()>1200 && rc.getRoundNum()<1800) || rc.getRoundNum()>2500;
		
		while(true){
		   
	        try {
	
	            gameInfo();
	            trySenseEnemyArchon();
	            enemyArchonKilled();
	            boolean roman = rc.readBroadcast(ROMAN_EMPIRE_1) == rc.getID() || rc.readBroadcast(ROMAN_EMPIRE_2)== rc.getID() || rc.readBroadcast(ROMAN_EMPIRE_3) == rc.getID();
	            boolean tank = rc.readBroadcast(SQUAD_1) == rc.getID() || rc.readBroadcast(SQUAD_2)== rc.getID() || rc.readBroadcast(SQUAD_3) == rc.getID();
	            boolean viking = rc.readBroadcast(VIKING_1) == rc.getID() || rc.readBroadcast(VIKING_2)== rc.getID() || rc.readBroadcast(VIKING_3) == rc.getID();


	            if(rc.readBroadcast(ROMAN_EMPIRE_1)==0) {
		         	rc.broadcast(ROMAN_EMPIRE_1, rc.getID());
		        }
		        else if (rc.readBroadcast(ROMAN_EMPIRE_2)==0 && rc.readBroadcast(ROMAN_EMPIRE_1)!=rc.getID()){
		         	rc.broadcast(ROMAN_EMPIRE_2, rc.getID());
		        }
		        else if (rc.readBroadcast(ROMAN_EMPIRE_3)==0 && rc.readBroadcast(ROMAN_EMPIRE_1)!=rc.getID() && rc.readBroadcast(ROMAN_EMPIRE_2)!=rc.getID()) {
		         	rc.broadcast(ROMAN_EMPIRE_3, rc.getID());
		        }
	            
		   		if(rc.readBroadcast(VIKING_1)==0 && !roman && !tank) {
		          	rc.broadcast(VIKING_1, rc.getID());
		        }
		        else if (rc.readBroadcast(VIKING_2)==0 && rc.readBroadcast(VIKING_1)!=rc.getID() && !roman && !tank){
		          	rc.broadcast(VIKING_2, rc.getID());
		        }
		        else if (rc.readBroadcast(VIKING_3)==0 && rc.readBroadcast(VIKING_1)!=rc.getID() && rc.readBroadcast(VIKING_2)!=rc.getID() && !roman && !tank) {
		        	rc.broadcast(VIKING_3, rc.getID());
		        }
		   		
		   		if(rc.readBroadcast(SQUAD_1)==0 && !roman && !viking) {
		          	rc.broadcast(SQUAD_1, rc.getID());
		        }
		        else if (rc.readBroadcast(SQUAD_2)==0 && rc.readBroadcast(SQUAD_1)!=rc.getID() && !roman && !viking){
		          	rc.broadcast(SQUAD_2, rc.getID());
		        }
		        else if (rc.readBroadcast(SQUAD_3)==0 && rc.readBroadcast(SQUAD_1)!=rc.getID() && rc.readBroadcast(SQUAD_2)!=rc.getID() && !roman && !viking) {
		        	rc.broadcast(SQUAD_3, rc.getID());
		        }
	            
		   		
		   		if(roman && !viking && !tank){
	            	romanEmpireStrategy();
	            }
		   		else if(!rc.readBroadcastBoolean(ENEMY_ARCHON_KILLED)){
	            	
	            	if(getNumTank()!=0 && tank){
		            	tankSquadStrategy();
		            }
		            else{
		            	rc.broadcast(SQUAD_1, 0);
		        		rc.broadcast(SQUAD_2, 0);
		        		rc.broadcast(SQUAD_3, 0);
		            }
		            
		            if(timeForVikings) {
		            	vikingStrategy();
		            }
		            else{
		        		rc.broadcast(VIKING_1, 0);
		        		rc.broadcast(VIKING_2, 0);
		        		rc.broadcast(VIKING_3, 0);
		        		totalHelpStrategy();
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

