package examplefuncsplayer;
import battlecode.common.*;

public class RobotPlayer{ 
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
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
            	break;
        }
        
        logica.run();
	}
    
}
