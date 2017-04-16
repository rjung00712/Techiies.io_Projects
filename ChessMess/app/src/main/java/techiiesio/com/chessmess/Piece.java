package techiiesio.com.chessmess;

/**
 * Created by awing_000 on 4/10/2017.
 */

public class Piece
{
    protected char Color;	//Color of the piece ('B' or 'W')
    protected int xCoord;	//X coordinate of the piece
    protected int yCoord;	//Y coordinate of the piece
    protected boolean moved;	//Says whether or not the piece has been moved
    protected char type;    //Holds the type of the piece
    protected int enPassant;

    public Piece(char c, int x, int y)
    {
        Color = c;
        xCoord = x;
        yCoord = y;
        moved = false;
    }

    public void setColor(char c) {Color = c;}

    public void setCoords(int x, int y)
    {
        xCoord = x;
        yCoord = y;
    }

    public void setMoved(boolean m)	{moved = m;}

    public char getColor() {return Color;}

    public int getX() {return xCoord;}

    public int getY() {return yCoord;}

    public boolean hasMoved() {return moved;}

    public char getType() {return type;}

    public void setType(char t) {type = t;}

    //Returns false if the move does not cause your king to be in check
    public boolean causesCheck(int x, int y, Board b)
    {
        //Make a copy of the x- y- coords
        int tempX = xCoord;
        int tempY = yCoord;
        //Get the white King
        King whiteKing = b.getKing('W');
        //Get the black King
        King blackKing = b.getKing('B');
        Piece temp = b.getBoard()[y][x];
        b.getBoard()[y][x] = b.getBoard()[yCoord][xCoord];
        b.getBoard()[yCoord][xCoord] = null;
        xCoord = x;
        yCoord = y;
        if(this.Color == 'W')
        {
            if(!whiteKing.checkForCheck(whiteKing.xCoord, whiteKing.yCoord, b))
            {
                //Reset everything so that a move isn't actually made
                b.getBoard()[tempY][tempX] = b.getBoard()[y][x];
                b.getBoard()[y][x] = temp;
                xCoord = tempX;
                yCoord = tempY;
                return true;
            }
        }
        else
        {
            if(!blackKing.checkForCheck(blackKing.xCoord, blackKing.yCoord, b))
            {
                //Reset everything so that a move isn't actually made
                b.getBoard()[tempY][tempX] = b.getBoard()[y][x];
                b.getBoard()[y][x] = temp;
                xCoord = tempX;
                yCoord = tempY;
                return true;
            }
        }
        //Reset everything so that a move isn't actually made
        b.getBoard()[tempY][tempX] = b.getBoard()[y][x];
        b.getBoard()[y][x] = temp;
        xCoord = tempX;
        yCoord = tempY;
        return false;
    }

    public int getEnPassant() {return enPassant;}

    public void setEnPassant(int e) {enPassant = e;}
}