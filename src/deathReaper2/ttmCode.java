package deathReaper2;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.Signal;

public class ttmCode {
	static void ttmCode() throws GameActionException {
    RobotInfo[] visibleEnemyArray = rc.senseHostileRobots(rc.getLocation(), 1000000);
    Signal[] incomingSignals = rc.emptySignalQueue();
    MapLocation[] enemyArray = combineThings(visibleEnemyArray,incomingSignals);
    MapLocation archon = new MapLocation(archonX, archonY);
    Utilities.readInstructions();
    if(enemyArray.length>0){
        rc.unpack();
        //could not find any enemies adjacent to attack
        //try to move toward them
        if(rc.isCoreReady()){
            MapLocation goal = enemyArray[0];
            Direction toEnemy = rc.getLocation().directionTo(goal);
            rc.move(tryToMove(toEnemy));
        }
    }else{//there are no enemies nearby
        //check to see if we are in the way of friends
        //we are obstructing them
        if(rc.isCoreReady()){
            RobotInfo[] nearbyFriends = rc.senseNearbyRobots(3, rc.getTeam());
            if(nearbyFriends.length>3){
                Direction away = randomDirection();
            	if(archonFound)
            		rc.move(tryToMove(rc.getLocation().directionTo(archon.add(away, 5))));
                rc.move(tryToMove(away));
            }else{//maybe a friend is in need!
                RobotInfo[] alliesToHelp = rc.senseNearbyRobots(1000000,rc.getTeam());
                MapLocation weakestOne = findWeakest(alliesToHelp);
                if(weakestOne!=null){//found a friend most in need
                    Direction towardFriend = rc.getLocation().directionTo(weakestOne);
                    rc.move(tryToMove(towardFriend));
                }
            }
        }
    }
    Clock.yield();
}

}
