package deathReaper2;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {

    static RobotController rc;
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rcIN) {
        rc =rcIN;
        while(true){
        	try {
        		switch(rc.getType()){
	        		case ARCHON: {
	                    ArchonCode.archonCode(rc);break;}
	        		case VIPER:{
	                    Infantry.viperCode(rc);break;}
	        		case GUARD:{
	        			Infantry.guardCode(rc);break;}
	        		case SCOUT:{
	        			 scoutCode.Scout(rc);break;}
	        		case SOLDIER:{
	        			Infantry.soldierCode(rc);break;}
	        		case TURRET:{
	        			LongKnives.turretCode(rc);break;}
	        		case TTM:{
	        			LongKnives.ttmCode(rc);break;}
				default:
					break;
        		}
            }catch (Exception e) {
                e.printStackTrace();}
            Clock.yield();
        }
    }   
}
