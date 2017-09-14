package examplefuncsplayer;

import battlecode.common.*;

public abstract class RobotLogic {
	
	 public RobotController rc;
	 
	 Team enemy;
	 Team ally;
	 MapLocation myLocation;
	 RobotInfo[] enemyRobots;
	 RobotInfo[] allyRobots;  
	 TreeInfo[] trees;
	 BulletInfo[] bullets;
	 float angleRunner=0;
	 public float angolo=0;
	 public static final int DEATH_DIVIDER=4;
	 public static final int REGENERATION_ROUNDS=20;
	 public static final int VIKING_NUM_SOLDIER = 3;
	 public static final int ENOUGHT_BULLETS_FOR_TRIAD = 50;
	 public static final float SOLAR_ANGLE_1=-0.45f;
	 public static final float SOLAR_ANGLE_2=0.0f;
	 public static final float SOLAR_ANGLE_3=+0.45f;
	 boolean farm=false;
	 int i = 0;
	 int j=0;

	 
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
	 public static final int IS_A_GOOD_ROAD=18;
	 public static final int FARMING_GARDENER=19;
	 public static final int FARMING_SCOUT=20;
	 public static final int SOLAR_1=21;
	 public static final int SOLAR_2=22;
	 public static final int SOLAR_3=23;
	 public static final int FORMICA_X=100;
	 public static final int FORMICA_Y=100;

	 
	 
	 
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
	 
	 /**
	  * metodo astratto da implementare in ogni classe
	  * @throws GameActionException
	  */
	 public abstract void run() throws GameActionException;
	 
	 /**
	  * tattica che fa muvovere il robot verso l'archon nemico implementatndo la strategia solare
	  * e la strategia matrix nel caso ci siano robot nemici vicini
	  * @throws GameActionException
	  */
	 public void runnerStrategy() throws GameActionException{
		 
		 gameInfo();
		 MapLocation[] enemyArchon = rc.getInitialArchonLocations(enemy);
		 if(enemyRobots.length>0) matrixStrategy();
		 if(myLocation.distanceTo(enemyArchon[0])>35){
		     Direction toEnemyArchon = myLocation.directionTo(enemyArchon[0]);
		     float angle=(toEnemyArchon.radians+solarStrategy())+((float)Math.sin(angleRunner)*1.5f);  
			 Direction runner_dir= new Direction(angle); 
			 tryMove(runner_dir);
			
			 angleRunner=(float)Math.PI/10+angleRunner;
		 }
		 else{
			 tryMove(randomDirection());
		 }	 
		 
	 }
	
	 /**
	  * tattica per l'esplorazione della mappa e per la comunicazione della presenza di aree
	  * con alberi e senza nemici
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
				 tryMove(myLocation.directionTo(myLocation));
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
					 tryMove(toMyArchon);
				 }
			 }
		 }
		 
		 angolo+= 0.09;
		 Direction dir = new Direction(angolo);
		 tryMove(dir); 
			
	 }
	
	
	
	 public void seguiFormica() throws GameActionException{
		 gameInfo();
		 float prossimoPassoX = rc.readBroadcastFloat(FORMICA_X+j);		
		 j++;
		 
		 float prossimoPassoY = rc.readBroadcastFloat(FORMICA_Y+j);
		 j++;
		
		 tryMove(new Direction(prossimoPassoX,prossimoPassoY));
		  }
	 	
	 
	public void formica() throws GameActionException {
	    	
	    gameInfo();
	   	float allyArchonX = rc.readBroadcastFloat(ARCHON_LOCATION_X);
		float allyArchonY = rc.readBroadcastFloat(ARCHON_LOCATION_Y);    		
	   	if(!(isInDanger()) && rc.getRoundNum()%20==0 && i<40) {
	   		MapLocation myLocation = rc.getLocation();
	   		rc.broadcastFloat(FORMICA_X+i, myLocation.x);
	   		rc.broadcastFloat(FORMICA_Y+i+1, myLocation.y);
    		i=i+2;
    		MapLocation [] enemyLocationArchon = rc.getInitialArchonLocations(enemy);
    		Direction enemyLocation = myLocation.directionTo(enemyLocationArchon[0]);
			tryMove(enemyLocation);
		}else if(isInDanger()) {
    		tryShoot();
    	}else{
    		MapLocation [] enemyLocationArchon = rc.getInitialArchonLocations(enemy);
    		Direction enemyLocation = myLocation.directionTo(enemyLocationArchon[0]);
    		tryMove(enemyLocation);
    	}
    
    }	 
	 /**
	  * tattica che fa spostare truppe di tipo lumberjack e gardner verso zone sicure in cui sono
	  * presenti alberi comunicate dalla exploreStrategy()
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
				 tryMove(toFarmZone);
			 }
			 if(rc.getLocation().distanceTo(farmZone)<5 && rc.getHealth()>RobotType.LUMBERJACK.maxHealth*4/5){
				 rc.broadcastBoolean(IS_A_GOOD_ROAD, true);
			 }
		 }
		 
		 if(rc.readBroadcast(FARMING_GARDENER)==rc.getID() && farmerScoutX!=0 && farmerScoutY!=0){
			 if(rc.getLocation().distanceTo(farmZone)>2 && rc.readBroadcastBoolean(IS_A_GOOD_ROAD)){
				 tryMove(toFarmZone);
			 }
		 }
		 
		 if(trees.length==0) tryMove(toAllyArchon);
	 }
	 
	 /**
	  * metodo per il movimento lungo linee partendo dalla base
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
	  * tattica che invia tre soldati alla volta verso l'archon nemico
	  * se i soldati sono meno di tre, non hanno nemici nel proprio raggio e non si trovano nel 
	  * raggio dell'arhon si muovono verso l'archon alleato
	  * @throws GameActionException
	  */
	 public void vikingStrategy() throws GameActionException{
	
		 gameInfo();
		
		 MapLocation[] enemyArchon = rc.getInitialArchonLocations(enemy); 
		 float allyArchonX = rc.readBroadcastFloat(ARCHON_LOCATION_X);
		 float allyArchonY = rc.readBroadcastFloat(ARCHON_LOCATION_Y);
		 MapLocation allyArchon = new MapLocation(allyArchonX,allyArchonY);
		 int nearEnemies = enemyRobots.length;	
		 boolean nearEnemyArchon = myLocation.isWithinDistance(enemyArchon[0], RobotType.ARCHON.sensorRadius); 
		 boolean nearAllyArchon = myLocation.isWithinDistance(allyArchon, RobotType.ARCHON.sensorRadius);
		
		 if(rc.readBroadcast(VIKING_1)==0) {
         	rc.broadcast(VIKING_1, rc.getID());
         }
         else if (rc.readBroadcast(VIKING_2)==0 && rc.readBroadcast(VIKING_1)!=rc.getID()){
         	rc.broadcast(VIKING_2, rc.getID());
         }
         else if (rc.readBroadcast(VIKING_3)==0 && rc.readBroadcast(VIKING_1)!=rc.getID() && rc.readBroadcast(VIKING_2)!=rc.getID()) {
         	rc.broadcast(VIKING_3, rc.getID());
         }
		 
		 boolean soldier = rc.readBroadcast(VIKING_1)== rc.getID() || rc.readBroadcast(VIKING_2)== rc.getID() || rc.readBroadcast(VIKING_3) == rc.getID();
		 if(soldier && rc.readBroadcast(VIKING_1)!=0 && rc.readBroadcast(VIKING_2)!=0 && rc.readBroadcast(VIKING_3)!=0 && rc.getRoundNum()%1000<500  && nearEnemies == 0){
			 Direction toEnemyArchon = myLocation.directionTo(enemyArchon[0]);
			 tryMove(toEnemyArchon);
		 }
		 else if((rc.readBroadcast(VIKING_1)==0 || rc.readBroadcast(VIKING_2)==0 || rc.readBroadcast(VIKING_3)==0) && !nearAllyArchon && nearEnemies==0){
			 Direction toAllyArchon = myLocation.directionTo(allyArchon);
			 tryMove(toAllyArchon);
		 }
		 else if(nearEnemyArchon || nearEnemies != 0 || nearAllyArchon || !soldier){
			 exploreStrategy();
		 }
	 }
	 
	 /**
	  * le truppe di tipo soldier e tank vanno in soccorso delle truppe che vengono colpite
	  * dal nemico
	  * @throws GameActionException
	  */
	 public void totalHelpStrategy() throws GameActionException{
		 
		 gameInfo();
		 
		 if(isArchonInDanger()){
			 
			 if(rc.getType()==RobotType.SOLDIER || rc.getType()==RobotType.TANK){
				 MapLocation myArchon= new MapLocation(rc.readBroadcastFloat(ARCHON_LOCATION_X),rc.readBroadcastFloat(ARCHON_LOCATION_Y));
				 Direction toMyArchon= rc.getLocation().directionTo(myArchon);
				 tryMove(toMyArchon);
				 
				 tryShoot();
				  
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
	 
	 /**
	  * tre soldati girano attorno all'archon alleato in modo da difenderlo
	  * @throws GameActionException
	  */
	 public void romanEmpireStrategy() throws GameActionException{
		 
			gameInfo();
			float allyArchonX = rc.readBroadcastFloat(ARCHON_LOCATION_X);
			float allyArchonY = rc.readBroadcastFloat(ARCHON_LOCATION_Y);
			MapLocation allyArchon = new MapLocation(allyArchonX,allyArchonY);
			boolean nearAllyArchon = myLocation.isWithinDistance(allyArchon, RobotType.ARCHON.sensorRadius);
			int id = rc.getID();
			if(id == rc.readBroadcastInt(ROMAN_EMPIRE_1) || id == rc.readBroadcastInt(ROMAN_EMPIRE_2) || id == rc.readBroadcastInt(ROMAN_EMPIRE_3)){
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
	 
	 /**
	  * le truppe cercano di evitare i colpi nemici quando gli stanno sparando
	  * @throws GameActionException
	  */
	 public void matrixStrategy() throws GameActionException {
	   	 
		 gameInfo();
		 
		 MapLocation currLocation = rc.getLocation();
		 MapLocation enemyLocation = enemyRobots[0].location;
	   	 Direction toEnemy = currLocation.directionTo(enemyLocation);
	   	 Direction farFromEnemy= new Direction( toEnemy.radians+(float)Math.PI);
	
	   	 float minDamage = rc.getHealth();
	   	 int bestAngle = -40;
	   	 for (int angle = -40; angle < 40; angle += 10) {
		   	 MapLocation expectedLocation = currLocation.add((farFromEnemy).rotateLeftDegrees(angle), rc.getType().strideRadius);
		   	 float damage = expectedDamage(bullets, expectedLocation);
		
		   	 if (damage < minDamage) {
			   	 bestAngle = angle;
			   	 minDamage = damage;
			   	 }
		   	 }
		
		   	 tryMove(farFromEnemy.rotateLeftDegrees(bestAngle));
   	 }
	 
	 /**
	  * danno che si aspetta di subire dai proiettili
	  * @param bullets proiettili che vengono sparati dal nemico
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
	
	 
	 /**
	  * è in pericolo se ci sono nemici nelle vicinanze 
	  * @return TRUE: se è in pericolo FALSE: se non lo è 
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
	 
	 /**
	  * controlla se l'archon è in pericolo
	  * @return TRUE: se è in pericolo, FALSE: se non lo è
	  * @throws GameActionException
	  */
	 public boolean isArchonInDanger() throws GameActionException{
	    	return rc.readBroadcastBoolean(IS_ARCHON_IN_DANGER);	
	    }
	 
    /**
     * ritona una direzione random
     * @return una Direzione random
     */
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

    /**
     * controlla se la truppa è morta
     * @param birthRound round di creazione della truppa
     * @return
     * @throws GameActionException 
     */
    public boolean isDead(int birthRound) throws GameActionException{
    	if(rc.getHealth()<rc.getType().maxHealth/DEATH_DIVIDER && rc.getRoundNum()-birthRound> REGENERATION_ROUNDS){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    /**
     * controlla se la truppa può sparare e in caso contrartio esegue la matrixStrategy()
     * @throws GameActionException
     */
    public void tryShoot() throws GameActionException{
    	
		gameInfo();
        if(rc.getType().equals(RobotType.SCOUT)){
        	
	       	 if (enemyRobots.length==1) {
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
    boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
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
    boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

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
     *
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
     * metodi utili in ogni strategia
     */
    public void gameInfo(){
		myLocation = rc.getLocation();
		enemyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemy);
		allyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, ally);//per le strategie
		trees = rc.senseNearbyTrees(rc.getType().sensorRadius);
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
     * ritorna il numero di soldier
     * @return il numero di soldier
     * @throws GameActionException
     */
    public int getNumSoldier() throws GameActionException{
    	return rc.readBroadcast(NUM_SOLDIER);
    }
    
    /**
     * setta il numero di soldier
     * @param n numero di truppe da aggiungere/rimuovere
     * @throws GameActionException
     */
    public void setNumSoldier(int n) throws GameActionException{
    	rc.broadcast(NUM_SOLDIER, rc.readBroadcast(NUM_SOLDIER) + n );
    }
    
    /**
     * ritorna il numero di gardener
     * @return il numero di gardener
     * @throws GameActionException
     */
    public int getNumGardener() throws GameActionException{
    	return rc.readBroadcast(NUM_GARDENER);
    }
    
    /**
     * setta il numero di gardener
     * @param n numero di truppe da aggiungere/rimuovere
     * @throws GameActionException
     */
    public void setNumGardener(int n) throws GameActionException{
    	rc.broadcast(NUM_GARDENER, rc.readBroadcast(NUM_GARDENER) + n );
    }
    
    /**
     * ritorna il numero di lumberjack
     * @return il numero di lumberjack
     * @throws GameActionException
     */
    public int getNumLumberjack() throws GameActionException{
    	return rc.readBroadcast(NUM_LUMBERJACK);
    }
    
    /**
     * setta il numero di lumberjack
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
