package examplefuncsplayer;

import battlecode.common.*;

public class SoldierLogic extends RobotLogic{
	int birthRound;
	boolean isDead;
	public SoldierLogic (RobotController rc) throws GameActionException{
		super(rc);
		birthRound=rc.getRoundNum();
		isDead = false;
		//setNumSoldier(+1);
	}
	
	@Override
	public void run() throws GameActionException{
		
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
	        	
	            romanEmpireStrategy();
	            
	            if((rc.getRoundNum()>300 && rc.getRoundNum()<500) || (rc.getRoundNum()>1200 && rc.getRoundNum()<1400)) {
	            	vikingStrategy();
	            }
	            else{
	        		exploreStrategy();
	        		rc.broadcast(VIKING_1, 0);
	        		rc.broadcast(VIKING_2, 0);
	        		rc.broadcast(VIKING_3, 0);
	        	}
	            tryShoot();
	            
	            
	            if(!isDead){
	            	if(isDead(birthRound)){
	            		//whichSoldierIsDead();
	            		setNumSoldier(-1);
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
		else if(rc.readBroadcast(VIKING_2)==rc.getID()) {
			rc.broadcast(VIKING_2, 0);
		}
		else if(rc.readBroadcast(VIKING_3)==rc.getID()) {
			rc.broadcast(VIKING_3, 0);
		}
		if(rc.readBroadcast(ROMAN_EMPIRE_1)==rc.getID()) {
			rc.broadcast(ROMAN_EMPIRE_1, 0);
		}
		else if(rc.readBroadcast(ROMAN_EMPIRE_2)==rc.getID()) {
			rc.broadcast(ROMAN_EMPIRE_2, 0);
		}
		else if(rc.readBroadcast(ROMAN_EMPIRE_3)==rc.getID()) {
			rc.broadcast(ROMAN_EMPIRE_3, 0);
		}
	}
}

