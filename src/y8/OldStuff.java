package y8;

import battleship.interfaces.Board;
import battleship.interfaces.Fleet;
import battleship.interfaces.Position;
import battleship.interfaces.Ship;

public class OldStuff {

//    private void oldPlaceShips(Fleet fleet, Board board)
//    {
//        for (int i = fleet.getNumberOfShips() - 1; i >= 0; i--)
//        {
//            Ship ship = fleet.getShip(i);
//            boolean vertical = rnd.nextBoolean();
//            Position pos = pickPosition(vertical, ship);
//
//            while (checkValidPlacement(pos, vertical, ship) == false)
//            {
//                vertical = rnd.nextBoolean();
//                pos = pickPosition(vertical, ship);
//            }
//            board.placeShip(pos, ship, vertical);
//            mapShips(pos, vertical, ship.size());
//        }
//    }
//
//    private Position pickPosition(boolean vertical, Ship ship)
//    {
//        if (vertical)
//        {
//            int x = rnd.nextInt(sizeX);
//            int y = rnd.nextInt(sizeY - (ship.size() - 1));
//            return new Position(x, y);
//        } else
//        {
//            int x = rnd.nextInt(sizeX - (ship.size() - 1));
//            int y = rnd.nextInt(sizeY);
//            return new Position(x, y);
//        }
//    }
//
//    private boolean checkValidPlacement(Position pos, boolean vertical, Ship ship)
//    {
//        if (vertical)
//        {
//            for (int i = pos.y; i < ship.size() + pos.y; i++)
//            {
//                if (shipPositions[pos.x][i])
//                {
//                    return false;
//                }
//            }
//        } else
//        {
//            for (int i = pos.x; i < ship.size() + pos.x; i++)
//            {
//                if (shipPositions[i][pos.y])
//                {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
}
