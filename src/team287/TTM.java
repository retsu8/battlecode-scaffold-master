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
public class TTM extends BaseBot {
    
    public TTM(RobotController rc) throws GameActionException{
        super(rc);
    }
    
    public void turn() throws GameActionException {
        rc.unpack();
    }
}
