package examplefuncsplayer;
import battlecode.common.*;

public class GardenerLogic extends RobotLogic{
	
	private static final int NO_SCOUT_NEEDED=1500;
	private static final int NO_TREES=0;
	private static final int MAX_TREES=3;
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
		int birthRound=rc.getRoundNum();
		
		while(true){
			
			try {
				
				Direction dir = randomDirection();
				
				gameInfo();
				trySenseEnemyArchon();
			    
				if(rc.readBroadcastBoolean(ENEMY_ARCHON_KILLED)) winningStrategy();
			    
				
		    	if (trees.length > NO_TREES && !isInDanger() && rc.readBroadcast(FARMING_GARDENER)!=rc.getID()){
	            	MapLocation treeLocation=trees[0].getLocation();
	            	Direction toTree=myLocation.directionTo(treeLocation);
	            	if(!moved) tryMove(toTree);
	            	if(rc.canWater(treeLocation)){
	            		rc.water(treeLocation);
	            	}
	            	if(getNumLumberjack()==0 && rc.canBuildRobot(RobotType.LUMBERJACK, randomDirection())){
	            		rc.buildRobot(RobotType.LUMBERJACK, dir);
	            		setNumLumberjack(+1);
	            	}
	            }
				
				if(isInDanger()){
					rc.broadcastFloat(ENEMY_FOUND_X, myLocation.x);
					rc.broadcastFloat(ENEMY_FOUND_Y, myLocation.y);
					matrixStrategy();
				}
				
	            if(rc.readBroadcastBoolean(FARM_ZONE) && (rc.readBroadcast(FARMING_GARDENER)==0 || rc.readBroadcast(FARMING_GARDENER)==rc.getID())){
                	rc.broadcast(FARMING_GARDENER, rc.getID());
                	farmStrategy();
                }
	            
	            if(rc.getRoundNum()-birthRound<130 && rc.canPlantTree(dir) && trees.length<MAX_TREES){
	            	rc.plantTree(dir);
	            }
	            else{
	            
		            if(shouldBuildTank()) {
		                rc.buildRobot(RobotType.TANK, dir);
		                setNumTank(+1);
		            }
		            
		            if(rc.readBroadcastBoolean(FARM_ZONE)  && getNumLumberjack()<=1 && rc.canBuildRobot(RobotType.LUMBERJACK, randomDirection())){
	            		rc.buildRobot(RobotType.LUMBERJACK, dir);
	            		setNumLumberjack(+1);
	            	}
		            
		            if (shouldBuildScout()) {
		                rc.buildRobot(RobotType.SCOUT, dir);
		                setNumScout(+1);
		            } 
		            
		            if (shouldBuildSoldier()) {
		                rc.buildRobot(RobotType.SOLDIER, dir);
		                setNumSoldier(+1);
		            } 
		           
		            if (/*rc.getTeamBullets()>TREE_BULLET && trees.length<=MAX_TREES*/ rc.getRoundNum()%100<5 && rc.canPlantTree(dir)){
		            	rc.plantTree(dir);
		            }
		            
		            if (shouldBuildLumberjack()) {
		                rc.buildRobot(RobotType.LUMBERJACK, dir);
		                setNumLumberjack(+1);
		            }
                }
		          
			    if(!moved) tryMove(dir);
			       
			    
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
	 * Decides when it's possible to build Scouts
	 * @return  TRUE: When it's possible FALSE: When it's not
	 */
	public boolean shouldBuildScout() throws GameActionException{
		return (getNumScout()<= MAX_SCOUTS && rc.getRoundNum()< NO_SCOUT_NEEDED && getNumScout()<=getNumSoldier() && rc.canBuildRobot(RobotType.SCOUT, randomDirection()));
	}

	/**
	 * Decides when it's possible to build Soldiers
	 * @return  TRUE: When it's possible FALSE: When it's not
	 */
	public boolean shouldBuildSoldier() throws GameActionException {
		return (rc.canBuildRobot(RobotType.SOLDIER, randomDirection()) && (isInDanger() || (getNumSoldier()<= MAX_SOLDIERS && rc.getTeamBullets()> BULLETS_NEEDED)));
	}
	
	/**
	 * Decides when it's possible to build Lumberjacks
	 * @return  TRUE: When it's possible FALSE: When it's not
	 */
	public boolean shouldBuildLumberjack() throws GameActionException {
		return (getNumLumberjack()<=MAX_LUMBERJACKS && (trees.length>=TREES_LUMBERJACK || (rc.getRoundNum()>ROUND_LUMBERJACK && getNumLumberjack()==MIN_LUMBERJACKS)) && rc.canBuildRobot(RobotType.LUMBERJACK, randomDirection()));
	}
	
	/**
	 * Decides when it's possible to build Tanks
	 * @return  TRUE: When it's possible FALSE: When it's not
	 */
	public boolean shouldBuildTank() throws GameActionException {
		return ((rc.getRobotCount()>=21 || rc.getRoundNum()>ROUND_TANK) && getNumTank()== MIN_TANKS && rc.canBuildRobot(RobotType.TANK, randomDirection()));
	}
	
	public void winningStrategy() throws GameActionException{  
		if (rc.getTeamBullets() >= (GameConstants.VICTORY_POINTS_TO_WIN - rc.getTeamVictoryPoints())* rc.getVictoryPointCost()) {
			rc.donate(rc.getTeamBullets());
		}
	}

}