package y8;

import battleship.interfaces.BattleshipsPlayer;
import battleship.interfaces.Fleet;
import battleship.interfaces.Position;
import battleship.interfaces.Board;
import battleship.interfaces.Ship;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Benjamin Rasmussen, Christian Barth, Marco Frydshou.
 */
public class LowOrbitIonCannon implements BattleshipsPlayer {

    private final static Random rnd = new Random();
    private Position lastShot = new Position(0, 0);
    private Position holdShot;
    private HashMap<Integer, Position> targetMap = new HashMap();
    private ArrayList<Position> targetList = new ArrayList();
    private boolean[][] shipPositions;
    private boolean[][] shotPositions;
    private int[][] heatMap;
    private boolean hit;
    private boolean onHunt;
    private int shotAtTarget = 0;
    private int startEnemyShips;
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
        for (int i = 0; i < fleet.getNumberOfShips(); i++)
        {
            Ship ship = fleet.getShip(i);
            boolean vertical = rnd.nextBoolean();
            Position pos = pickPosition(vertical, ship);

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
        if (hit && targetMap.isEmpty())
        {
            huntPatternOLD();
        }
        if (shotAtTarget >= targetMap.size())
        {
            targetMap.clear();
            shotAtTarget = 0;
            hit = false;
        }
        if (!targetMap.isEmpty())
        {
            while (shotAtTarget < targetMap.size())
            {
                shotAtTarget++;
                Position shot = targetMap.get(shotAtTarget);
                if (!shotPositions[shot.x][shot.y])
                {
                    lastShot = shot;
                    shotPositions[shot.x][shot.y] = true;
                    return shot;
                }
            }
        }

        Position pos = shotManager();
        heatMap[pos.x][pos.y] = 0;
        lastShot = pos;
        shotPositions[pos.x][pos.y] = true;
        return pos;
    }

    private Position shotManager()
    {
        targetList.clear();
        int maxHeat = 0;
        for (int[] heatMap1 : heatMap)
        {
            for (int i = 0; i < heatMap.length; i++)
            {
                if (maxHeat < heatMap1[i])
                    maxHeat = heatMap1[i];
            }
        }
        
        for (int i = 0; i < heatMap.length; i++)
        {
            for (int j = 0; j < heatMap.length; j++)
            {
                if (maxHeat == heatMap[i][j])
                    targetList.add(new Position(i, j));
            }
        }
        Collections.shuffle(targetList);
        return targetList.get(0);
    }

    private void heatManager()
    {
        heatMap[lastShot.x][lastShot.y] = 0;

        if (hit && !onHunt)
        {
            hit = false;
            onHunt = true;
            holdShot = lastShot;
            applyHeat(huntPattern(), 3);
        }
    }

    private Position[] huntPattern()
    {
        if (lastShot.x == 0 && lastShot.y == 0)
        {
            Position[] posArray =
            {
                new Position(1, 0), new Position(0, 1)
            };
            return posArray;
        }
        if (lastShot.x == sizeX - 1 && lastShot.y == sizeY - 1)
        {
            Position[] posArray =
            {
                new Position(sizeX - 2, sizeY - 1),
                new Position(sizeX - 1, sizeY - 2)
            };
            return posArray;
        }
        if (lastShot.x == 0 && lastShot.y == sizeY - 1)
        {
            Position[] posArray =
            {
                new Position(1, sizeY - 1), new Position(0, sizeY - 2)
            };
            return posArray;
        }
        if (lastShot.x == sizeX - 1 && lastShot.y == 0)
        {
            Position[] posArray =
            {
                new Position(sizeX - 1, 1), new Position(sizeX - 2, 0)
            };
            return posArray;
        }
        if (lastShot.x == sizeX - 1)
        {
            Position[] posArray =
            {
                new Position(lastShot.x, lastShot.y + 1),
                new Position(lastShot.x, lastShot.y - 1),
                new Position(lastShot.x - 1, lastShot.y)
            };
            return posArray;
        }
        if (lastShot.y == sizeY - 1)
        {
            Position[] posArray =
            {
                new Position(lastShot.x - 1, lastShot.y),
                new Position(lastShot.x + 1, lastShot.y),
                new Position(lastShot.x, lastShot.y - 1)
            };
            return posArray;
        }
        if (lastShot.x == 0)
        {
            Position[] posArray =
            {
                new Position(lastShot.x, lastShot.y - 1),
                new Position(lastShot.x, lastShot.y + 1),
                new Position(lastShot.x + 1, lastShot.y)
            };
            return posArray;
        }
        if (lastShot.y == 0)
        {
            Position[] posArray =
            {
                new Position(lastShot.x - 1, lastShot.y),
                new Position(lastShot.x + 1, lastShot.y),
                new Position(lastShot.x, lastShot.y + 1)
            };
            return posArray;
        } else
        {
            Position[] posArray =
            {
                new Position(lastShot.x + 1, lastShot.y),
                new Position(lastShot.x - 1, lastShot.y),
                new Position(lastShot.x, lastShot.y + 1),
                new Position(lastShot.x, lastShot.y - 1)
            };
            return posArray;
        }
    }

    private void applyHeat(Position[] posArr, int heat)
    {
        for (Position pos : posArr)
        {
            if (heatMap[pos.x][pos.y] != 0)
                heatMap[pos.x][pos.y] = heat;
        }
    }

    private void huntPatternOLD()
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
            return;
        }
        if (lastShot.y == sizeY - 1)
        {
            targetMap.put(1, new Position(lastShot.x - 1, lastShot.y));
            targetMap.put(2, new Position(lastShot.x + 1, lastShot.y));
            targetMap.put(3, new Position(lastShot.x, lastShot.y - 1));
            return;
        }
        if (lastShot.x == 0)
        {
            targetMap.put(1, new Position(lastShot.x, lastShot.y + 1));
            targetMap.put(2, new Position(lastShot.x, lastShot.y - 1));
            targetMap.put(3, new Position(lastShot.x + 1, lastShot.y));
            return;
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
        startEnemyShips = ships.getNumberOfShips();
    }

    /**
     * Called at the beginning of each round.
     *
     * @param round int the current round number.
     */
    @Override
    public void startRound(int round)
    {
        heatMap = new int[sizeX][sizeY];
        shipPositions = new boolean[sizeX][sizeY];
        shotPositions = new boolean[sizeX][sizeY];
        currentEnemyShips = startEnemyShips;
        resetHeatmap();
//        this.printHeatmap();
    }

    private void resetHeatmap()
    {
        int heat = 1;
        for (int i = 0; i < heatMap.length; i++)
        {
            for (int j = 0; j < heatMap.length; j++)
            {
                heatMap[i][j] = heat;
                if (heat == 1)
                    heat = 2;
                else
                    heat = 1;
            }
            if (heat == 1)
                heat = 2;
            else
                heat = 1;
        }
    }

    private void printHeatmap()
    {
        System.out.println("#####################################################");
        for (int i = 0; i < heatMap.length; i++)
        {
            for (int j = 0; j < heatMap.length; j++)
            {
                System.out.print("[" + heatMap[i][j] + "]");
            }
            System.out.println("");
        }
        System.out.println("####################################################");
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
