package deathReaper2;

import java.util.ArrayList;
import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Signal;

public class Utilities {
	 private static void runaway() throws GameActionException{ //code to have robot run away from all enemy troops Run cost = 141
	        RobotInfo[] enemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
	        RobotInfo[] friends = rc.senseNearbyRobots(rc.getLocation(), rc.getType().attackRadiusSquared, rc.getTeam());
			MapLocation toClose = closestRobot(enemies);
			if(rc.getHealth()>20)
				rc.move(tryToMove(rc.getLocation().directionTo(toClose).opposite()));
			else{
				MapLocation archon = new MapLocation(FOUND_ARCHON_X, FOUND_ARCHON_Y);
				rc.move(tryToMove(rc.getLocation().directionTo(archon)));
			}
	    }
	    private static MapLocation closestRobot(RobotInfo[] robot) throws GameActionException{ //Run rotation cost once = 12
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
	    private static MapLocation closestRobot(RobotInfo[] robot, RobotType findRobot) throws GameActionException{
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
	    private static Direction randomDirection() throws GameActionException{
	    	Random rand = new Random(rc.getID());
			int fate = rand.nextInt(1000)*rc.getRobotCount();
	        return directions[fate%8];
	    }
	    private static MapLocation findWeakest(RobotInfo[] listOfRobots){
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
	    private static MapLocation[] combineThings(RobotInfo[] visibleEnemyArray, Signal[] incomingSignals) {
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
	    public static Direction tryToMove(Direction forward) throws GameActionException{ //Works to get location able to move to Run cost is = 16
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
	    private static void creationSpot(Direction ahead, RobotType robot) throws GameActionException //find spot to build
	    {
	        Direction candidateDirection = ahead;
	        MapLocation loc = rc.getLocation();
	        if(rc.isCoreReady())
	        {
	        	if(robot == RobotType.SCOUT && scoutNum <= 4){
	        		scoutNum++;}
	        	else if (robot == RobotType.SCOUT) {robot = RobotType.SOLDIER;}
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
	    private static void leaderElection() throws GameActionException {
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
	    private static void sendInstructions(MapLocation enemy) throws GameActionException {
	        // Possible improvement: stop sending the same message over and over again
	        // since it will just increase our delay.
	        MapLocation loc = rc.getLocation();
	        if (!archonFound && rc.getRoundNum()%3 == 4) {
	            rc.broadcastMessageSignal(MOVE_X, loc.x, infinity);
	            rc.broadcastMessageSignal(MOVE_Y, loc.y, infinity);
	        } else {
	            rc.broadcastMessageSignal(MOVE_X, archonX, infinity);
	            rc.broadcastMessageSignal(MOVE_Y, archonY, infinity);
	        }
	        if(enemy.distanceSquaredTo(loc) < 3)
	        	{rc.broadcastMessageSignal(targetX, targetY, rc.getType().attackRadiusSquared);}
	    }
	    static void readInstructions() throws GameActionException {
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
