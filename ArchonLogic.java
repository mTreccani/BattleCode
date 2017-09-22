package examplefuncsplayer;

import battlecode.common.*;

public class ArchonLogic extends RobotLogic{
	
	public ArchonLogic (RobotController rc) throws GameActionException{
		super(rc);
	}
	
	@Override
	public void run() throws GameActionException {
        System.out.println("I'm an archon!");

        rc.broadcast(NUM_SCOUT, 0);
        rc.broadcast(NUM_SOLDIER, 0);
        rc.broadcast(NUM_LUMBERJACK, 0);
        rc.broadcast(NUM_TANK, 0);
        rc.broadcast(NUM_GARDENER, 0);
        rc.broadcast(VIKING_1, 0);
        rc.broadcast(VIKING_2, 0);
        rc.broadcast(VIKING_3, 0);
        rc.broadcast(ENEMY_FOUND_X, 0);
        rc.broadcast(ENEMY_FOUND_Y, 0);
        rc.broadcast(ROMAN_EMPIRE_1, 0);
        rc.broadcast(ROMAN_EMPIRE_2, 0);
        rc.broadcast(ROMAN_EMPIRE_3, 0);
        rc.broadcast(FARMER_X, 0);
        rc.broadcast(FARMER_Y, 0);
        rc.broadcast(FARMING_LUMBERJACK,0);
        rc.broadcast(FARMING_LUMBERJACK,0);
        rc.broadcastBoolean(FARM_ZONE, false);
        rc.broadcast(SOLAR_1, 0);
        rc.broadcast(SOLAR_2, 0);
        rc.broadcast(SOLAR_3, 0);
        rc.broadcastBoolean(IS_A_GOOD_ROAD, false);
        rc.broadcast(TANK_1,0);
        rc.broadcast(TANK_2,0);
        rc.broadcast(SQUAD_0, 0);
        rc.broadcast(SQUAD_1, 0);
        rc.broadcast(SQUAD_2, 0);
        rc.broadcast(SQUAD_3, 0);
        rc.broadcastBoolean(FORMICA, false);
        
        MapLocation[] enemyArchon = rc.getInitialArchonLocations(enemy);
        rc.broadcastFloat(ENEMY_ARCHON_X, enemyArchon[0].x);
        rc.broadcastFloat(ENEMY_ARCHON_Y, enemyArchon[0].y);
        rc.broadcastBoolean(ENEMY_ARCHON_KILLED, false);

        
        while (true) {

            try {

                gameInfo();

                rc.broadcastFloat(ARCHON_LOCATION_X, myLocation.x);
                rc.broadcastFloat(ARCHON_LOCATION_Y, myLocation.y);
                archonInDanger();
                

                if (shouldBuildGardener()) {
                	Direction toInitialEnemyArchon = myLocation.directionTo(enemyArchon[0]);
                    rc.hireGardener(toInitialEnemyArchon);
                    setNumGardener(+1);
                }
                
                MapLocation[] allyArchon = rc.getInitialArchonLocations(ally);
                if (myLocation.distanceTo(allyArchon[0])<6){
                	angolo+=0.5;
                	Direction dir = new Direction(angolo);
           		    if(!rc.hasMoved()) tryMove(dir);
                } 
                else{
                	Direction toMyArchon = myLocation.directionTo(allyArchon[0]);
                	if(!rc.hasMoved()) tryMove(toMyArchon);
                }
				

                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
	}
	
	 /**
	  * metodo che stabilisce se è il momento di produrre un gardener
	  * @return TRUE se è il momento, FALSE altrimenti
	  * @throws GameActionException
	  */
	public boolean shouldBuildGardener() throws GameActionException{
		return (rc.canHireGardener(randomDirection()) &&  rc.getTeamBullets() >= 200 && !isInDanger() && getNumGardener()<=2);
	}
	
	/**
	 * metodo che controlla se l'archon è in nemico, in caso affermativo
	 * lo fa sapere in broadcast agli alleati
	 * @throws GameActionException
	 */
	public void archonInDanger() throws GameActionException{
		
		if(enemyRobots.length>0){
			int enemySoldiers=0;
			int allySoldiers=0;
			for(int i=0; i<enemyRobots.length; i++){
				if(enemyRobots[i].getType()==RobotType.SOLDIER || enemyRobots[i].getType()==RobotType.TANK || enemyRobots[i].getType()==RobotType.LUMBERJACK) enemySoldiers++;
			}
			for(int j=0; j<allyRobots.length; j++){
				if(allyRobots[j].getType()==RobotType.SOLDIER || allyRobots[j].getType()==RobotType.TANK) allySoldiers++;
			}
			
			if(enemySoldiers>allySoldiers){
				rc.broadcastBoolean(IS_ARCHON_IN_DANGER, true);
			}
			else{
				rc.broadcastBoolean(IS_ARCHON_IN_DANGER, false);
			}
		}
		else{
			rc.broadcastBoolean(IS_ARCHON_IN_DANGER, false);
		}
	}
}