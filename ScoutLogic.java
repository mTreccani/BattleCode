package examplefuncsplayer;

import battlecode.common.*;


public class ScoutLogic extends RobotLogic{
	
	Team enemy = rc.getTeam().opponent();
	
	public ScoutLogic (RobotController rc) throws GameActionException{
		super(rc);
		//setNumScout(+1);
	}
	
	@Override
	public void run() throws GameActionException {
		
		int birthRound=rc.getRoundNum();
		boolean isDead=false;
		
        //codice che viene eseguito ogni round
		while(true){
		
            //il try/catch gestisce le eccezioni che altrimenti farebbero scomparire il robot
	        try {
	        	
	        	gameInfo();
	
	        	if(rc.getRoundNum()-birthRound < 70 || (rc.getRoundNum()-birthRound > 300 && rc.getRoundNum()-birthRound <= 350)){
	        		runnerStrategy();
	        	}
	        	else{
	        		exploreStrategy();
	        	}
	        	
	        	if(!isDead){
	        		if(isDead(birthRound)) setNumScout(-1);
	        		whichScoutIsDead();
	        		isDead=true;
	        	}
                // Clock.yield() fa terminare il round
	        	Clock.yield();
	        
	        } catch (Exception e) {
	            System.out.println("Scout Exception");
	            e.printStackTrace();
	        }
		}
	}

	
	/**
	 * controlla quale scout è morto per la tattica solare
	 * @throws GameActionException
	 */
	public void whichScoutIsDead() throws GameActionException {
		if(rc.readBroadcast(SOLAR_1)==rc.getID()) {
			rc.broadcast(SOLAR_1, 0);
		}
		if(rc.readBroadcast(SOLAR_2)==rc.getID()) {
			rc.broadcast(SOLAR_2, 0);
		}
		if(rc.readBroadcast(SOLAR_3)==rc.getID()) {
			rc.broadcast(SOLAR_3, 0);
		}
	}
}

