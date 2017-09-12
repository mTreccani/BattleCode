package examplefuncsplayer;

import battlecode.common.*;


public class ScoutLogic extends RobotLogic{
	
	Team enemy = rc.getTeam().opponent();
	boolean isAlive;
	int birthRound;
	
	public ScoutLogic (RobotController rc) throws GameActionException{
		super(rc);
		birthRound=rc.getRoundNum();
		isAlive = true;
	}
	
	@Override
	public void run() throws GameActionException {
		
		while(true){
			
	        // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
	        try {
	        	
	        	gameInfo();
	        	
	        	basicScout();
	        	
	        	if(isAlive){
	            	if(isDead(birthRound)){
		            	setNumScout(-1);
		            	isAlive = false;
		            }	
	            }
	
	        } catch (Exception e) {
	            System.out.println("Soldier Exception");
	            e.printStackTrace();
	            Clock.yield();
	        }
		}
	}

	public void basicScout() throws GameActionException {
		// See if there are any nearby enemy robots

		shoot();
		
		// Move randomly
		tryMove(randomDirection());
	}
}

