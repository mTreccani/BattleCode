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
	 boolean moved;
	 boolean attacked;
	 boolean farm=false;
	 
	 private int i=0;
	 private int j=0;
	 
	 float angleRunner=0;
	 public float angolo=0;
	 
	 public static final int DEATH_DIVIDER=5;
	 
	 public static final int ENOUGHT_BULLETS_FOR_TRIAD = 50;
	 
	 public static final float SOLAR_ANGLE_1=-0.45f;
	 public static final float SOLAR_ANGLE_2=0.0f;
	 public static final float SOLAR_ANGLE_3=+0.45f;

	 
	 //broadcast channels
	 public static final int NUM_SCOUT=50;
	 public static final int NUM_SOLDIER=51;
	 public static final int NUM_GARDENER=52;
	 public static final int NUM_LUMBERJACK=53;
	 public static final int NUM_TANK=54;
	 
	 public static final int EXPLORER_X=0;
	 public static final int EXPLORER_Y=1;
	 public static final int IS_A_GOOD_ZONE=2;
	 
	 public static final int ARCHON_LOCATION_X=3;
	 public static final int ARCHON_LOCATION_Y=4;
	 public static final int IS_ARCHON_IN_DANGER=5;
	 
	 public static final int ENEMY_FOUND_X=6;
	 public static final int ENEMY_FOUND_Y=7;
	 
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
	 public static final int IS_A_GOOD_ROAD=18;
	 public static final int FARMING_GARDENER=19;
	 public static final int FARMING_SCOUT=20;
	 
	 public static final int SOLAR_1=21;
	 public static final int SOLAR_2=22;
	 public static final int SOLAR_3=23;
	 
	 public static final int TANK_1=24;
	 public static final int TANK_2=25;
	 public static final int SQUAD_0=26;
	 public static final int SQUAD_1=27;
	 public static final int SQUAD_2=28;
	 public static final int SQUAD_3=29;
	 
	 public static final int FORMICA=99;  
	 public static final int FORMICA_X=100;
	 public static final int FORMICA_Y=101;


	 public RobotLogic(RobotController rc) {
		 this.rc=rc;
		 enemy = rc.getTeam().opponent();
		 ally = rc.getTeam();
		 myLocation = rc.getLocation();
		 enemyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemy);
		 allyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, ally);//per le strategie
		 trees = rc.senseNearbyTrees(rc.getType().sensorRadius);
		 bullets = rc.senseNearbyBullets(rc.getType().bodyRadius+1);
	 }
	 
	 public abstract void run() throws GameActionException;
	 
	 public void runnerStrategy() throws GameActionException{
		 
		 gameInfo();
		 MapLocation[] enemyArchon = rc.getInitialArchonLocations(enemy);
		 if(enemyRobots.length>0) matrixStrategy();
		 if(myLocation.distanceTo(enemyArchon[0])>35){
		     Direction toEnemyArchon = myLocation.directionTo(enemyArchon[0]);
		     float angle=(toEnemyArchon.radians+solarStrategy())+((float)Math.sin(angleRunner)*1.5f);  
			 Direction runner_dir= new Direction(angle); 
			 if(!moved) tryMove(runner_dir);
			 angleRunner=(float)Math.PI/10+angleRunner;
		 }
		 else{
			 if(!moved) tryMove(randomDirection());
		 }	 
		 
	 }
	
	 public void exploreStrategy() throws GameActionException{
		 
		 gameInfo();
		 
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
				 if(!moved) tryMove(myLocation.directionTo(myLocation));
			 }
			 else {
				 rc.broadcastBoolean(FARM_ZONE, false);
				 farm=false;
			 }
		 }
		 
		 if(enemyRobots.length>0){
			 
			 int enemySoldiers=0;
			 for(int i=0; i<enemyRobots.length; i++){
					if(enemyRobots[i].getType()==RobotType.SOLDIER || enemyRobots[i].getType()==RobotType.TANK || enemyRobots[i].getType()==RobotType.SCOUT ||  enemyRobots[i].getType()==RobotType.LUMBERJACK) enemySoldiers++;
				}
			
			 if(enemySoldiers>0){
				 
				 rc.broadcastBoolean(IS_A_GOOD_ZONE, false);
				 if(rc.readBroadcast(FARMING_SCOUT)==rc.getID()){
					 rc.broadcastBoolean(FARM_ZONE, false);
					 rc.broadcastFloat(FARMER_X, 0);
					 rc.broadcastFloat(FARMER_Y, 0);
					 rc.broadcastFloat(ENEMY_FOUND_X, enemyRobots[0].location.x);
					 rc.broadcastFloat(ENEMY_FOUND_Y, enemyRobots[0].location.y);
				 }
				 if (rc.canFireSingleShot()) {
				        rc.fireSingleShot(rc.getLocation().directionTo(enemyRobots[0].location));
				    }
				 else{
					 MapLocation[] myArchon = rc.getInitialArchonLocations(ally);
					 Direction toMyArchon = myLocation.directionTo(myArchon[0]);
					 if(!moved) tryMove(toMyArchon);
				 }
			 }
		 }
		 
		 angolo+= 0.11;
		 Direction dir = new Direction(angolo);
		 if(!moved) tryMove(dir); 
			
	 }
	 
	 public void formica(MapLocation destination) throws GameActionException {
	    	
		    gameInfo(); 		
		   	if(!isInDanger() && rc.getRoundNum()%20==0 && i<40) {
		   		rc.broadcastBoolean(FORMICA, true);
		   		rc.broadcastFloat(FORMICA_X+i, myLocation.x);
		   		rc.broadcastFloat(FORMICA_Y+i, myLocation.y);
	    		i=i+2;
	    		Direction road = myLocation.directionTo(destination);
				if(!moved) tryMove(road);
			}else if(isInDanger()) {
				rc.broadcastBoolean(FORMICA, false);
				if(!attacked) tryShoot();
	    	}else{
	    		Direction road = myLocation.directionTo(destination);
	    		if(!moved) tryMove(road);
	    	}
	    
	 }	
	 
	 public void seguiFormica() throws GameActionException{
		 gameInfo();
		 if(rc.readBroadcastBoolean(FORMICA)){
			 float prossimoPassoX = rc.readBroadcastFloat(FORMICA_X+j);					 
			 float prossimoPassoY = rc.readBroadcastFloat(FORMICA_Y+j);
			 j=j+2;
			 
			 if(!moved) tryMove(new Direction(prossimoPassoX,prossimoPassoY));
		 } else {
			 float allyArchonX = rc.readBroadcastFloat(ARCHON_LOCATION_X);
			 float allyArchonY = rc.readBroadcastFloat(ARCHON_LOCATION_Y);
			 MapLocation allyArchon= new MapLocation(allyArchonX,allyArchonY);
			 Direction toAllyArchon= myLocation.directionTo(allyArchon);
			 if(!moved) tryMove(toAllyArchon);
		 }
	}


	 public void farmStrategy() throws GameActionException{
		 
		 gameInfo();
		 
		 float allyArchonX = rc.readBroadcastFloat(ARCHON_LOCATION_X);
		 float allyArchonY = rc.readBroadcastFloat(ARCHON_LOCATION_Y);
		 float farmerScoutX= rc.readBroadcastFloat(FARMER_X);
		 float farmerScoutY= rc.readBroadcastFloat(FARMER_Y);
		 MapLocation farmZone= new MapLocation(farmerScoutX,farmerScoutY);
		 Direction toFarmZone= myLocation.directionTo(farmZone);
		 MapLocation allyArchon= new MapLocation(allyArchonX,allyArchonY);
		 Direction toAllyArchon= myLocation.directionTo(allyArchon);
		 
		 if(rc.readBroadcast(FARMING_LUMBERJACK)==rc.getID() && farmerScoutX!=0 && farmerScoutY!=0){
			 if(rc.getLocation().distanceTo(farmZone)>5){
				 if(!moved) tryMove(toFarmZone);
			 }
			 if(rc.getLocation().distanceTo(farmZone)<5 && rc.getHealth()>RobotType.LUMBERJACK.maxHealth*4/5){
				 rc.broadcastBoolean(IS_A_GOOD_ROAD, true);
			 }
		 }
		 
		 if(rc.readBroadcast(FARMING_GARDENER)==rc.getID() && farmerScoutX!=0 && farmerScoutY!=0){
			 if(rc.getLocation().distanceTo(farmZone)>2 && rc.readBroadcastBoolean(IS_A_GOOD_ROAD)){
				 if(!moved) tryMove(toFarmZone);
			 }
		 }
		 
		 if(trees.length==0 && !moved) tryMove(toAllyArchon);
	 }
	 
     public float solarStrategy() throws GameActionException{
		 
    	 if (rc.readBroadcast(SOLAR_1)==0 || rc.readBroadcast(SOLAR_1)==rc.getID())
		 {
			 rc.broadcast(SOLAR_1, rc.getID());
			 return SOLAR_ANGLE_1;
		 }
		 else if (rc.readBroadcast(SOLAR_2)==0 && rc.readBroadcast(SOLAR_1)!=rc.getID() || rc.readBroadcast(SOLAR_2)==rc.getID())
		 {			 	 
			 rc.broadcast(SOLAR_2, rc.getID());
			 return SOLAR_ANGLE_2;
		 }
		 else if (rc.readBroadcast(SOLAR_3)==0 && rc.readBroadcast(SOLAR_1)!=rc.getID() && rc.readBroadcast(SOLAR_2)!=rc.getID() || rc.readBroadcast(SOLAR_3)==rc.getID())
		 {
			 rc.broadcast(SOLAR_3, rc.getID());
			 return SOLAR_ANGLE_3;
		 }	
		 return 0.0f;	 
		
	 }

	 
	 public void vikingStrategy() throws GameActionException{
	
		 gameInfo();
		
		 MapLocation[] enemyArchon = rc.getInitialArchonLocations(enemy); 
		 float allyArchonX = rc.readBroadcastFloat(ARCHON_LOCATION_X);
		 float allyArchonY = rc.readBroadcastFloat(ARCHON_LOCATION_Y);
		 MapLocation allyArchon = new MapLocation(allyArchonX,allyArchonY);
		 int nearEnemies = enemyRobots.length;	
		 boolean nearEnemyArchon = myLocation.isWithinDistance(enemyArchon[0], RobotType.ARCHON.sensorRadius); 
		 boolean nearAllyArchon = myLocation.isWithinDistance(allyArchon, RobotType.ARCHON.sensorRadius);
		
		 boolean viking = rc.readBroadcast(VIKING_1) == rc.getID() || rc.readBroadcast(VIKING_2)== rc.getID() || rc.readBroadcast(VIKING_3) == rc.getID();
		 boolean roman = rc.readBroadcast(ROMAN_EMPIRE_1) == rc.getID() || rc.readBroadcast(ROMAN_EMPIRE_2)== rc.getID() || rc.readBroadcast(ROMAN_EMPIRE_3) == rc.getID();

		 if(rc.readBroadcast(VIKING_1)==0 && !roman) {
         	rc.broadcast(VIKING_1, rc.getID());
         }
         else if (rc.readBroadcast(VIKING_2)==0 && rc.readBroadcast(VIKING_1)!=rc.getID() && !roman){
         	rc.broadcast(VIKING_2, rc.getID());
         }
         else if (rc.readBroadcast(VIKING_3)==0 && rc.readBroadcast(VIKING_1)!=rc.getID() && rc.readBroadcast(VIKING_2)!=rc.getID() && !roman) {
         	rc.broadcast(VIKING_3, rc.getID());
         }
		 
		
		 if(!roman && viking && rc.readBroadcast(VIKING_1)!=0 && rc.readBroadcast(VIKING_2)!=0 && rc.readBroadcast(VIKING_3)!=0  && nearEnemies == 0){
			 Direction toEnemyArchon = myLocation.directionTo(enemyArchon[0]);
			 if(!moved) tryMove(toEnemyArchon);
		 }
		 else if((rc.readBroadcast(VIKING_1)==0 || rc.readBroadcast(VIKING_2)==0 || rc.readBroadcast(VIKING_3)==0) && !nearAllyArchon && nearEnemies==0 && !roman){
			 Direction toAllyArchon = myLocation.directionTo(allyArchon);
			 if(!moved) tryMove(toAllyArchon);
		 }
		 else if((nearEnemyArchon || nearEnemies != 0 || nearAllyArchon || !viking )&& !roman){
			 exploreStrategy();
		 }
		 else if(roman){
			 romanEmpireStrategy();
		 }
	 }
	 
	 public void totalHelpStrategy() throws GameActionException{
		 
		 gameInfo();
		 
		 if(isArchonInDanger()){
			 
			 if(rc.getType()==RobotType.SOLDIER || rc.getType()==RobotType.TANK){
				 MapLocation myArchon= new MapLocation(rc.readBroadcastFloat(ARCHON_LOCATION_X),rc.readBroadcastFloat(ARCHON_LOCATION_Y));
				 Direction toMyArchon= rc.getLocation().directionTo(myArchon);
				 if(!moved) tryMove(toMyArchon);
				 
				 if (enemyRobots.length>1 && rc.getTeamBullets()>=ENOUGHT_BULLETS_FOR_TRIAD) {
			            if (rc.canFireTriadShot()) {
			                rc.fireTriadShot(rc.getLocation().directionTo(enemyRobots[0].location));
			            }
			     } 
			 }
		 }
				 
		 else if(rc.readBroadcastFloat(ENEMY_FOUND_X)!= 0 && rc.readBroadcastFloat(ENEMY_FOUND_Y)!= 0){
			 if(rc.getType()==RobotType.SOLDIER || rc.getType()==RobotType.TANK){
				 MapLocation fireZone= new MapLocation(rc.readBroadcastFloat(ENEMY_FOUND_X),rc.readBroadcastFloat(ENEMY_FOUND_Y));
				 Direction toFireZone= rc.getLocation().directionTo(fireZone);
				 if(!moved) tryMove(toFireZone);
				 if(rc.getLocation().distanceTo(fireZone)<3 && enemyRobots.length<=1){
						 rc.broadcastFloat(ENEMY_FOUND_X, 0);
						 rc.broadcastFloat(ENEMY_FOUND_Y, 0);
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
			
			if(rc.readBroadcast(ROMAN_EMPIRE_1)==0) {
	         	rc.broadcast(ROMAN_EMPIRE_1, rc.getID());
	        }
	        else if (rc.readBroadcast(ROMAN_EMPIRE_2)==0 && rc.readBroadcast(ROMAN_EMPIRE_1)!=rc.getID()){
	         	rc.broadcast(ROMAN_EMPIRE_2, rc.getID());
	        }
	        else if (rc.readBroadcast(ROMAN_EMPIRE_3)==0 && rc.readBroadcast(ROMAN_EMPIRE_1)!=rc.getID() && rc.readBroadcast(ROMAN_EMPIRE_2)!=rc.getID()) {
	         	rc.broadcast(ROMAN_EMPIRE_3, rc.getID());
	        }
			 
			
			if(id == rc.readBroadcastInt(ROMAN_EMPIRE_1) || id == rc.readBroadcastInt(ROMAN_EMPIRE_2) || id == rc.readBroadcastInt(ROMAN_EMPIRE_3)){
				if(!nearAllyArchon){
					Direction toAllyArchon = myLocation.directionTo(allyArchon);
					if(!moved) tryMove(toAllyArchon);
				}
				else{
					angolo += 0.01;
					Direction dir = new Direction(angolo);
					if(!moved) tryMove(dir);
				}
			}
		 }
	 
	 public void matrixStrategy() throws GameActionException {
	   	 gameInfo();
		 MapLocation enemyLocation = enemyRobots[0].location;
	   	 Direction toEnemy = myLocation.directionTo(enemyLocation);
	   	 Direction farFromEnemy= new Direction( toEnemy.radians+(float)Math.PI);
	
	   	 float minDamage = rc.getHealth();
	   	 int bestAngle = -40;
	   	 for (int angle = -40; angle < 40; angle += 10) {
		   	 MapLocation expectedLocation = myLocation.add((farFromEnemy).rotateLeftDegrees(angle), rc.getType().strideRadius);
		   	 float damage = expectedDamage(bullets, expectedLocation);
		
	   	 if (damage < minDamage) {
		   	 bestAngle = angle;
		   	 minDamage = damage;
		   	 }
	   	 }
		
	   	 if(!moved) tryMove(farFromEnemy.rotateLeftDegrees(bestAngle));
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
	 
	 public void tankSquadStrategy() throws GameActionException	 {
		 gameInfo();
		 if(rc.getType()==RobotType.TANK && (rc.readBroadcast(SQUAD_0)==0 || rc.readBroadcast(SQUAD_0)==rc.getID())){			 
			 rc.broadcast(SQUAD_0, rc.getID());
			 rc.broadcastFloat(TANK_1, myLocation.x);
			 rc.broadcastFloat(TANK_2, myLocation.y);
			 
		 }
		 
		 else if(rc.readBroadcast(SQUAD_0)!=0){
			 MapLocation myTank = new MapLocation(rc.readBroadcastFloat(TANK_1), rc.readBroadcastFloat(TANK_2));
			 Direction toMyTank = myLocation.directionTo(myTank);
			 boolean nearMyTank = myLocation.isWithinDistance(myTank, (RobotType.TANK.sensorRadius));
			 boolean viking = rc.readBroadcast(VIKING_1) == rc.getID() || rc.readBroadcast(VIKING_2)== rc.getID() || rc.readBroadcast(VIKING_3) == rc.getID();
			 boolean roman = rc.readBroadcast(ROMAN_EMPIRE_1) == rc.getID() || rc.readBroadcast(ROMAN_EMPIRE_2)== rc.getID() || rc.readBroadcast(ROMAN_EMPIRE_3) == rc.getID();
			 
			 if (!roman && !viking && rc.getType()==RobotType.SOLDIER && (rc.readBroadcast(SQUAD_1)==0 || rc.readBroadcast(SQUAD_1)==rc.getID())){					
				 rc.broadcast(SQUAD_1, rc.getID());
				 if(!nearMyTank){
					 if(!moved) tryMove(toMyTank);
				 }
				 else{
					 if(!moved) tryMove(randomDirection());
				 }
				 
			 }
			 else if (!roman && !viking && rc.getType()==RobotType.SOLDIER && (rc.readBroadcast(SQUAD_2)==0 && rc.readBroadcast(SQUAD_1)!=rc.getID() || rc.readBroadcast(SQUAD_2)==rc.getID())){
				 rc.broadcast(SQUAD_2, rc.getID());
				 if(!nearMyTank){
					 if(!moved) tryMove(toMyTank);
				 }
				 else{
					 if(!moved) tryMove(randomDirection());
				 }
			 }
	 		 else if (rc.getType()==RobotType.SCOUT && (rc.readBroadcast(SQUAD_3)==0 && rc.readBroadcast(SQUAD_1)!=rc.getID() && rc.readBroadcast(SQUAD_2)!=rc.getID() || rc.readBroadcast(SQUAD_3)==rc.getID())){
	 			rc.broadcast(SQUAD_3, rc.getID());
	 			 if(!nearMyTank){
	 				 if(!moved) tryMove(toMyTank);
				 }
				 else{
					 if(!moved) tryMove(randomDirection());
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
			 int enemySoldiers=0;
			 for(int i=0; i<enemyRobots.length; i++){
					if(enemyRobots[i].getType()==RobotType.SOLDIER || enemyRobots[i].getType()==RobotType.TANK || enemyRobots[i].getType()==RobotType.SCOUT || enemyRobots[i].getType()==RobotType.LUMBERJACK) enemySoldiers++;
				}
			 if(enemySoldiers>0){
				 return true;
			 }
			 else return false;
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

    public boolean isDead(){
    	if(rc.getHealth()<rc.getType().maxHealth/DEATH_DIVIDER) {
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    public void tryShoot() throws GameActionException{
    	
		gameInfo();
		 
        if(rc.getType().equals(RobotType.SCOUT)){
        	
	       	 if (enemyRobots.length>0) {
		             if (rc.canFireSingleShot()) {
		                 rc.fireSingleShot(rc.getLocation().directionTo(enemyRobots[0].location));
		             }
		             else matrixStrategy();
	         }
        }
        else if(rc.getType().equals(RobotType.SOLDIER)){
        
	       	 if (enemyRobots.length==1) {
		             if (rc.canFireSingleShot()) {
		                 rc.fireSingleShot(rc.getLocation().directionTo(enemyRobots[0].location));
		             }
		             else matrixStrategy();
		     }
	       	 else if(enemyRobots.length>1){
	                 if (rc.canFireTriadShot() && rc.getTeamBullets()>ENOUGHT_BULLETS_FOR_TRIAD) {
	                      rc.fireTriadShot(rc.getLocation().directionTo(enemyRobots[0].location));
	                 }
	                 else if (rc.canFireSingleShot()) {
		                 rc.fireSingleShot(rc.getLocation().directionTo(enemyRobots[0].location));
		             }
		             else matrixStrategy();
	           } 
        }
        else if(rc.getType().equals(RobotType.TANK)){
	            if (enemyRobots.length==1) {
	                if (rc.canFireSingleShot()) {
	                    rc.fireSingleShot(rc.getLocation().directionTo(enemyRobots[0].location));
	                }
	                else matrixStrategy();
	            }
	            else if (enemyRobots.length>1 && enemyRobots.length<=3 && rc.getTeamBullets()>ENOUGHT_BULLETS_FOR_TRIAD) {
	                if (rc.canFireTriadShot()) {
	                    rc.fireTriadShot(rc.getLocation().directionTo(enemyRobots[0].location));
	                }
	                else if (rc.canFireSingleShot()) {
		                 rc.fireSingleShot(rc.getLocation().directionTo(enemyRobots[0].location));
		             }
		             
	            }else if(enemyRobots.length>3){
	                if (rc.canFirePentadShot()) {
	                    rc.firePentadShot(rc.getLocation().directionTo(enemyRobots[0].location));
	                }
	                else if (rc.canFireTriadShot()) {
	                    rc.fireTriadShot(rc.getLocation().directionTo(enemyRobots[0].location));
	                }
	                else if (rc.canFireSingleShot()) {
		                 rc.fireSingleShot(rc.getLocation().directionTo(enemyRobots[0].location));
		             }
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
		bullets = rc.senseNearbyBullets(rc.getType().sensorRadius);
		moved= rc.hasMoved();
		attacked= rc.hasAttacked();
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
    
   }