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
public class Turret extends AttackBot {
    
    public Turret(RobotController rc) throws GameActionException {
        super(rc);
    }
    
    /**
     *
     * @throws battlecode.common.GameActionException
     */
    @Override
    public void turn() throws GameActionException {
        enemies = getTurretAttackableEnemies();
        zombies = getTurretAttackableZombies();
        attack();
    }
    
    private RobotInfo[] getTurretAttackableEnemies(){
        int count = 0;
        for (int i = 0; i < enemies.length; i++){
            if (enemies[i].location.distanceSquaredTo(myLocation) > GameConstants.TURRET_MINIMUM_RANGE){
                count++;
            }
        }
        RobotInfo[] newEnemies = new RobotInfo[count];
        count = 0;
        for (int i = 0; i < enemies.length; i++){
            if (enemies[i].location.distanceSquaredTo(myLocation) > GameConstants.TURRET_MINIMUM_RANGE){
                newEnemies[count] = enemies[i];
                count++;
            }
        }
        return newEnemies;
    }
    
    private RobotInfo[] getTurretAttackableZombies(){
        int count = 0;
        for (int i = 0; i < zombies.length; i++){
            if (zombies[i].location.distanceSquaredTo(myLocation) > GameConstants.TURRET_MINIMUM_RANGE){
                count++;
            }
        }
        RobotInfo[] newZombies = new RobotInfo[count];
        count = 0;
        for (int i = 0; i < zombies.length; i++){
            if (zombies[i].location.distanceSquaredTo(myLocation) > GameConstants.TURRET_MINIMUM_RANGE){
                newZombies[count] = zombies[i];
                count++;
            }
        }
        return newZombies;
   }
    
}
