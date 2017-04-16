package techiiesio.com.chessmess;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;

public class ChessActivity extends AppCompatActivity {

    int[] images = {    // chess background images per each square of the chess board
            R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square,
            R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square,
            R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square,
            R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square,
            R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square,
            R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square,
            R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square,
            R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square,
            R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square,
            R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square,
            R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square,
            R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square,
            R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square,
            R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square,
            R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square,
            R.drawable.brown_square, R.drawable.tan_square, R.drawable.brown_square, R.drawable.tan_square,
            };

    int[] pieces = {    // chess piece images per each square of the chess board
            R.drawable.black_rook, R.drawable.black_knight, R.drawable.black_bishop, R.drawable.black_queen,
            R.drawable.black_king, R.drawable.black_bishop, R.drawable.black_knight, R.drawable.black_rook,
            R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn,
            R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn,
            -1, -1, -1, -1,
            -1, -1, -1, -1,
            -1, -1, -1, -1,
            -1, -1, -1, -1,
            -1, -1, -1, -1,
            -1, -1, -1, -1,
            -1, -1, -1, -1,
            -1, -1, -1, -1,
            R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn,
            R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn,
            R.drawable.white_rook, R.drawable.white_knight, R.drawable.white_bishop, R.drawable.white_queen,
            R.drawable.white_king, R.drawable.white_bishop, R.drawable.white_knight, R.drawable.white_rook,
    };

    GridView gridView;  // gridview object that references the grid view container
    private Board board;
    private boolean hasOne;
    private int position1;
    private int position2;
    String userChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);
        position1 = 0;
        position2 = 0;
        hasOne = false;
        board = new Board();

        gridView = (GridView) findViewById(R.id.gridView);

        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

        GridAdapter gridAdapter = new GridAdapter(this, images, pieces, this);
        gridView.setAdapter(gridAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case R.id.saveGame:
                saveGame();
                return true;
            case R.id.loadGame:
                loadGame();
                return true;
            case R.id.newGame:
                newGame();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveGame() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

//        String json = gson.toJson(board.getBoard());
        String json = gson.toJson(board);
        editor.putString("board", json);
        editor.commit();

        Toast toast = Toast.makeText(this, "Game Saved", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void loadGame() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("board", "");


        if(json != null){
            board = gson.fromJson(json, Board.class);
            board.generateNewPieces();
            setPieces(board.getBoard());
            if(board.getTurn() == 'B')
                flipBoard();
            GridAdapter gridAdapter = new GridAdapter(this, images, pieces, this);
            gridView.setAdapter(gridAdapter);
        }
    }

    private void newGame() {
        board = new Board();
        setPieces(board.getBoard());
        GridAdapter gridAdapter = new GridAdapter(this, images, pieces, this);
        gridView.setAdapter(gridAdapter);
    }

    public Board getBoard() {return board;}

    public void setBoard(Board b) {board = b;}

    public void setHasOne(boolean h) {hasOne = h;}

    public void setPosition1(int p) {position1 = p;}

    public void setPosition2(int p) {position2 = p;}

    public int getPosition1() {return position1;}

    public int getPosition2() {return position2;}

    public boolean isHasOne() {return hasOne;}

    public void move()
    {
        int startX = getX(position1);
        int startY = getY(position1);
        int endX = getX(position2);
        int endY = getY(position2);
        boolean moved = board.makeMove(startX, startY, endX, endY);
        if(moved)
        {
            if ((endY == 0 || endY == 7) && board.getBoard()[endY][endX] instanceof Pawn)
                createAlert(endX, endY);

            setPieces(board.getBoard());
            if(board.getTurn() == 'B')
                flipBoard();
            if(board.getCheckmate())
            {
                Toast toast = Toast.makeText(this, "Checkmate!", Toast.LENGTH_SHORT);
                toast.show();
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.checkmate);
                mp.start();
            }
            else if(board.getCheck())
            {
                Toast toast = Toast.makeText(this, "Check!", Toast.LENGTH_SHORT);
                toast.show();
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.toasty);
                mp.start();
            }
        }
        else
        {
            Toast toast = Toast.makeText(this, "That is not a valid move!", Toast.LENGTH_SHORT);
            toast.show();
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.illogical);
            mp.start();
        }
        GridAdapter gridAdapter = new GridAdapter(this, images, pieces, this);
        gridView.setAdapter(gridAdapter);
    }

    public int getX(int position)
    {
        if(board.getTurn() == 'W')
            return position % 8;
        return (63 - position) % 8;
    }

    public int getY(int position)
    {
        if(board.getTurn() == 'W')
            return position / 8;
        else
            return (63 - position) / 8;
    }

    public void setPieces(Piece[][] p)
    {
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                if(p[i][j] != null)
                {
                    if(p[i][j] instanceof Pawn)
                    {
                        if(p[i][j].getColor() == 'W')
                            pieces[(8 * i) + j] = R.drawable.white_pawn;
                        else
                            pieces[(8 * i) + j] = R.drawable.black_pawn;
                    }
                    else if(p[i][j] instanceof Rook)
                    {
                        if(p[i][j].getColor() == 'W')
                            pieces[(8 * i) + j] = R.drawable.white_rook;
                        else
                            pieces[(8 * i) + j] = R.drawable.black_rook;
                    }
                    else if(p[i][j] instanceof Knight)
                    {
                        if (p[i][j].getColor() == 'W')
                            pieces[(8 * i) + j] = R.drawable.white_knight;
                        else
                            pieces[(8 * i) + j] = R.drawable.black_knight;
                    }
                    else if(p[i][j] instanceof Bishop)
                    {
                        if(p[i][j].getColor() == 'W')
                            pieces[(8 * i) + j] = R.drawable.white_bishop;
                        else
                            pieces[(8 * i) + j] = R.drawable.black_bishop;
                    }
                    else if(p[i][j] instanceof Queen)
                    {
                        if(p[i][j].getColor() == 'W')
                            pieces[(8 * i) + j] = R.drawable.white_queen;
                        else
                            pieces[(8 * i) + j] = R.drawable.black_queen;
                    }
                    else if(p[i][j] instanceof King)
                    {
                        if(p[i][j].getColor() == 'W')
                            pieces[(8 * i) + j] = R.drawable.white_king;
                        else
                            pieces[(8 * i) + j] = R.drawable.black_king;
                    }
                }
                else
                    pieces[(8 * i) + j] = -1;
            }
    }

    public void flipBoard()
    {
        int[] temp = new int[pieces.length];
        int x = pieces.length - 1;
        for(int i = 0; i < pieces.length; i++, x--)
            temp[x] = pieces[i];
        pieces = temp;
    }

    public void createAlert(final int x, final int y) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View mView = inflater.inflate(R.layout.user_choice, null);
        final EditText editText = (EditText) mView.findViewById(R.id.userchoice);

        // inflate and set the layout for the dialog
        // pass null as a parent view because its going in the dialog layout
        builder.setView(mView)
                .setPositiveButton("enter", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        userChoice = editText.getText().toString().toLowerCase();

                        if(!userChoice.equals("queen") &&
                                !userChoice.equals("rook") &&
                                !userChoice.equals("knight") &&
                                !userChoice.equals("bishop")) {
                            Toast.makeText(ChessActivity.this, "Please enter Queen, Rook, Knight, or Bishop", Toast.LENGTH_SHORT).show();
                            createAlert(x, y);
                        }
                        board.updateBoard(x, y, userChoice);
                        setPieces(board.getBoard());
                        if(board.getTurn() == 'B')
                            flipBoard();
                        if(board.getCheckmate())
                        {
                            Toast toast = Toast.makeText(ChessActivity.this, "Checkmate!", Toast.LENGTH_SHORT);
                            toast.show();
                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.checkmate);
                            mp.start();
                        }
                        else if(board.getCheck())
                        {
                            Toast toast = Toast.makeText(ChessActivity.this, "Check!", Toast.LENGTH_SHORT);
                            toast.show();
                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.toasty);
                            mp.start();
                        }

                        GridAdapter gridAdapter = new GridAdapter(ChessActivity.this, images, pieces, ChessActivity.this);
                        gridView.setAdapter(gridAdapter);
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }
}
