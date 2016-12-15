package y8;

import battleship.interfaces.Position;
import java.util.Collections;
import java.util.ArrayList;

public class Shooter {

    public int[][] heatMap;
    public int startEnemyShips;
    private int compareEnemyShips;
    public int currentEnemyShips;
    int resetCounter = 0;
    private Position lastShot = new Position(0, 0);
    private Position holdShot;
    private Position[] holdPattern;
    private ArrayList<Position> targetList = new ArrayList();
    public ArrayList<Integer> enemyShipListTemplate = new ArrayList();
    public ArrayList<Integer> enemyShipList = new ArrayList();
    public boolean hit;
    private boolean onHunt;

    public Position shotManager()
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

    public void heatManager()
    {
        heatMap[lastShot.x][lastShot.y] = 0;

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

        int incrementX = (int) Math.signum(lastShot.x - holdShot.x);
        int incrementY = (int) Math.signum(lastShot.y - holdShot.y);
        /* Increases heat in a direction to lastShot, reduces heat around
           the shot that initialized the hunt (holdShot) */
        if (hit && onHunt)
        {
            // Checks may not be necessary because of interface error-handling //
            Position[] increaseHeat = new Position[1];
            if (incrementX != 0 && huntPattern(lastShot)[2] != null && huntPattern(lastShot)[3] != null)
            {
                increaseHeat[0] = new Position(lastShot.x + incrementX, lastShot.y);
            } else if (incrementY != 0 && huntPattern(lastShot)[0] != null && huntPattern(lastShot)[1] != null)
            {
                increaseHeat[0] = new Position(lastShot.x, lastShot.y + incrementY);
            }
            applyHeat(increaseHeat, 6);
        }

        if (!hit && onHunt)
        {
            Position[] backwardHeat = new Position[1];
            if (incrementX != 0)
            {
                if (incrementX > 0 && holdPattern[2] != null)
                {
                    backwardHeat[0] = new Position(holdShot.x - 1, holdShot.y);
                }
                if (incrementX < 0 && holdPattern[3] != null)
                {
                    backwardHeat[0] = new Position(holdShot.x + 1, holdShot.y);
                }
            } else if (incrementY != 0)
            {
                if (incrementY > 0 && holdPattern[1] != null)
                {
                    backwardHeat[0] = new Position(holdShot.x, holdShot.y - 1);
                }
                if (incrementY < 0 && holdPattern[0] != null)
                {
                    backwardHeat[0] = new Position(holdShot.x, holdShot.y + 1);
                }
            }
            applyHeat(backwardHeat, 5);
        }
    }

    // Unused code //
    private int probabillityChecker(int shotsTaken)
    {
        int keepGoing = 0;
        int turnAround = 0;
        for (Integer shipSize : enemyShipList)
        {
            if (shotsTaken < shipSize / 2)
                keepGoing++;
            if (shotsTaken >= shipSize / 2)
                turnAround++;
        }
        if (keepGoing > turnAround)
            return 1;
        if (keepGoing < turnAround)
            return -1;
        else
            return 0;
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
    public static Position[] huntPattern(Position lastShot)
    {
        // In order: Up, Down, Left, Right //
        Position[] posArray = new Position[4];

        for (int i = 0; i < posArray.length; i++)
        {
            try
            {
                if (i == 0)
                    posArray[i] = LOIC.position2DArr[lastShot.x][lastShot.y + 1];
                if (i == 1)
                    posArray[i] = LOIC.position2DArr[lastShot.x][lastShot.y - 1];
                if (i == 2)
                    posArray[i] = LOIC.position2DArr[lastShot.x - 1][lastShot.y];
                if (i == 3)
                    posArray[i] = LOIC.position2DArr[lastShot.x + 1][lastShot.y];
            } catch (java.lang.ArrayIndexOutOfBoundsException ex)
            {
            }
        }
        return posArray;
    }

    public void resetParameter()
    {
        enemyShipList = enemyShipListTemplate;
        compareEnemyShips = startEnemyShips;
        currentEnemyShips = startEnemyShips;
        resetHeatMap();
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
                    heat = 2;
                else
                    heat = 1;
            }
            if (heat == 1)
                heat = 2;
            else
                heat = 1;
        }
        resetCounter++;

        if (resetCounter <= 2)
        {
            heatMap[3][6] = 3;
            heatMap[5][6] = 3;
            heatMap[4][5] = 3;
            heatMap[5][4] = 3;
            heatMap[6][5] = 3;
            heatMap[3][4] = 3;
            heatMap[4][3] = 3;
            heatMap[6][3] = 3;
        }
        if (resetCounter == 3)
            resetCounter = 0;
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
}
