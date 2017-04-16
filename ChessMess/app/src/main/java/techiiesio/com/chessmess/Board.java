package techiiesio.com.chessmess;

/**
 * Created by awing_000 on 4/10/2017.
 */

public class Board
{
    private Piece[][] board;
    private King whiteKing;
    private King blackKing;
    private char turn;
    private boolean check;
    private boolean checkmate;

    public Board()
    {
        board = new Piece[8][8];
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                board[i][i] = null;
        whiteKing = new King('W', 4, 7);
        blackKing = new King('B', 4, 0);
        board[7][4] = whiteKing;
        board[0][4] = blackKing;
        turn = 'W';
        initBoard('W', 6, 7);
        initBoard('B', 1, 0);
        check = false;
        checkmate = false;
    }

    //Initializes the board with the correct pieces in the correct spots
    private void initBoard(char c, int pawnY, int restY)
    {
        for(int i = 0; i < 8; i++)
            board[pawnY][i] = new Pawn(c, i, pawnY);
        board[restY][0] = new Rook(c, 0, restY);
        board[restY][7] = new Rook(c, 7, restY);
        board[restY][1] = new Knight(c, 1, restY);
        board[restY][6] = new Knight(c, 6, restY);
        board[restY][2] = new Bishop(c, 2, restY);
        board[restY][5] = new Bishop(c, 5, restY);
        board[restY][3] = new Queen(c, 3, restY);
    }


    public boolean makeMove(int startX, int startY, int endX, int endY)
    {
        boolean validMove = true;
        check = false;
        checkmate = true;
        Piece piece = board[startY][startX];

        if(piece == null || piece.getColor() != turn)
            validMove = false;
        if(validMove)
        {
            if(piece instanceof Pawn)
            {
                Pawn p = (Pawn) piece;
                if(p.isValid(endX, endY, this, false))
                {
                    p.setCoords(endX, endY);
                    p.setMoved(true);
                    if(endY == startY + 2 || endY == startY - 2)
                        p.setEnPassant(2);
                    board[endY][endX] = p;
                    board[startY][startX] = null;
                    if(p.getColor() == 'W')
                    {
                        if(board[endY + 1][endX] != null && board[endY + 1][endX].getColor() != p.getColor())
                            board[endY + 1][endX] = null;
//                        if(endY == 0)
//                        {
//                            int choice = 0;		//This will be changed, it was used for testing
//                            switch(choice)
//                            {
//                                case 0:
//                                    Queen q = new Queen('W', p.getX(), p.getY());
//                                    q.setMoved(true);
//                                    board[endY][endX] = q;
//                                    if(q.isValid(blackKing.getX(), blackKing.getY(), this, true))
//                                        check = true;
//                                    break;
//                                case 1:
//                                    Knight kn = new Knight('W', p.getX(), p.getY());
//                                    kn.setMoved(true);
//                                    board[endY][endX] = kn;
//                                    if(kn.isValid(blackKing.getX(), blackKing.getY(), this, true))
//                                        check = true;
//                                    break;
//                                case 2:
//                                    Bishop b = new Bishop('W', p.getX(), p.getY());
//                                    b.setMoved(true);
//                                    board[endY][endX] = b;
//                                    if(b.isValid(blackKing.getX(), blackKing.getY(), this, true))
//                                        check = true;
//                                    break;
//                                case 3:
//                                    Rook r = new Rook('W', p.getX(), p.getY());
//                                    r.setMoved(true);
//                                    board[endY][endX] = r;
//                                    if(r.isValid(blackKing.getX(), blackKing.getY(), this, true))
//                                        check = true;
//                                    break;
//                            }
//                        }
//                        else
//                        {
//                            if(p.isValid(blackKing.getX(), blackKing.getY(), this, true))
//                                check = true;
//                        }
                    }
                    else
                    {
                        if(board[endY - 1][endX] != null && board[endY - 1][endX].getColor() != p.getColor())
                            board[endY - 1][endX] = null;
//                        if(p.getY() == 7)
//                        {
//                            int choice = 0;
//                            switch(choice)
//                            {
//                                case 0:
//                                    Queen q = new Queen('B', p.getX(), p.getY());
//                                    q.setMoved(true);
//                                    board[endY][endX] = q;
//                                    if(q.isValid(whiteKing.getX(), whiteKing.getY(), this, true))
//                                        check = true;
//                                    break;
//                                case 1:
//                                    Knight kn = new Knight('B', p.getX(), p.getY());
//                                    kn.setMoved(true);
//                                    board[endY][endX] = kn;
//                                    if(kn.isValid(whiteKing.getX(), whiteKing.getY(), this, true))
//                                        check = true;
//                                    break;
//                                case 2:
//                                    Bishop b = new Bishop('B', p.getX(), p.getY());
//                                    b.setMoved(true);
//                                    board[endY][endX] = b;
//                                    if(b.isValid(whiteKing.getX(), whiteKing.getY(), this, true))
//                                        check = true;
//                                    break;
//                                case 3:
//                                    Rook r = new Rook('B', p.getX(), p.getY());
//                                    r.setMoved(true);
//                                    board[endY][endX] = r;
//                                    if(r.isValid(whiteKing.getX(), whiteKing.getY(), this, true))
//                                        check = true;
//                                    break;
//                            }
//                        }
//                        else
//                        {
//                            if(p.isValid(whiteKing.getX(), whiteKing.getY(), this, true))
//                                check = true;
//                        }
                    }
                }
                else
                    validMove = false;
            }
            if(piece instanceof Rook)
            {
                Rook r = (Rook) piece;
                if(r.isValid(endX, endY, this, false))
                {
                    r.setCoords(endX, endY);
                    r.setMoved(true);
                    board[endY][endX] = r;
                    board[startY][startX] = null;
                    if(r.getColor() == 'W')
                    {
                        if(r.isValid(blackKing.getX(), blackKing.getY(), this, true))
                            check = true;
                    }
                    else
                    {
                        if(r.isValid(whiteKing.getX(), whiteKing.getY(), this, true))
                            check = true;
                    }
                }
                else
                    validMove = false;
            }
            if(piece instanceof Knight)
            {
                Knight kn = (Knight) piece;
                if(kn.isValid(endX, endY, this, false))
                {
                    kn.setCoords(endX, endY);
                    kn.setMoved(true);
                    board[endY][endX] = kn;
                    board[startY][startX] = null;
                    if(kn.getColor() == 'W')
                    {
                        if(kn.isValid(blackKing.getX(), blackKing.getY(), this, true))
                            check = true;
                    }
                    else
                    {
                        if(kn.isValid(whiteKing.getX(), whiteKing.getY(), this, true))
                            check = true;
                    }
                }
                else
                    validMove = false;
            }
            if(piece instanceof Bishop)
            {
                Bishop b = (Bishop) piece;
                if(b.isValid(endX, endY, this, false))
                {
                    b.setCoords(endX, endY);
                    b.setMoved(true);
                    board[endY][endX] = b;
                    board[startY][startX] = null;
                    if(b.getColor() == 'W')
                    {
                        if(b.isValid(blackKing.getX(), blackKing.getY(), this, true))
                            check = true;
                    }
                    else
                    {
                        if(b.isValid(whiteKing.getX(), whiteKing.getY(), this, true))
                            check = true;
                    }
                }
                else
                    validMove = false;
            }
            if(piece instanceof Queen)
            {
                Queen q = (Queen) piece;
                if(q.isValid(endX, endY, this, false))
                {
                    q.setCoords(endX, endY);
                    q.setMoved(true);
                    board[endY][endX] = q;
                    board[startY][startX] = null;
                    if(q.getColor() == 'W')
                    {
                        if(q.isValid(blackKing.getX(), blackKing.getY(), this, true))
                            check = true;
                    }
                    else
                    {
                        if(q.isValid(whiteKing.getX(), whiteKing.getY(), this, true))
                            check = true;
                    }
                }
                else
                    validMove = false;
            }
            if(piece instanceof King)
            {
                King k = (King) piece;
                if(k.isValid(endX, endY, this))
                {
                    k.setCoords(endX, endY);
                    k.setMoved(true);
                    if(k.getColor() == 'W')
                        whiteKing.setCoords(endX, endY);
                    else
                        blackKing.setCoords(endX, endY);
                    if(endX == startX + 2)
                    {
                        Rook r = (Rook) board[startY][7];
                        r.setCoords(5, startY);
                        r.setMoved(true);
                        board[startY][5] = r;
                        board[startY][7] = null;
                    }
                    else if(endX == startX - 2)
                    {
                        Rook r = (Rook) board[startY][0];
                        r.setCoords(3, startY);
                        r.setMoved(true);
                        board[startY][3] = r;
                        board[startY][0] = null;
                    }
                    board[endY][endX] = k;
                    board[startY][startX] = null;
                }
                else
                    validMove = false;
            }
            for(int i = 3; i <= 4; i++)
                for(int j = 0; j < 8; j++)
                {
                    if(board[i][j] != null && board[i][j] instanceof Pawn)
                    {
                        Pawn p = (Pawn) board[i][j];
                        if(p.getEnPassant() > 0)
                            p.setEnPassant(p.getEnPassant() - 1);
                        board[i][j] = p;
                    }
                }
            if(check)
            {
                for(int i = 0; i < 8; i++)
                {
                    if(checkmate)
                    {
                        for(int j = 0; j < 8; j++)
                        {
                            if(board[i][j] != null && board[i][j].getColor() != piece.getColor())
                            {
                                if(board[i][j] instanceof Pawn)
                                {
                                    Pawn pawn = (Pawn) board[i][j];
                                    if(pawn.hasValid(this))
                                    {
                                        checkmate = false;
                                        break;
                                    }
                                }
                                if(board[i][j] instanceof Rook)
                                {
                                    Rook rook = (Rook) board[i][j];
                                    if(rook.hasValid(this))
                                    {
                                        checkmate = false;
                                        break;
                                    }
                                }
                                if(board[i][j] instanceof Knight)
                                {
                                    Knight knight = (Knight) board[i][j];
                                    if(knight.hasValid(this))
                                    {
                                        checkmate = false;
                                        break;
                                    }
                                }
                                if(board[i][j] instanceof Bishop)
                                {
                                    Bishop bishop = (Bishop) board[i][j];
                                    if(bishop.hasValid(this))
                                    {
                                        checkmate = false;
                                        break;
                                    }
                                }
                                if(board[i][j] instanceof Queen)
                                {
                                    Queen queen = (Queen) board[i][j];
                                    if(queen.hasValid(this))
                                    {
                                        checkmate = false;
                                        break;
                                    }
                                }
                                if(board[i][j] instanceof King)
                                {
                                    King king = (King) board[i][j];
                                    if(king.hasValid(this))
                                    {
                                        checkmate = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    else
                        break;
                }
            }
            else
            {
                checkmate = false;
            }
        }
        if(validMove)
        {
            if(turn == 'W')
                turn = 'B';
            else
                turn = 'W';
        }
        else
        {
            //System.out.println("That is not a valid move!");
        }
        return validMove;
    }

    public Piece[][] getBoard() {return board;}

    public void setBoard(Piece[][] b) {board = b;}

    public King getKing(char c)
    {
        if(c == 'W')
            return whiteKing;
        else
            return blackKing;
    }

    public void setKing(King k)
    {
        if(k.getColor() == 'W')
            whiteKing = k;
        else
            blackKing = k;
    }

    public char getTurn() {return turn;}

    public void setTurn(char t) {turn = t;}

    public boolean getCheckmate() {return checkmate;}

    public boolean getCheck() {return check;}

    public void generateNewPieces()
    {
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                if(board[i][j] != null)
                {
                    if (board[i][j].getType() == 'P')
                    {
                        Pawn p = new Pawn(board[i][j].getColor(), board[i][j].getX(), board[i][j].getY());
                        p.setMoved(board[i][j].hasMoved());
                        p.setEnPassant(board[i][j].getEnPassant());
                        board[i][j] = p;
                    }
                    if (board[i][j].getType() == 'N')
                    {
                        Knight kn = new Knight(board[i][j].getColor(), board[i][j].getX(), board[i][j].getY());
                        kn.setMoved(board[i][j].hasMoved());
                        board[i][j] = kn;
                    }
                    if (board[i][j].getType() == 'B')
                    {
                        Bishop b = new Bishop(board[i][j].getColor(), board[i][j].getX(), board[i][j].getY());
                        b.setMoved(board[i][j].hasMoved());
                        board[i][j] = b;
                    }
                    if (board[i][j].getType() == 'R')
                    {
                        Rook r = new Rook(board[i][j].getColor(), board[i][j].getX(), board[i][j].getY());
                        r.setMoved(board[i][j].hasMoved());
                        board[i][j] = r;
                    }
                    if (board[i][j].getType() == 'Q')
                    {
                        Queen q = new Queen(board[i][j].getColor(), board[i][j].getX(), board[i][j].getY());
                        q.setMoved(board[i][j].hasMoved());
                        board[i][j] = q;
                    }
                    if (board[i][j].getType() == 'K')
                    {
                        King k = new King(board[i][j].getColor(), board[i][j].getX(), board[i][j].getY());
                        k.setMoved(board[i][j].hasMoved());
                        board[i][j] = k;
                    }
                }
            }
    }

    public void updateBoard(int x, int y, String userChoice)
    {
        check = false;
        checkmate = false;
        Piece piece = board[y][x];
        if(y == 0)
        {
            if (userChoice.equals("queen")) {
                Queen q = new Queen(board[y][x].getColor(), x, y);
                q.setMoved(true);
                board[y][x] = q;
                if (q.isValid(blackKing.getX(), blackKing.getY(), this, true))
                    check = true;
            }
            if (userChoice.equals("rook")) {
                Rook r = new Rook(board[y][x].getColor(), x, y);
                r.setMoved(true);
                board[y][x] = r;
                if (r.isValid(blackKing.getX(), blackKing.getY(), this, true))
                    check = true;
            }
            if (userChoice.equals("knight")) {
                Knight kn = new Knight(board[y][x].getColor(), x, y);
                kn.setMoved(true);
                board[y][x] = kn;
                if (kn.isValid(blackKing.getX(), blackKing.getY(), this, true))
                    check = true;
            }
            if (userChoice.equals("bishop")) {
                Bishop b = new Bishop(board[y][x].getColor(), x, y);
                b.setMoved(true);
                board[y][x] = b;
                if (b.isValid(blackKing.getX(), blackKing.getY(), this, true))
                    check = true;
            }
        }
        else
        {
            if (userChoice.equals("queen")) {
                Queen q = new Queen(board[y][x].getColor(), x, y);
                q.setMoved(true);
                board[y][x] = q;
                if (q.isValid(whiteKing.getX(), whiteKing.getY(), this, true))
                    check = true;
            }
            else if (userChoice.equals("rook")) {
                Rook r = new Rook(board[y][x].getColor(), x, y);
                r.setMoved(true);
                board[y][x] = r;
                if (r.isValid(whiteKing.getX(), whiteKing.getY(), this, true))
                    check = true;
            }
            else if (userChoice.equals("knight")) {
                Knight kn = new Knight(board[y][x].getColor(), x, y);
                kn.setMoved(true);
                board[y][x] = kn;
                if (kn.isValid(whiteKing.getX(), whiteKing.getY(), this, true))
                    check = true;
            }
            else if (userChoice.equals("bishop")) {
                Bishop b = new Bishop(board[y][x].getColor(), x, y);
                b.setMoved(true);
                board[y][x] = b;
                if (b.isValid(whiteKing.getX(), whiteKing.getY(), this, true))
                    check = true;
            }
        }
        if(check)
        {
            checkmate = true;
            for(int i = 0; i < 8; i++)
            {
                if(checkmate)
                {
                    for(int j = 0; j < 8; j++)
                    {
                        if(board[i][j] != null && board[i][j].getColor() != piece.getColor())
                        {
                            if(board[i][j] instanceof Pawn)
                            {
                                Pawn pawn = (Pawn) board[i][j];
                                if(pawn.hasValid(this))
                                {
                                    checkmate = false;
                                    break;
                                }
                            }
                            if(board[i][j] instanceof Rook)
                            {
                                Rook rook = (Rook) board[i][j];
                                if(rook.hasValid(this))
                                {
                                    checkmate = false;
                                    break;
                                }
                            }
                            if(board[i][j] instanceof Knight)
                            {
                                Knight knight = (Knight) board[i][j];
                                if(knight.hasValid(this))
                                {
                                    checkmate = false;
                                    break;
                                }
                            }
                            if(board[i][j] instanceof Bishop)
                            {
                                Bishop bishop = (Bishop) board[i][j];
                                if(bishop.hasValid(this))
                                {
                                    checkmate = false;
                                    break;
                                }
                            }
                            if(board[i][j] instanceof Queen)
                            {
                                Queen queen = (Queen) board[i][j];
                                if(queen.hasValid(this))
                                {
                                    checkmate = false;
                                    break;
                                }
                            }
                            if(board[i][j] instanceof King)
                            {
                                King king = (King) board[i][j];
                                if(king.hasValid(this))
                                {
                                    checkmate = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                else
                    break;
            }
        }
    }
}