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
public class Scout extends BaseBot {

    boolean isSentry;       // Flags to indicate which role this scout is in
    boolean isExplorer;
    boolean isLeader;
    boolean isPuller;
    int pullerTurns;
    //int pullerMoves;
    MapLocation travelDestination;
    RobotInfo[] zombies;

    public Scout(RobotController rc) throws GameActionException {
        super(rc);
        isSentry = true;
        isExplorer = false;
        isLeader = false;
        isPuller = false;
    }

    /**
     *
     * @throws GameActionException
     */
    @Override
    public void begin() throws GameActionException {
        super.begin();
        checkMessages();
        zombies = rc.senseNearbyRobots(INFINITY, Team.ZOMBIE);
    }

    /**
     *
     * @throws GameActionException
     */
    @Override
    public void turn() throws GameActionException {
        // If performing sentry duties for an archon:
        if (isSentry) {
            // Move toward my archon
            setDirection(myLeader.location);
            // Find our best target, and aim our attackers at it.
            chooseAttackTarget();
        } else if (isLeader) {
            // Move toward our goal
            setDirection(travelDestination);
            // Find our best target, and aim our attackers at it
            chooseAttackTarget();
        } else if (isPuller) {
            // Move, move, move!!!
            pullerMove();
        } else { // If we are an explorer scout

        }
        super.turn();
    }

    protected RobotInfo findLowestHealthUninfectedEnemy() {
        RobotInfo[] enemies = rc.senseNearbyRobots(INFINITY, theirTeam);
        RobotInfo lowestHealthUninfectedEnemy = null;
        double minHealth = INFINITY;
        for (int i = 0; i < enemies.length; i++) {
            if (enemies[i].zombieInfectedTurns == 0
                    && enemies[i].health < minHealth) {
                minHealth = enemies[i].health;
                lowestHealthUninfectedEnemy = enemies[i];
            }
        }
        return lowestHealthUninfectedEnemy;
    }

    private void checkMessages() {
        for (int i = 0; i < signals.length; i++) {
            if (signals[i].getTeam() == myTeam) {
                int[] messages = signals[i].getMessage();
                if (messages.length > 0 && messages[0] % 100000 == rc.getID()) {  
                                                // We have received a command
                    if (messages[0] / 100000 == LEAD_PATROL){
                        // We will be leading a patrol
                        isLeader = true;
                        isSentry = false;
                        isExplorer = false;
                        isPuller = false;
                        travelDestination = 
                        new MapLocation(messages[1] / 1000, messages[1] % 1000);
                    } else if (messages[0] / 100000 == PULL_ZOMBIES) {
                        // We will be pulling zombies away from the group
                        isLeader = false;
                        isSentry = false;
                        isExplorer = false;
                        isPuller = true;
                        travelDestination =
                        new MapLocation(messages[1] / 1000, messages[1] % 1000);
                    }
                }
            }
        }
    }

    private void setDirection(MapLocation location) {
        nextMove = myLocation.add(
                myLocation.directionTo(location));
    }
    
    // Needs more work!
    private void pullerMove() {
        if (zombies.length > 0 || pullerTurns < 5) {
            nextMove = travelDestination;
        } else {
            nextMove = myLocation;
        }
        pullerTurns++;
        if (pullerTurns > MAX_PULLER_TURNS){
            isPuller = false;
            isSentry = true;
            pullerTurns = 0;
        }
    }

    private void chooseAttackTarget() throws GameActionException {
        RobotInfo target = findLowestHealthUninfectedEnemy();
        if (target != null) {
            int coords = target.location.x * 1000 + target.location.y;
            rc.broadcastMessageSignal(myLeader.ID, coords, myType.sensorRadiusSquared);
        } else {
            target = findNearestHostile();
            if (target != null) {
                int coords = target.location.x * 1000 + target.location.y;
                rc.broadcastMessageSignal(myLeader.ID, coords, myType.sensorRadiusSquared);
            } else {
                rc.broadcastMessageSignal(myLeader.ID, 0, myType.sensorRadiusSquared);
            }
        }
    }
}
