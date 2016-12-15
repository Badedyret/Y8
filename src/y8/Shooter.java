package y8;

import battleship.interfaces.Position;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Shooter {

    public int[][] heatMap;
    public int startEnemyShips;
    private int compareEnemyShips;
    public int currentEnemyShips;
    private Position lastShot = new Position(0, 0);
    private Position holdShot;
    private Position[] holdPattern;
    private ArrayList<Position> targetList = new ArrayList();
    private HashMap<Integer, Boolean> enemyShipMap = new HashMap();
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
                applyHeat(increaseHeat, 6);
            } else if (incrementY != 0)
            {
                Position[] increaseHeat =
                {
                    new Position(lastShot.x, lastShot.y + incrementY)
                };
                applyHeat(increaseHeat, 6);
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

    public void parameterReset()
    {
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
}
