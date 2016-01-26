package deathReaper2;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {

    static RobotController rc;
    static Random rnd;
    static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
            Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    static RobotType[] robotTypes = {RobotType.GUARD, RobotType.SCOUT, RobotType.SOLDIER, RobotType.SOLDIER,
            RobotType.SOLDIER, RobotType.SOLDIER, RobotType.TURRET, RobotType.TURRET};
    static int[] possibleMovements = new int[]{0,1,-1,2,-2,3,-3,4};
    static ArrayList<MapLocation> pastLocations = new ArrayList<MapLocation>();
    static boolean leader = false;
    private static int ELECTION = 73646;
    private static int infinity = 1000;
    static int targetX = -1;
    static int targetY = -1;
    static int archonX = -1;
    static int archonY = -1;
    static boolean archonFound = false;
    static int MOVE_X = 182632;
    static int MOVE_Y = 1827371;
    static int FOUND_ARCHON_X = 756736;
    static int FOUND_ARCHON_Y = 256253;
    static int scoutNum = 0;
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
	                    ArchonCode.archonCode(rcIN);break;}
	        		case VIPER:{
	                    viperCode.viperCode();break;}
	        		case GUARD:{
	        			guardCode.guardCode();break;}
	        		case SCOUT:{
	        			 scoutCode.scoutCode();break;}
	        		case SOLDIER:{
	        			soldierCode.soldierCode();break;}
	        		case TURRET:{
	        			turretCode.turretCode();break;}
	        		case TTM:{
	        			ttmCode.ttmCode();break;}
				default:
					break;
        		}
            }catch (Exception e) {
                e.printStackTrace();}
            Clock.yield();
        }
    }   
}
