package techiiesio.com.chessmess;

/**
 * Created by awing_000 on 4/10/2017.
 */

public class King extends Piece {
    public King(char c, int x, int y) {
        super(c, x, y);
        type = 'K';
    }

    public boolean isValid(int x, int y, Board b) {
        Piece[][] board = b.getBoard();
        boolean[][] valid = new boolean[8][8];
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                valid[i][j] = false;

        if(x < 0 || x > 7 || y < 0 || y > 7)
            return false;

        if(!moved) {
            if(canCastleShort(b))
                valid[yCoord][xCoord + 2] = true;
            if(canCastleLong(b))
                valid[yCoord][xCoord - 2] = true;
        }

        if(yCoord - 1 >= 0) {
            //Check spot up one and left one
            if(xCoord - 1 >= 0)
                if(board[yCoord - 1][xCoord - 1] == null || board[yCoord - 1][xCoord - 1].getColor() != this.Color)
                    valid[yCoord - 1][xCoord - 1] = checkForCheck(xCoord - 1, yCoord - 1, b);
            //Check spot up one
            if(board[yCoord - 1][xCoord] == null || board[yCoord - 1][xCoord].getColor() != this.Color)
                valid[yCoord - 1][xCoord] = checkForCheck(xCoord, yCoord - 1, b);
            //Check spot up one and right one
            if(xCoord + 1 <= 7)
                if(board[yCoord - 1][xCoord + 1] == null || board[yCoord - 1][xCoord + 1].getColor() != this.Color)
                    valid[yCoord - 1][xCoord + 1] = checkForCheck(xCoord + 1, yCoord - 1, b);
        }

        //Check spot left one
        if(xCoord - 1 >= 0)
            if(board[yCoord][xCoord - 1] == null || board[yCoord][xCoord - 1].getColor() != this.Color)
                valid[yCoord][xCoord - 1] = checkForCheck(xCoord - 1, yCoord, b);

        valid[yCoord][xCoord] = checkForCheck(xCoord, yCoord, b);

        //Check spot right one
        if(xCoord + 1 <= 7)
            if(board[yCoord][xCoord + 1] == null || board[yCoord][xCoord + 1].getColor() != this.Color)
                valid[yCoord][xCoord + 1] = checkForCheck(xCoord + 1, yCoord, b);

        if(yCoord + 1 <= 7) {
            //Check spot up one and left one
            if(xCoord - 1 >= 0)
                if(board[yCoord + 1][xCoord - 1] == null || board[yCoord + 1][xCoord - 1].getColor() != this.Color)
                    valid[yCoord + 1][xCoord - 1] = checkForCheck(xCoord - 1, yCoord + 1, b);
            //Check spot up one
            if(board[yCoord + 1][xCoord] == null || board[yCoord + 1][xCoord].getColor() != this.Color)
                valid[yCoord + 1][xCoord] = checkForCheck(xCoord, yCoord + 1, b);
            //Check spot up one and right one
            if(xCoord + 1 <= 7)
                if(board[yCoord + 1][xCoord + 1] == null || board[yCoord + 1][xCoord + 1].getColor() != this.Color)
                    valid[yCoord + 1][xCoord + 1] = checkForCheck(xCoord + 1, yCoord + 1, b);
        }

        return valid[y][x];
    }

    public boolean checkForCheck(int x, int y, Board b)
    {
        Piece[][] board = b.getBoard();
        //Check every spot on the board for a piece that puts the King in check at the given location
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                //Do nothing if the spot is empty
                if(board[i][j] != null)
                {
                    //Do nothing if the piece is the same color
                    if(board[i][j].getColor() != this.Color)
                    {
                        //Check if the Pawn is able to move to the given location
                        if(board[i][j] instanceof Pawn)
                        {
                            Pawn p = (Pawn) board[i][j];
                            if(p.isValid(x, y, b, true))
                                return false;
                        }
                        //Check if the Rook is able to move to the given location
                        else if(board[i][j] instanceof Rook)
                        {
                            Rook r = (Rook) board[i][j];
                            if(r.isValid(x, y, b, true))
                                return false;
                        }
                        //Check if the Knight is able to move to the given location
                        else if(board[i][j] instanceof Knight)
                        {
                            Knight kn = (Knight) board[i][j];
                            if(kn.isValid(x, y, b, true))
                                return false;
                        }
                        //Check if the Bishop is able to move to the given location
                        else if(board[i][j] instanceof Bishop)
                        {
                            Bishop bishop = (Bishop) board[i][j];
                            if(bishop.isValid(x, y, b, true))
                                return false;
                        }
                        //Check if the Queen is able to move to the given location
                        else if(board[i][j] instanceof Queen)
                        {
                            Queen q = (Queen) board[i][j];
                            if(q.isValid(x, y, b, true))
                                return false;
                        }
                        //Check if the King is able to move to the given location
                        //Cannot use the isValid method of the King or it may cause an infinite loop
                        else
                        {
                            King k = (King) board[i][j];
                            //Check all positions one away from the given location and the given location itself
                            if(k.getY() - 1 == y)
                            {
                                if(k.getX() - 1 == x)
                                    return false;
                                if(k.getX() == x)
                                    return false;
                                if(k.getX() + 1 == x)
                                    return false;
                            }

                            if(k.getY() == y)
                            {
                                if(k.getX() - 1 == x)
                                    return false;
                                if(k.getX() == x)
                                    return false;
                                if(k.getX() + 1 == x)
                                    return false;
                            }

                            if(k.getY() + 1 == y)
                            {
                                if(k.getX() - 1 == x)
                                    return false;
                                if(k.getX() == x)
                                    return false;
                                if(k.getX() + 1 == x)
                                    return false;
                            }
                        }
                    }
                }
            }
        return true;	//No problems so return true
    }

    public boolean canCastleShort(Board b)
    {
        Piece[][] board = b.getBoard();
        if(board[yCoord][7] == null || board[yCoord][7].hasMoved())
            return false;
        if(board[yCoord][5] != null || board[yCoord][6] != null)
            return false;
        if(!checkForCheck(6, yCoord, b) || !checkForCheck(5, yCoord, b) || !checkForCheck(4, yCoord, b))
            return false;
        return true;
    }

    public boolean canCastleLong(Board b)
    {
        Piece[][] board = b.getBoard();
        if(board[yCoord][0] == null || board[yCoord][0].hasMoved())
            return false;
        if(board[yCoord][3] != null || board[yCoord][2] != null || board[yCoord][1] != null)
            return false;
        if(!checkForCheck(2, yCoord, b) || !checkForCheck(3, yCoord, b) || !checkForCheck(4, yCoord, b))
            return false;
        return true;
    }

    public boolean hasValid(Board b)
    {
        Piece[][] board = b.getBoard();
        boolean[][] valid = new boolean[8][8];
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                valid[i][j] = false;

        if(!moved)
        {
            if(canCastleShort(b))
                valid[yCoord][xCoord + 2] = true;
            if(canCastleLong(b))
                valid[yCoord][xCoord - 2] = true;
        }

        if(yCoord - 1 >= 0)
        {
            //Check spot up one and left one
            if(xCoord - 1 >= 0)
                if(board[yCoord - 1][xCoord - 1] == null || board[yCoord - 1][xCoord - 1].getColor() != this.Color)
                    valid[yCoord - 1][xCoord - 1] = checkForCheck(xCoord - 1, yCoord - 1, b);
            //Check spot up one
            if(board[yCoord - 1][xCoord] == null || board[yCoord - 1][xCoord].getColor() != this.Color)
                valid[yCoord - 1][xCoord] = checkForCheck(xCoord, yCoord - 1, b);
            //Check spot up one and right one
            if(xCoord + 1 <= 7)
                if(board[yCoord - 1][xCoord + 1] == null || board[yCoord - 1][xCoord + 1].getColor() != this.Color)
                    valid[yCoord - 1][xCoord + 1] = checkForCheck(xCoord + 1, yCoord - 1, b);
        }

        //Check spot left one
        if(xCoord - 1 >= 0)
            if(board[yCoord][xCoord - 1] == null || board[yCoord][xCoord - 1].getColor() != this.Color)
                valid[yCoord][xCoord - 1] = checkForCheck(xCoord - 1, yCoord, b);

        valid[yCoord][xCoord] = checkForCheck(xCoord, yCoord, b);

        //Check spot right one
        if(xCoord + 1 <= 7)
            if(board[yCoord][xCoord + 1] == null || board[yCoord][xCoord + 1].getColor() != this.Color)
                valid[yCoord][xCoord + 1] = checkForCheck(xCoord + 1, yCoord, b);

        if(yCoord + 1 <= 7)
        {
            //Check spot up one and left one
            if(xCoord - 1 >= 0)
                if(board[yCoord + 1][xCoord - 1] == null || board[yCoord + 1][xCoord - 1].getColor() != this.Color)
                    valid[yCoord + 1][xCoord - 1] = checkForCheck(xCoord - 1, yCoord + 1, b);
            //Check spot up one
            if(board[yCoord + 1][xCoord] == null || board[yCoord + 1][xCoord].getColor() != this.Color)
                valid[yCoord + 1][xCoord] = checkForCheck(xCoord, yCoord + 1, b);
            //Check spot up one and right one
            if(xCoord + 1 <= 7)
                if(board[yCoord + 1][xCoord + 1] == null || board[yCoord + 1][xCoord + 1].getColor() != this.Color)
                    valid[yCoord + 1][xCoord + 1] = checkForCheck(xCoord + 1, yCoord + 1, b);
        }

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                if(valid[j][i])
                    valid[j][i] = !causesCheck(i, j, b);
                if(valid[j][i])
                    return true;
            }
        return false;
    }
}