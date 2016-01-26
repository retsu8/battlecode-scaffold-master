package deathReaper2;

import battlecode.common.*;

public class scoutCode {
	static int turnsLeft = 0; 
    static Direction direction = null;
    private static Direction pickNewDirection() throws GameActionException {
        Direction scoutDirection = randomDirection();
        int turnsLeft = 100;
        return scoutDirection;
    }
    static void scoutCode() throws GameActionException { 
        RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared);
        if (rc.isCoreReady()) {
            if (turnsLeft <= 0) {
               direction = pickNewDirection();
            } else {
                turnsLeft--;
                rc.move(tryToMove(direction));
                }
            }
            for (RobotInfo r : enemies) {
                if (r.type == RobotType.ARCHON && r.team == rc.getTeam().opponent()) {
                    rc.broadcastMessageSignal(targetX =r.location.x, r.location.x, infinity);
                    rc.broadcastMessageSignal(targetY=r.location.y, r.location.y, infinity);
                }
                else if(r.team == rc.getTeam().opponent()){
                    rc.broadcastMessageSignal(MOVE_X, r.location.x, rc.getType().attackRadiusSquared);
                    rc.broadcastMessageSignal(MOVE_Y, r.location.y, rc.getType().attackRadiusSquared);
                	runaway();
                }
                else if(r.team == Team.NEUTRAL){
                	direction = rc.getLocation().directionTo(r.location);
                    rc.broadcastMessageSignal(MOVE_X, r.location.x, rc.getType().attackRadiusSquared);
                    rc.broadcastMessageSignal(MOVE_Y, r.location.y, rc.getType().attackRadiusSquared);                	
                }
            }
            Clock.yield();
        }
}
