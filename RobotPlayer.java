package examplefuncsplayer;
import battlecode.common.*;

public class RobotPlayer{
	 static RobotController rc;
	 private static int numScout=0;
	 private static int numSoldier=0;
	 private static int numGardener=0;
	 private static int numLumberjack=0;
	 private static int numArchon=1;
	 private static int numTank=0;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;
        RobotLogic logica;
        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
        switch (rc.getType()) {
            case ARCHON:
                logica = new ArchonLogic(rc);
                break;
            case GARDENER:
            	logica = new GardenerLogic(rc);
                break;
            case SOLDIER:
            	logica = new SoldierLogic(rc);
                break;
            case LUMBERJACK:
            	logica = new LumberjackLogic(rc);
                break;
            case TANK:
            	logica = new TankLogic(rc);
            	break;
            case SCOUT:
            	logica = new ScoutLogic(rc);
            	break;
            default:
            	logica = null;
        }
        logica.run();
	}

    
    //metodi RobotLogic
    

    public static int getNumScout(){
    	return numScout;
    }
    
    public static void setNumScout(int n){
    	numScout++;
    }
    
    public static int getNumSoldier(){
    	return numSoldier;
    }
    
    public static void setNumSoldier(int n){
    	numSoldier=numSoldier+n;
    }
    
    public static int getNumGardener(){
    	return numGardener;
    }
    
    public static void setNumGardener(int n){
    	numGardener=numGardener+n;
    }
    
    public static int getNumLumberjack(){
    	return numLumberjack;
    }
    
    public static void setNumLumberjack(int n){
    	numLumberjack=numLumberjack+n;
    }
    
    public static int getNumTank(){
    	return numTank;
    }
    
    public static void setNumTank(int n){
    	numTank=numTank+n;
    }
    
    public static int getNumArchon(){
    	return numArchon;
    }
    
    public static void setNumArchon(int n){
    	numArchon=numArchon+n;
    }
    
    
}
