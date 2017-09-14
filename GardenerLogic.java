package examplefuncsplayer;
import battlecode.common.*;

public class GardenerLogic extends RobotLogic{
	private static final int NO_SCOUT_NEEDED=1500;
	private static final int NO_TREES=0;
	private static final int TREEBULLET=100;
	private static final int MAXTREES=4;
	private static final int MAXSCOUTS=3;
	private static final int MAXSOLDIERS=5;
	private static final int MAXLUMBERJACKS=3;
	private static final int MINLUMBERJACKS=0;
	private static final int TREESLUMBERJACK=2;
	private static final int ROUNDLUMBERJACK=200;
	private static final int ROUNDTANK=600;
	private static final int MINTANKS=0;
	
	public float distanceFromArchon;
	public MapLocation lastKnownArchonLocation;
	
	public GardenerLogic (RobotController rc) throws GameActionException{
		super(rc);
		//setNumGardener(+1);
	}
	
	@Override
	public void run() throws GameActionException{
		
		int birthRound=rc.getRoundNum();
		boolean isDead=false;
		
        //codice che viene eseguito ogni round
		while(true){
			
            //il try/catch gestisce le eccezioni che altrimenti farebbero scomparire il robot
			try {
	            
				gameInfo();
				
				/*lastKnownArchonLocation= new MapLocation(rc.readBroadcastFloat(ARCHON_LOCATION_X), rc.readBroadcastFloat(ARCHON_LOCATION_Y));
	            distanceFromArchon=myLocation.distanceTo(lastKnownArchonLocation);
	            
	            if(distanceFromArchon>25 && getNumGardener()<2){
	            	Direction toMyArchon = myLocation.directionTo(lastKnownArchonLocation);
	            	tryMove(toMyArchon);
	            }*/
	            
				if (trees.length > NO_TREES && !isInDanger() && rc.readBroadcast(FARMING_GARDENER)!=rc.getID()){
	            	MapLocation treeLocation=trees[0].getLocation();
	            	Direction toTree=myLocation.directionTo(treeLocation);
	            	tryMove(toTree);
	            	if(rc.canWater(treeLocation)){
	            		rc.water(treeLocation);
	            	}
	            }
				if(isInDanger()){
					rc.broadcastFloat(I_HAVE_BEEN_HIT_X, myLocation.x);
					rc.broadcastFloat(I_HAVE_BEEN_HIT_Y, myLocation.y);
					matrixStrategy();
				}
				
	            if(rc.readBroadcastBoolean(FARM_ZONE) && (rc.readBroadcast(FARMING_GARDENER)==0 || rc.readBroadcast(FARMING_GARDENER)==rc.getID())){
                	rc.broadcast(FARMING_GARDENER, rc.getID());
                	farmStrategy();
                }
	           
	            Direction dir = randomDirection();
	            
	            if(getNumLumberjack()<1){
	            	rc.canBuildRobot(RobotType.LUMBERJACK, randomDirection());
	            	rc.buildRobot(RobotType.LUMBERJACK, dir);
                    setNumLumberjack(+1);
	            }
	            if (shouldBuildSoldier()) {
	                rc.buildRobot(RobotType.SOLDIER, dir);
	                setNumSoldier(+1);
	            } 
	            if (shouldBuildScout()) {
	                rc.buildRobot(RobotType.SCOUT, dir);
	                setNumScout(+1);
	            } 
	            if (shouldBuildLumberjack()) {
	                rc.buildRobot(RobotType.LUMBERJACK, dir);
	                setNumLumberjack(+1);
	            }
	            if (shouldBuildTank()) {
	                rc.buildRobot(RobotType.TANK, dir);
	                setNumTank(+1);
	            }
	            if (rc.getTeamBullets()>TREEBULLET && trees.length<MAXTREES && rc.canPlantTree(dir)){
	            	rc.plantTree(dir);
	            }
	            
	            // Move randomly
	            tryMove(dir);
	            
	            if(!isDead){
	            	if(isDead(birthRound)) setNumGardener(-1);
	            	if(rc.readBroadcast(FARMING_GARDENER)==rc.getID()) rc.broadcast(FARMING_GARDENER, 0);
	            	isDead=true;
	            }
	
                // Clock.yield() fa terminare il round
	            Clock.yield();
	        } catch (Exception e) {
	        	System.out.println("Gardener Exception");
	        	e.printStackTrace();
	        }	
		}		
	}
	/**
	 * controlla quando è possibile creare uno scout
	 * @return  TRUE: se è possibile FALSE: se non lo è
	 */
	public boolean shouldBuildScout() throws GameActionException{
		return (getNumScout()< MAXSCOUTS && rc.getRoundNum()< NO_SCOUT_NEEDED && rc.canBuildRobot(RobotType.SCOUT, randomDirection()));
	}
	/**
	 * controlla quando è possibile creare un soldier
	 * @return  TRUE: se è possibile FALSE: se non lo è
	 */
	public boolean shouldBuildSoldier() throws GameActionException {
		return ((isInDanger() && rc.canBuildRobot(RobotType.SOLDIER, randomDirection())) || getNumSoldier()< MAXSOLDIERS);
	}
	/**
	 * controlla quando è possibile creare un Lumberjack
	 * @return  TRUE: se è possibile FALSE: se non lo è
	 */
	public boolean shouldBuildLumberjack() throws GameActionException {
		return (getNumLumberjack()<=MAXLUMBERJACKS && (trees.length>=TREESLUMBERJACK || (rc.getRoundNum()>ROUNDLUMBERJACK && getNumLumberjack()==MINLUMBERJACKS)) && rc.canBuildRobot(RobotType.LUMBERJACK, randomDirection()));
	}
	/**
	 * controlla quando è possibile creare un Tank
	 * @return  TRUE: se è possibile FALSE: se non lo è
	 */
	public boolean shouldBuildTank() throws GameActionException {
		return (rc.getRoundNum()>ROUNDTANK && getNumTank()== MINTANKS && rc.canBuildRobot(RobotType.TANK, randomDirection()));
	}
	
}