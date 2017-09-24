package examplefuncsplayer;
import battlecode.common.*;

/**
 * classe che richiama il metodo run di ogni robot e quindi li mantiene vivi in modo che non
 * muoiano appena vengono creati
 *
 */
public class RobotPlayer{ 
    
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
