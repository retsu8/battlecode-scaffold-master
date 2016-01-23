/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team287;

import battlecode.common.*;

/**
 *
 * @author james
 */
public class Archon extends BaseBot {
    
    public static final int OFF_THE_MAP = 10001;
    
    protected boolean sentried;   // Do we have a scout watching over us?
    
    protected int myID;           // Keep our ID handy
    
    protected int allyCount;   // The number of friends available to help us
    
    protected RobotInfo[] allies;  // Which friendlies are nearby
    protected RobotInfo[] neutralBots;  // Neutrals on the map
    
    MapLocation partsDestination;  // Nearby parts stockpile we can move to
    MapLocation neutralBotDestination; // Neutral bot on the map to be activated
    
    ZombieSpawnSchedule schedule;
    int[] zombieRounds;
    protected boolean isZombieThreat;
    
    public Archon(RobotController rc) throws GameActionException {
        super(rc);
        myLeader = rc.senseRobotAtLocation(myLocation);
        sentried = false;
        myID = rc.getID();
        partsDestination = null;
        schedule = rc.getZombieSpawnSchedule();
        zombieRounds = schedule.getRounds();
    }

    /**
     * Operations which need to happen at the beginning of each turn
     * @throws GameActionException
     */
    @Override
    public void begin() throws GameActionException {
        super.begin();
        checkIfSentried();
        checkZombieThreat();
        allies = rc.senseNearbyRobots(INFINITY, myTeam);
        neutralBots = rc.senseNearbyRobots(INFINITY, Team.NEUTRAL);
        findNeutralRobots();
        findParts();
    }
    
    /**
     * Contains the main logic for this robot's turn
     * @throws battlecode.common.GameActionException
     */
    @Override
    public void turn() throws GameActionException {
        move();
        repair();
        build();
        if (isZombieThreat){
            sendPullers();
        } else if (allyCount > 12){
            sendMission();
        }
        super.turn();
    }
    
    // Find a nearby ally we can repair
    private RobotInfo getDamagedAlly(){
        RobotInfo[] nearbyAllies = 
                   rc.senseNearbyRobots(myType.attackRadiusSquared, myTeam);
        for (int i = 0; i < nearbyAllies.length; i++){
            if (nearbyAllies[i].health < nearbyAllies[i].maxHealth && 
                                nearbyAllies[i].type != RobotType.ARCHON){
                return nearbyAllies[i];
            }
        }
        return null;
    }
    
    private void repair() throws GameActionException {
        RobotInfo toRepair = getDamagedAlly();
        if (toRepair != null && rc.isCoreReady()){
            rc.repair(toRepair.location);
        }
    }
    
    private void build() throws GameActionException {
        Direction clearDirection = getClearDirectionLeft(getRandomDirection());
        double check = Math.random() * GameConstants.NUMBER_OF_ARCHONS_MAX;
        RobotType toBuild = chooseTypeToBuild();
        if (check < 1 && clearDirection != null && rc.isCoreReady() 
                      && rc.canBuild(clearDirection, toBuild)){
            rc.build(clearDirection, toBuild);
        }
    }
    
    protected void move() throws GameActionException {
        RobotInfo nearestHostile = findNearestHostile();
        if (nearestHostile != null){
            nextMove = myLocation.add(
                      nearestHostile.location.directionTo(myLocation));
            super.move();
        } else if (neutralBotDestination != null){
            nextMove = neutralBotDestination;
            super.move();
        } else if (partsDestination != null) {
           nextMove = partsDestination;
           super.move();
        } else {  // Stay still
            nextMove = myLocation;
        }
    }
    
    protected RobotType chooseTypeToBuild(){
        int nearbyTotal = 0;
        int nearbyScouts = 0;
        int nearbySoldiers = 0;
        int nearbyGuards = 0;
        int nearbyTurrets = 0;
        // Count up the nearby friendlies
        for (int i = 0; i < allies.length; i++) {
            nearbyTotal++;
            switch (allies[i].type){
                case SCOUT:
                    if (allies[i].location.isAdjacentTo(myLocation)){
                        nearbyScouts++;
                    }
                    break;
                case SOLDIER:
                    nearbySoldiers++;
                    break;
                case GUARD:
                    nearbyGuards++;
                    break;
                case TURRET:
                case TTM:
                    nearbyTurrets++;
                    break;
            }
        }
        allyCount = nearbyTotal;
        // If I don't have a scout paird with me, build a scout
        if (!sentried && nearbyScouts == 0) return RobotType.SCOUT;
        // If I have fewer than two soldiers, build soldier
        if (nearbySoldiers < 2) return RobotType.SOLDIER;
        // If I have fewer than two guards, build guards
        if (nearbyGuards < 2) return RobotType.GUARD;
        // If I have fewer than two turrets, build turrets
        if (nearbyTurrets < 2) return RobotType.TURRET;
        // Otherwise, maintain balance of 4 soldiers to two guards to one turret
        switch ((int) (Math.random() * 3)){
                case 0:
                    return RobotType.SOLDIER;
                case 1:
                    return RobotType.GUARD;
                default:
                    return RobotType.TURRET;
        }
    }

    private void checkIfSentried() {
        sentried = false;
        for (int i = 0; i < signals.length; i++){
            if (signals[i].getTeam() == myTeam){
                int[] message = signals[i].getMessage();
                if (message.length > 0 && message[0] == myID){
                    sentried = true;
                    return;
                }
            }
        }
    }

    private void sendMission() throws GameActionException {
        RobotInfo leader = null;
        for (int i = 0; i < allies.length && leader == null; i++) {
            if (allies[i].type == RobotType.SCOUT){
                leader = allies[i];
                break;
            }
        }
        // Send them to a good attack coordinate (For now just use position 1/1)
        if (leader != null){
            rc.broadcastMessageSignal( 
                           100000 * LEAD_PATROL + leader.ID, OFF_THE_MAP, myType.sensorRadiusSquared);
            for (int i = 0; i < allies.length; i++) {
                if (i % 2 == 0) {
                    rc.broadcastMessageSignal(allies[i].ID, leader.ID, myType.sensorRadiusSquared);
                }
            }
        }
    }

    private void findParts() {
        MapLocation hasMostParts = null;
        double mostPartsCount = 0;
        double currentParts;
        MapLocation[] nearbyParts = rc.sensePartLocations(-1);
        for (int i = 0; i < nearbyParts.length; i++){
            currentParts = rc.senseParts(nearbyParts[i]);
            if (currentParts > mostPartsCount) {
                mostPartsCount = currentParts;
                hasMostParts = nearbyParts[i];
            }
        }
        partsDestination = hasMostParts;
    }

    private void findNeutralRobots() throws GameActionException {
        MapLocation nearestNeutral = null;
        float nearestNeutralDistance = INFINITY;
        float currentDistance;
        for (int i = 0; i < neutralBots.length; i++){
            currentDistance = 
                      neutralBots[i].location.distanceSquaredTo(myLocation);
            if (currentDistance < nearestNeutralDistance){
                nearestNeutralDistance = currentDistance;
                nearestNeutral = neutralBots[i].location;
            }
        }
        if (nearestNeutralDistance < 3 && rc.isCoreReady()) {
            rc.activate(nearestNeutral);
            nearestNeutral = null;
        }
        neutralBotDestination = nearestNeutral;
    }

    private void checkZombieThreat() {
        if (zombieRounds.length == 0){
            return;
        }
        int roundNum = rc.getRoundNum();
        int index;
        for (index = 0; index < zombieRounds.length 
                             && zombieRounds[index] < roundNum; index++);
        if (zombieRounds[index] - roundNum > -ZOMBIE_THREAT_TURNS && 
            zombieRounds[index] - roundNum < ZOMBIE_THREAT_TURNS ) {
            isZombieThreat = true;
        } else {
            isZombieThreat = false;
        }
    }

    private void sendPullers() throws GameActionException {
        for (int i = 0; i < allies.length; i++) {
            if (allies[i].type == RobotType.SCOUT){
                MapLocation scoutLoc = allies[i].location;
                int xDistance = scoutLoc.x - myLocation.x;
                int yDistance = scoutLoc.y - myLocation.y;
                int xCoord = scoutLoc.x + xDistance * ZOMBIE_PULLER_DISTANCE;
                int yCoord = scoutLoc.y + yDistance * ZOMBIE_PULLER_DISTANCE;
                rc.broadcastMessageSignal(100000 * PULL_ZOMBIES + allies[i].ID, 
                              xCoord * 1000 + yCoord, myType.sensorRadiusSquared);
            }
        }
    }
}
