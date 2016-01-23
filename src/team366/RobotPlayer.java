package team366;

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
    static boolean leader = false;
    private static int ELECTION = 73646;
    private static int infinity = 1000;
    static int targetX = -1;
    static int targetY = -1;
    static int archonX = -1;
    static int archonY = -1;
    static boolean archonFound = false;
    static int MOVE_X = 182632;
    static int MOVE_Y = 1827371;
    static int FOUND_ARCHON_X = 756736;
    static int FOUND_ARCHON_Y = 256253;
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
                else if(rc.getType() == RobotType.TTM){
                    ttmCode();}
            }catch (Exception e) {
                e.printStackTrace();}
            Clock.yield();
        }
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
                rc.move(tryToMove(toEnemy));
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
                        rc.move(tryToMove(towardFriend));
                    }
                }
            }
        }
        Clock.yield();
    }
    private static void soldierCode() throws GameActionException{
        while(true){
            readInstructions();
            RobotInfo[] nearbyEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared/2);
            if (nearbyEnemies.length > 0) {
                if (rc.isWeaponReady()) {
                    MapLocation toAttack = findWeakest(nearbyEnemies);
                    rc.attackLocation(toAttack);
                }
            }
            else if (rc.isCoreReady()) {
                RobotInfo[] nearbyFriends = rc.senseNearbyRobots(2, rc.getTeam());
                if(nearbyFriends.length>3){
                    Direction away = tryToMove(nearbyFriends[0].location.directionTo(rc.getLocation()));
                    rc.move(away);}
                MapLocation target = new MapLocation(archonX, archonY);
                Direction dir = rc.getLocation().directionTo(target);
                rc.move(tryToMove(dir));
            }
            Clock.yield();
        }
    }
    private static void archonCode() throws GameActionException{
		Random rand = new Random(rc.getID());
		int fate = rand.nextInt(1000)*rc.getRobotCount();
		RobotType robot = robotTypes[fate%8];
		MapLocation[] parts = rc.sensePartLocations(rc.getType().attackRadiusSquared);
		RobotInfo[] neutral = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared,Team.NEUTRAL);
		MapLocation target = new MapLocation(targetX, targetY);
		Direction dir = rc.getLocation().directionTo(target);			
		RobotInfo[] enemies = rc.senseHostileRobots(rc.getLocation(), infinity);
		leaderElection();
		readInstructions();
		if(leader && rc.getID() == 0)
			sendInstructions();		
		if (rc.isCoreReady()) {
			if(rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared).length > 0)
				runaway();
			if(neutral.length > 0){
				if(rc.canMove(rc.getLocation().directionTo(neutral[0].location))){
					rc.move(tryToMove(rc.getLocation().directionTo(neutral[0].location)));
				}else
					rc.activate(neutral[0].location);
			}
			if(parts.length > 0){
				if(rc.canMove(rc.getLocation().directionTo(parts[0]))){
					rc.move(tryToMove(rc.getLocation().directionTo(parts[0])));
				}
			}
			if(rc.getRoundNum()<20){
				creationSpot(directions[2], RobotType.GUARD); }
			else if(rc.canBuild(tryToMove(directions[2]), robot)){
				creationSpot(directions[2], robot);}	
			if (enemies.length > 0) {
				Direction away = rc.getLocation().directionTo(enemies[0].location).opposite();
				rc.move(tryToMove(away));
			} else {
				rc.move(tryToMove(dir));
				}
			}
		Clock.yield();	
		}
    static int turnsLeft = 0; // number of turns to move in scoutDirection
    static Direction scoutDirection = Direction.NONE; // random direction
    private static void pickNewDirection() throws GameActionException {
        scoutDirection = randomDirection();
        turnsLeft = 100;
    }
    private static void scoutCode() throws GameActionException {
        if (rc.isCoreReady()) {
            if (turnsLeft == 0) {
                pickNewDirection();
            } else {
                turnsLeft--;
                if (!rc.onTheMap(rc.getLocation().add(scoutDirection))) {
                    pickNewDirection();
                }
                rc.move(tryToMove(scoutDirection));
            }
            RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared);
            for (RobotInfo r : enemies) {
                if (r.type == RobotType.ARCHON && r.team == rc.getTeam().opponent()) {
                    rc.broadcastMessageSignal(targetX =r.location.x, r.location.x, infinity);
                    rc.broadcastMessageSignal(targetY=r.location.y, r.location.y, infinity);
                }
                else if(r.team == rc.getTeam().opponent()){
                    rc.broadcastMessageSignal(MOVE_X, r.location.x, rc.getType().attackRadiusSquared);
                    rc.broadcastMessageSignal(MOVE_Y, r.location.y, rc.getType().attackRadiusSquared);
                }
            }
            Clock.yield();
        }
    }
    private static void viperCode() throws GameActionException {
        Team myTeam = rc.getTeam();
        RobotInfo[] team = rc.senseNearbyRobots(rc.getType().attackRadiusSquared,myTeam);
        while(true){
            if(team.length > 0 && rc.getType().canInfect()){
                RobotInfo[] nearbyEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
                if(rc.senseNearbyRobots().equals(RobotType.ARCHON))
                {
                    if (nearbyEnemies.length > 0) {
                        if (rc.isWeaponReady()) {
                            MapLocation toAttack = findWeakest(nearbyEnemies);
                            rc.attackLocation(toAttack);
                        }
                        return;
                    }
                    if (rc.isCoreReady()) {
                        MapLocation target = new MapLocation(archonX *5, archonY*5);
                        Direction dir = rc.getLocation().directionTo(target);
                        rc.move(tryToMove(dir));
                    }
                }
                Clock.yield();}
        }
    }
    @SuppressWarnings("static-access")
    private static void runaway() throws GameActionException{ //code to have robot run away from all enemy troops
        RobotInfo[] enemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
        MapLocation toClose = closestRobot(enemies);
        Direction away = rc.getLocation().directionTo(toClose).opposite();
        rc.move(tryToMove(away));
    }
    private static MapLocation closestRobot(RobotInfo[] robot) throws GameActionException{
        double nearSoFar = -100;
        MapLocation nearestRobot = null;
        for(RobotInfo r:robot)
        {
            if(r.type != rc.getType().SCOUT){
                int near = r.location.distanceSquaredTo(rc.getLocation());
                if(near> nearSoFar){
                    nearestRobot=r.location;
                    nearSoFar = near;
                }
            }
        }
        return nearestRobot;
    }
    private static void guardCode() throws GameActionException {
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
        }else{
            int nearSoFar = -100;
            MapLocation nearestArchon = null;
            for(RobotInfo r:friends)
            {
                if(r.type == rc.getType().ARCHON){
                    int near = r.location.distanceSquaredTo(rc.getLocation());
                    if(near> nearSoFar){
                        nearestArchon = r.location;
                        nearSoFar = near;
                    }
                }
            }
            rc.move(tryToMove(nearestArchon.directionTo(rc.getLocation())));

        }
    }
    private static void turretCode() throws GameActionException {
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
    private static Direction randomDirection() {
        return Direction.values()[(int)(rnd.nextDouble()*8)];
    }
    private static MapLocation findWeakest(RobotInfo[] listOfRobots){
        double weakestSoFar = -100;
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
    public static Direction tryToMove(Direction forward) throws GameActionException{
        for(int deltaD:possibleMovements){
            Direction maybeForward = Direction.values()[(forward.ordinal()+deltaD+8)%8];
            if(rc.canMove(maybeForward) && !pastLocations.contains(maybeForward)){
                return maybeForward;
            }
        }
        if(rc.getType().canClearRubble()){
            //failed to move, look to clear rubble
            MapLocation ahead = rc.getLocation().add(forward);
            if(rc.senseRubble(ahead)>=GameConstants.RUBBLE_OBSTRUCTION_THRESH){
                rc.clearRubble(forward);
            }
        }
        return forward;
    }
    private static void creationSpot(Direction ahead, RobotType robot) throws GameActionException //find spot to build
    {
        Direction candidateDirection = ahead;
        MapLocation loc = rc.getLocation();
        if(rc.isCoreReady())
        {
            for(int i:possibleMovements){
                if(rc.isCoreReady()){
                    candidateDirection = Direction.values()[(ahead.ordinal()+i+8)%8];
                    loc = rc.getLocation().add(candidateDirection);
                    if(rc.isLocationOccupied(loc) == false && rc.senseRubble(loc) < GameConstants.RUBBLE_OBSTRUCTION_THRESH){
                        if(rc.canBuild(candidateDirection, robot)){
                            rc.build(candidateDirection, robot);}
                        break;
                    }
                }
            }
        }
    }
    private static void leaderElection() throws GameActionException {
        if (rc.getRoundNum() % 100 == 0) {
            // First step: elect a leader archon
            if (rc.getType() == RobotType.ARCHON) {
                rc.broadcastMessageSignal(ELECTION, 0, infinity);

                Signal[] received = rc.emptySignalQueue();
                int numArchons = 0;
                for (Signal s : received) {
                    if (s.getMessage() != null && s.getMessage()[0] == ELECTION) {
                        numArchons++;
                    }
                }
                if (numArchons == 0) {
                    // If you haven't received anything yet, then you're the leader.
                    leader = true;
                    rc.setIndicatorString(0, "I'm the leader!");
                } else {
                    leader = false;
                    rc.setIndicatorString(0, "I'm not the ldaer");
                }
            }
        }
    }
    private static void sendInstructions() throws GameActionException {
        // Possible improvement: stop sending the same message over and over again
        // since it will just increase our delay.
        if (!archonFound && rc.getRoundNum()%3 == 4) {
            MapLocation loc = rc.getLocation();
            rc.broadcastMessageSignal(MOVE_X, loc.x, infinity);
            rc.broadcastMessageSignal(MOVE_Y, loc.y, infinity);
        } else {
            rc.broadcastMessageSignal(MOVE_X, archonX, infinity);
            rc.broadcastMessageSignal(MOVE_Y, archonY, infinity);
        }
    }
    private static void readInstructions() throws GameActionException {
        Signal[] signals = rc.emptySignalQueue();
        for (Signal s : signals) {
            if (s.getTeam() != rc.getTeam()) {
                continue;
            }
            if (s.getMessage() == null) {
                continue;
            }
            int command = s.getMessage()[0];
            if (command == MOVE_X) {
                targetX = s.getMessage()[1];
            } else if (command == MOVE_Y) {
                targetY = s.getMessage()[1];
            } else if (command == FOUND_ARCHON_X) {
                archonX = s.getMessage()[1];
            } else if (command == FOUND_ARCHON_Y) {
                archonY = s.getMessage()[1];
                archonFound = true;
            }
        }
    }

}
