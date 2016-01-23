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
public class BaseBot {
    public static final int INFINITY = Integer.MAX_VALUE;
    
    // information for decoding Archon orders
    public static final int LEAD_PATROL = 1;
    public static final int PULL_ZOMBIES = 2;
    
    // constants for location information
    public static final int NORTH = 100;
    public static final int SOUTH = 200;
    public static final int EAST  = 300;
    public static final int WEST  = 400;
    
    // constants related to zombie spawns
    public static final int ZOMBIE_THREAT_TURNS = 5;
    public static final int ZOMBIE_PULLER_DISTANCE = 100;
    public static final int MAX_PULLER_TURNS = 2000;
    
    protected RobotController rc;          // This robot
    
    protected Team myTeam;                 // Need to know which team we are
    protected Team theirTeam;
    
    protected RobotType myType;            // What type of robot?
    
    protected RobotInfo myLeader;          // The archon this bot belongs to
    
    protected MapLocation nextMove;     //Where are we headed?
    
    protected MapLocation myLocation;      //Always need to know our current loc
    protected MapLocation centerOfMap;     //Center of the map?
    
    protected int mapHeight;               // Map Dimensions
    protected int mapWidth;
    
    protected Direction[] directions;      // List of all directions
    
    Signal[] signals;
    
    
    // Constructor
    public BaseBot(RobotController inRC) throws GameActionException {
        rc = inRC;
        myTeam = rc.getTeam();
        theirTeam = myTeam.opponent();
        myType = rc.getType();
        directions = Direction.values();
        myLocation = rc.getLocation();
        myLeader = findNearestArchon();
    }
    
    // Operations which need to happen at the beginning of each turn
    public void begin() throws GameActionException {
        if (myLeader != null){
            if (rc.canSenseRobot(myLeader.ID)){ // Update info on my archon
                myLeader = rc.senseRobot(myLeader.ID);
            }
        } else {
            myLeader = findNearestArchon();
        }
        myLocation = rc.getLocation();
        signals = rc.emptySignalQueue();
    }
    
    // General turn operation (moving, attacking, thinking)
    public void turn() throws GameActionException {
        move();
    }
    
    // Clean up and yield turn
    public void end(){
        Clock.yield();
    }
    
    // Just get a random direction (Mostly for testing purposes)
    protected Direction getRandomDirection(){
        int direction = (int) (Math.random() * 8);
        return getClearDirectionLeft(directions[direction]);
    }
    
    // Find the closest movable direction to where we are trying to go, turning
    //  in a counter-clockwise direction
    protected Direction getClearDirectionLeft(Direction d){
        int count = 0;
        while(!rc.canMove(d) && 
             !(myType.canClearRubble() && rc.senseRubble(myLocation.add(d)) > 0) 
                     && count < 8){
            d = d.rotateLeft();
        }
        if (count < 8){
            return d;
        } else {
            return null;
        }
    }

    // Find a nearby archon to attach to
    private RobotInfo findNearestArchon() {
        RobotInfo nearestArchon = null;
        int minDistance = INFINITY;
        int currentDistance;
        RobotInfo[] allies = rc.senseNearbyRobots(myType.sensorRadiusSquared, myTeam);
        for (int i = 0; i < allies.length; i++){
            if (allies[i].type == RobotType.ARCHON){
                currentDistance = allies[i].location.distanceSquaredTo(myLocation);
                if (currentDistance < minDistance){
                    minDistance = currentDistance;
                    nearestArchon = allies[i];
                }
            }
        }
        return nearestArchon;
    }
    
    // Find the hostile robot closest to this robot
    protected RobotInfo findNearestHostile(){
        RobotInfo[] hostiles = rc.senseHostileRobots(myLocation, INFINITY);
        RobotInfo nearestHostile = null;
        int minDistance = INFINITY;
        int currentDistance;
        for (int i = 0; i < hostiles.length; i++){
            currentDistance = myLocation.distanceSquaredTo(hostiles[i].location);
            if (currentDistance < minDistance){
                minDistance = currentDistance;
                nearestHostile = hostiles[i];
            }
        }
        return nearestHostile;
    }
    
    protected RobotInfo findNearestAlly(){
        RobotInfo[] allies = rc.senseNearbyRobots(INFINITY, myTeam);
        RobotInfo nearestAlly = null;
        int minDistance = INFINITY;
        int currentDistance;
        for (int i = 0; i < allies.length; i++){
            currentDistance = myLocation.distanceSquaredTo(allies[i].location);
            if (currentDistance < minDistance){
                minDistance = currentDistance;
                nearestAlly = allies[i];
            }
        }
        return nearestAlly;
    }
    
    protected void move() throws GameActionException {
        if (rc.isCoreReady()){
            Direction toMove 
                     = getClearDirectionLeft(myLocation.directionTo(nextMove));
            if (rc.canMove(toMove)){
                rc.move(toMove);
            } else if (toMove!= Direction.OMNI && toMove != Direction.NONE) {
                rc.clearRubble(toMove);
            }
        }
    }
}
