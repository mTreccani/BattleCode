package examplefuncsplayer;

import battlecode.common.*;

public class TankLogic extends RobotLogic{

	public TankLogic (RobotController rc) throws GameActionException{
		super(rc);
		//setNumTank(+1);
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
	            	if(isDead(birthRound)) setNumTank(-1);
	            	isDead=true;
	            }
	            
                // Clock.yield() fa terminare il round
	            Clock.yield();
	
	        } catch (Exception e) {
	            System.out.println("Tank Exception");
	            e.printStackTrace();
	        }
	    }
	}
}
