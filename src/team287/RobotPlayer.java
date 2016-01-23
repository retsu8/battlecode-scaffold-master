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
public class RobotPlayer {
    
    public static void run(RobotController rc){
        BaseBot robot = null;
        
        boolean created = false;
        while (!created){
            try{
            switch(rc.getType()){
                case ARCHON: 
                    robot = new Archon(rc);
                    created = true;
                    break;
                case TURRET:
                    robot = new Turret(rc);
                    created = true;
                    break;
                case TTM:
                    robot = new TTM(rc);
                    created = true;
                    break;
                case SCOUT:
                    robot = new Scout(rc);
                    created = true;
                    break;
                case GUARD:
                case SOLDIER:
                case VIPER:
                    robot = new AttackBot(rc);
                    created = true;
                    break;
            }
            } catch (Exception e){
                System.out.println(e);
            }
        }
        while(true){
            try {
                robot.begin();
                robot.turn();
                robot.end();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
}
