package deathReaper2;

import java.util.ArrayList;
import java.util.Random;

import battlecode.common.*;


public class Utilities {
	static Random rnd;
    static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
            Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    static RobotType[] robotTypes = {RobotType.GUARD, RobotType.SCOUT, RobotType.SOLDIER, RobotType.SOLDIER,
            RobotType.SOLDIER, RobotType.SOLDIER, RobotType.TURRET, RobotType.TURRET};
    static int[] possibleMovements = new int[]{0,1,2,3,4,5,6,7};
    static int FOUND_ARCHON_X = 756736;
    static int FOUND_ARCHON_Y = 256253;
    static ArrayList<MapLocation> pastLocations = new ArrayList<MapLocation>();
    static boolean leader = false;
    public static int ELECTION = 73646;
    public static int infinity = 1000;
    static int archonX = -1;
    static int archonY = -1;
    static boolean archonFound = false;
    static int scoutNum = 0;
    static int targetX = -1;
    static int targetY = -1;
    static int MOVE_X = 182632;
    static int MOVE_Y = 1827371;
	static int turnsLeft = 0; 
    static Direction direction = null;
    static Direction pickNewDirection(RobotController rc) throws GameActionException {
        Direction scoutDirection = Utilities.randomDirection(rc);
        int turnsLeft = 100;
        return scoutDirection;
    }    
	 static void runaway(RobotController rc) throws GameActionException{ //code to have robot run away from all enemy troops Run cost = 141
	        RobotInfo[] enemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
	        RobotInfo[] friends = rc.senseNearbyRobots(rc.getLocation(), rc.getType().attackRadiusSquared, rc.getTeam());
			MapLocation toClose = closestRobot(rc, enemies);
			if(rc.getHealth()>20)
				rc.move(tryToMove(rc, rc.getLocation().directionTo(toClose).opposite()));
			else{
				MapLocation archon = new MapLocation(FOUND_ARCHON_X, FOUND_ARCHON_Y);
				rc.move(tryToMove(rc, rc.getLocation().directionTo(archon)));
			}
	    }
	    static MapLocation closestRobot(RobotController rc, RobotInfo[] robot) throws GameActionException{ //Run rotation cost once = 12
	        double nearSoFar = -100;
	        MapLocation nearestRobot = null;
	        for(RobotInfo r:robot) {
	            int near = r.location.distanceSquaredTo(rc.getLocation());
	            if(near> nearSoFar){
	                nearestRobot=r.location;
	                nearSoFar = near;
	        	}
	        }
	        return nearestRobot;
	    }
	    static MapLocation closestRobot(RobotController rc, RobotInfo[] robot, RobotType findRobot) throws GameActionException{
	        double nearSoFar = -100;
	        MapLocation nearestRobot = null;
	        for(RobotInfo r:robot)
	        {
	        	if(r.type == findRobot){
		            int near = r.location.distanceSquaredTo(rc.getLocation());
		            if(near> nearSoFar){
		                nearestRobot=r.location;
		                nearSoFar = near;
	                }
	        	}
	        }
	        return nearestRobot;
	    }

		public static MapLocation closestRobot(RobotController rc, MapLocation[] enemyArray) throws GameActionException{
	        double nearSoFar = -100;
	        MapLocation nearestRobot = null;
	        for(MapLocation r: enemyArray)
	        {
	            int near = r.distanceSquaredTo(rc.getLocation());
	            if(near> nearSoFar){
	                nearestRobot=r;
	                nearSoFar = near;
	        	}
	        }
	        return nearestRobot;
	    }
	    static Direction randomDirection(RobotController rc) throws GameActionException{
	    	Random rand = new Random(rc.getID());
			int fate = rand.nextInt(1000)*rc.getRobotCount();
	        return directions[fate%8];
	    }
	    static MapLocation findWeakest(RobotInfo[] listOfRobots){
	        double weakestSoFar = -100;
	        MapLocation weakestLocation = null;
	        for(RobotInfo r:listOfRobots){
	            double weakness = r.maxHealth-r.health;
	            if(weakness>weakestSoFar){
	                weakestLocation = r.location;
	                weakestSoFar=weakness;
	            }
	        }
	        return weakestLocation;
	    }
	    static MapLocation[] combineThings(RobotController rc, RobotInfo[] visibleEnemyArray, Signal[] incomingSignals) {
	        ArrayList<MapLocation> attackableEnemyArray = new ArrayList<MapLocation>();
	        for(RobotInfo r:visibleEnemyArray){
	            attackableEnemyArray.add(r.location);
	        }
	        for(Signal s:incomingSignals){
	            if(s.getTeam()==rc.getTeam().opponent()){
	                MapLocation enemySignalLocation = s.getLocation();
	                int distanceToSignalingEnemy = rc.getLocation().distanceSquaredTo(enemySignalLocation);
	                if(distanceToSignalingEnemy<=rc.getType().attackRadiusSquared){
	                    attackableEnemyArray.add(enemySignalLocation);
	                }
	            }
	        }
	        MapLocation[] finishedArray = new MapLocation[attackableEnemyArray.size()];
	        for(int i=0;i<attackableEnemyArray.size();i++){
	            finishedArray[i]=attackableEnemyArray.get(i);
	        }
	        return finishedArray;
	    }
	    public static Direction tryToMove(RobotController rc, Direction forward) throws GameActionException{ //Works to get location able to move to Run cost is = 16
	    	MapLocation ahead = rc.getLocation().add(forward);
	        for(int deltaD:possibleMovements){
	            Direction maybeForward = directions[(forward.ordinal()+deltaD+8)%8];
	            if(rc.canMove(maybeForward) && !pastLocations.contains(maybeForward)){
	               return maybeForward;
	            }else
	            {
	            	if(rc.getType().canClearRubble()){
	    	            if(rc.senseRubble(ahead)>=GameConstants.RUBBLE_OBSTRUCTION_THRESH){
	    	                rc.clearRubble(forward);
	    	            }
	                }
	            }
	        }
			return forward;
	    }
	    public static void creationSpot(RobotController rc, Direction ahead, RobotType robot, RobotInfo[] enemies) throws GameActionException //find spot to build
	    {
	        Direction candidateDirection = ahead;
	        MapLocation loc = rc.getLocation();
	        int zombie = 0;
			int opponent = 0;
	        if(rc.isCoreReady())
	        {
	        	for(RobotInfo r: enemies){
	        		if(r.team == Team.ZOMBIE){
						zombie++;
	        		}
	        		if(r.team == rc.getTeam().opponent()){
						opponent++;
	        		}
		        }
	        	if(zombie > 3){
        			robot = RobotType.GUARD;}
        		else if(robot == RobotType.SCOUT && scoutNum <= 3){
	        		scoutNum++;}
	        	else if (robot == RobotType.SCOUT) {
	        		robot = RobotType.SOLDIER;}
	        	for(int i:possibleMovements){
	            	 if(rc.isCoreReady()){
	                    candidateDirection = directions[i];
	                    loc = rc.getLocation().add(candidateDirection);
	                    if(rc.isLocationOccupied(loc) == false && rc.senseRubble(loc) < GameConstants.RUBBLE_OBSTRUCTION_THRESH){
	                        if(rc.canBuild(candidateDirection, robot)){
	                            rc.build(candidateDirection, robot);}
	                        	break;
	                    	}
	            	 	}
	            	}
	        }
	    }
	    public static void leaderElection(RobotController rc) throws GameActionException {
	        if (rc.getRoundNum()  == 0) {
	            // First step: elect a leader archon
	            if (rc.getType() == RobotType.ARCHON) {
	                rc.broadcastMessageSignal(ELECTION, 0, infinity);

	                Signal[] received = rc.emptySignalQueue();
	                int numArchons = 0;
	                for (Signal s : received) {
	                    if (s.getMessage() != null && s.getMessage()[0] == ELECTION) {
	                        numArchons++;
	                    }
	                }
	                if (numArchons == 0) {
	                    // If you haven't received anything yet, then you're the leader.
	                    leader = true;
	                    rc.setIndicatorString(0, "I'm the leader!");
	                } else {
	                    leader = false;
	                    rc.setIndicatorString(0, "I'm not the ldaer");
	                }
	            }
	        }
	    }
	    static void sendInstructions(RobotController rc, MapLocation enemy) throws GameActionException {
	        // Possible improvement: stop sending the same message over and over again
	        // since it will just increase our delay.
	        MapLocation loc = rc.getLocation();
	        if (!archonFound && rc.getRoundNum()%3 == 4) {
	            rc.broadcastMessageSignal(MOVE_X, loc.x, rc.getType().attackRadiusSquared);
	            rc.broadcastMessageSignal(MOVE_Y, loc.y, rc.getType().attackRadiusSquared);
	        } else {
	            rc.broadcastMessageSignal(MOVE_X, archonX, rc.getType().sensorRadiusSquared);
	            rc.broadcastMessageSignal(MOVE_Y, archonY, rc.getType().sensorRadiusSquared);
	        }
	        if(enemy.distanceSquaredTo(loc) < 3)
	        	{rc.broadcastMessageSignal(targetX, targetY, rc.getType().sensorRadiusSquared);}
	    }
	    static void readInstructions(RobotController rc) throws GameActionException {
	        Signal[] signals = rc.emptySignalQueue();
	        for (Signal s : signals) {
	            if (s.getTeam() != rc.getTeam()) {
	                continue;
	            }
	            if (s.getMessage() == null) {
	                continue;
	            }
	            int command = s.getMessage()[0];
	            if (command == MOVE_X) {
	                targetX = s.getMessage()[1];
	            } else if (command == MOVE_Y) {
	                targetY = s.getMessage()[1];
	            } else if (command == FOUND_ARCHON_X) {
	                archonX = s.getMessage()[1];
	            } else if (command == FOUND_ARCHON_Y) {
	                archonY = s.getMessage()[1];
	                archonFound = true;
	            }
	        }
	    }
}
