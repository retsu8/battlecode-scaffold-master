package deathReaper2;

import battlecode.common.*;

public class soldierCode {
	static void soldierCode() throws GameActionException{
        while(true){
            readInstructions();
            RobotInfo[] target = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam());
            RobotInfo[] nearbyEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
            RobotInfo[] nearbyFriends = rc.senseNearbyRobots(2, rc.getTeam());
            if (nearbyEnemies.length > 0) {
            	sendInstructions(closestRobot(nearbyEnemies));
                if (rc.isWeaponReady()) {
                    rc.attackLocation(findWeakest(nearbyEnemies));
                }
            }else if (rc.isCoreReady()) {
                if(nearbyFriends.length>3){
                	rc.move(tryToMove(closestRobot(nearbyFriends).directionTo(rc.getLocation())));
                    }
                else{
	                rc.move(tryToMove(rc.getLocation().directionTo(closestRobot(target, RobotType.ARCHON))));
              }
             }
            Clock.yield();
        }
    }

}
