package examplefuncsplayer;

import battlecode.common.*;

/**
 * classe che contiene tutte le strategie e i metodi utili per il movimento e il
 * combattimento tra i robot
 *
 */
public abstract class RobotLogic {
	
	 public RobotController rc;
	 
	 Team enemy;
	 Team ally;
	 MapLocation myLocation;
	 RobotInfo[] enemyRobots;
	 RobotInfo[] allyRobots;
	 TreeInfo[] trees;
	 TreeInfo[] allyTrees;
	 BulletInfo[] bullets;
	 Direction toEnemyArchon;
	 boolean farm=false;
	 
	 
	 float angleRunner=0;
	 public float angolo=0;
	 int killThemAll = 0;

	 public static final int DEATH_DIVIDER=5;
	 
	 public static final int ENOUGHT_BULLETS_FOR_TRIAD = 50;
	 
	 public static final float SOLAR_ANGLE_1=-0.45f;
	 public static final float SOLAR_ANGLE_2=0.0f;
	 public static final float SOLAR_ANGLE_3=+0.45f;

	 
	 //canali broadcast
	 
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
	 
	 public static final int ENEMY_ARCHON_X=30;
	 public static final int ENEMY_ARCHON_Y=31;
	 public static final int ENEMY_ARCHON_KILLED=32;
	 


	 public RobotLogic(RobotController rc) {
		 this.rc=rc;
		 enemy = rc.getTeam().opponent();
		 ally = rc.getTeam();
	 }
	 
	 /**
	 * metodo astratto da implementare in ogni classe
	 * @throws GameActionException
	 */
	 public abstract void run() throws GameActionException;
	 
	 /**
	 * tattica che fa muvovere il robot lungo la mappa implementatndo la strategia solare
	 * e la strategia matrix nel caso ci siano robot nemici vicini
	 * @throws GameActionException
	 */
	 public void runnerStrategy() throws GameActionException{
		 
		 gameInfo();
		 MapLocation[] enemyArchon = rc.getInitialArchonLocations(enemy);
		 if(enemyRobots.length>0){
			 matrixStrategy();
		 }
		 if(myLocation.distanceTo(enemyArchon[0])>30){
		     Direction toEnemyArchon = myLocation.directionTo(enemyArchon[0]);
		     float angle=(toEnemyArchon.radians+solarStrategy())+((float)Math.sin(angleRunner)*1.5f);  
			 Direction runner_dir= new Direction(angle); 
			 if(!rc.hasMoved()) tryMove (runner_dir);
			 angleRunner=(float)Math.PI/10+angleRunner;
		 }
		 else{
			 if(!rc.hasMoved()) tryMove (randomDirection());
		 }	 
		 
	 }
	
	 /**
	 * tattica per l'esplorazione della mappa e per la comunicazione in broadcast
	 * della presenza di aree con alberi e senza nemici
	 * @throws GameActionException
	 */
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
				 if(!rc.hasMoved()) tryMove (myLocation.directionTo(myLocation));
			 }
			 else {
				 rc.broadcastBoolean(FARM_ZONE, false);
				 farm=false;
			 }
		 }
		 else if(enemyRobots.length != 0){	 
			rc.broadcastBoolean(IS_A_GOOD_ZONE, false);
			rc.broadcastFloat(ENEMY_FOUND_X, enemyRobots[0].location.x);
			rc.broadcastFloat(ENEMY_FOUND_Y, enemyRobots[0].location.y);
			if(rc.readBroadcast(FARMING_SCOUT)==rc.getID()){
				rc.broadcastBoolean(FARM_ZONE, false);
				rc.broadcastFloat(FARMER_X, 0);
				rc.broadcastFloat(FARMER_Y, 0);
				rc.broadcastFloat(ENEMY_FOUND_X, enemyRobots[0].location.x);
				rc.broadcastFloat(ENEMY_FOUND_Y, enemyRobots[0].location.y);
			}
		}
		 angolo+= 0.1;
		 Direction dir = new Direction(angolo);
		 if(!rc.hasMoved()) tryMove (dir); 	
		 if(!rc.hasAttacked()) tryShoot();
	 }
	 
	 
	 /**
	 * tattica che fa spostare truppe di tipo lumberjack e gardner verso zone sicure,
	 * comunicate dalla exploreStrategy(), dove produrre unità e tagliare alberi
	 * @throws GameActionException
	 */
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
				 if(!rc.hasMoved()) tryMove (toFarmZone);
			 }
			 if(rc.getLocation().distanceTo(farmZone)<5 && rc.getHealth()>RobotType.LUMBERJACK.maxHealth*4/5){
				 rc.broadcastBoolean(IS_A_GOOD_ROAD, true);
			 }
		 }
		 
		 if(rc.readBroadcast(FARMING_GARDENER)==rc.getID() && farmerScoutX!=0 && farmerScoutY!=0){
			 if(rc.getLocation().distanceTo(farmZone)>2 && rc.readBroadcastBoolean(IS_A_GOOD_ROAD)){
				 if(!rc.hasMoved()) tryMove (toFarmZone);
			 }
		 }
		 
		 if(trees.length==0){
			 if(!rc.hasMoved()) tryMove (toAllyArchon);
		 }
	 }
	 
	 /**
	 * metodo per l'assegnamento di determinate direzioni da seguire ai robot
	 * @return l'angolo di movimento
	 * @throws GameActionException
	 */
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

     /**
     * tattica che invia soldati verso l'archon nemico in determinate condizioni, 
     * definite dalla distanza, dal numero di round trascorsi e di soldati e dalla presenza o meno
     * di nemici nelle vicinanze
     * @throws GameActionException
     */
	 public void vikingStrategy() throws GameActionException{
	
		 gameInfo();
		
		 int nearEnemies = enemyRobots.length;	
		 MapLocation enemyArchon= new MapLocation(rc.readBroadcastFloat(ENEMY_ARCHON_X),rc.readBroadcastFloat(ENEMY_ARCHON_Y));
		 boolean nearEnemyArchon = myLocation.isWithinDistance(enemyArchon, RobotType.ARCHON.sensorRadius); 
		
		 boolean viking = rc.readBroadcast(VIKING_1) == rc.getID() || rc.readBroadcast(VIKING_2)== rc.getID() || rc.readBroadcast(VIKING_3) == rc.getID();
		 boolean roman = rc.readBroadcast(ROMAN_EMPIRE_1) == rc.getID() || rc.readBroadcast(ROMAN_EMPIRE_2)== rc.getID() || rc.readBroadcast(ROMAN_EMPIRE_3) == rc.getID();
		 boolean tank = rc.readBroadcast(SQUAD_1) == rc.getID() || rc.readBroadcast(SQUAD_2)== rc.getID() || rc.readBroadcast(SQUAD_3) == rc.getID();

		 if(rc.readBroadcast(VIKING_1)==0 && !roman && !tank){
	         rc.broadcast(VIKING_1, rc.getID());
	     }
	     else if (rc.readBroadcast(VIKING_2)==0 && rc.readBroadcast(VIKING_1)!=rc.getID() && !roman && !tank){
	    	 rc.broadcast(VIKING_2, rc.getID());
	     }
	     else if (rc.readBroadcast(VIKING_3)==0 && rc.readBroadcast(VIKING_1)!=rc.getID() && rc.readBroadcast(VIKING_2)!=rc.getID() && !roman && !tank) {
	     	rc.broadcast(VIKING_3, rc.getID());
	     }
		 
		
		 if(!roman && !tank && viking && rc.readBroadcast(VIKING_1)!=0 && rc.readBroadcast(VIKING_2)!=0 && rc.readBroadcast(VIKING_3)!=0  && nearEnemies == 0){
			 if(!rc.hasMoved()) tryMove (directionToEnemyArchon());
		 }
		 else if((nearEnemyArchon || nearEnemies != 0)&& !roman){
			 if(!rc.hasMoved()) tryMove (randomDirection());
		 }
		 if(!rc.hasAttacked()) tryShoot();
	 }
	 
	 /**
	 * tattica destinata alle unità combattive per il soccorso dell'archon
	 * e di alleati in pericolo in determinate condizioni
	 * @throws GameActionException
	 */
	 public void totalHelpStrategy() throws GameActionException{
		 
		 gameInfo();
		 
		 float allyArchonX = rc.readBroadcastFloat(ARCHON_LOCATION_X);
		 float allyArchonY = rc.readBroadcastFloat(ARCHON_LOCATION_Y);
		 MapLocation allyArchon = new MapLocation(allyArchonX,allyArchonY);
		 boolean nearAllyArchon = myLocation.isWithinDistance(allyArchon, RobotType.ARCHON.sensorRadius);
		 
		 if(isArchonInDanger()){ 
			 if((rc.getType()==RobotType.SOLDIER || rc.getType()==RobotType.TANK) && !nearAllyArchon){
				 Direction toMyArchon= myLocation.directionTo(allyArchon);
				 if(!rc.hasMoved()) tryMove (toMyArchon);
			 }
		 }		 
		 else if(rc.readBroadcastFloat(ENEMY_FOUND_X)!= 0 && rc.readBroadcastFloat(ENEMY_FOUND_Y)!= 0){
			 if(rc.getType()==RobotType.SOLDIER || rc.getType()==RobotType.TANK){
				 MapLocation fireZone= new MapLocation(rc.readBroadcastFloat(ENEMY_FOUND_X),rc.readBroadcastFloat(ENEMY_FOUND_Y));
				 Direction toFireZone= rc.getLocation().directionTo(fireZone);
				 if(!rc.hasMoved()) tryMove (toFireZone);
				 if(rc.getLocation().distanceTo(fireZone)<3 && enemyRobots.length<=1){
					rc.broadcastFloat(ENEMY_FOUND_X, 0);
					rc.broadcastFloat(ENEMY_FOUND_Y, 0);
				 }
			 }
		 }
		 if(!rc.hasAttacked()) tryShoot();
	 }
	 
	 /**
	 * tattica difensiva tale per cui 3 soldati stanno nelle vicinanze
	 * dell'archon per difenderlo in caso di nemici nelle vicinanze
	 * @throws GameActionException
	 */
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
					if(!rc.hasMoved()) tryMove (toAllyArchon);
				}
				else{
					angolo += 0.1;
					Direction dir = new Direction(angolo);
					if(!rc.hasMoved()) tryMove (dir);
				}
			}
			if(!rc.hasAttacked()) tryShoot();
	 }
	 
	 /**
	 * tattica che permette alle unità di provare ad evitare i colpi dei
	 * nemici, scegliendo la direzione migliore in cui muoversi
	 * @throws GameActionException
	 */
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
		
	   	 if(!rc.hasMoved()) tryMove (farFromEnemy.rotateLeftDegrees(bestAngle));
   	 }
	 
	 /**
	 * metodo che calcola il danno che si aspetta di subire dai proiettili nelle vicinanze
	 * @param bullets proiettili nemici nelle vicinanze
	 * @param loc
	 * @return
	 */
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
		 
		 int soldierSquad = 0;
		 MapLocation enemyArchon= new MapLocation(rc.readBroadcastFloat(ENEMY_ARCHON_X),rc.readBroadcastFloat(ENEMY_ARCHON_Y));
		 boolean nearEnemyArchon = myLocation.isWithinDistance(enemyArchon, RobotType.ARCHON.sensorRadius); 
		 
		 
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
					 if(!rc.hasMoved()) tryMove (toMyTank);
				 }
				 else{
					 if(!rc.hasMoved()) tryMove (randomDirection());
				 }
				 
			 }
			 else if (!roman && !viking && rc.getType()==RobotType.SOLDIER && (rc.readBroadcast(SQUAD_2)==0 && rc.readBroadcast(SQUAD_1)!=rc.getID() || rc.readBroadcast(SQUAD_2)==rc.getID())){
				 rc.broadcast(SQUAD_2, rc.getID());
				 if(!nearMyTank){
					 if(!rc.hasMoved()) tryMove (toMyTank);
				 }
				 else{
					 if(!rc.hasMoved()) tryMove (randomDirection());
				 }
			 }
	 		 else if (!roman && !viking && rc.getType()==RobotType.SOLDIER && (rc.readBroadcast(SQUAD_3)==0 && rc.readBroadcast(SQUAD_1)!=rc.getID() && rc.readBroadcast(SQUAD_2)!=rc.getID() || rc.readBroadcast(SQUAD_3)==rc.getID())){
	 			rc.broadcast(SQUAD_3, rc.getID());
	 			 if(!nearMyTank){
	 				 if(!rc.hasMoved()) tryMove (toMyTank);
				 }
				 else{
					 if(!rc.hasMoved()) tryMove (randomDirection());
				 }
			 } 
		 }
		 
		 if(rc.getType().equals(RobotType.TANK)){
			 RobotInfo[] readyToAttack = rc.senseNearbyRobots(rc.getType().sensorRadius, ally);
			 for(int i=0;i<readyToAttack.length;i++){
				 if(readyToAttack[i].getType().equals(RobotType.SOLDIER)){
					 soldierSquad++;
				 }
			 }
		 }
		 if(soldierSquad>=3 && !nearEnemyArchon){
			 if(!rc.hasMoved()) tryMove (directionToEnemyArchon());
		 }
		 else{
			 if(!rc.hasMoved()) tryMove (randomDirection());
		 }
		 if(!rc.hasAttacked()) tryShoot();
	 }
	 
	 /**
	  * metodo che permette alle unità di mandare in broadcast la nuova posizione 
	  * dell'archon nemico, se questo viene localizzato
	  * @throws GameActionException
	  */
	 public void trySenseEnemyArchon() throws GameActionException{
		 
		 gameInfo();
		 
		 if(enemyRobots.length>0){
			 for(int i=0; i<enemyRobots.length; i++){
				 if(enemyRobots[i].getType()==RobotType.ARCHON){
					 rc.broadcastFloat(ENEMY_ARCHON_X, enemyRobots[i].location.x);
					 rc.broadcastFloat(ENEMY_ARCHON_Y, enemyRobots[i].location.y);
				 }
			 }
		 }
	 }
	 
	 /**
	  * metodo che permette di leggere in Broadcast la posizione 
	  * dell'archon nemico e creare la direzione verso lui
	  *@return toEnemyArchon: la direzione verso L'archon nemico 
	  *@throws GameActionException
	  */
	 public Direction directionToEnemyArchon() throws GameActionException{
		 
		 float enemyArchonX= rc.readBroadcastFloat(ENEMY_ARCHON_X);
		 float enemyArchonY= rc.readBroadcastFloat(ENEMY_ARCHON_Y); 
		 MapLocation enemyArchon= new MapLocation(enemyArchonX,enemyArchonY);
		 Direction toEnemyArchon= rc.getLocation().directionTo(enemyArchon);
		 return toEnemyArchon;
	 }

	
	 /**
	 * metodo che stabilisce se il robot è in pericolo, ovvero se ci sono
	 * nemici nelle vicinanze
	 * @return TRUE: se è in pericolo, FALSE altrimenti
	 */
	 boolean isInDanger() throws GameActionException{
		 
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
	 
	 /**
	 * metodo che controlla se l'archon è in pericolo
	 * @return TRUE: se è in pericolo, FALSE altrimenti
	 * @throws GameActionException
	 */
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
    * controlla se la truppa è da considerare morta
    * @return TRUE: se si può considerare morta, FALSE altrimenti
    * @throws GameActionException 
    */
    public boolean isDead(){
    	if(rc.getHealth()<rc.getType().maxHealth/DEATH_DIVIDER) {
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    /**
     * metodo che controlla se l'Archon nemico è considerabile morto
     * @throws GameActionException
     */
    public void enemyArchonKilled() throws GameActionException{
    	
    	gameInfo();
    	

    	float enemyArchonX= rc.readBroadcastFloat(ENEMY_ARCHON_X);
		float enemyArchonY= rc.readBroadcastFloat(ENEMY_ARCHON_Y); 
		MapLocation enemyArchon= new MapLocation(enemyArchonX,enemyArchonY);
		boolean nearEnemyArchon = myLocation.isWithinDistance(enemyArchon, RobotType.ARCHON.sensorRadius); 
		int archon = -1;
		if(nearEnemyArchon){
	    	for(int i=0; i<enemyRobots.length; i++){
				 if(enemyRobots[i].getType()==RobotType.ARCHON){
					 archon = i;
					 rc.broadcastBoolean(ENEMY_ARCHON_KILLED, false);
				 }
			} 
		}
		if(archon != -1 && enemyRobots[archon].getHealth()<RobotType.ARCHON.maxHealth*1/5){
			rc.broadcastBoolean(ENEMY_ARCHON_KILLED, true);
		}
    }
    
    /**metodo che permette alle unità combattenti di attaccare i nemici senza ulteriori strategie
     * @throws GameActionException
    */
    public void killThemAll() throws GameActionException{
    	gameInfo();

		MapLocation enemyArchon= new MapLocation(rc.readBroadcastFloat(ENEMY_ARCHON_X),rc.readBroadcastFloat(ENEMY_ARCHON_Y));
		boolean nearEnemyArchon = myLocation.isWithinDistance(enemyArchon, RobotType.ARCHON.sensorRadius); 
    	if(!nearEnemyArchon && killThemAll == 0){
    		Direction toEnemyArchon = myLocation.directionTo(enemyArchon);
    	    float angle=(toEnemyArchon.radians+solarStrategy())+((float)Math.sin(angleRunner)*1.5f);  
    		Direction runner_dir= new Direction(angle); 
    		if(!rc.hasMoved()) tryMove (runner_dir);
    		angleRunner=(float)Math.PI/10+angleRunner;
    	}
    	else{
    		killThemAll = 1;
    		
    	}
    	if(killThemAll != 0){
    		angolo+= (100/rc.getRoundNum());
   		 	Direction dir = new Direction(angolo);
    		if(!rc.hasMoved()) tryMove (dir);
    	}
		if(!rc.hasAttacked()) tryShoot();
		 	
    }
    
    /**
    * metodo che permette alle truppe di sparare, se possibile, 
    * in base al loro tipo( soldier, tank, etc..) 
    * in caso contrartio esegue la matrixStrategy()
    * @throws GameActionException
    */
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
    * cerca di muovere la truppa nella direzione data
    *
    * @param dir la direzione in cui si vuole far muovere la truppa
    * @return TRUE: se il movimento è stato eseguito FALSE: se non è stato eseguito
    * @throws GameActionException
    */
    boolean tryMove (Direction dir) throws GameActionException{
        return tryMove (dir,20,3);
    }

    /**
    * cerca di muovere la truppa nella direzione data
    *
    * @param dir la direzione in cui si vuole far muovere la truppa
    * @param degreeOffset spazio tra le direzioni controllate
    * @param checksPerSide numero di direzioni controllate su ogni lato nel caso non sia possibile utilizzare quella data
    * @return TRUE: se il movimento è stato eseguito FALSE: se non è stato eseguito
    * @throws GameActionException
    */
    boolean tryMove (Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            currentCheck++;
        }

        return false;
    }

    /**
    * controlla se un proiettile sta intrando in collisione con il robot
    * @param bullet il proiettile da controllare
    * @return TRUE: se il percorso del proiettile incide la posizione del robot FALSE: se non la incide
    */
    boolean willCollideWithMe(BulletInfo bullet) {
        MapLocation myLocation = rc.getLocation();

        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        if (Math.abs(theta) > Math.PI/2) {
            return false;
        }


        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius);
    }
    
    /**
    * metodo che aggiorna parametri utili in ogni istante della partita
    */
    public void gameInfo() throws GameActionException{
		myLocation = rc.getLocation();
		enemyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemy);
		allyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, ally);
		trees = rc.senseNearbyTrees(rc.getType().sensorRadius,Team.NEUTRAL);
		allyTrees = rc.senseNearbyTrees(rc.getType().sensorRadius,ally);
		bullets = rc.senseNearbyBullets(rc.getType().sensorRadius);
    }
   
    /**
    * ritorna il numero di scout
    * @return il numero di scout
    * @throws GameActionException
    */
    public int getNumScout() throws GameActionException{
    	return rc.readBroadcast(NUM_SCOUT);
    }
    
    /**
    * setta il numero di scout
    * @param n numero di truppe da aggiungere/rimuovere
    * @throws GameActionException
    */
    public void setNumScout(int n) throws GameActionException{
    	rc.broadcast(NUM_SCOUT, rc.readBroadcast(NUM_SCOUT) + n );
    }
    
    /**
     * ritorna il numero di soldiers
     * @return il numero di soldiers
     * @throws GameActionException
     */
    public int getNumSoldier() throws GameActionException{
    	return rc.readBroadcast(NUM_SOLDIER);
    }
    
    /**
     * setta il numero di soldiers
     * @param n numero di truppe da aggiungere/rimuovere
     * @throws GameActionException
     */
    public void setNumSoldier(int n) throws GameActionException{
    	rc.broadcast(NUM_SOLDIER, rc.readBroadcast(NUM_SOLDIER) + n );
    }
    
    /**
     * ritorna il numero di gardeners
     * @return il numero di gardeners
     * @throws GameActionException
     */
    public int getNumGardener() throws GameActionException{
    	return rc.readBroadcast(NUM_GARDENER);
    }
    
    /**
     * setta il numero di gardeners
     * @param n numero di truppe da aggiungere/rimuovere
     * @throws GameActionException
     */
    public void setNumGardener(int n) throws GameActionException{
    	rc.broadcast(NUM_GARDENER, rc.readBroadcast(NUM_GARDENER) + n );
    }
    
    /**
     * ritorna il numero di lumberjacks
     * @return il numero di lumberjacks
     * @throws GameActionException
     */
    public int getNumLumberjack() throws GameActionException{
    	return rc.readBroadcast(NUM_LUMBERJACK);
    }
    
    /**
     * setta il numero di lumberjacks
     * @param n numero di truppe da aggiungere/rimuovere
     * @throws GameActionException
     */
    public void setNumLumberjack(int n) throws GameActionException{
    	rc.broadcast(NUM_LUMBERJACK, rc.readBroadcast(NUM_LUMBERJACK) + n );
    }
    
    /**
     * ritorna il numero di tank
     * @return il numero di tank
     * @throws GameActionException
     */
    public int getNumTank() throws GameActionException{
    	return rc.readBroadcast(NUM_TANK);
    }
    
    /**
     * setta il numero di tank
     * @param n numero di truppe da aggiungere/rimuovere
     * @throws GameActionException
     */
    public void setNumTank(int n) throws GameActionException{
    	rc.broadcast(NUM_TANK, rc.readBroadcast(NUM_TANK) + n );
    }
    
   }