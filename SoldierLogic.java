package examplefuncsplayer;

import battlecode.common.*;

public class SoldierLogic extends RobotLogic{
	
	Team enemy = rc.getTeam().opponent();
	int birthRound;
	boolean isAlive;
	
	public SoldierLogic (RobotController rc){
		super(rc);
		birthRound=rc.getRoundNum();
		isAlive = true;
		
	}
	
	@Override
	public void run() throws GameActionException{
		
		while(true){
			
		    // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
	        try {

	            // See if there are any nearby enemy robots
	            
	            int id1 = rc.readBroadcastInt(IMPERO_ROMANO_1);	            
				int id2 = rc.readBroadcastInt(IMPERO_ROMANO_2);
				int id3 = rc.readBroadcastInt(IMPERO_ROMANO_3);
				if(id1 == 0 && id1 != rc.getID()){
					rc.broadcastInt(IMPERO_ROMANO_1, rc.getID());
				}
				else if(id2 == 0 && id1 != rc.getID() && id2 != rc.getID()){
					rc.broadcastInt(IMPERO_ROMANO_2, rc.getID());
				}
				else if(id3 == 0 && id1 != rc.getID() && id2 != rc.getID() && id3 != rc.getID()){
					rc.broadcastInt(IMPERO_ROMANO_3, rc.getID());
				}
				
	            imperoRomano();
	            
	            shoot();
	            
	            if(isAlive){
	            	if(isDead(birthRound)){
		            	setNumSoldier(-1);
		            	isAlive = false;
		            }	
	            }
	            
	            
	            // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
	            Clock.yield();
	
	        } catch (Exception e) {
	            System.out.println("Soldier Exception");
	            e.printStackTrace();
	        }
	    }
	}
	
}
