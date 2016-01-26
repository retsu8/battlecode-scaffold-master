package deathReaper2;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class guardCode {
	 static void guardCode() throws GameActionException {
	        RobotInfo[] enemyArray = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared,Team.ZOMBIE);
	        RobotInfo[] friends = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared,rc.getTeam());
	        readInstructions();
	    	if(rc.getHealth() > 20){
		        if(enemyArray.length>0){
		        	sendInstructions(closestRobot(enemyArray));
		            if(rc.isWeaponReady()){
		                //look for adjacent enemies to attack
			                for(RobotInfo oneEnemy:enemyArray){
			                    if(rc.canAttackLocation(oneEnemy.location)){
			                        rc.setIndicatorString(0,"trying to attack");
			                        rc.attackLocation(oneEnemy.location);
			                        break;
			                    }
			               }
		            }
	            //could not find any enemies adjacent to attack
	            //try to move toward them
	            if(rc.isCoreReady()){
	                MapLocation goal = findWeakest(enemyArray);
	                Direction toEnemy = rc.getLocation().directionTo(goal);
	                if(rc.canMove(toEnemy)){
	                    rc.setIndicatorString(0,"moving to enemy");
	                    rc.move(tryToMove(toEnemy));
	                }else{
	                    MapLocation ahead = rc.getLocation().add(toEnemy);
	                    if(rc.senseRubble(ahead)>=GameConstants.RUBBLE_OBSTRUCTION_THRESH){
	                        rc.clearRubble(toEnemy);
	                    	}
	                	}	
	               }
	            }else{
	            	rc.move(tryToMove(rc.getLocation().directionTo(closestRobot(friends, RobotType.ARCHON))));
	            	}
	        }else
	        	runaway();    	
	       Clock.yield();
	    }
}
