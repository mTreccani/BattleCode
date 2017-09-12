package examplefuncsplayer;

import battlecode.common.*;

public class ArchonLogic extends RobotLogic{
	
	public ArchonLogic (RobotController rc){
		super(rc);
		
	}
	
	@Override
	public void run() throws GameActionException {
        System.out.println("I'm an archon!");
        
        rc.broadcastInt(IMPERO_ROMANO_1, 0);
        rc.broadcastInt(IMPERO_ROMANO_2, 0);
        rc.broadcastInt(IMPERO_ROMANO_3, 0);
        
        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Generate a random direction
                Direction dir = randomDirection();
                gameInfo();

                rc.broadcastFloat(ARCHON_LOCATION_X, myLocation.x);
                rc.broadcastFloat(ARCHON_LOCATION_Y, myLocation.y);
                IsInDanger();
                
                if(isInDanger()){
                	 MapLocation[] myArchon = rc.getInitialArchonLocations(ally);
                	 Direction toMyInitialLocation = myLocation.directionTo(myArchon[0]);
                	 tryMove(toMyInitialLocation);
                }
               
                // Randomly attempt to build a gardener in this direction
                if (shouldBuildGardener()) {
                    rc.hireGardener(dir);
                    setNumGardener(+1);
                }

                // Move randomly
                tryMove(dir);

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
	
	public boolean IsInDanger() throws GameActionException{
		if(enemyRobots.length>1){
			int enemySoldiers=0;
			int allySoldiers=0;
			for(int i=0; i<enemyRobots.length; i++){
				if(enemyRobots[i].getType()==RobotType.SOLDIER || enemyRobots[i].getType()==RobotType.TANK || enemyRobots[i].getType()==RobotType.SCOUT) enemySoldiers++;
			}
			for(int i=0; i<allyRobots.length; i++){
				if(allyRobots[i].getType()==RobotType.SOLDIER || allyRobots[i].getType()==RobotType.TANK) allySoldiers++;
			}
			
			if(enemySoldiers>allySoldiers){
				rc.broadcastBoolean(IS_ARCHON_IN_DANGER, true);
				return true;
			}
			else{
				rc.broadcastBoolean(IS_ARCHON_IN_DANGER, false);
				return false;
			}
		}
		return false;
	}
}

