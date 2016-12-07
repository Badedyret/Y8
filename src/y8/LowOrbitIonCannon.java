package y8;

import battleship.interfaces.BattleshipsPlayer;
import battleship.interfaces.Fleet;
import battleship.interfaces.Position;
import battleship.interfaces.Board;
import battleship.interfaces.Ship;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author Benjamin Rasmussen, Christian Barth, Marco Frydshou.
 */
public class LowOrbitIonCannon implements BattleshipsPlayer {

    private final static Random rnd = new Random();
    private Position lastShot = new Position(0, 0);
    private HashMap<Integer, Position> targetMap = new HashMap();
    private boolean[][] shipPositions;
    private boolean[][] shotPositions;
    private boolean hit;
    private int shotsFired = 1;
    private int currentEnemyShips;
    private int sizeX;
    private int sizeY;

    public LowOrbitIonCannon()
    {
    }

    /**
     * The method called when its time for the AI to place ships on the board
     * (at the beginning of each round).
     *
     * The Ship object to be placed MUST be taken from the Fleet given (do not
     * create your own Ship objects!).
     *
     * A ship is placed by calling the board.placeShip(..., Ship ship, ...) for
     * each ship in the fleet (see board interface for details on placeShip()).
     *
     * A player is not required to place all the ships. Ships placed outside the
     * board or on top of each other are wrecked.
     *
     * @param fleet Fleet all the ships that a player should place.
     * @param board Board the board were the ships must be placed.
     */
    @Override
    public void placeShips(Fleet fleet, Board board)
    {
        for (int i = 0; i < fleet.getNumberOfShips(); ++i)
        {
            Ship ship = fleet.getShip(i);
            boolean vertical = rnd.nextBoolean();
            Position pos = pickPosition(vertical, ship);
            
            // Der er noget galt med checkValidPlacement når man kører den flere gange end en (SingleMatchVisualizer)
            while (checkValidPlacement(pos, vertical, ship.size()) == false)
            {
                vertical = rnd.nextBoolean();
                pos = pickPosition(vertical, ship);
            }
            board.placeShip(pos, ship, vertical);
            mapShips(pos, vertical, ship.size());
        }
    }

    private Position pickPosition(boolean vertical, Ship ship)
    {
        if (vertical)
        {
            int x = rnd.nextInt(sizeX);
            int y = rnd.nextInt(sizeY - (ship.size() - 1));
            return new Position(x, y);
        } else
        {
            int x = rnd.nextInt(sizeX - (ship.size() - 1));
            int y = rnd.nextInt(sizeY);
            return new Position(x, y);
        }
    }

    private boolean checkValidPlacement(Position pos, boolean vertical, int shipSize)
    {
        if (vertical)
        {
            for (int i = pos.y; i < shipSize + pos.y; i++)
            {
                if (shipPositions[pos.x][i])
                {
                    return false;
                }
            }
        } else
        {
            for (int i = pos.x; i < shipSize + pos.x; i++)
            {
                if (shipPositions[i][pos.y])
                {
                    return false;
                }
            }
        }
        return true;
    }

    private void mapShips(Position pos, boolean vertical, int shipSize)
    {
        if (vertical)
        {
            for (int i = pos.y; i < shipSize + pos.y; i++)
            {
                shipPositions[pos.x][i] = true;
            }
        } else
        {
            for (int i = pos.x; i < shipSize + pos.x; i++)
            {
                shipPositions[i][pos.y] = true;
            }
        }
    }

    /**
     * Called every time the enemy has fired a shot.
     *
     * The purpose of this method is to allow the AI to react to the enemy's
     * incoming fire and place his/her ships differently next round.
     *
     * @param pos Position of the enemy's shot
     */
    @Override
    public void incoming(Position pos)
    {

        //Do nothing
    }

    /**
     * Called by the Game application to get the Position of your shot.
     * hitFeedBack(...) is called right after this method.
     *
     * @param enemyShips Fleet the enemy's ships. Compare this to the Fleet
     * supplied in the hitFeedBack(...) method to see if you have sunk any
     * ships.
     *
     * @return Position of you next shot.
     */
    @Override
    public Position getFireCoordinates(Fleet enemyShips)
    {
//        if (hit && targetMap.isEmpty())
//        {
//            huntPattern();
//        }
//        if (!targetMap.isEmpty())
//        {
//            if (shotsFired <= targetMap.size())
//            {
//                Position shot = targetMap.get(shotsFired);
//                lastShot = shot;
//                shotsFired++;
//                return shot;
//            } else
//            {
//                targetMap.clear();
//                shotsFired = 1;
//                hit = false;
//            }
//        }
//
//        return randomShot();
        return lastShot;
    }

    private void huntPattern()
    {
        if (lastShot.x == 0 && lastShot.y == 0)
        {
            targetMap.put(1, new Position(lastShot.x, lastShot.y + 1));
            targetMap.put(2, new Position(lastShot.x + 1, lastShot.y));
            return;
        }
        if (lastShot.x == sizeX - 1 && lastShot.y == sizeY - 1)
        {
            targetMap.put(1, new Position(lastShot.x, lastShot.y - 1));
            targetMap.put(2, new Position(lastShot.x - 1, lastShot.y));
            return;
        }
        if (lastShot.x == 0 && lastShot.y == sizeY - 1)
        {
            targetMap.put(1, new Position(lastShot.x, lastShot.y - 1));
            targetMap.put(2, new Position(lastShot.x + 1, lastShot.y));
            return;
        }
        if (lastShot.x == sizeX - 1 && lastShot.y == 0)
        {
            targetMap.put(1, new Position(lastShot.x, lastShot.y + 1));
            targetMap.put(2, new Position(lastShot.x + 1, lastShot.y));
            return;
        }
        if (lastShot.x == sizeX - 1)
        {
            targetMap.put(1, new Position(lastShot.x, lastShot.y + 1));
            targetMap.put(2, new Position(lastShot.x, lastShot.y - 1));
            targetMap.put(3, new Position(lastShot.x - 1, lastShot.y));
        }
        if (lastShot.y == sizeY - 1)
        {
            targetMap.put(1, new Position(lastShot.x - 1, lastShot.y));
            targetMap.put(2, new Position(lastShot.x + 1, lastShot.y));
            targetMap.put(3, new Position(lastShot.x, lastShot.y - 1));
        }
        if (lastShot.x == 0)
        {
            targetMap.put(1, new Position(lastShot.x, lastShot.y + 1));
            targetMap.put(2, new Position(lastShot.x, lastShot.y - 1));
            targetMap.put(3, new Position(lastShot.x + 1, lastShot.y));
        }
        if (lastShot.y == 0)
        {
            targetMap.put(1, new Position(lastShot.x - 1, lastShot.y));
            targetMap.put(2, new Position(lastShot.x + 1, lastShot.y));
            targetMap.put(3, new Position(lastShot.x, lastShot.y + 1));
        } else
        {
            targetMap.put(1, new Position(lastShot.x, lastShot.y + 1));
            targetMap.put(2, new Position(lastShot.x, lastShot.y - 1));
            targetMap.put(3, new Position(lastShot.x + 1, lastShot.y));
            targetMap.put(4, new Position(lastShot.x - 1, lastShot.y));
        }
    }

    private Position randomShot()
    {
        Position shot = new Position(rnd.nextInt(sizeX), rnd.nextInt(sizeY));
        while (shotPositions[shot.x][shot.y])
        {
            shot = new Position(rnd.nextInt(sizeX), rnd.nextInt(sizeY));
        }
        shotPositions[shot.x][shot.y] = true;
        lastShot = shot;
        return shot;
    }

    /**
     * Called right after getFireCoordinates(...) to let your AI know if you hit
     * something or not.
     *
     * Compare the number of ships in the enemyShips with that given in
     * getFireCoordinates in order to see if you sunk a ship.
     *
     * @param hit boolean is true if your last shot hit a ship. False otherwise.
     * @param enemyShips Fleet the enemy's ships.
     */
    @Override
    public void hitFeedBack(boolean hit, Fleet enemyShips)
    {
        this.hit = hit;
    }

    /**
     * Called in the beginning of each match to inform about the number of
     * rounds being played.
     *
     * @param rounds int the number of rounds i a match
     */
    @Override
    public void startMatch(int rounds, Fleet ships, int sizeX, int sizeY)
    {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        shipPositions = new boolean[sizeX][sizeY];
        shotPositions = new boolean[sizeX][sizeY];
        currentEnemyShips = ships.getNumberOfShips();
    }

    /**
     * Called at the beginning of each round.
     *
     * @param round int the current round number.
     */
    @Override
    public void startRound(int round)
    {
        //Do nothing
    }

    /**
     * Called at the end of each round to let you know if you won or lost.
     * Compare your points with the enemy's to see who won.
     *
     * @param round int current round number.
     * @param points your points this round: 100 - number of shot used to sink
     * all of the enemy's ships.
     *
     * @param enemyPoints int enemy's points this round.
     */
    @Override
    public void endRound(int round, int points, int enemyPoints)
    {
        //Do nothing
    }

    /**
     * Called at the end of a match (that usually last 1000 rounds) to let you
     * know how many losses, victories and draws you scored.
     *
     * @param won int the number of victories in this match.
     * @param lost int the number of losses in this match.
     * @param draw int the number of draws in this match.
     */
    @Override
    public void endMatch(int won, int lost, int draw)
    {
        //Do nothing
    }
}
