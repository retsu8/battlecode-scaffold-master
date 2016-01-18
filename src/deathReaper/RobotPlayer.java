package deathReaper;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {

    static RobotController rc;
    static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
            Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    static RobotType[] robotTypes = {RobotType.GUARD, RobotType.SCOUT, RobotType.SOLDIER, RobotType.SOLDIER,
            RobotType.SOLDIER, RobotType.SOLDIER, RobotType.TURRET, RobotType.TURRET};
	static int[] possibleMovements = new int[]{0,1,-1,2,-2,3,-3,4};
	static ArrayList<MapLocation> pastLocations = new ArrayList<MapLocation>();
	static int patient = 30;
	static int id = 0;
	static Direction possibleDirections = Direction.EAST;
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rcIN) {
    	rc =rcIN;
    	Team myTeam = rc.getTeam();
        Team enemyTeam = myTeam.opponent();
        if(rc.getTeam()==myTeam)
        while(true){
        	try{
        		signaling();
        		repeat();
        		Clock.yield();
        	}catch (GameActionException e){
        		e.printStackTrace();
        	}
        }
    }
	private static void signaling() throws GameActionException {
		if(rc.getType()==RobotType.ARCHON){
			if(rc.getRoundNum()==0){
		    	Signal[] incomingMessages = rc.emptySignalQueue();
		    	id = incomingMessages.length;
				rc.broadcastMessageSignal(0, 0, 100);
			}else{
				if(id==0){
					sendinstructions();
				}else{
					followinstuctions();
				}
			}
		}else{
			followinstuctions();}
		
	}
	private static void followinstuctions() throws GameActionException{
		rc.broadcastMessageSignal(rc.getTeam().ordinal(), possibleDirections.ordinal(), 225);		
	}
	private static void sendinstructions() {
		Signal[] incomingMessages = rc.emptySignalQueue();
		if(incomingMessages.length == 0)
			return;
		Signal currentMessage = null;
		for(int messageIndex=0;messageIndex<incomingMessages.length;messageIndex++){
			currentMessage= incomingMessages[messageIndex];
			if(rc.getTeam().ordinal()==currentMessage.getMessage()[0]){
				break;
			}
		}
		if(currentMessage==null)
			return;
		MapLocation archonLocation = currentMessage.getLocation();
		Direction archonDirection = Direction.values()[currentMessage.getMessage()[1]];
		MapLocation goalLocation = archonLocation.add(archonDirection.dx*4,archonDirection.dy*4);
		possibleDirections = rc.getLocation().directionTo(goalLocation);
		
	}
	private static void repeat() throws GameActionException{
    	Team myTeam = rc.getTeam();
        Team enemyTeam = myTeam.opponent();
		RobotInfo[] zombieEnemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared,Team.ZOMBIE);
		RobotInfo[] enemy = rc.senseNearbyRobots(rc.getType().attackRadiusSquared,enemyTeam);
		RobotInfo[] allEnemys = joinRobotInfo(zombieEnemies,enemy); 
		
		if (allEnemys.length > 0 && rc.getType().canAttack())
			{if(rc.isWeaponReady()){
				rc.attackLocation(allEnemys[0].location);
			}
		}
		else
		{if(rc.isCoreReady())
			{if(rc.canMove(directions[2])){
				rc.move(directions[2]);}
			else
				looking(directions[2]);
			}
		}
	}
	private static void looking(Direction ahead) throws GameActionException{
		for(int i:possibleMovements){
			Direction candidateDirection = Direction.values()[(ahead.ordinal()+i+8)%8];
			MapLocation candidateLocation = rc.getLocation().add(candidateDirection);
			if(patient > 0){
				if(rc.canMove(candidateDirection) && !pastLocations.contains(candidateLocation)){
					pastLocations.add(rc.getLocation());
					if(pastLocations.size()> 20)
						pastLocations.remove(0);
					rc.move(candidateDirection);
					patient = Math.max(patient +1,30);
					return;					
				}
			}else{
				if(rc.canMove(candidateDirection)){
					rc.move(candidateDirection);
					return;
				}else{//dig!
					if(rc.senseRubble(candidateLocation)>GameConstants.RUBBLE_OBSTRUCTION_THRESH){
						rc.clearRubble(candidateDirection);
						return;
					}
				}
			}
		}
		patient = patient - 5;
	}
	private static RobotInfo[] joinRobotInfo(RobotInfo[] zombieEnemies, RobotInfo[] enemy) {
		RobotInfo[] enemyAll = new RobotInfo[zombieEnemies.length+enemy.length];
		int index = 0;
		for(RobotInfo i:zombieEnemies){
			enemyAll[index]=i;
			index++;
		}
		for(RobotInfo i:enemy){
			enemyAll[index]=i;
			index++;
		}
		return enemyAll;
	}
}
