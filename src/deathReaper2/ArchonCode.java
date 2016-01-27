package deathReaper2;

import java.util.Random;

import battlecode.common.*;
import deathReaper2.Utilities;

public class ArchonCode {
	public static void archonCode(RobotController rc) throws GameActionException{
		while(true){
			Random rand = new Random(rc.getID());
			int fate = rand.nextInt(1000)*rc.getRobotCount();
			RobotType robot = Utilities.robotTypes[fate%8];
			MapLocation[] parts = rc.sensePartLocations(rc.getType().sensorRadiusSquared);
			RobotInfo[] neutral = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared,Team.NEUTRAL);
			MapLocation target = new MapLocation(Utilities.targetX, Utilities.targetY);
			Direction dir = rc.getLocation().directionTo(target);
			RobotInfo[] enemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().sensorRadiusSquared);
			Utilities.leaderElection(rc);
			Utilities.readInstructions(rc);
			if(rc.getRoundNum()%5 == 0){
				Utilities.sendInstructions(rc, rc.getLocation());}
			if (rc.isCoreReady()) {
				if(neutral.length > 0){
					if(rc.canMove(rc.getLocation().directionTo(neutral[0].location))){
						rc.move(Utilities.tryToMove(rc, rc.getLocation().directionTo(neutral[0].location)));
					}else
						rc.activate(neutral[0].location);
				}
				else if(parts.length > 0){
					if(rc.canMove(rc.getLocation().directionTo(parts[0]))){
						rc.move(Utilities.tryToMove(rc, rc.getLocation().directionTo(parts[0])));
					}
				}
			}else {//move to get to clear spot
				rc.move(Utilities.tryToMove(rc, dir));
			}
			if(Utilities.leader && rc.getID() == 0)
				Utilities.sendInstructions(rc, target);
			if(enemies.length > 0)
				Utilities.runaway(rc);
			else{
				Utilities.creationSpot(rc, Utilities.directions[2], RobotType.GUARD, enemies); }			
			Clock.yield();
		} 
	}
}