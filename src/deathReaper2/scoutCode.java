package deathReaper2;

import battlecode.common.*;

public class scoutCode {
    static void scoutCode(RobotController rc) throws GameActionException { 
        RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared);
        if (rc.isCoreReady()) {
            if (Utilities.turnsLeft <= 0) {
            	Utilities.direction = Utilities.pickNewDirection();
            } else {
            	Utilities.turnsLeft--;
                rc.move(Utilities.tryToMove(Utilities.direction));
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
                    Utilities.runaway();
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
