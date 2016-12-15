package y8;

import battleship.interfaces.BattleshipsPlayer;
import tournament.player.PlayerFactory;

/**
 *
 * @author Benjamin Rasmussen, Christian Barth, Marco Frydshou.
 */
public class Y8 implements PlayerFactory<BattleshipsPlayer>
{
    public Y8()
    {
    }
    
    @Override
    public BattleshipsPlayer getNewInstance()
    {
        return new LOIC();
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
        String[] res = {"Benjamin Rasmussen", "Christian Barth", "Marco Frydshou"};
        return res;
    }
    
}
