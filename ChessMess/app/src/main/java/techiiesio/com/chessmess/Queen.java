package techiiesio.com.chessmess;

/**
 * Created by awing_000 on 4/10/2017.
 */

public class Queen extends Piece
{
    public Queen(char c, int x, int y)
    {
        super(c, x, y);
        type = 'Q';
    }

    public boolean isValid(int x, int y, Board b, boolean attacking)
    {
        Piece[][] board = b.getBoard();
        boolean[][] valid = new boolean[8][8];
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                valid[i][j] = false;

        //Checks all valid spots in the up and left diagonal
        int tempX = xCoord - 1;
        int tempY = yCoord - 1;
        while(tempX >= 0 && tempY >= 0 && board[tempY][tempX] == null)
        {
            valid[tempY][tempX] = true;
            tempX--;
            tempY--;
        }
        if(tempX >= 0 && tempY >= 0)
            if(board[tempY][tempX].getColor() != this.Color || attacking)
                valid[tempY][tempX] = true;

        //Checks all valid spots in the up and right diagonal
        tempX = xCoord + 1;
        tempY = yCoord - 1;
        while(tempX <= 7 && tempY >= 0 && board[tempY][tempX] == null)
        {
            valid[tempY][tempX] = true;
            tempX++;
            tempY--;
        }
        if(tempX <= 7 && tempY >= 0)
            if(board[tempY][tempX].getColor() != this.Color || attacking)
                valid[tempY][tempX] = true;

        //Checks all valid spots in the down and left diagonal
        tempX = xCoord - 1;
        tempY = yCoord + 1;
        while(tempX >= 0 && tempY <= 7 && board[tempY][tempX] == null)
        {
            valid[tempY][tempX] = true;
            tempX--;
            tempY++;
        }
        if(tempX >= 0 && tempY <= 7)
            if(board[tempY][tempX].getColor() != this.Color || attacking)
                valid[tempY][tempX] = true;

        //Checks all valid spots in the down and right diagonal
        tempX = xCoord + 1;
        tempY = yCoord + 1;
        while(tempX <= 7 && tempY <= 7 && board[tempY][tempX] == null)
        {
            valid[tempY][tempX] = true;
            tempX++;
            tempY++;
        }
        if(tempX <= 7 && tempY <= 7)
            if(board[tempY][tempX].getColor() != this.Color || attacking)
                valid[tempY][tempX] = true;

        //Checks all valid spots to the left
        tempX = xCoord - 1;
        while(tempX >= 0 && board[yCoord][tempX] == null)
        {
            valid[yCoord][tempX] = true;
            tempX--;
        }
        if(tempX >= 0)
            if(board[yCoord][tempX].getColor() != this.Color || attacking)
                valid[yCoord][tempX] = true;

        //Checks all valid spots to the right
        tempX = xCoord + 1;
        while(tempX <= 7 && board[yCoord][tempX] == null)
        {
            valid[yCoord][tempX] = true;
            tempX++;
        }
        if(tempX <= 7)
            if(board[yCoord][tempX].getColor() != this.Color || attacking)
                valid[yCoord][tempX] = true;

        //Checks all valid spots above
        tempY = yCoord - 1;
        while(tempY >= 0 && board[tempY][xCoord] == null)
        {
            valid[tempY][xCoord] = true;
            tempY--;
        }
        if(tempY >= 0)
            if(board[tempY][xCoord].getColor() != this.Color || attacking)
                valid[tempY][xCoord] = true;

        //Checks all valid spots below
        tempY = yCoord + 1;
        while(tempY <= 7 && board[tempY][xCoord] == null)
        {
            valid[tempY][xCoord] = true;
            tempY++;
        }
        if(tempY <= 7)
            if(board[tempY][xCoord].getColor() != this.Color || attacking)
                valid[tempY][xCoord] = true;

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

        //Checks all valid spots in the up and left diagonal
        int tempX = xCoord - 1;
        int tempY = yCoord - 1;
        while(tempX >= 0 && tempY >= 0 && board[tempY][tempX] == null)
        {
            valid[tempY][tempX] = true;
            tempX--;
            tempY--;
        }
        if(tempX >= 0 && tempY >= 0)
            if(board[tempY][tempX].getColor() != this.Color)
                valid[tempY][tempX] = true;

        //Checks all valid spots in the up and right diagonal
        tempX = xCoord + 1;
        tempY = yCoord - 1;
        while(tempX <= 7 && tempY >= 0 && board[tempY][tempX] == null)
        {
            valid[tempY][tempX] = true;
            tempX++;
            tempY--;
        }
        if(tempX <= 7 && tempY >= 0)
            if(board[tempY][tempX].getColor() != this.Color)
                valid[tempY][tempX] = true;

        //Checks all valid spots in the down and left diagonal
        tempX = xCoord - 1;
        tempY = yCoord + 1;
        while(tempX >= 0 && tempY <= 7 && board[tempY][tempX] == null)
        {
            valid[tempY][tempX] = true;
            tempX--;
            tempY++;
        }
        if(tempX >= 0 && tempY <= 7)
            if(board[tempY][tempX].getColor() != this.Color)
                valid[tempY][tempX] = true;

        //Checks all valid spots in the down and right diagonal
        tempX = xCoord + 1;
        tempY = yCoord + 1;
        while(tempX <= 7 && tempY <= 7 && board[tempY][tempX] == null)
        {
            valid[tempY][tempX] = true;
            tempX++;
            tempY++;
        }
        if(tempX <= 7 && tempY <= 7)
            if(board[tempY][tempX].getColor() != this.Color)
                valid[tempY][tempX] = true;

        //Checks all valid spots to the left
        tempX = xCoord - 1;
        while(tempX >= 0 && board[yCoord][tempX] == null)
        {
            valid[yCoord][tempX] = true;
            tempX--;
        }
        if(tempX >= 0)
            if(board[yCoord][tempX].getColor() != this.Color)
                valid[yCoord][tempX] = true;

        //Checks all valid spots to the right
        tempX = xCoord + 1;
        while(tempX <= 7 && board[yCoord][tempX] == null)
        {
            valid[yCoord][tempX] = true;
            tempX++;
        }
        if(tempX <= 7)
            if(board[yCoord][tempX].getColor() != this.Color)
                valid[yCoord][tempX] = true;

        //Checks all valid spots above
        tempY = yCoord - 1;
        while(tempY >= 0 && board[tempY][xCoord] == null)
        {
            valid[tempY][xCoord] = true;
            tempY--;
        }
        if(tempY >= 0)
            if(board[tempY][xCoord].getColor() != this.Color)
                valid[tempY][xCoord] = true;

        //Checks all valid spots below
        tempY = yCoord + 1;
        while(tempY <= 7 && board[tempY][xCoord] == null)
        {
            valid[tempY][xCoord] = true;
            tempY++;
        }
        if(tempY <= 7)
            if(board[tempY][xCoord].getColor() != this.Color)
                valid[tempY][xCoord] = true;

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