package techiiesio.com.chessmess;

/**
 * Created by awing_000 on 4/10/2017.
 */

public class Pawn extends Piece
{
    public Pawn(char c, int x, int y)
    {
        super(c, x, y);
        type = 'P';
        enPassant = 0;
    }

    public boolean isValid(int x, int y, Board b, boolean attacking)
    {
        Piece[][] board = b.getBoard();
        boolean[][] valid = new boolean[8][8];
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                valid[i][j] = false;

        //Pawns cannot go backwards so white moves up and black moves down
        if(this.Color == 'W')
        {
            //Checks one spots up
            if(board[yCoord - 1][xCoord] == null && !attacking)
            {
                valid[yCoord - 1][xCoord] = true;
                //Checks two spots up
                if(!moved && board[yCoord - 2][xCoord] == null)
                    valid[yCoord - 2][xCoord] = true;
            }
            //Checks spot left and up one
            if(xCoord - 1 >= 0 && board[yCoord - 1][xCoord - 1] != null
                    && (board[yCoord - 1][xCoord - 1].getColor() != this.Color || attacking))
                valid[yCoord - 1][xCoord - 1] = true;
            //Checks spot right and up one
            if(xCoord + 1 <= 7 && board[yCoord - 1][xCoord + 1] != null
                    && (board[yCoord - 1][xCoord + 1].getColor() != this.Color || attacking))
                valid[yCoord - 1][xCoord + 1] = true;
            if(xCoord - 1 >= 0 && board[yCoord][xCoord - 1] != null
                    && board[yCoord][xCoord - 1] instanceof Pawn
                    && board[yCoord][xCoord - 1].getColor() != this.Color)
            {
                Pawn p = (Pawn) board[yCoord][xCoord - 1];
                if(p.getEnPassant() > 0)
                    valid[yCoord - 1][xCoord - 1] = true;
            }
            if(xCoord + 1 <= 7 && board[yCoord][xCoord + 1] != null
                    && board[yCoord][xCoord + 1] instanceof Pawn
                    && board[yCoord][xCoord + 1].getColor() != this.Color)
            {
                Pawn p = (Pawn) board[yCoord][xCoord + 1];
                if(p.getEnPassant() > 0)
                    valid[yCoord - 1][xCoord + 1] = true;
            }
        }
        else
        {
            //Checks one spots down
            if(board[yCoord + 1][xCoord] == null && !attacking)
            {
                valid[yCoord + 1][xCoord] = true;
                //Checks two spots down
                if(!moved && board[yCoord + 2][xCoord] == null)
                    valid[yCoord + 2][xCoord] = true;
            }
            //Checks spot left and down one
            if(xCoord - 1 >= 0 && board[yCoord + 1][xCoord - 1] != null
                    && (board[yCoord + 1][xCoord - 1].getColor() != this.Color || attacking))
                valid[yCoord + 1][xCoord - 1] = true;
            //Checks spot right and down one
            if(xCoord + 1 <= 7 && board[yCoord + 1][xCoord + 1] != null
                    && (board[yCoord + 1][xCoord + 1].getColor() != this.Color || attacking))
                valid[yCoord + 1][xCoord + 1] = true;
            if(xCoord - 1 >= 0 && board[yCoord][xCoord - 1] != null
                    && board[yCoord][xCoord - 1] instanceof Pawn
                    && board[yCoord][xCoord - 1].getColor() != this.Color)
            {
                Pawn p = (Pawn) board[yCoord][xCoord - 1];
                if(p.getEnPassant() > 0)
                    valid[yCoord + 1][xCoord - 1] = true;
            }
            if(xCoord + 1 <= 7 && board[yCoord][xCoord + 1] != null
                    && board[yCoord][xCoord + 1] instanceof Pawn
                    && board[yCoord][xCoord + 1].getColor() != this.Color)
            {
                Pawn p = (Pawn) board[yCoord][xCoord + 1];
                if(p.getEnPassant() > 0)
                    valid[yCoord + 1][xCoord + 1] = true;
            }
        }

        if(valid[y][x] && !attacking)
            if(causesCheck(x, y, b))
                valid[y][x] = false;

        return valid[y][x];
    }

    public boolean hasValid(Board b)
    {
        Piece[][] board = b.getBoard();
        boolean[][] valid = new boolean[8][8];
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                valid[i][j] = false;

        //Pawns cannot go backwards so white moves up and black moves down
        if(this.Color == 'W')
        {
            //Checks one spots up
            if(board[yCoord - 1][xCoord] == null)
            {
                valid[yCoord - 1][xCoord] = true;
                //Checks two spots up
                if(!moved && board[yCoord - 2][xCoord] == null)
                    valid[yCoord - 2][xCoord] = true;
            }
            //Checks spot left and up one
            if(xCoord - 1 >= 0 && board[yCoord - 1][xCoord - 1] != null
                    && board[yCoord - 1][xCoord - 1].getColor() != this.Color)
                valid[yCoord - 1][xCoord - 1] = true;
            //Checks spot right and up one
            if(xCoord + 1 <= 7 && board[yCoord - 1][xCoord + 1] != null
                    && board[yCoord - 1][xCoord + 1].getColor() != this.Color)
                valid[yCoord - 1][xCoord + 1] = true;
            if(xCoord - 1 >= 0 && board[yCoord][xCoord - 1] != null
                    && board[yCoord][xCoord - 1] instanceof Pawn
                    && board[yCoord][xCoord - 1].getColor() != this.Color)
            {
                Pawn p = (Pawn) board[yCoord][xCoord - 1];
                if(p.getEnPassant() > 0)
                    valid[yCoord - 1][xCoord - 1] = true;
            }
            if(xCoord + 1 <= 7 && board[yCoord][xCoord + 1] != null
                    && board[yCoord][xCoord + 1] instanceof Pawn
                    && board[yCoord][xCoord + 1].getColor() != this.Color)
            {
                Pawn p = (Pawn) board[yCoord][xCoord + 1];
                if(p.getEnPassant() > 0)
                    valid[yCoord - 1][xCoord + 1] = true;
            }
        }
        else
        {
            //Checks one spots down
            if(board[yCoord + 1][xCoord] == null)
            {
                valid[yCoord + 1][xCoord] = true;
                //Checks two spots down
                if(!moved && board[yCoord + 2][xCoord] == null)
                    valid[yCoord + 2][xCoord] = true;
            }
            //Checks spot left and down one
            if(xCoord - 1 >= 0 && board[yCoord + 1][xCoord - 1] != null
                    && board[yCoord + 1][xCoord - 1].getColor() != this.Color)
                valid[yCoord + 1][xCoord - 1] = true;
            //Checks spot right and down one
            if(xCoord + 1 <= 7 && board[yCoord + 1][xCoord + 1] != null
                    && board[yCoord + 1][xCoord + 1].getColor() != this.Color)
                valid[yCoord + 1][xCoord + 1] = true;
            if(xCoord - 1 >= 0 && board[yCoord][xCoord - 1] != null
                    && board[yCoord][xCoord - 1] instanceof Pawn
                    && board[yCoord][xCoord - 1].getColor() != this.Color)
            {
                Pawn p = (Pawn) board[yCoord][xCoord - 1];
                if(p.getEnPassant() > 0)
                    valid[yCoord + 1][xCoord - 1] = true;
            }
            if(xCoord + 1 <= 7 && board[yCoord][xCoord + 1] != null
                    && board[yCoord][xCoord + 1] instanceof Pawn
                    && board[yCoord][xCoord + 1].getColor() != this.Color)
            {
                Pawn p = (Pawn) board[yCoord][xCoord + 1];
                if(p.getEnPassant() > 0)
                    valid[yCoord + 1][xCoord + 1] = true;
            }
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