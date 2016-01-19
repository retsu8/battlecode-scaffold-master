package deathReaper2;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {

    static RobotController rc;
	static Random rnd;
    static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
            Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    static RobotType[] robotTypes = {RobotType.GUARD, RobotType.SCOUT, RobotType.SOLDIER, RobotType.SOLDIER,
            RobotType.SOLDIER, RobotType.SOLDIER, RobotType.TURRET, RobotType.TURRET};
	static int[] possibleMovements = new int[]{0,1,-1,2,-2,3,-3,4};
	static ArrayList<MapLocation> pastLocations = new ArrayList<MapLocation>();
	private static MapLocation[] parts = rc.sensePartLocations(1000000);
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rcIN) {
    	rc =rcIN;
    	while(true){
		try {
	        if(rc.getType() == RobotType.ARCHON){
	        	archonCode();}
	        else if(rc.getType() == RobotType.VIPER){
	        	viperCode();}
	        else if(rc.getType() == RobotType.GUARD){
				guardCode();}
	        else if(rc.getType() == RobotType.SCOUT){
	        	scoutCode();}
	        else if(rc.getType() == RobotType.SOLDIER){
	        	soldierCode();}
	        else if(rc.getType() == RobotType.TURRET){
						turretCode();}
	        else{ttmCode();}
	    }catch (GameActionException e) {
			e.printStackTrace();
		} }
	}    
	private static void ttmCode() throws GameActionException {
		RobotInfo[] visibleEnemyArray = rc.senseHostileRobots(rc.getLocation(), 1000000);
		Signal[] incomingSignals = rc.emptySignalQueue();
		MapLocation[] enemyArray = combineThings(visibleEnemyArray,incomingSignals);
		
		if(enemyArray.length>0){
			rc.unpack();
			//could not find any enemies adjacent to attack
			//try to move toward them
			if(rc.isCoreReady()){
				MapLocation goal = enemyArray[0];
				Direction toEnemy = rc.getLocation().directionTo(goal);
				tryToMove(toEnemy);
			}
		}else{//there are no enemies nearby
			//check to see if we are in the way of friends
			//we are obstructing them
			if(rc.isCoreReady()){
				RobotInfo[] nearbyFriends = rc.senseNearbyRobots(2, rc.getTeam());
				if(nearbyFriends.length>3){
					Direction away = randomDirection();
					tryToMove(away);
				}else{//maybe a friend is in need!
					RobotInfo[] alliesToHelp = rc.senseNearbyRobots(1000000,rc.getTeam());
					MapLocation weakestOne = findWeakest(alliesToHelp);
					if(weakestOne!=null){//found a friend most in need
						Direction towardFriend = rc.getLocation().directionTo(weakestOne);
						tryToMove(towardFriend);
					}
				}
			}
		}
	}
	private static void soldierCode() throws GameActionException{
		while(true){
			try {
				repeat();
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
	}
	private static void archonCode() throws GameActionException{
		Random rand = new Random(rc.getID());
		int fate = rand.nextInt(1000);
		RobotType robot = robotTypes[fate%8];
    	while(true){
    		try {
    			if(rc.senseNearbyRobots().equals(rc.getTeam().opponent()) == true)
    				runaway();
	    		if(rc.getRoundNum()<20){
	    			creationSpot(directions[2], RobotType.TURRET);
	    			}
	    		else
	    			creationSpot(directions[2], robot);
				if(rc.isCoreReady())
				{if(rc.canMove(directions[2])){
					rc.move(directions[2]);}
				else{
					tryToMove(directions[2]);
					getParts(directions[2]);}
				}
			} catch (GameActionException e) {
				e.printStackTrace();
			}
    	}
		
	}

	private static void scoutCode() {	
		while(true){
			if(rc.isWeaponReady()){
				try {
					repeat();
				} catch (GameActionException e) {
					e.printStackTrace();
				}}
			} 		
	}

	private static void viperCode() {		
		Team myTeam = rc.getTeam();
		RobotInfo[] team = rc.senseNearbyRobots(rc.getType().attackRadiusSquared,myTeam);
		while(true){
		if(team.length > 0 && rc.getType().canInfect()){
			if(rc.isWeaponReady()){
				try {
					//rc.attackLocation(team[0].location);
					repeat();
				} catch (GameActionException e) {
					e.printStackTrace();
				}}
			} 
		else
			try {
				repeat();
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private static void getParts(Direction directions) throws GameActionException{
		if(parts.length != 0){
			MapLocation[] partsLocation = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), rc.getType().attackRadiusSquared);
			if(rc.canMove(partsLocation[0].directionTo(rc.getLocation()))){
					rc.move(partsLocation[0].directionTo(rc.getLocation()));
				}
		}
	}
	private static void runaway() throws GameActionException{ //code to have robot run away from all enemy troops/ currently broken
		Team enemyTeam = rc.getTeam().opponent();
		RobotInfo[] hostile = rc.senseNearbyRobots(rc.getType().attackRadiusSquared,enemyTeam );
		rc.emptySignalQueue();
		while(rc.senseNearbyRobots(rc.getType().attackRadiusSquared, enemyTeam) != null){
			try {
				if(rc.canSenseLocation(hostile[0].location) == true)
				{
					for(int i=0; i<8; i++){
						if(rc.canMove(directions[i]))
							rc.move(directions[i]);
							Clock.getBytecodeNum();
					}
				}
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		if(rc.getHealth() < 20)
		{
			try {
				rc.repair(rc.getLocation());
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
		}
		
	}
	private static void repeat() throws GameActionException{	
		Team myTeam = rc.getTeam();
	    Team enemyTeam = myTeam.opponent();
		RobotInfo[] zombieEnemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared,Team.ZOMBIE);
		RobotInfo[] enemy = rc.senseNearbyRobots(rc.getType().attackRadiusSquared,enemyTeam);
		RobotInfo[] allEnemys = joinRobotInfo(zombieEnemies,enemy); 
		if(rc.getHealth()<20)
			runaway();
		if (allEnemys.length > 0 && rc.getType().canAttack()){
			if(rc.isWeaponReady()){
				double weakestSoFar = 0;
				MapLocation weakestLocation = null;
				for(RobotInfo r:allEnemys){
					double weakness = r.maxHealth-r.health;
					if(weakness>weakestSoFar){
						rc.setIndicatorString(0, "Killing the weakest");
						weakestLocation = r.location;
						weakestSoFar=weakness;
					}
				}
				if(weakestLocation != null)
					rc.attackLocation(weakestLocation);
				else
					rc.attackLocation(allEnemys[0].location);
			}
		}
		else
		{if(rc.isCoreReady()){
			MapLocation goal = findWeakest(allEnemys);
			Direction toEnemy = rc.getLocation().directionTo(goal);
			if(rc.canMove(toEnemy)){
				rc.setIndicatorString(0,"moving to enemy");
				rc.move(toEnemy);
			}else{
				MapLocation ahead = rc.getLocation().add(toEnemy);
				if(rc.senseRubble(ahead)>=GameConstants.RUBBLE_OBSTRUCTION_THRESH){
					rc.clearRubble(toEnemy);
				}
			}
		}
		}
	}
private static void guardCode() throws GameActionException {
		RobotInfo[] enemyArray = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared,Team.ZOMBIE);
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
			//could not find any enemies adjacent to attack
			//try to move toward them
			if(rc.isCoreReady()){
				MapLocation goal = enemyArray[0].location;
				Direction toEnemy = rc.getLocation().directionTo(goal);
				if(rc.canMove(toEnemy)){
					rc.setIndicatorString(0,"moving to enemy");
					rc.move(toEnemy);
				}else{
					MapLocation ahead = rc.getLocation().add(toEnemy);
					if(rc.senseRubble(ahead)>=GameConstants.RUBBLE_OBSTRUCTION_THRESH){
						rc.clearRubble(toEnemy);
					}
				}
			}
		}
	}
	private static void turretCode() throws GameActionException {
		RobotInfo[] visibleEnemyArray = rc.senseHostileRobots(rc.getLocation(), 1000000);
		Signal[] incomingSignals = rc.emptySignalQueue();
		MapLocation[] enemyArray = combineThings(visibleEnemyArray,incomingSignals);
		
		while(true){
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
					Direction away = randomDirection();
					rc.pack();
				}
			}
		}
		}
	}
	private static Direction randomDirection() {
		return Direction.values()[(int)(rnd.nextDouble()*8)];
	}
	private static MapLocation findWeakest(RobotInfo[] listOfRobots){
		double weakestSoFar = 0;
		MapLocation weakestLocation = null;
		for(RobotInfo r:listOfRobots){
			double weakness = r.maxHealth-r.health;
			if(weakness>weakestSoFar){
				weakestLocation = r.location;
				weakestSoFar=weakness;
			}
		}
		return weakestLocation;
	}
	private static MapLocation[] combineThings(RobotInfo[] visibleEnemyArray, Signal[] incomingSignals) {
		ArrayList<MapLocation> attackableEnemyArray = new ArrayList<MapLocation>();
		for(RobotInfo r:visibleEnemyArray){
			attackableEnemyArray.add(r.location);
		}
		for(Signal s:incomingSignals){
			if(s.getTeam()==rc.getTeam().opponent()){
				MapLocation enemySignalLocation = s.getLocation();
				int distanceToSignalingEnemy = rc.getLocation().distanceSquaredTo(enemySignalLocation);
				if(distanceToSignalingEnemy<=rc.getType().attackRadiusSquared){
					attackableEnemyArray.add(enemySignalLocation);
				}
			}
		}
		MapLocation[] finishedArray = new MapLocation[attackableEnemyArray.size()];
		for(int i=0;i<attackableEnemyArray.size();i++){ 
			finishedArray[i]=attackableEnemyArray.get(i);
		}
		return finishedArray;
	}
	public static void tryToMove(Direction forward) throws GameActionException{
		if(rc.isCoreReady()){
			for(int deltaD:possibleMovements){
				Direction maybeForward = Direction.values()[(forward.ordinal()+deltaD+8)%8];
				if(rc.canMove(maybeForward)){
					rc.move(maybeForward);
					return;
				}
			}
			if(rc.getType().canClearRubble()){
				//failed to move, look to clear rubble
				MapLocation ahead = rc.getLocation().add(forward);
				if(rc.senseRubble(ahead)>=GameConstants.RUBBLE_OBSTRUCTION_THRESH){
					rc.clearRubble(forward);
				}
			}
		}
	}
	private static RobotInfo[] joinRobotInfo(RobotInfo[] zombieEnemies, RobotInfo[] enemy) {
		//join robot lists
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
	private static void creationSpot(Direction ahead, RobotType robot) throws GameActionException //find spot to build
	{
		if(rc.isCoreReady())
		{
			for(int i:possibleMovements){
			if(rc.isCoreReady()){
				Direction candidateDirection = Direction.values()[(ahead.ordinal()+i+8)%8];
				MapLocation loc = rc.getLocation().add(candidateDirection);
				if(rc.isLocationOccupied(loc) == false && rc.senseRubble(loc) < GameConstants.RUBBLE_OBSTRUCTION_THRESH && rc.canBuild(candidateDirection, robot)){
					rc.build(candidateDirection, robot);}
				}
			}
		}
	}
	
}
