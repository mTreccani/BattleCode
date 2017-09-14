package examplefuncsplayer;
import battlecode.common.*;

public class RobotPlayer{ 
    /**
     * questo metodo viene chiamato per la creazione dei robot
     * 
    **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        RobotLogic logica;

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
