package deathReaper2;


import battlecode.common.*;
import deathReaper2.Utilities;

public class Infantry {
	 static void guardCode(RobotController rc) throws GameActionException {
		 while(true){
			 try{
				RobotInfo[] visibleEnemyArray = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
				Signal[] incomingSignals = rc.emptySignalQueue();
				MapLocation[] enemyArray = Utilities.combineThings(rc, visibleEnemyArray,incomingSignals);
		        RobotInfo[] friends = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared,rc.getTeam());
	            RobotInfo[] nearbyFriends = rc.senseNearbyRobots(3, rc.getTeam());
		        if(enemyArray.length>0){
		            if(rc.isWeaponReady()){
		                //look for adjacent enemies to attack
		                for(MapLocation oneEnemy:enemyArray){
		                    if(rc.canAttackLocation(oneEnemy)){
		                        rc.setIndicatorString(0,"trying to attack");
		                        rc.attackLocation(oneEnemy);
			                    break;
			                    }
		                    else  if(rc.canMove(Utilities.tryToMove(rc, rc.getLocation().directionTo(oneEnemy)))){
			                    rc.setIndicatorString(0,"moving to enemy");
			                    rc.move(Utilities.tryToMove(rc, rc.getLocation().directionTo(oneEnemy)));}
			                }
			            }
		            }
		            //could not find any enemies adjacent to attack
		            //try to move toward them
		        else if (rc.isCoreReady()) {
		        	if(nearbyFriends.length>3){
	                	rc.move(Utilities.tryToMove(rc, Utilities.closestRobot(rc, nearbyFriends).directionTo(rc.getLocation())));
	                    }
	                else{
		                rc.move(Utilities.tryToMove(rc, rc.getLocation().directionTo(Utilities.closestRobot(rc, friends, RobotType.ARCHON))));
	                }
	             }
			     Clock.yield();
	            }catch (GameActionException e) {
	                e.printStackTrace();}
			}
	    }
		static void soldierCode(RobotController rc) throws GameActionException{
	        while(true){
	        	Utilities.readInstructions(rc);
	        	RobotInfo[] visibleEnemyArray = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
				Signal[] incomingSignals = rc.emptySignalQueue();
				MapLocation[] enemyArray = Utilities.combineThings(rc, visibleEnemyArray,incomingSignals);
	            RobotInfo[] target = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam());
	            RobotInfo[] nearbyEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
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
	                else if(enemyArray.length >0){
	                	rc.move(Utilities.tryToMove(rc, Utilities.closestRobot(rc, enemyArray).directionTo(rc.getLocation())));
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
				 Utilities.readInstructions(rc);
			    RobotInfo[] visibleEnemyArray = rc.senseNearbyRobots(rc.getLocation(), rc.getType().attackRadiusSquared, rc.getTeam().opponent());
				Signal[] incomingSignals = rc.emptySignalQueue();
				MapLocation[] enemyArray = Utilities.combineThings(rc, visibleEnemyArray,incomingSignals);
				MapLocation target = new MapLocation(Utilities.targetX, Utilities.targetY);
			   try{
			        if(enemyArray.length > 0){
			                if (visibleEnemyArray.length > 0) {
			                    if (rc.isWeaponReady()) {
			                        MapLocation toAttack = Utilities.findWeakest(visibleEnemyArray);
			                        rc.attackLocation(toAttack);
			                    }
			                }
			        if (rc.isCoreReady()) {
		                	if(enemyArray.length >0){
			                	rc.move(Utilities.tryToMove(rc, Utilities.closestRobot(rc, enemyArray).directionTo(rc.getLocation())));}
		                	else if(rc.getTeam() == Team.ZOMBIE){
		                		Utilities.runaway(rc);
		                		}
			        	}
			        }
			        else{
		                rc.move(Utilities.tryToMove(rc, rc.getLocation().directionTo(target)));
			            } Clock.yield();
			        }catch (GameActionException e) {
			                e.printStackTrace();}
				}
			}
		}
