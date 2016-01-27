package deathReaper2;


import battlecode.common.*;
import deathReaper2.Utilities;

public class Infantry {
	static RobotController rc;
	 static void guardCode(RobotController rc) throws GameActionException {
		 while(true){
			 try{
		        RobotInfo[] enemyArray = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared,Team.ZOMBIE);
		        RobotInfo[] friends = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared,rc.getTeam());
		        if(enemyArray.length>0){
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
		            }
		            //could not find any enemies adjacent to attack
		            //try to move toward them
	           if(rc.isCoreReady()){
		            Direction toEnemy = rc.getLocation().directionTo(Utilities.findWeakest(enemyArray));
		            MapLocation ahead = rc.getLocation().add(toEnemy);
		            if(rc.senseRubble(ahead)>=GameConstants.RUBBLE_OBSTRUCTION_THRESH){
                        rc.clearRubble(toEnemy);}
                   else if(rc.canMove(toEnemy)){
	                   rc.setIndicatorString(0,"moving to enemy");
	                   rc.move(toEnemy);}
	                else{
	                	if(friends.length > 3)
	                		rc.move(Utilities.tryToMove(rc, rc.getLocation().directionTo(Utilities.closestRobot(rc, friends)).opposite()));
	                	}
	                }
	            }catch (GameActionException e) {
	                e.printStackTrace();}
		     Clock.yield();
			}
	    }
		static void soldierCode(RobotController rc) throws GameActionException{
	        while(true){
	        	Utilities.readInstructions(rc);
	            RobotInfo[] target = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam());
	            RobotInfo[] nearbyEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
	            RobotInfo[] zombies = rc.senseNearbyRobots(rc.getLocation(), rc.getType().attackRadiusSquared, Team.ZOMBIE);
	            RobotInfo[] nearbyFriends = rc.senseNearbyRobots(3, rc.getTeam());
	            if (nearbyEnemies.length > 0) {
	            	Utilities.sendInstructions(rc, Utilities.closestRobot(rc, nearbyEnemies));
	                if (rc.isWeaponReady()) {
	                    rc.attackLocation(Utilities.findWeakest(nearbyEnemies));
	                }
	            }else if (rc.isCoreReady()) {
	                if(nearbyFriends.length>3){
	                	rc.move(Utilities.tryToMove(rc, Utilities.closestRobot(rc, nearbyFriends).directionTo(rc.getLocation())));
	                    }
	                else{
		                rc.move(Utilities.tryToMove(rc, rc.getLocation().directionTo(Utilities.closestRobot(rc, target, RobotType.ARCHON))));
	                }
	             }
	            Clock.yield();
	        }
		}
		static void viperCode(RobotController rc) throws GameActionException {
			while(true){
			    Team myTeam = rc.getTeam();
			    RobotInfo[] team = rc.senseNearbyRobots(rc.getType().attackRadiusSquared,myTeam);
			    while(true){
			        if(team.length > 0 && rc.getType().canInfect()){
			            RobotInfo[] nearbyEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
			            if(rc.senseNearbyRobots().equals(RobotType.ARCHON))
			            {
			                if (nearbyEnemies.length > 0) {
			                    if (rc.isWeaponReady()) {
			                        MapLocation toAttack = Utilities.findWeakest(nearbyEnemies);
			                        rc.attackLocation(toAttack);
			                    }
			                    return;
			                }
			                if (rc.isCoreReady()) {
			                    RobotInfo[] target = rc.senseHostileRobots(rc.getLocation(), Utilities.infinity);
			                    rc.move(Utilities.tryToMove(rc, rc.getLocation().directionTo(Utilities.closestRobot(rc, target))));
			                }
			            }
			            Clock.yield();}
			    }
			}
	}

}
