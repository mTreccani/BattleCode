package examplefuncsplayer;

import battlecode.common.*;

public class SoldierLogic extends RobotLogic{
	
	public SoldierLogic (RobotController rc) throws GameActionException{
		super(rc);
		//setNumSoldier(+1);
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
	        	/*if(getNumSoldier()>3){
	            	if(rc.readBroadcast(ROMAN_EMPIRE_1)==0) {
	            		rc.broadcast(VIKING_ONE, rc.getID());
	            		System.out.println("FIRST ROMAN  " + rc.getID());
	            	}
	            	else if (rc.readBroadcast(ROMAN_EMPIRE_2)==0 && rc.readBroadcast(ROMAN_EMPIRE_1)!=rc.getID()){
	            		rc.broadcast(ROMAN_EMPIRE_2, rc.getID());
	            		System.out.println("SECOND ROMAN  " + rc.getID());
	            	}
	            	else if (rc.readBroadcast(ROMAN_EMPIRE_3)==0 && rc.readBroadcast(ROMAN_EMPIRE_1)!=rc.getID() && rc.readBroadcast(ROMAN_EMPIRE_2)!=rc.getID()) {
	            		rc.broadcast(ROMAN_EMPIRE_3, rc.getID());
	            		System.out.println("THIRD ROMAN  " + rc.getID());
	            	}
	            }*/
	        	
	        	totalHelpStrategy();
	        	
	           
            	if(rc.readBroadcast(VIKING_1)==0) {
            		rc.broadcast(VIKING_1, rc.getID());
            		System.out.println("FIRST VIKING  " + rc.getID());
            	}
            	else if (rc.readBroadcast(VIKING_2)==0 && rc.readBroadcast(VIKING_1)!=rc.getID()){
            		rc.broadcast(VIKING_2, rc.getID());
            		System.out.println("SECOND VIKING  " + rc.getID());
            	}
            	else if (rc.readBroadcast(VIKING_3)==0 && rc.readBroadcast(VIKING_1)!=rc.getID() && rc.readBroadcast(VIKING_2)!=rc.getID()) {
            		rc.broadcast(VIKING_3, rc.getID());
            		System.out.println("THIRD VIKING  " + rc.getID());
            	}
            
            
	            
	            if(rc.readBroadcast(VIKING_1)==rc.getID() || rc.readBroadcast(VIKING_2)==rc.getID() || rc.readBroadcast(VIKING_3)==rc.getID()){
	            	vikingStrategy();
	            }
	            
	            tryShoot();
	            
	            if(enemyRobots.length > 0) {
	                MapLocation enemyLocation = enemyRobots[0].getLocation();
	                Direction toEnemy = myLocation.directionTo(enemyLocation);
	                tryMove(toEnemy);
	            }
	            else{
	                	tryMove(randomDirection());
	            }
	            
	            if(!isDead){
	            	
	            	if(isDead(birthRound)){
	            		
	            		setNumSoldier(-1);
	            	    whichSoldierIsDead();
	            		isDead=true;
	            	}
	            }
	            
                // Clock.yield() fa terminare il round
	            Clock.yield();
	
	        } catch (Exception e) {
	            System.out.println("Soldier Exception");
	            e.printStackTrace();
	        }
	    }
	}

	/**
	 * controlla quale scout è morto per la tattica solare e impero romano
	 * @throws GameActionException
	 */
	public void whichSoldierIsDead() throws GameActionException {
		
		if(rc.readBroadcast(VIKING_1)==rc.getID()) {
			rc.broadcast(VIKING_1, 0);
		}
		if(rc.readBroadcast(VIKING_2)==rc.getID()) {
			rc.broadcast(VIKING_2, 0);
		}
		if(rc.readBroadcast(VIKING_3)==rc.getID()) {
			rc.broadcast(VIKING_3, 0);
		}
		if(rc.readBroadcast(ROMAN_EMPIRE_1)==rc.getID()) {
			rc.broadcast(ROMAN_EMPIRE_1, 0);
		}
		if(rc.readBroadcast(ROMAN_EMPIRE_2)==rc.getID()) {
			rc.broadcast(ROMAN_EMPIRE_2, 0);
		}
		if(rc.readBroadcast(ROMAN_EMPIRE_3)==rc.getID()) {
			rc.broadcast(ROMAN_EMPIRE_3, 0);
		}
	}
}

