package examplefuncsplayer;

import battlecode.common.*;

public abstract class RobotLogic {
	
	 public RobotController rc;
	 
	 Team enemy;
	 Team ally;
	 MapLocation myLocation;
	 RobotInfo[] enemyRobots;
	 RobotInfo[] allyRobots;  //per le strategie
	 TreeInfo[] trees;
	 BulletInfo[] bullets;
	 private float angolo;
	 public static final int DEATHDIVIDER=4;
	 public static final int REGENERATIONROUNDS=20;
	 public static final int VIKING_NUM_SOLDIER = 3;
	 
	 //broadcast channels
	 public static final int NUM_SCOUT=50;
	 public static final int NUM_SOLDIER=51;
	 public static final int NUM_GARDENER=52;
	 public static final int NUM_LUMBERJACK=53;
	 public static final int NUM_ARCHON=55;
	 public static final int NUM_TANK=54;
	 public static final int EXPLORER_X=0;
	 public static final int EXPLORER_Y=1;
	 public static final int IS_A_GOOD_ZONE=2;
	 public static final int ARCHON_LOCATION_X=3;
	 public static final int ARCHON_LOCATION_Y=4;
	 public static final int IS_ARCHON_IN_DANGER=5;
	 public static final int I_HAVE_BEEN_HIT_X=6;
	 public static final int I_HAVE_BEEN_HIT_Y=7;
	 
	 public RobotLogic(RobotController rc) {
		 this.rc=rc;
		 enemy = rc.getTeam().opponent();
		 ally = rc.getTeam();
		 myLocation = rc.getLocation();
		 enemyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemy);
		 allyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, ally);//per le strategie
		 trees = rc.senseNearbyTrees(rc.getType().sensorRadius);
		 bullets = rc.senseNearbyBullets(rc.getType().bodyRadius+1);
		 angolo = 0;
	 }
	 
	 public abstract void run() throws GameActionException;
	 
	 public void runnerStrategy() throws GameActionException{
		 gameInfo();
		 MapLocation[] enemyArchon = rc.getInitialArchonLocations(enemy);
		 if(rc.getRoundNum()%100<40 && myLocation.distanceTo(enemyArchon[0])>35){
		     Direction toEnemyArchon = myLocation.directionTo(enemyArchon[0]);
		     rc.move(toEnemyArchon);
		 }
		 else{
			 tryMove(randomDirection());
		 }
		 
		 
	 }
	
	 public void exploreStrategy() throws GameActionException{
		 
		 gameInfo();
		 
		 if(trees.length>0){
			 rc.broadcastFloat(EXPLORER_X, myLocation.x);
			 rc.broadcastFloat(EXPLORER_Y, myLocation.y);
			 rc.broadcastBoolean(IS_A_GOOD_ZONE, true);	 
		 }
		 
		 if(enemyRobots.length>0){
			 rc.broadcastBoolean(IS_A_GOOD_ZONE, false);
			 if (rc.canFireSingleShot()) {
			        rc.fireSingleShot(rc.getLocation().directionTo(enemyRobots[0].location));
			    }
			 else{
				 MapLocation[] myArchon = rc.getInitialArchonLocations(ally);
				 Direction toMyArchon = myLocation.directionTo(myArchon[0]);
				 rc.move(toMyArchon);
			 }
		 }
		 
		 angolo+= 0.2;
		 Direction dir = new Direction(angolo);
		 tryMove(dir); 
			
	 }
	 
	 public void vikingStrategy() throws GameActionException{
		
		gameInfo();
		
		MapLocation[] enemyArchon = rc.getInitialArchonLocations(enemy); 
		float allyArchonX = rc.readBroadcastFloat(ARCHON_LOCATION_X);
		float allyArchonY = rc.readBroadcastFloat(ARCHON_LOCATION_Y);
		MapLocation allyArchon = new MapLocation(allyArchonX,allyArchonY);
		int nearEnemy = enemyRobots.length;	
		boolean nearEnemyArchon = myLocation.isWithinDistance(enemyArchon[0], RobotType.ARCHON.sensorRadius); 
		boolean nearAllyArchon = myLocation.isWithinDistance(allyArchon, RobotType.ARCHON.sensorRadius);
		int numSoldier = getNumSoldier();
		
		if(numSoldier>=VIKING_NUM_SOLDIER && nearEnemy == 0){
			Direction toEnemyArchon = myLocation.directionTo(enemyArchon[0]);
			tryMove(toEnemyArchon);
		}
		else if(numSoldier<VIKING_NUM_SOLDIER && !nearAllyArchon){
			Direction toAllyArchon = myLocation.directionTo(allyArchon);
			tryMove(toAllyArchon);
		}
		else if(nearEnemyArchon || nearEnemy != 0 || nearAllyArchon){
			runnerStrategy();
		}
	 }
	 
	 public void totalHelpStrategy() throws GameActionException{
		 
		 gameInfo();
				 
		 if(rc.readBroadcastFloat(I_HAVE_BEEN_HIT_X)!= 0 && rc.readBroadcastFloat(I_HAVE_BEEN_HIT_Y)!= 0){
			 if(rc.getType()==RobotType.SOLDIER || rc.getType()==RobotType.TANK){
				 MapLocation fireZone= new MapLocation(rc.readBroadcastFloat(I_HAVE_BEEN_HIT_X),rc.readBroadcastFloat(I_HAVE_BEEN_HIT_Y));
				 Direction toFireZone= rc.getLocation().directionTo(fireZone);
				 tryMove(toFireZone);
				 if(rc.getLocation().distanceTo(fireZone)<3 && allyRobots.length>=3){
					 int soldiers=0;
					 for(int i=0; i<allyRobots.length; i++){
						 if(allyRobots[i].getType()==RobotType.SOLDIER || allyRobots[i].getType()==RobotType.TANK) soldiers++;
					 }
					 
					 if(soldiers>=3){
						 rc.broadcastFloat(I_HAVE_BEEN_HIT_X, 0);
						 rc.broadcastFloat(I_HAVE_BEEN_HIT_Y, 0);
					 }
				 }
			 }
		 }
	 }
	 
	 /**
	  * è in pericolo se ci sono nemici nelle vicinanze 
	  * @return true se è in pericolo
	  */
	 boolean isInDanger(){
		 
		 gameInfo();
		 
		 if(enemyRobots.length>0){
			 return true;
		 }
		 else{
			 return false;
		 }
	 }
	 
	 public boolean isArchonInDanger() throws GameActionException{
	    	return rc.readBroadcastBoolean(IS_ARCHON_IN_DANGER);	
	    }
	 
    /**
     * Returns a random Direction
     * @return a random Direction
     */
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }

    /**
     * A slightly more complicated example function, this returns true if the given bullet is on a collision
     * course with the current robot. Doesn't take into account objects between the bullet and this robot.
     *
     * @param bullet The bullet in question
     * @return True if the line of the bullet's path intersects with this robot's current position.
     */
    boolean willCollideWithMe(BulletInfo bullet) {
        MapLocation myLocation = rc.getLocation();

        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI/2) {
            return false;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius);
    }
    
    public void gameInfo(){
		myLocation = rc.getLocation();
		enemyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemy);
		allyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, ally);//per le strategie
		trees = rc.senseNearbyTrees(rc.getType().sensorRadius);
		bullets = rc.senseNearbyBullets(rc.getType().bodyRadius+1);
    }
   
    public int getNumScout() throws GameActionException{
    	return rc.readBroadcast(NUM_SCOUT);
    }
    
    public void setNumScout(int n) throws GameActionException{
    	rc.broadcast(NUM_SCOUT, rc.readBroadcast(NUM_SCOUT) + n );
    }
    
    public int getNumSoldier() throws GameActionException{
    	return rc.readBroadcast(NUM_SOLDIER);
    }
    
    public void setNumSoldier(int n) throws GameActionException{
    	rc.broadcast(NUM_SOLDIER, rc.readBroadcast(NUM_SOLDIER) + n );
    }
    
    public int getNumGardener() throws GameActionException{
    	return rc.readBroadcast(NUM_GARDENER);
    }
    
    public void setNumGardener(int n) throws GameActionException{
    	rc.broadcast(NUM_GARDENER, rc.readBroadcast(NUM_GARDENER) + n );
    }
    
    public int getNumLumberjack() throws GameActionException{
    	return rc.readBroadcast(NUM_LUMBERJACK);
    }
    
    public void setNumLumberjack(int n) throws GameActionException{
    	rc.broadcast(NUM_LUMBERJACK, rc.readBroadcast(NUM_LUMBERJACK) + n );
    }
    
    public int getNumTank() throws GameActionException{
    	return rc.readBroadcast(NUM_TANK);
    }
    
    public void setNumTank(int n) throws GameActionException{
    	rc.broadcast(NUM_TANK, rc.readBroadcast(NUM_TANK) + n );
    }
    
    public int getNumArchon() throws GameActionException{
    	return rc.readBroadcast(NUM_ARCHON);
    }
    
    public void setNumArchon(int n) throws GameActionException{
    	rc.broadcast(NUM_ARCHON, rc.readBroadcast(NUM_ARCHON) + n );
    }

}

