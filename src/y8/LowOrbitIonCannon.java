package y8;

import battleship.interfaces.BattleshipsPlayer;
import battleship.interfaces.Fleet;
import battleship.interfaces.Position;
import battleship.interfaces.Board;
import battleship.interfaces.Ship;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author Benjamin Rasmussen, Christian Barth, Marco Frydshou.
 */
public class LowOrbitIonCannon implements BattleshipsPlayer
{

    private final static Random rnd = new Random();
    private Position lastShot = new Position(0, 0);
    private Position holdShot;
    private Position[] holdPattern;
    private Position[][] position2DArr;
    private ArrayList<Position> targetList = new ArrayList();
    private boolean[][] shipPositions;
    private int[][] heatMap;
    private int[][] enemyHeatMap;
    private boolean hit;
    private boolean onHunt;
    private int startEnemyShips;
    private int compareEnemyShips;
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
    // Places the largest ship first //
    public void placeShips(Fleet fleet, Board board)
    {
        plantTheSeeds(fleet, board);
//        this.oldPlaceShips(fleet, board);
//        System.out.println(board);
    }

    private void plantTheSeeds(Fleet fleet, Board board)
    {
//        board.placeShip(new Position(8, 9), fleet.getShip(0), false);
//        board.placeShip(new Position(0, 7), fleet.getShip(1), true);
//        board.placeShip(new Position(6, 1), fleet.getShip(2), false);
//        board.placeShip(new Position(3, 6), fleet.getShip(3), false);
//        board.placeShip(new Position(1, 0), fleet.getShip(4), true);
        ArrayList<Position> possiblePlacement = new ArrayList();
        for (int i = 0; i < fleet.getNumberOfShips(); i++)
        {
            Ship ship = fleet.getShip(i);
            possiblePlacement.clear();
            
        }
    }

    private void oldPlaceShips(Fleet fleet, Board board)
    {
        for (int i = fleet.getNumberOfShips() - 1; i >= 0; i--)
        {
            Ship ship = fleet.getShip(i);
            boolean vertical = rnd.nextBoolean();
            Position pos = pickPosition(vertical, ship);

            while (checkValidPlacement(pos, vertical, ship) == false)
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

    private boolean checkValidPlacement(Position pos, boolean vertical, Ship ship)
    {
        if (vertical)
        {
            for (int i = pos.y; i < ship.size() + pos.y; i++)
            {
                if (shipPositions[pos.x][i])
                {
                    return false;
                }
            }
        } else
        {
            for (int i = pos.x; i < ship.size() + pos.x; i++)
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

    private ArrayList<Position> shipPlacementX(int shipSize)
    {
        int counter = 0;
        Position holdPos = null;
        ArrayList<Position> possiblePlacementX = new ArrayList();

        for (int y = 0; y < shipPositions.length - 1; y++)
        {
            for (int x = 0; x < shipPositions.length - 1; x++)
            {
                if (holdPos == null)
                    holdPos = new Position(x, y);
                if (!shipPositions[x][y])
                    counter++;
                else
                {
                    counter = 0;
                    holdPos = null;
                }

                if (counter == shipSize)
                {
                    possiblePlacementX.add(holdPos);
                    holdPos = null;
                }
            }
        }

        return possiblePlacementX;
    }
    
    private ArrayList<Position> shipPlacementY(int shipSize)
    {
        int counter = 0;
        Position holdPos = null;
        ArrayList<Position> possiblePlacementY = new ArrayList();

        for (int x = 0; x < shipPositions.length - 1; x++)
        {
            for (int y = 0; y < shipPositions.length - 1; y++)
            {
                if (holdPos == null)
                    holdPos = new Position(x, y);
                if (!shipPositions[x][y])
                    counter++;
                else
                {
                    counter = 0;
                    holdPos = null;
                }

                if (counter == shipSize)
                {
                    possiblePlacementY.add(holdPos);
                    holdPos = null;
                }
            }
        }

        return possiblePlacementY;
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
        enemyHeatMap[pos.x][pos.y] += 1;
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
        return shotManager();
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
                {
                    maxHeat = heatMap1[i];
                }
            }
        }

        for (int i = 0; i < heatMap.length; i++)
        {
            for (int j = 0; j < heatMap.length; j++)
            {
                if (maxHeat == heatMap[i][j])
                {
                    targetList.add(new Position(i, j));
                }
            }
        }
        Collections.shuffle(targetList);
        lastShot = targetList.get(0);
        return targetList.get(0);
    }

    private void heatManager()
    {
        heatMap[lastShot.x][lastShot.y] = 0;

        // Counter wrecked from start //
        if (!hit && currentEnemyShips < compareEnemyShips)
        {
            compareEnemyShips = currentEnemyShips;
        }

        if (currentEnemyShips < compareEnemyShips)
        {
            hit = false;
            onHunt = false;
            applyHeat(holdPattern, 1);
            compareEnemyShips = currentEnemyShips;
        }

        if (hit && !onHunt)
        {
            onHunt = true;
            holdShot = lastShot;
            holdPattern = huntPattern(lastShot);
            applyHeat(holdPattern, 4);
        }

        /* Increases heat in a direction to lastShot, reduces heat around
           the shot that initialized the hunt (holdShot) */
        if (hit && onHunt)
        {
            int incrementX = (int) Math.signum(lastShot.x - holdShot.x);
            int incrementY = (int) Math.signum(lastShot.y - holdShot.y);

            if (incrementX != 0)
            {
                Position[] increaseHeat =
                {
                    new Position(lastShot.x + incrementX, lastShot.y)
                };
                applyHeat(increaseHeat, 5);
            } else if (incrementY != 0)
            {
                Position[] increaseHeat =
                {
                    new Position(lastShot.x, lastShot.y + incrementY)
                };
                applyHeat(increaseHeat, 5);
            }
        }
    }

    private void applyHeat(Position[] posArr, int heat)
    {
        for (Position pos : posArr)
        {
            if (pos != null && heatMap[pos.x][pos.y] != 0)
            {
                heatMap[pos.x][pos.y] = heat;
            }
        }
    }

    // Returns tiles linear adjacent to the hit //
    private Position[] huntPattern(Position lastShot)
    {
        // In order: Up, Down, Left, Right //
        Position[] posArray = new Position[4];

        for (int i = 0; i < posArray.length; i++)
        {
            try
            {
                if (i == 0)
                    posArray[i] = position2DArr[lastShot.x][lastShot.y + 1];
                if (i == 1)
                    posArray[i] = position2DArr[lastShot.x][lastShot.y - 1];
                if (i == 2)
                    posArray[i] = position2DArr[lastShot.x - 1][lastShot.y];
                if (i == 3)
                    posArray[i] = position2DArr[lastShot.x + 1][lastShot.y];
            } catch (java.lang.ArrayIndexOutOfBoundsException ex)
            {
            }
        }
        return posArray;
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
        currentEnemyShips = enemyShips.getNumberOfShips();
        heatManager();
//        this.printHeatmap();
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
        heatMap = new int[sizeX][sizeY];
        enemyHeatMap = new int[sizeX][sizeY];
        startEnemyShips = ships.getNumberOfShips();
        position2DArr = new Position[sizeX][sizeY];
        for (int x = 0; x < position2DArr.length; x++)
        {
            for (int y = 0; y < position2DArr.length; y++)
            {
                position2DArr[x][y] = new Position(x, y);
            }
        }
    }

    /**
     * Called at the beginning of each round.
     *
     * @param round int the current round number.
     */
    @Override
    public void startRound(int round)
    {
        shipPositions = new boolean[sizeX][sizeY];
        compareEnemyShips = startEnemyShips;
        currentEnemyShips = startEnemyShips;
        resetHeatMap();
//        this.printHeatmap();
    }

    private void resetHeatMap()
    {
        int heat = 1;
        for (int i = 0; i < heatMap.length; i++)
        {
            for (int j = 0; j < heatMap.length; j++)
            {
                heatMap[i][j] = heat;
                if (heat == 1)
                {
                    heat = 2;
                } else
                {
                    heat = 1;
                }
            }
            if (heat == 1)
            {
                heat = 2;
            } else
            {
                heat = 1;
            }
        }
        heatMap[3][6] = 3;
        heatMap[5][6] = 3;
        heatMap[4][5] = 3;
        heatMap[6][5] = 3;
        heatMap[3][4] = 3;
        heatMap[5][4] = 3;
        heatMap[4][3] = 3;
        heatMap[6][3] = 3;
    }

    private void printHeatmap()
    {
        System.out.println("#####################################################");
        for (int i = heatMap.length - 1; i >= 0; i--)
        {
            for (int j = 0; j < heatMap.length; j++)
            {
                System.out.print("[" + heatMap[j][i] + "]");
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
