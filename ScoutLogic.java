package examplefuncsplayer;

import battlecode.common.*;


public class ScoutLogic extends RobotLogic{
	
	Team enemy = rc.getTeam().opponent();
	
	public ScoutLogic (RobotController rc) throws GameActionException{
		super(rc);
	}
	
	@Override
	public void run() throws GameActionException {
		
		int birthRound=rc.getRoundNum();
		boolean isDead=false;
		
		while(true){
		
	        try {
	        	
	        	boolean timeToRun=rc.getRoundNum()-birthRound < 40 || (rc.getRoundNum()-birthRound > 300 && rc.getRoundNum()-birthRound <= 340);
	        	
	        	gameInfo();
	        	trySenseEnemyArchon();

	        	
	        	if(timeToRun){
	        		runnerStrategy();
	        	}
	        	else{
	        		exploreStrategy();
	        	}
	        	
	        	
	        	if(!isDead){
	        		if(isDead()) setNumScout(-1);
	        		whichScoutIsDead();
	        		isDead=true;
	        	}
	        	
	        	Clock.yield();
	        
	        } catch (Exception e) {
	            System.out.println("Scout Exception");
	            e.printStackTrace();
	            Clock.yield();
	        }
		}
	}

	/**
	 * metodo che controlla se lo scout morto occupava un canale broadcast,
	 * in caso lo libera
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

