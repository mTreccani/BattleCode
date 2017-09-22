package examplefuncsplayer;

import battlecode.common.*;

public class TankLogic extends RobotLogic{

	public TankLogic (RobotController rc) throws GameActionException{
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
	            

	            if(!rc.readBroadcastBoolean(ENEMY_ARCHON_KILLED)){

	            	tankSquadStrategy();

	            }
	            else{
	            	killThemAll();
	            }
	            
	            if(!rc.hasMoved()){
	            	tryMove(randomDirection());
	            }
	            
	            if(!isDead){
	            	if(isDead()) {
	            		setNumTank(-1);
	            		isDead=true;
	            		if(rc.readBroadcast(SQUAD_0)==rc.getID()) rc.broadcast(SQUAD_0, 0);
	            	}
	            }
	            
	            Clock.yield();
	
	        } catch (Exception e) {
	            System.out.println("Tank Exception");
	            e.printStackTrace();
	        }
	    }
	}
}
