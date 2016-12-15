package y8;

import battleship.interfaces.Board;
import battleship.interfaces.Fleet;
import battleship.interfaces.Position;
import battleship.interfaces.Ship;
import java.util.ArrayList;
import java.util.Collections;

public class Placer {

    private boolean[][] shipPositions;

    public void staticShipPlace(Board board, Fleet fleet)
    {
        board.placeShip(new Position(8, 9), fleet.getShip(0), false);
        board.placeShip(new Position(0, 7), fleet.getShip(1), true);
        board.placeShip(new Position(6, 1), fleet.getShip(2), false);
        board.placeShip(new Position(3, 6), fleet.getShip(3), false);
        board.placeShip(new Position(0, 1), fleet.getShip(4), true);
    }

    public void dynamicShipPlace(Fleet fleet, Board board)
    {
        ArrayList<Position> possiblePlacement = new ArrayList();
        for (int i = 0; i < fleet.getNumberOfShips(); i++)
        {
            possiblePlacement.clear();
            Ship ship = fleet.getShip(i);
            boolean vertical = LOIC.rnd.nextBoolean();

            if (vertical)
                possiblePlacement = shipPlacementY(ship.size());
            else
                possiblePlacement = shipPlacementX(ship.size());

            Collections.shuffle(possiblePlacement);
            board.placeShip(possiblePlacement.get(0), ship, vertical);
            mapShips(possiblePlacement.get(0), vertical, ship.size());
        }
    }

    private void mapShips(Position pos, boolean vertical, int shipSize)
    {
        ArrayList<Position> mapPattern = new ArrayList();
        for (Position holdAdjacent : Shooter.huntPattern(pos))
        {
            if (holdAdjacent != null)
                mapPattern.add(holdAdjacent);
        }
        mapPattern.add(pos);
        if (vertical)
        {
            for (Position holdPos : mapPattern)
            {
                for (int i = holdPos.y; i < shipSize + holdPos.y; i++)
                {
                    if (i > LOIC.sizeY - 1)
                        break;
                    shipPositions[holdPos.x][i] = true;
                }
            }
        } else
        {
            for (Position holdPos : mapPattern)
            {
                for (int i = holdPos.x; i < shipSize + holdPos.x; i++)
                {
                    if (i > LOIC.sizeX - 1)
                        break;
                    shipPositions[i][holdPos.y] = true;
                }
            }
        }
    }

    private ArrayList<Position> shipPlacementX(int shipSize)
    {
        int counter;
        Position holdPos;
        ArrayList<Position> possiblePlacement = new ArrayList();

        for (int y = 0; y < LOIC.sizeY; y++)
        {
            counter = 0;
            holdPos = null;
            for (int x = 0; x < LOIC.sizeX; x++)
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

                if (counter >= shipSize)
                {
                    possiblePlacement.add(new Position(holdPos.x + (counter - shipSize), holdPos.y));
                }
            }
        }
        return possiblePlacement;
    }

    private ArrayList<Position> shipPlacementY(int shipSize)
    {
        int counter;
        Position holdPos;
        ArrayList<Position> possiblePlacement = new ArrayList();

        for (int x = 0; x < LOIC.sizeY; x++)
        {
            counter = 0;
            holdPos = null;
            for (int y = 0; y < LOIC.sizeX; y++)
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

                if (counter >= shipSize)
                {
                    possiblePlacement.add(new Position(holdPos.x, holdPos.y + (counter - shipSize)));
                }
            }
        }
        return possiblePlacement;
    }

    public void resetParameter()
    {
        shipPositions = new boolean[LOIC.sizeX][LOIC.sizeY];
    }
}
