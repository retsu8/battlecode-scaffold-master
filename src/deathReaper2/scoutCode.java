package deathReaper2;

import battlecode.common.*;
import deathReaper2.Utilities;

public class scoutCode {
    static void Scout(RobotController rc) throws GameActionException { 
    	while(true){
        RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared);
        if (rc.isCoreReady()) {
            if (Utilities.turnsLeft <= 0) {
            	Utilities.direction = Utilities.pickNewDirection(rc);
            } else {
            	Utilities.turnsLeft--;
                rc.move(Utilities.tryToMove(rc, Utilities.direction));
                }
            }
            for (RobotInfo r : enemies) {
                if (r.type == RobotType.ARCHON && r.team == rc.getTeam().opponent()) {
                    rc.broadcastMessageSignal(Utilities.targetX =r.location.x, r.location.x, Utilities.infinity);
                    rc.broadcastMessageSignal(Utilities.targetY=r.location.y, r.location.y, Utilities.infinity);
                }
                else if(r.team == rc.getTeam().opponent()){
                    rc.broadcastMessageSignal(Utilities.MOVE_X, r.location.x, rc.getType().attackRadiusSquared);
                    rc.broadcastMessageSignal(Utilities.MOVE_Y, r.location.y, rc.getType().attackRadiusSquared);
                    Utilities.runaway(rc);
                }
                else if(r.team == Team.NEUTRAL){
                	Utilities.direction = rc.getLocation().directionTo(r.location);
                    rc.broadcastMessageSignal(Utilities.MOVE_X, r.location.x, rc.getType().attackRadiusSquared);
                    rc.broadcastMessageSignal(Utilities.MOVE_Y, r.location.y, rc.getType().attackRadiusSquared);                	
                }
            }
            Clock.yield();
        }
    }
}
