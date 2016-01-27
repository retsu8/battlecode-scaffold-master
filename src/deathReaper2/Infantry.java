package deathReaper2;


import battlecode.common.*;

public class Infantry {
	static RobotController rc;
	 static void guardCode(RobotController rc) throws GameActionException {
	        RobotInfo[] enemyArray = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared,Team.ZOMBIE);
	        RobotInfo[] friends = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared,rc.getTeam());
	        Utilities.readInstructions();
	    	if(rc.getHealth() > 20){
		        if(enemyArray.length>0){
		        	Utilities.sendInstructions(Utilities.closestRobot(enemyArray));
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
	                MapLocation goal = Utilities.findWeakest(enemyArray);
	                Direction toEnemy = rc.getLocation().directionTo(goal);
	                if(rc.canMove(toEnemy)){
	                    rc.setIndicatorString(0,"moving to enemy");
	                    rc.move(Utilities.tryToMove(toEnemy));
	                }else{
	                    MapLocation ahead = rc.getLocation().add(toEnemy);
	                    if(rc.senseRubble(ahead)>=GameConstants.RUBBLE_OBSTRUCTION_THRESH){
	                        rc.clearRubble(toEnemy);
	                    	}
	                	}	
	               }
	            }else{
	            	rc.move(Utilities.tryToMove(rc.getLocation().directionTo(Utilities.closestRobot(friends, RobotType.ARCHON))));
	            	}
	        }else
	        	Utilities.runaway();    	
	       Clock.yield();
	    }

		static void soldierCode(RobotController rc) throws GameActionException{
	        while(true){
	        	Utilities.readInstructions();
	            RobotInfo[] target = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam());
	            RobotInfo[] nearbyEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
	            RobotInfo[] nearbyFriends = rc.senseNearbyRobots(2, rc.getTeam());
	            if (nearbyEnemies.length > 0) {
	            	Utilities.sendInstructions(Utilities.closestRobot(nearbyEnemies));
	                if (rc.isWeaponReady()) {
	                    rc.attackLocation(Utilities.findWeakest(nearbyEnemies));
	                }
	            }else if (rc.isCoreReady()) {
	                if(nearbyFriends.length>3){
	                	rc.move(Utilities.tryToMove(Utilities.closestRobot(nearbyFriends).directionTo(rc.getLocation())));
	                    }
	                else{
		                rc.move(Utilities.tryToMove(rc.getLocation().directionTo(Utilities.closestRobot(target, RobotType.ARCHON))));
	              }
	             }
	            Clock.yield();
	        }
	    }
		static void viperCode(RobotController rc) throws GameActionException {
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
	                    rc.move(Utilities.tryToMove(rc.getLocation().directionTo(Utilities.closestRobot(target))));
	                }
	            }
	            Clock.yield();}
	    }
	}

}
