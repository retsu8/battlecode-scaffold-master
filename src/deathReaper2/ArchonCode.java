package deathReaper2;

import java.util.Random;

import battlecode.common.*;

public class ArchonCode {
	public static void archonCode(RobotController rc) throws GameActionException{
	Random rand = new Random(rc.getID());
	int fate = rand.nextInt(1000)*rc.getRobotCount();
	RobotType robot = robotTypes[fate%8];
	MapLocation[] parts = rc.sensePartLocations(rc.getType().attackRadiusSquared);
	RobotInfo[] neutral = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared,Team.NEUTRAL);
	MapLocation target = new MapLocation(targetX, targetY);
	Direction dir = rc.getLocation().directionTo(target);
	RobotInfo[] enemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
	leaderElection();
	readInstructions();
	if(leader && rc.getID() == 0)
		sendInstructions(target);
	if(enemies.length > 0)
			runaway();
	if(rc.getRoundNum()<20){
		creationSpot(directions[2], RobotType.GUARD); }
	else if(rc.canBuild(directions[0], RobotType.TTM)){
		creationSpot(directions[2], RobotType.TTM);}
	else{
		creationSpot(directions[2], robot);}
	if (rc.isCoreReady()) {
		if(neutral.length > 0){
			if(rc.canMove(rc.getLocation().directionTo(neutral[0].location))){
				rc.move(tryToMove(rc.getLocation().directionTo(neutral[0].location)));
			}else
				rc.activate(neutral[0].location);
		}
		else if(parts.length > 0){
			if(rc.canMove(rc.getLocation().directionTo(parts[0]))){
				rc.move(tryToMove(rc.getLocation().directionTo(parts[0])));
			}
		}
	}else {//move to get to clear spot
		rc.move(tryToMove(dir));
	}
	Clock.yield();
} 
}