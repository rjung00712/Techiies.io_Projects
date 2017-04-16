package techiiesio.com.chessmess;

/**
 * Created by awing_000 on 4/10/2017.
 */

public class Knight extends Piece
{
    public Knight(char c, int x, int y)
    {
        super(c, x, y);
        type = 'N';
    }

    public boolean isValid(int x, int y, Board b, boolean attacking)
    {
        Piece[][] board = b.getBoard();
        boolean[][] valid = new boolean[8][8];
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                valid[i][j] = false;

        //Checks spot up two and left one
        if(xCoord - 1 >= 0 && yCoord - 2 >= 0)
        {
            if(board[yCoord - 2][xCoord - 1] == null
                    || board[yCoord - 2][xCoord - 1].getColor() != this.Color || attacking)
                valid[yCoord - 2][xCoord - 1] = true;
        }

        //Checks spot up two and right one
        if(xCoord + 1 <= 7 && yCoord - 2 >= 0)
        {
            if(board[yCoord - 2][xCoord + 1] == null
                    || board[yCoord - 2][xCoord + 1].getColor() != this.Color || attacking)
                valid[yCoord - 2][xCoord + 1] = true;
        }

        //Checks spot down two and left one
        if(xCoord - 1 >= 0 && yCoord + 2 <= 7)
        {
            if(board[yCoord + 2][xCoord - 1] == null
                    || board[yCoord + 2][xCoord - 1].getColor() != this.Color || attacking)
                valid[yCoord + 2][xCoord - 1] = true;
        }

        //Checks spot down two and right one
        if(xCoord + 1 <= 7 && yCoord + 2 <= 7)
        {
            if(board[yCoord + 2][xCoord + 1] == null
                    || board[yCoord + 2][xCoord + 1].getColor() != this.Color || attacking)
                valid[yCoord + 2][xCoord + 1] = true;
        }

        //Checks spot up one and left two
        if(xCoord - 2 >= 0 && yCoord - 1 >= 0)
        {
            if(board[yCoord - 1][xCoord - 2] == null
                    || board[yCoord - 1][xCoord - 2].getColor() != this.Color || attacking)
                valid[yCoord - 1][xCoord - 2] = true;
        }

        //Checks spot up one and right two
        if(xCoord + 2 <= 7 && yCoord - 1 >= 0)
        {
            if(board[yCoord - 1][xCoord + 2] == null
                    || board[yCoord - 1][xCoord + 2].getColor() != this.Color || attacking)
                valid[yCoord - 1][xCoord + 2] = true;
        }

        //Checks spot down one and left two
        if(xCoord - 2 >= 0 && yCoord + 1 <= 7)
        {
            if(board[yCoord + 1][xCoord - 2] == null
                    || board[yCoord + 1][xCoord - 2].getColor() != this.Color || attacking)
                valid[yCoord + 1][xCoord - 2] = true;
        }

        //Checks spot down one and right two
        if(xCoord + 2 <= 7 && yCoord + 1 <= 7)
        {
            if(board[yCoord + 1][xCoord + 2] == null
                    || board[yCoord + 1][xCoord + 2].getColor() != this.Color || attacking)
                valid[yCoord + 1][xCoord + 2] = true;
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

        //Checks spot up two and left one
        if(xCoord - 1 >= 0 && yCoord - 2 >= 0)
        {
            if(board[yCoord - 2][xCoord - 1] == null
                    || board[yCoord - 2][xCoord - 1].getColor() != this.Color)
                valid[yCoord - 2][xCoord - 1] = true;
        }

        //Checks spot up two and right one
        if(xCoord + 1 <= 7 && yCoord - 2 >= 0)
        {
            if(board[yCoord - 2][xCoord + 1] == null
                    || board[yCoord - 2][xCoord + 1].getColor() != this.Color)
                valid[yCoord - 2][xCoord + 1] = true;
        }

        //Checks spot down two and left one
        if(xCoord - 1 >= 0 && yCoord + 2 <= 7)
        {
            if(board[yCoord + 2][xCoord - 1] == null
                    || board[yCoord + 2][xCoord - 1].getColor() != this.Color)
                valid[yCoord + 2][xCoord - 1] = true;
        }

        //Checks spot down two and right one
        if(xCoord + 1 <= 7 && yCoord + 2 <= 7)
        {
            if(board[yCoord + 2][xCoord + 1] == null
                    || board[yCoord + 2][xCoord + 1].getColor() != this.Color)
                valid[yCoord + 2][xCoord + 1] = true;
        }

        //Checks spot up one and left two
        if(xCoord - 2 >= 0 && yCoord - 1 >= 0)
        {
            if(board[yCoord - 1][xCoord - 2] == null
                    || board[yCoord - 1][xCoord - 2].getColor() != this.Color)
                valid[yCoord - 1][xCoord - 2] = true;
        }

        //Checks spot up one and right two
        if(xCoord + 2 <= 7 && yCoord - 1 >= 0)
        {
            if(board[yCoord - 1][xCoord + 2] == null
                    || board[yCoord - 1][xCoord + 2].getColor() != this.Color)
                valid[yCoord - 1][xCoord + 2] = true;
        }

        //Checks spot down one and left two
        if(xCoord - 2 >= 0 && yCoord + 1 <= 7)
        {
            if(board[yCoord + 1][xCoord - 2] == null
                    || board[yCoord + 1][xCoord - 2].getColor() != this.Color)
                valid[yCoord + 1][xCoord - 2] = true;
        }

        //Checks spot down one and right two
        if(xCoord + 2 <= 7 && yCoord + 1 <= 7)
        {
            if(board[yCoord + 1][xCoord + 2] == null
                    || board[yCoord + 1][xCoord + 2].getColor() != this.Color)
                valid[yCoord + 1][xCoord + 2] = true;
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