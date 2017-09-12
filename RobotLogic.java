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
	 public static final int ENOUGHT_BULLETS_FOR_TRIAD = 50;
	 public static final float ANGOLO_SOL_1=-1.5f;
	 public static final float ANGOLO_SOL_2=-1.0f;
	 public static final float ANGOLO_SOL_3=-0.5f;

	 
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
	 public static final int VIKING_1=8;
	 public static final int VIKING_2=9;
	 public static final int VIKING_3=10;
	 public static final int ROMAN_EMPIRE_1=11;
	 public static final int ROMAN_EMPIRE_2=12;
	 public static final int ROMAN_EMPIRE_3=13;
	 public static final int FARM_ZONE=14;
	 public static final int FARMER_X=15;
	 public static final int FARMER_Y=16;
	 public static final int FARMING_LUMBERJACK=17;
	 public static final int FARMING_GARDENER=18;
	 public static final int FARMING_SCOUT=19;
	 public static final int SOLAR_1=20;
	 public static final int SOLAR_2=21;
	 public static final int SOLAR_3=22;

	 
	 
	 
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
		 boolean farm=false;
		 
		 if(trees.length>0 && enemyRobots.length==0){
			 rc.broadcastFloat(EXPLORER_X, myLocation.x);
			 rc.broadcastFloat(EXPLORER_Y, myLocation.y);
			 rc.broadcastBoolean(IS_A_GOOD_ZONE, true);
			 if(trees.length>3 && (farm==false || rc.readBroadcast(FARMING_SCOUT)==rc.getID())){
				 rc.broadcast(FARMING_SCOUT, rc.getID());
				 farm=true;
				 rc.broadcastBoolean(FARM_ZONE, true);
				 rc.broadcastFloat(FARMER_X, myLocation.x);
				 rc.broadcastFloat(FARMER_Y, myLocation.y);
				 Clock.yield();
			 }
			 else {
				 rc.broadcastBoolean(FARM_ZONE, false);
				 farm=false;
			 }
		 }
		 
		 if(enemyRobots.length>0){
			 
			 int enemySoldiers=0;
			 for(int i=0; i<enemyRobots.length; i++){
					if(enemyRobots[i].getType()==RobotType.SOLDIER || enemyRobots[i].getType()==RobotType.TANK || enemyRobots[i].getType()==RobotType.SCOUT) enemySoldiers++;
				}
			
			 if(enemySoldiers>1){
				 rc.broadcastBoolean(IS_A_GOOD_ZONE, false);
				 rc.broadcastBoolean(FARM_ZONE, false);
				 rc.broadcastFloat(FARMER_X, 0);
				 rc.broadcastFloat(FARMER_Y, 0);
				 if (rc.canFireSingleShot()) {
				        rc.fireSingleShot(rc.getLocation().directionTo(enemyRobots[0].location));
				    }
				 else{
					 MapLocation[] myArchon = rc.getInitialArchonLocations(ally);
					 Direction toMyArchon = myLocation.directionTo(myArchon[0]);
					 rc.move(toMyArchon);
				 }
			 }
		 }
		 
		 angolo+= 0.2;
		 Direction dir = new Direction(angolo);
		 tryMove(dir); 
			
	 }
	 
	 public void farmStrategy() throws GameActionException{
		 
		 gameInfo();
		 
		 
		 float allyArchonX = rc.readBroadcastFloat(ARCHON_LOCATION_X);
		 float allyArchonY = rc.readBroadcastFloat(ARCHON_LOCATION_Y);
		 /*if(rc.getType().equals(RobotType.LUMBERJACK) && id1 == 0){
			 if(rc.readBroadcastBoolean(FARM_ZONE)){
				 tryMove(new Direction(FARMER_X,FARMER_Y));
				 rc.broadcastInt(FARMING_LUMBERJACK, rc.getID());
				 
			 }
		 }
		 if(rc.getType().equals(RobotType.GARDENER) && id2 == 0){
			 if(rc.readBroadcastBoolean(FARM_ZONE)){
				 tryMove(new Direction(FARMER_X,FARMER_Y));
				 rc.broadcastInt(FARMING_GARDENER, rc.getID());
			 } 
			 if(trees.length==0){
				 tryMove(new Direction(allyArchonX,allyArchonY));
				 rc.broadcastBoolean(FARM_ZONE, false);
			 }
		 }
		 if(trees.length==0||(!rc.getType().equals(RobotType.SCOUT))){
			 tryMove(new Direction(allyArchonX,allyArchonY));
			 rc.broadcastBoolean(FARM_ZONE, false);
		 }*/
		 
		 if(rc.readBroadcastBoolean(FARM_ZONE)){
			 
			 if(rc.readBroadcast(FARMING_LUMBERJACK)==rc.getID() && rc.readBroadcast(FARMER_X)!=0 && rc.readBroadcast(FARMER_Y)!=0){
				 if(rc.getLocation().distanceTo(new MapLocation(FARMER_X,FARMER_Y))>2){
					 tryMove(new Direction(FARMER_X,FARMER_Y));
				 }
			 }
			 
			 if(rc.readBroadcast(FARMING_GARDENER)==rc.getID() && rc.readBroadcast(FARMER_X)!=0 && rc.readBroadcast(FARMER_Y)!=0){
				 if(rc.getLocation().distanceTo(new MapLocation(FARMER_X,FARMER_Y))>2){
					 tryMove(new Direction(FARMER_X,FARMER_Y));
				 }
			 }
		 }
		 
		 if(trees.length==0) tryMove(new Direction(allyArchonX,allyArchonY));
	 }
	 
     public void solarStrategy() throws GameActionException{
		 
		 if (rc.readBroadcast(SOLAR_1)==0 || rc.readBroadcast(SOLAR_1)==rc.getID())
		 {
			 Direction dir= new Direction(ANGOLO_SOL_1);
			 tryMove(dir);
			 rc.broadcast(SOLAR_1, rc.getID());
		 }
		 else if (rc.readBroadcast(SOLAR_2)==0 && rc.readBroadcast(SOLAR_1)!=rc.getID() || rc.readBroadcast(SOLAR_2)==rc.getID())
		 {
			 Direction dir= new Direction(ANGOLO_SOL_2);
			 tryMove(dir);
			 rc.broadcast(SOLAR_2, rc.getID());
		 }
		 else if (rc.readBroadcast(SOLAR_3)==0 && rc.readBroadcast(SOLAR_1)!=rc.getID() && rc.readBroadcast(SOLAR_2)!=rc.getID() || rc.readBroadcast(SOLAR_3)==rc.getID())
		 {
			 Direction dir= new Direction(ANGOLO_SOL_3);
			 tryMove(dir);
			 rc.broadcast(SOLAR_3, rc.getID());
		 }		 
		
	 }

	 
	 public void vikingStrategy() throws GameActionException{
		
		gameInfo();
		
		/*MapLocation[] enemyArchon = rc.getInitialArchonLocations(enemy); 
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
		}*/
		
		MapLocation[] enemyArchon = rc.getInitialArchonLocations(enemy); 
		Direction toEnemyArchon = myLocation.directionTo(enemyArchon[0]);
		float allyArchonX = rc.readBroadcastFloat(ARCHON_LOCATION_X);
		float allyArchonY = rc.readBroadcastFloat(ARCHON_LOCATION_Y);
		MapLocation allyArchon = new MapLocation(allyArchonX,allyArchonY);
		Direction toAllyArchon = myLocation.directionTo(allyArchon);
		
		if(rc.readBroadcast(VIKING_1)!=0 && rc.readBroadcast(VIKING_2)!=0 && rc.readBroadcast(VIKING_3)!=0 && rc.getRoundNum()%1000<500){
			
			tryMove(toEnemyArchon);
			if(rc.getLocation().distanceTo(enemyArchon[0])<10) tryMove(randomDirection());
		}
		else{
			
			tryMove(toAllyArchon);
		}
	 }
	 
	 public void totalHelpStrategy() throws GameActionException{
		 
		 gameInfo();
		 
		 if(isArchonInDanger()){
			 
			 if(rc.getType()==RobotType.SOLDIER || rc.getType()==RobotType.TANK){
				 MapLocation myArchon= new MapLocation(rc.readBroadcastFloat(ARCHON_LOCATION_X),rc.readBroadcastFloat(ARCHON_LOCATION_Y));
				 Direction toMyArchon= rc.getLocation().directionTo(myArchon);
				 tryMove(toMyArchon);
				 
				 if (enemyRobots.length>1 && rc.getTeamBullets()>=ENOUGHT_BULLETS_FOR_TRIAD) {
			            // And we have enough bullets, and haven't attacked yet this turn...
			            if (rc.canFireTriadShot()) {
			                // ...Then fire a bullet in the direction of the enemy.
			                rc.fireTriadShot(rc.getLocation().directionTo(enemyRobots[0].location));
			            }
			     } 
				  
				 /*if(rc.getLocation().distanceTo(myArchon)<3 && allyRobots.length>=3){
					 int soldiers=0;
					 for(int i=0; i<allyRobots.length; i++){
						 if(allyRobots[i].getType()==RobotType.SOLDIER || allyRobots[i].getType()==RobotType.TANK) soldiers++;
					 }
					 
					 if(soldiers>=2){
						 rc.broadcastBoolean(IS_ARCHON_IN_DANGER, false);
					 }
				 }*/
			 }
		 }
				 
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
	 
	 public void romanEmpireStrategy() throws GameActionException{
		 
			gameInfo();
			float allyArchonX = rc.readBroadcastFloat(ARCHON_LOCATION_X);
			float allyArchonY = rc.readBroadcastFloat(ARCHON_LOCATION_Y);
			MapLocation allyArchon = new MapLocation(allyArchonX,allyArchonY);
			boolean nearAllyArchon = myLocation.isWithinDistance(allyArchon, RobotType.ARCHON.sensorRadius);
			int id = rc.getID();
			if(id == rc.readBroadcastInt(8) || id == rc.readBroadcastInt(9) || id == rc.readBroadcastInt(10)){
				if(!nearAllyArchon){
					Direction toAllyArchon = myLocation.directionTo(allyArchon);
					tryMove(toAllyArchon);
				}
				else{
					angolo+= 0.01;
					Direction dir = new Direction(angolo);
					tryMove(dir);
				}
			}
		 }
	 
	 public void MatrixStrategy(MapLocation enemy, BulletInfo[] bullets) throws GameActionException {
	   	 MapLocation currLocation = rc.getLocation();
	   	 Direction toEnemy = currLocation.directionTo(enemy);
	
	   	 float minDamage = rc.getHealth();
	   	 int bestAngle = -40;
	   	 for (int angle = -40; angle < 40; angle += 10) {
		   	 MapLocation expectedLocation = currLocation.add(toEnemy.rotateLeftDegrees(angle), rc.getType().strideRadius);
		   	 float damage = expectedDamage(bullets, expectedLocation);
		
		   	 if (damage < minDamage) {
			   	 bestAngle = angle;
			   	 minDamage = damage;
			   	 }
		   	 }
		
		   	 tryMove(toEnemy.rotateLeftDegrees(bestAngle));
   	 }
	 
	 public float expectedDamage(BulletInfo[] bullets, MapLocation loc) {
	   	 float totalDamage = 0;
	   	 for (BulletInfo bullet : bullets) {
		   	 if (willCollideWithMe(bullet)) {
		   		 totalDamage += bullet.damage;
		   	 }
	   	 }
	   	 return totalDamage;
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

    public boolean isDead(int birthRound){
    	if(rc.getHealth()<rc.getType().maxHealth/DEATHDIVIDER && rc.getRoundNum()-birthRound< REGENERATIONROUNDS) return true;
    	return false;
    }
    
    public void tryShoot() throws GameActionException{
    	
    	gameInfo();
  
    	if (enemyRobots.length==1) {
            // And we have enough bullets, and haven't attacked yet this turn...
            if (rc.canFireSingleShot()) {
                // ...Then fire a bullet in the direction of the enemy.
                rc.fireSingleShot(rc.getLocation().directionTo(enemyRobots[0].location));
            }
        }
        
        // If there are some....
       if (enemyRobots.length>1 && rc.getTeamBullets()>=ENOUGHT_BULLETS_FOR_TRIAD) {
            // And we have enough bullets, and haven't attacked yet this turn...
            if (rc.canFireTriadShot()) {
                // ...Then fire a bullet in the direction of the enemy.
                rc.fireTriadShot(rc.getLocation().directionTo(enemyRobots[0].location));
            }
        } 
        
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