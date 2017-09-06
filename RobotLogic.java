package examplefuncsplayer;

import battlecode.common.*;

public abstract class RobotLogic {
	
	 public RobotController rc;
	 
	 Team ally;
	 Team enemy;
	 MapLocation myLocation;
	 RobotInfo[] enemyRobots;   //per le strategie
	 RobotInfo[] allyRobots;   //per le strategie
	 TreeInfo[] trees;
	 BulletInfo[] bullets;
	 
	 boolean goodAreaExplorer=false;
	 private Direction dirExplorer= new Direction(0);
	 private int numScout=0;
	 private int numSoldier=0;
	 private int numGardener=0;
	 private int numLumberjack=0;
	 private int numArchon=1;
	 private int numTank=0;
	 private float angolo;
	 
	 
	 int dir =0 ;
	 
	 public RobotLogic(RobotController rc) {
		 this.rc=rc;
		 enemy = rc.getTeam().opponent();
		 ally = rc.getTeam();
		 myLocation = rc.getLocation();
		 enemyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemy); 
		 allyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, ally);
		 trees = rc.senseNearbyTrees(rc.getType().sensorRadius);
		 bullets = rc.senseNearbyBullets(rc.getType().bodyRadius+1);
		 angolo = 1;
	 }
	 
	 public abstract void run() throws GameActionException;
	 
	 
	 public void runnerStrategy() throws GameActionException{
		 angolo +=(angolo/rc.getRoundNum());
		 Direction dir = new Direction(angolo);
		 tryMove(dir);
		 
	 }

 
	 
	 public void exploreStrategy() throws GameActionException{
/*		
		if(treesExplorer.length>=5 && enemyRobots.length<=2) {
			goodAreaExplorer=true;
			rc.broadcast(4,(int)myLocation.x);
			rc.broadcast(5,(int)myLocation.y);
		}
		
		if(!goodAreaExplorer){
			dir+=10;
			Direction dire = new Direction(dir);
			tryMove(dire);
		}
		else{
			tryMove(randomDirection());
		}*/
		 MapLocation[] enemyArchon = rc.getInitialArchonLocations(enemy);
		 MapLocation[] myArchon = rc.getInitialArchonLocations(ally);
		 if(rc.senseNearbyRobots(rc.getType().SCOUT.sensorRadius).length<1){
			 Direction toMyArchon = myLocation.directionTo(myArchon[0]);
			 rc.move(toMyArchon);
		 }
		 else{
			 Direction toEnemyArchon = myLocation.directionTo(enemyArchon[0]);
			 rc.move(toEnemyArchon);
		 }
		
	 }
	 
	 public void vikingStrategy() throws GameActionException{
		
	 }
	 
	 /**
	  * è in pericolo se ci sono nemici nelle vicinanze o proiettili che lo stanno per colpire
	  * @return true se è in pericolo
	  */
	 boolean isInDanger(){
		 if(enemyRobots.length>0 || bullets.length>0){
			 return true;
		 }
		 else{
			 return false;
		 }
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
    

    public int getNumScout(){
    	return numScout;
    }
    
    public void setNumScout(int n){
    	numScout= numScout + n;
    }
    
    public int getNumSoldier(){
    	return numSoldier;
    }
    
    public void setNumSoldier(int n){
    	numSoldier=numSoldier+n;
    }
    
    public int getNumGardener(){
    	return numGardener;
    }
    
    public void setNumGardener(int n){
    	numGardener=numGardener+n;
    }
    
    public int getNumLumberjack(){
    	return numLumberjack;
    }
    
    public void setNumLumberjack(int n){
    	numLumberjack=numLumberjack+n;
    }
    
    public int getNumTank(){
    	return numTank;
    }
    
    public void setNumTank(int n){
    	numTank=numTank+n;
    }
    
    public int getNumArchon(){
    	return numArchon;
    }
    
    public void setNumArchon(int n){
    	numArchon=numArchon+n;
    }

}



