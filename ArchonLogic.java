package examplefuncsplayer;

import battlecode.common.*;

public class ArchonLogic extends RobotLogic{
	
	
	public ArchonLogic (RobotController rc){
		super(rc);
	}
	
	@Override
	public void run() throws GameActionException {
        System.out.println("I'm an archon!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Generate a random direction
                Direction dir = randomDirection();

                // Randomly attempt to build a gardener in this direction
                if (shouldBuildGardener()) {
                    rc.hireGardener(dir);
                    setNumGardener(+1);
                }

                // Move randomly
                tryMove(dir);

                // Broadcast archon's location for other robots on the team to know
                if(isInDanger() || rc.getRoundNum()%10==0){
                MapLocation myLocation = rc.getLocation();
                rc.broadcast(0,(int)myLocation.x);
                rc.broadcast(1,(int)myLocation.y);
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
	}
	public boolean shouldBuildGardener() throws GameActionException{
		return (rc.canHireGardener(randomDirection()) &&  rc.getTeamBullets() >= 200 && !isInDanger() && rc.getRobotCount()<15);
	}
}

