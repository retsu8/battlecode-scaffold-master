package deathReaper2;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.Signal;

public class turretCode {
	 static void turretCode() throws GameActionException {
	        RobotInfo[] visibleEnemyArray = rc.senseHostileRobots(rc.getLocation(), 1000000);
	        Signal[] incomingSignals = rc.emptySignalQueue();
	        MapLocation[] enemyArray = combineThings(visibleEnemyArray,incomingSignals);

	        if(enemyArray.length>0){
	            if(rc.isWeaponReady()){
	                //look for adjacent enemies to attack
	                for(MapLocation oneEnemy:enemyArray){
	                    if(rc.canAttackLocation(oneEnemy)){
	                        rc.setIndicatorString(0,"trying to attack");
	                        rc.attackLocation(oneEnemy);
	                        break;
	                    }
	                }
	            }
	            //could not find any enemies adjacent to attack
	            //try to move toward them
	            if(rc.isCoreReady()){
	                MapLocation goal = enemyArray[0];
	                Direction toEnemy = rc.getLocation().directionTo(goal);
	                rc.pack();
	            }
	        }else{//there are no enemies nearby
	            //check to see if we are in the way of friends
	            //we are obstructing them
	            if(rc.isCoreReady()){
	                RobotInfo[] nearbyFriends = rc.senseNearbyRobots(2, rc.getTeam());
	                if(nearbyFriends.length>3){
	                    Direction away = tryToMove(nearbyFriends[0].location.directionTo(rc.getLocation()));
	                    rc.pack();
	                }
	            }
	        }
	    }
}
