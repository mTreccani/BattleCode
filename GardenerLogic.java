package examplefuncsplayer;

import battlecode.common.*;

public class GardenerLogic extends RobotLogic{

	private static final int NO_SCOUT_NEEDED=1500;
	
	public GardenerLogic (RobotController rc){
		super(rc);
	}
	
	@Override
	public void run() throws GameActionException{
		
		while(true){
			
			int birthRound=rc.getRoundNum();
			
			try {
	
	            // Listen for home archon's location
	            int xPos = rc.readBroadcast(0);
	            int yPos = rc.readBroadcast(1);
	            MapLocation archonLoc = new MapLocation(xPos,yPos);
	            TreeInfo[] trees= rc.senseNearbyTrees(RobotType.GARDENER.sensorRadius);
	            // Generate a random direction
	            Direction dir = randomDirection();
	
	            if (trees.length>0 && !isInDanger()){
	            	MapLocation myLocation=rc.getLocation();
	            	MapLocation treeLocation=trees[0].getLocation();
	            	Direction toTree=myLocation.directionTo(treeLocation);
	            	tryMove(toTree);
	            	if(rc.canWater(treeLocation)) rc.water(treeLocation);
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
	            if (rc.getTeamBullets()>100 && trees.length<4 && rc.canPlantTree(dir))
	            {
	            	rc.plantTree(dir);
	            }
	            // Move randomly
	            tryMove(randomDirection());
	            
	            if(rc.getHealth()<RobotType.GARDENER.maxHealth/5 && rc.getRoundNum()-birthRound>20) setNumGardener(-1);
	
	            // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
	            Clock.yield();
	
	        } catch (Exception e) {
	            System.out.println("Gardener Exception");
	            e.printStackTrace();
	        }	
		}		
	}
	
	public boolean shouldBuildScout() throws GameActionException{
		return (getNumScout()<3 && rc.getRoundNum()< NO_SCOUT_NEEDED && rc.canBuildRobot(RobotType.SCOUT, randomDirection()));
	}
	
	public boolean shouldBuildSoldier() throws GameActionException {
		return ((isInDanger() && rc.canBuildRobot(RobotType.SOLDIER, randomDirection())) || getNumSoldier()<3);
	}
	
	public boolean shouldBuildLumberjack() throws GameActionException {
		return (getNumLumberjack()<=3 && (trees.length>=2 || (rc.getRoundNum()>200 && getNumLumberjack()==0)) && rc.canBuildRobot(RobotType.LUMBERJACK, randomDirection()));
	}
	
	public boolean shouldBuildTank() throws GameActionException {
		return (rc.getRoundNum()>600 && getNumTank()==0 && rc.canBuildRobot(RobotType.TANK, randomDirection()));
	}
	

}

