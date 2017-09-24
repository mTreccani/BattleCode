package examplefuncsplayer;
import battlecode.common.*;

/**
 * classe che descrive i metodi di creazione degli altri tipi di robot
 * 
 */
public class GardenerLogic extends RobotLogic{
	
	private static final int NO_SCOUT_NEEDED=1500;
	private static final int NO_TREES=0;
	private static final int MAX_TREES=1;
	private static final int MAX_SCOUTS=3;
	private static final int MAX_SOLDIERS=10;
	private static final int MAX_LUMBERJACKS=2;
	private static final int MIN_LUMBERJACKS=0;
	private static final int TREES_LUMBERJACK=2;
	private static final int ROUND_LUMBERJACK=200;
	private static final int ROUND_TANK=1500;
	private static final int MIN_TANKS=0;
	private static final int BULLETS_NEEDED=200;
	
	public GardenerLogic (RobotController rc) throws GameActionException{
		super(rc);
	}
	
	@Override
	public void run() throws GameActionException{
		
		boolean isDead=false;
		
		while(true){
			
			try {
				
				Direction dir = randomDirection();

				gameInfo();
				trySenseEnemyArchon();
			    
				
				if(rc.readBroadcastBoolean(FARM_ZONE) && (rc.readBroadcast(FARMING_GARDENER)==0 || rc.readBroadcast(FARMING_GARDENER)==rc.getID())){
                	rc.broadcast(FARMING_GARDENER, rc.getID());
                	farmStrategy();
                }
				
				if(rc.readBroadcastBoolean(ENEMY_ARCHON_KILLED) || rc.getTeamBullets() >= 3500 || rc.getRoundNum()>2750){
				    winningStrategy();
			    }
				
		    	if (allyTrees.length > NO_TREES && !isInDanger() && rc.readBroadcast(FARMING_GARDENER)!=rc.getID()){
	            	MapLocation treeLocation=allyTrees[0].getLocation();
	            	Direction toTree=myLocation.directionTo(treeLocation);
	            	if(!rc.hasMoved()) tryMove(toTree);
	            	if(rc.canWater(treeLocation)){
	            		rc.water(treeLocation);
	            	}
	            	if(getNumLumberjack()<MAX_LUMBERJACKS && rc.canBuildRobot(RobotType.LUMBERJACK, dir)){
	            		rc.buildRobot(RobotType.LUMBERJACK, dir);
	            		setNumLumberjack(+1);
	            	}
	            }
				
				if(isInDanger()){
					rc.broadcastFloat(ENEMY_FOUND_X, myLocation.x);
					rc.broadcastFloat(ENEMY_FOUND_Y, myLocation.y);
					matrixStrategy();
				}
				
	            if(rc.getRoundNum()%100<5 && rc.canPlantTree(dir) && trees.length<=MAX_TREES){
	            	rc.plantTree(dir);
	            }
	            else{
		            if(shouldBuildTank()) {
		                rc.buildRobot(RobotType.TANK, dir);
		                setNumTank(+1);
		            }
		            
		            else if(rc.readBroadcastBoolean(FARM_ZONE)  && getNumLumberjack()<MAX_LUMBERJACKS && rc.canBuildRobot(RobotType.LUMBERJACK, dir)){
	            		rc.buildRobot(RobotType.LUMBERJACK, dir);
	            		setNumLumberjack(+1);
	            	}
		            
		            else if (shouldBuildScout()) {
		                rc.buildRobot(RobotType.SCOUT, dir);
		                setNumScout(+1);
		            } 
		            
		            else if (shouldBuildSoldier()) {
		                rc.buildRobot(RobotType.SOLDIER, dir);
		                setNumSoldier(+1);
		            } 
		            
		            
                }
 				
			    if(!rc.hasMoved()){
			    	if(!rc.hasMoved()) tryMove(dir);
			    }
			       
			    
	            if(!isDead){
	            	if(isDead()){ 
	            		setNumGardener(-1);
	            		if(rc.readBroadcast(FARMING_GARDENER)==rc.getID()) rc.broadcast(FARMING_GARDENER, 0);
	            		isDead=true;
	            	}
	            }
	
	            // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
	            Clock.yield();
	        } catch (Exception e) {
	        	System.out.println("Gardener Exception");
	        	e.printStackTrace();
	        }	
		}		
	}
	
	/**
	  * metodo che stabilisce se è il momento di produrre uno scout
	  * @return TRUE se è il momento, FALSE altrimenti
	  * @throws GameActionException
	  */
	public boolean shouldBuildScout() throws GameActionException{
		return (getNumScout()<= MAX_SCOUTS && rc.getRoundNum()< NO_SCOUT_NEEDED && getNumScout()<=getNumSoldier() && rc.canBuildRobot(RobotType.SCOUT, randomDirection()));
	}

	/**
	  * metodo che stabilisce se è il momento di produrre un soldier
	  * @return TRUE se è il momento, FALSE altrimenti
	  * @throws GameActionException
	  */
	public boolean shouldBuildSoldier() throws GameActionException {
		return (rc.canBuildRobot(RobotType.SOLDIER, randomDirection()) && (isInDanger() || (getNumSoldier()<= MAX_SOLDIERS) && rc.getTeamBullets()> BULLETS_NEEDED));
	}
	
	/**
	  * metodo che stabilisce se è il momento di produrre un lumberjack
	  * @return TRUE se è il momento, FALSE altrimenti
	  * @throws GameActionException
	  */
	public boolean shouldBuildLumberjack() throws GameActionException {
		return (getNumLumberjack()<=MAX_LUMBERJACKS && (trees.length>=TREES_LUMBERJACK || (rc.getRoundNum()>ROUND_LUMBERJACK && getNumLumberjack()==MIN_LUMBERJACKS)) && rc.canBuildRobot(RobotType.LUMBERJACK, randomDirection()));
	}
	
	/**
	  * metodo che stabilisce se è il momento di produrre un tank
	  * @return TRUE se è il momento, FALSE altrimenti
	  * @throws GameActionException
	  */
	public boolean shouldBuildTank() throws GameActionException {
		return ((rc.getRobotCount()>=15 || rc.getRoundNum()>ROUND_TANK) && getNumTank()== MIN_TANKS && rc.canBuildRobot(RobotType.TANK, randomDirection()));
	}
	
	/**
	 * tattica che consiste nel vendere proiettili per accumulare
	 * punti vittoria, invece che piantare o produrre unità
	 * @throws GameActionException
	 */
	public void winningStrategy() throws GameActionException{  
		if (rc.getTeamBullets() >= 200) {
			rc.donate(rc.getTeamBullets());
		}
	}

}