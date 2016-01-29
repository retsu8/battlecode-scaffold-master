package deathReaper2;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Signal;
import deathReaper2.Utilities;

public class LongKnives {
	static void ttmCode(RobotController rc) throws GameActionException {
		while(true){
			try{
		    RobotInfo[] visibleEnemyArray = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
		    Signal[] incomingSignals = rc.emptySignalQueue();
		    MapLocation[] enemyArray = Utilities.combineThings(rc, visibleEnemyArray,incomingSignals);
		    MapLocation archon = new MapLocation(Utilities.archonX, Utilities.archonY);
		    RobotInfo[] nearbyFriends = rc.senseNearbyRobots(3, rc.getTeam());
		    Utilities.readInstructions(rc);
		    if(enemyArray.length>0){
		        rc.unpack();
		        //could not find any enemies adjacent to attack
		        //try to move toward them
		        if(rc.isCoreReady()){
		            MapLocation goal = enemyArray[0];
		            Direction toEnemy = rc.getLocation().directionTo(goal);
		            rc.move(Utilities.tryToMove(rc, toEnemy));
		        }
		    }else{//there are no enemies nearby
		        //check to see if we are in the way of friends
		        //we are obstructing them
		        if(rc.isCoreReady()){
		            if(nearbyFriends.length>3){
		                Direction away = Utilities.randomDirection(rc);
		            	if(Utilities.archonFound){
		            		rc.move(Utilities.tryToMove(rc, away).opposite());}
		            }else{//maybe a friend is in need!
		                RobotInfo[] alliesToHelp = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared,rc.getTeam());
		                MapLocation weakestOne = Utilities.findWeakest(alliesToHelp);
		                if(weakestOne!=null){//found a friend most in need
		                    Direction towardFriend = rc.getLocation().directionTo(weakestOne);
		                    rc.move(Utilities.tryToMove(rc, towardFriend));
		                }
		            }
		        }
		    }
		}catch (Exception e) {
            e.printStackTrace();}
		    Clock.yield();
		}
	}
	 static void turretCode(RobotController rc) throws GameActionException {
		 while(true){
	        RobotInfo[] visibleEnemyArray = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
	        Signal[] incomingSignals = rc.emptySignalQueue();
	        MapLocation[] enemyArray = Utilities.combineThings(rc, visibleEnemyArray,incomingSignals);

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
	                    Direction away = Utilities.tryToMove(rc, nearbyFriends[0].location.directionTo(rc.getLocation()));
	                    rc.pack();
	                }
	            }
	        }
	    }
	 }
}
