package examplefuncsplayer;

import battlecode.common.*;

public class SoldierLogic extends RobotLogic{
	
	public SoldierLogic (RobotController rc) throws GameActionException{
		super(rc);
		//setNumSoldier(+1);
	}
	
	@Override
	public void run() throws GameActionException{
		
		boolean isDead=false;
		
		
		while(true){
		   
	        try {
	
	            gameInfo();
	        	
	            romanEmpireStrategy();
	            
	            if(getNumTank()>0){
	            	tankSquadStrategy();
	            }
	            
	            if((rc.getRoundNum()>500 && rc.getRoundNum()<800) || (rc.getRoundNum()>1400 && rc.getRoundNum()<1700)) {
	            	vikingStrategy();
	            }
	            else{
	        		rc.broadcast(VIKING_1, 0);
	        		rc.broadcast(VIKING_2, 0);
	        		rc.broadcast(VIKING_3, 0);
	        		totalHelpStrategy();
	        	}
	            
	            if(enemyRobots.length > 0) {
	            	if(!attacked) tryShoot();
	                MapLocation enemyLocation = enemyRobots[0].getLocation();
	                Direction toEnemy = myLocation.directionTo(enemyLocation);
	                if(!moved) tryMove(toEnemy);
	            }
	            else{
                	if(!moved) tryMove(randomDirection());
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

