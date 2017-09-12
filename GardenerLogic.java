package examplefuncsplayer;
import battlecode.common.*;

public class GardenerLogic extends RobotLogic{
	private static final int NO_SCOUT_NEEDED=1500;
	private static final int MINTREES=0;
	private static final int TREEBULLET=100;
	private static final int MAXTREES=4;
	private static final int REGENERATIONROUNDS=20;
	private static final int DEATHDIVIDER=5;
	private static final int MAXSCOUTS=3;
	private static final int MAXSOLDIERS=3;
	private static final int MAXLUMBERJACKS=3;
	private static final int MINLUMBERJACKS=0;
	private static final int TREESLUMBERJACK=2;
	private static final int ROUNDLUMBERJACK=200;
	private static final int ROUNDTANK=600;
	private static final int MINTANKS=0;
	boolean isAlive;
	int birthRound;
	
	public GardenerLogic (RobotController rc){
		super(rc);
		birthRound=rc.getRoundNum();
		isAlive = true;
	}
	
	@Override
	public void run() throws GameActionException{
		while(true){
			try {
	            // Listen for home archon's location
	            int xPos = rc.readBroadcast(0);
	            int yPos = rc.readBroadcast(1);
	            MapLocation archonLoc = new MapLocation(xPos,yPos);
	            TreeInfo[] trees= rc.senseNearbyTrees(RobotType.GARDENER.sensorRadius);
	            // Generate a random direction
	            Direction dir = randomDirection();
	            if (trees.length > MINTREES && !isInDanger()){
	            	MapLocation myLocation=rc.getLocation();
	            	MapLocation treeLocation=trees[0].getLocation();
	            	Direction toTree=myLocation.directionTo(treeLocation);
	            	tryMove(toTree);
	            	if(rc.canWater(treeLocation)){
	            		rc.water(treeLocation);
	            	}
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
	                setNumSoldier(+1);
	            }
	            if (rc.getTeamBullets()>TREEBULLET && trees.length<MAXTREES && rc.canPlantTree(dir)){
	            	rc.plantTree(dir);
	            }
	            // Move randomly
	            tryMove(randomDirection());
	            if(isAlive){
	            	if(isDead(birthRound)){
		            	setNumGardener(-1);
		            	isAlive = false;
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