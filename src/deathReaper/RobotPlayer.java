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
        		repeat();
        		Clock.yield();
        	}catch (GameActionException e){
        		e.printStackTrace();
        	}
        }
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
			}
		}
	}
	private static void finder(Direction ahead) throws GameActionException{
		for(int i:possibleMovements){
			Direction candidateDirection = Direction.values()[(ahead.ordinal()+i+8)%8];
			if(rc.canMove(candidateDirection));{
				rc.move(candidateDirection);
				break;
			}
		}
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
