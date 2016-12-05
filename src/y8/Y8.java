/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package y8;

import battleship.interfaces.BattleshipsPlayer;
import tournament.player.PlayerFactory;

/**
 *
 * @author Tobias Grundtvig
 */
public class Y8 implements PlayerFactory<BattleshipsPlayer>
{
    public Y8(){}
    
    
    @Override
    public BattleshipsPlayer getNewInstance()
    {
        return new LowOrbitIonCannon();
    }

    @Override
    public String getID()
    {
        return "Y8";
    }

    @Override
    public String getName()
    {
        return "Low Orbit Ion Cannon";
    }

    @Override
    public String[] getAuthors()
    {
        String[] res = {"Joes"};
        return res;
    }
    
}
