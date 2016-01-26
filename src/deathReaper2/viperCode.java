package deathReaper2;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class viperCode { 
	static void viperCode() throws GameActionException {
    Team myTeam = rc.getTeam();
    RobotInfo[] team = rc.senseNearbyRobots(rc.getType().attackRadiusSquared,myTeam);
    while(true){
        if(team.length > 0 && rc.getType().canInfect()){
            RobotInfo[] nearbyEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
            if(rc.senseNearbyRobots().equals(RobotType.ARCHON))
            {
                if (nearbyEnemies.length > 0) {
                    if (rc.isWeaponReady()) {
                        MapLocation toAttack = findWeakest(nearbyEnemies);
                        rc.attackLocation(toAttack);
                    }
                    return;
                }
                if (rc.isCoreReady()) {
                    RobotInfo[] target = rc.senseHostileRobots(rc.getLocation(), infinity);
                    rc.move(tryToMove(rc.getLocation().directionTo(closestRobot(target))));
                }
            }
            Clock.yield();}
    }
}

}
