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
    private ArrayList<Position> targetList = new ArrayList();
    private boolean[][] shipPositions;
    // Only used in old pattern //
    private HashMap<Integer, Position> targetMap = new HashMap();
    private boolean[][] shotPositions;
    //
    private int[][] heatMap;
    private boolean hit;
    private boolean onHunt;
    private int shotAtTarget = 0;
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
        board.placeShip(new Position(8,9), fleet.getShip(0), false);
        for (int i = fleet.getNumberOfShips() - 1; i >= 1; i--)
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
//        System.out.println(board);
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
    
//    private boolean checkShipSides(Position pos, boolean vertical, Ship ship) 
//    {
//       if (vertical) 
//       {
//           if () 
//           {
//               
//           }
//       }
//       else 
//       {
//           
//       }
//        
//        return false;
//    }

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
//            huntPatternOLD();
//        }
//        if (shotAtTarget >= targetMap.size())
//        {
//            targetMap.clear();
//            shotAtTarget = 0;
//            hit = false;
//        }
//        if (!targetMap.isEmpty())
//        {
//            while (shotAtTarget < targetMap.size())
//            {
//                shotAtTarget++;
//                Position shot = targetMap.get(shotAtTarget);
//                if (!shotPositions[shot.x][shot.y])
//                {
//                    lastShot = shot;
//                    shotPositions[shot.x][shot.y] = true;
//                    return shot;
//                }
//            }
//        }
//        Position pos = shotManager();
//        heatMap[pos.x][pos.y] = 0;
//        lastShot = pos;
//        shotPositions[pos.x][pos.y] = true;
//        return pos;
        return shotManager();
    }

    // Fix iterate for maxHeat //
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

//        Workaround for ships wrecked before round start //
        if (!hit && currentEnemyShips < compareEnemyShips)
            compareEnemyShips = currentEnemyShips;

        if (currentEnemyShips < compareEnemyShips)
        {
            hit = false;
            onHunt = false;
            applyHeat(holdPattern, 1);
            compareEnemyShips = currentEnemyShips;
        }
        if (hit && !onHunt)
        {
            System.out.println("HUNT STARTED");
            onHunt = true;
            holdShot = lastShot;
            holdPattern = huntPattern(lastShot);
            applyHeat(holdPattern, 3);
        }

        /* Increases heat in a direction to lastShot, reduces heat around
           the shot that initialized the hunt (holdShot) */
        if (hit && onHunt)
        {
            System.out.println("HIT and ONHUNT");
            int incrementX = (int) Math.signum(lastShot.x - holdShot.x);
            int incrementY = (int) Math.signum(lastShot.y - holdShot.y);
            System.out.println(incrementX + ", " + incrementY);

            if (incrementX != 0)
            {
                System.out.println("X AXIS ???????????????????????????");
                // Needs to be optimised to not do everytime //
                Position[] reduceHeat =
                {
                    holdPattern[0],
                    holdPattern[1]
                };
                Position[] increaseHeat =
                {
                    new Position(lastShot.x + incrementX, lastShot.y)
                };
                applyHeat(reduceHeat, 1);
                applyHeat(increaseHeat, 4);
            }
            else if (incrementY != 0)
            {
                System.out.println("Y AXIS ???????????????????????????");
                Position[] reduceHeat =
                {
                    holdPattern[2],
                    holdPattern[3]
                };
                Position[] increaseHeat =
                {
                    new Position(lastShot.x, lastShot.y + incrementY)
                };
                applyHeat(reduceHeat, 1);
                applyHeat(increaseHeat, 4);
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
        if (lastShot.x == 0 && lastShot.y == 0)
        {
            posArray[0] = new Position(0, 1);
            posArray[3] = new Position(1, 0);
            return posArray;
        }
        if (lastShot.x == sizeX - 1 && lastShot.y == sizeY - 1)
        {
            posArray[1] = new Position(sizeX - 1, sizeY - 2);
            posArray[2] = new Position(sizeX - 2, sizeY - 1);
            return posArray;
        }
        if (lastShot.x == 0 && lastShot.y == sizeY - 1)
        {

            posArray[1] = new Position(0, sizeY - 2);
            posArray[3] = new Position(1, sizeY - 1);
            return posArray;
        }
        if (lastShot.x == sizeX - 1 && lastShot.y == 0)
        {
            posArray[0] = new Position(sizeX - 1, 1);
            posArray[2] = new Position(sizeX - 2, 0);
            return posArray;
        }
        if (lastShot.x == sizeX - 1)
        {
            posArray[0] = new Position(lastShot.x, lastShot.y + 1);
            posArray[1] = new Position(lastShot.x, lastShot.y - 1);
            posArray[2] = new Position(lastShot.x - 1, lastShot.y);
            return posArray;
        }
        if (lastShot.y == sizeY - 1)
        {
            posArray[1] = new Position(lastShot.x, lastShot.y - 1);
            posArray[2] = new Position(lastShot.x - 1, lastShot.y);
            posArray[3] = new Position(lastShot.x + 1, lastShot.y);
            return posArray;
        }
        if (lastShot.x == 0)
        {
            posArray[0] = new Position(lastShot.x, lastShot.y + 1);
            posArray[1] = new Position(lastShot.x, lastShot.y - 1);
            posArray[3] = new Position(lastShot.x + 1, lastShot.y);
            return posArray;
        }
        if (lastShot.y == 0)
        {
            posArray[0] = new Position(lastShot.x, lastShot.y + 1);
            posArray[2] = new Position(lastShot.x - 1, lastShot.y);
            posArray[3] = new Position(lastShot.x + 1, lastShot.y);
            return posArray;
        } else
        {
            posArray[0] = new Position(lastShot.x, lastShot.y + 1);
            posArray[1] = new Position(lastShot.x, lastShot.y - 1);
            posArray[2] = new Position(lastShot.x - 1, lastShot.y);
            posArray[3] = new Position(lastShot.x + 1, lastShot.y);
            return posArray;
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
        shipPositions = new boolean[sizeX][sizeY];
        shotPositions = new boolean[sizeX][sizeY];
        compareEnemyShips = startEnemyShips;
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
