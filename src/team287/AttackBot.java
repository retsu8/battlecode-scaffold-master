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
public class AttackBot extends BaseBot {
    
    RobotInfo[] enemies;        // Keep track of who is nearby
    RobotInfo[] zombies;
    
    MapLocation attackTarget;   // Have we been directed to a target?
    
    int attackRadiusMinusOneSquared;  // To help us keep an ideal range

    public AttackBot(RobotController rc) throws GameActionException {
        super(rc);
        attackRadiusMinusOneSquared = 
                  (int) Math.pow(Math.sqrt(myType.attackRadiusSquared) - 1, 2);
    }

    /**
     * Combat specific turn operations
     * @throws GameActionException
     */
    @Override
    public void begin() throws GameActionException {
        super.begin();
        
        // Find who is near us!
        enemies = rc.senseNearbyRobots(myType.attackRadiusSquared, theirTeam);
        zombies = rc.senseNearbyRobots(myType.attackRadiusSquared, Team.ZOMBIE);
        findAttackTarget();
    }
    
    /**
     * Combat specific turn actions
     * 
     * @throws GameActionException
     */
    @Override
    public void turn() throws GameActionException {
        checkForOrders();
        attack();
        selectDestination();
        super.turn();
    }
    
    protected void attack() throws GameActionException {
        if (attackTarget != null && rc.canAttackLocation(attackTarget) 
                                                    && rc.isWeaponReady()){
            rc.attackLocation(attackTarget);
        } else if (rc.isWeaponReady()) {
            RobotInfo enemyToKill = findLowestHealthUninfectedEnemy();
            if (enemyToKill != null) {
                rc.attackLocation(enemyToKill.location);
            } else if (zombies.length > 0) {
                rc.attackLocation(zombies[0].location);
            } 
        }
    }
    
    protected void selectDestination(){
        if (attackTarget != null){
            if (attackTarget.distanceSquaredTo(myLocation) > 
                                                myType.attackRadiusSquared) {
                nextMove = attackTarget;
            } else {
                nextMove = myLocation;
            }
        } else if (enemies.length > 0){
            nextMove = enemies[0].location;
        } else if (zombies.length > 0) {
            nextMove = zombies[0].location;
        } else if (myLeader != null) {
            if (rc.getHealth() < myType.maxHealth){
                nextMove = myLeader.location;
            } else if (myLocation.distanceSquaredTo(myLeader.location) < attackRadiusMinusOneSquared){
                nextMove = myLocation.add(myLeader.location.directionTo(myLocation));
            } else if (myLocation.distanceSquaredTo(myLeader.location) > myType.attackRadiusSquared){
                nextMove = myLocation.add(myLocation.directionTo(myLeader.location));
            } else {
                nextMove = myLocation.add(myLocation.directionTo(myLeader.location).rotateLeft().rotateLeft());
            }
        }
    }
    
    protected RobotInfo findLowestHealthUninfectedEnemy(){
        RobotInfo nearestUninfectedEnemy = null;
        double minHealth = INFINITY;
        for (int i = 0; i < enemies.length; i++){
            if (enemies[i].zombieInfectedTurns == 0 &&
                        enemies[i].health < minHealth){
                minHealth = enemies[i].health;
                nearestUninfectedEnemy = enemies[i];
            }
        }
        return nearestUninfectedEnemy;
    }
    
    protected void findAttackTarget(){
        if (myLeader == null) {
            attackTarget = null;
            return;
        }
        boolean targeted = false;
        for (int i = 0; i < signals.length && !targeted; i++){
            if (signals[i].getTeam() == myTeam){
                int[] messages = signals[i].getMessage();
                if (messages.length > 0 && messages[0] == myLeader.ID){
                    targeted = true;
                    attackTarget = new 
                          MapLocation(messages[1] / 1000, messages[1] % 1000);
                }
            }
        }
        attackTarget = null;
    }
    
    protected void checkForOrders() throws GameActionException {
        for (int i = 0; i < signals.length; i++) {
            if (signals[i].getTeam() == myTeam) {
                int[] messages = signals[i].getMessage();
                // We have received a command to join a patrol
                if (messages.length > 0 && messages[0] == rc.getID()) {
                    if (rc.canSenseRobot(messages[1])){
                        myLeader = rc.senseRobot(messages[1]);
                    }
                }
            }
        }
    }
}
