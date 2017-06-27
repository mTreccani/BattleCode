package examplefuncsplayer;

import battlecode.common.*;

public class GardenerLogic extends RobotLogic{

	@Override
	public void run() throws GameActionException {
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
            if (isInDanger() && rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
                setNumSoldier(+1);
            } 
            if ((rc.getRoundNum()<=35 || getNumScout()<=3)  && rc.canBuildRobot(RobotType.SCOUT, dir)) {
                rc.buildRobot(RobotType.SCOUT, dir);
                setNumScout(+1);
            } 
            if (getNumLumberjack()<=3 && (trees.length>=2 || (rc.getRoundNum()>200 && getNumLumberjack()==0)) && rc.canBuildRobot(RobotType.LUMBERJACK, dir)) {
                rc.buildRobot(RobotType.LUMBERJACK, dir);
                setNumLumberjack(+1);
            }
            if (rc.getRoundNum()>600 && getNumTank()==0 && rc.canBuildRobot(RobotType.TANK, dir)) {
                rc.buildRobot(RobotType.TANK, dir);
                setNumSoldier(+1);
            } 
            if (rc.getTeamBullets()>200 && rc.canBuildRobot(RobotType.SOLDIER, dir))
            { 
            	rc.buildRobot(RobotType.SOLDIER, dir);
                setNumSoldier(+1);
            }
            if (rc.getTeamBullets()>100 && trees.length<4 && rc.canPlantTree(dir))
            {
            	rc.plantTree(dir);
            }
            // Move randomly
            tryMove(randomDirection());

            // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
            Clock.yield();

        } catch (Exception e) {
            System.out.println("Gardener Exception");
            e.printStackTrace();
        }
		
	}
	

}
