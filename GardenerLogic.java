package examplefuncsplayer;
import battlecode.common.*;

public class GardenerLogic extends RobotLogic{
	private static final int NO_SCOUT_NEEDED=1500;
	private static final int NO_TREES=0;
	private static final int TREEBULLET=100;
	private static final int MAXTREES=4;
	private static final int MAXSCOUTS=3;
	private static final int MAXSOLDIERS=5;
	private static final int MAXLUMBERJACKS=3;
	private static final int MINLUMBERJACKS=0;
	private static final int TREESLUMBERJACK=2;
	private static final int ROUNDLUMBERJACK=200;
	private static final int ROUNDTANK=600;
	private static final int MINTANKS=0;
	
	public float distanceFromArchon;
	public MapLocation lastKnownArchonLocation;
	
	public GardenerLogic (RobotController rc) throws GameActionException{
		super(rc);
		//setNumGardener(+1);
	}
	
	@Override
	public void run() throws GameActionException{
		
		int birthRound=rc.getRoundNum();
		boolean isDead=false;
		
		while(true){
			
			try {
	            
				gameInfo();
				
				/*lastKnownArchonLocation= new MapLocation(rc.readBroadcastFloat(ARCHON_LOCATION_X), rc.readBroadcastFloat(ARCHON_LOCATION_Y));
	            distanceFromArchon=myLocation.distanceTo(lastKnownArchonLocation);
	            
	            if(distanceFromArchon>25 && getNumGardener()<2){
	            	Direction toMyArchon = myLocation.directionTo(lastKnownArchonLocation);
	            	tryMove(toMyArchon);
	            }*/
	            
				if (trees.length > NO_TREES && !isInDanger() && rc.readBroadcast(FARMING_GARDENER)!=rc.getID()){
	            	MapLocation treeLocation=trees[0].getLocation();
	            	Direction toTree=myLocation.directionTo(treeLocation);
	            	tryMove(toTree);
	            	if(rc.canWater(treeLocation)){
	            		rc.water(treeLocation);
	            	}
	            }
				if(isInDanger()){
					rc.broadcastFloat(I_HAVE_BEEN_HIT_X, myLocation.x);
					rc.broadcastFloat(I_HAVE_BEEN_HIT_Y, myLocation.y);
					matrixStrategy();
				}
				
	            if(rc.readBroadcastBoolean(FARM_ZONE) && (rc.readBroadcast(FARMING_GARDENER)==0 || rc.readBroadcast(FARMING_GARDENER)==rc.getID())){
                	rc.broadcast(FARMING_GARDENER, rc.getID());
                	farmStrategy();
                }
	           
	            Direction dir = randomDirection();
	            
	            if(getNumLumberjack()<1){
	            	rc.canBuildRobot(RobotType.LUMBERJACK, randomDirection());
	            	rc.buildRobot(RobotType.LUMBERJACK, dir);
                    setNumLumberjack(+1);
	            }
	            if (shouldBuildSoldier()) {
	                rc.buildRobot(RobotType.SOLDIER, dir);
	                setNumSoldier(+1);
	            } 
	            if (shouldBuildScout()) {
	                rc.buildRobot(RobotType.SCOUT, dir);
	                setNumScout(+1);
	            } 
	            if (shouldBuildLumberjack()) {
	                rc.buildRobot(RobotType.LUMBERJACK, dir);
	                setNumLumberjack(+1);
	            }
	            if (shouldBuildTank()) {
	                rc.buildRobot(RobotType.TANK, dir);
	                setNumTank(+1);
	            }
	            if (rc.getTeamBullets()>TREEBULLET && trees.length<MAXTREES && rc.canPlantTree(dir)){
	            	rc.plantTree(dir);
	            }
	            
	            // Move randomly
	            tryMove(dir);
	            
	            if(!isDead){
	            	if(isDead(birthRound)) setNumGardener(-1);
	            	if(rc.readBroadcast(FARMING_GARDENER)==rc.getID()) rc.broadcast(FARMING_GARDENER, 0);
	            	isDead=true;
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
		return (getNumScout()< MAXSCOUTS && rc.getRoundNum()< NO_SCOUT_NEEDED && rc.canBuildRobot(RobotType.SCOUT, randomDirection()));
	}
	/**
	 * Decides when it's possible to build Soldiers
	 * @return  TRUE: When it's possible FALSE: When it's not
	 */
	public boolean shouldBuildSoldier() throws GameActionException {
		return ((isInDanger() && rc.canBuildRobot(RobotType.SOLDIER, randomDirection())) || getNumSoldier()< MAXSOLDIERS);
	}
	/**
	 * Decides when it's possible to build Lumberjacks
	 * @return  TRUE: When it's possible FALSE: When it's not
	 */
	public boolean shouldBuildLumberjack() throws GameActionException {
		return (getNumLumberjack()<=MAXLUMBERJACKS && (trees.length>=TREESLUMBERJACK || (rc.getRoundNum()>ROUNDLUMBERJACK && getNumLumberjack()==MINLUMBERJACKS)) && rc.canBuildRobot(RobotType.LUMBERJACK, randomDirection()));
	}
	/**
	 * Decides when it's possible to build Tanks
	 * @return  TRUE: When it's possible FALSE: When it's not
	 */
	public boolean shouldBuildTank() throws GameActionException {
		return (rc.getRoundNum()>ROUNDTANK && getNumTank()== MINTANKS && rc.canBuildRobot(RobotType.TANK, randomDirection()));
	}
	
}