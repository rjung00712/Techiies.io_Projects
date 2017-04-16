package io.techies.com.puzzle_8;

import android.support.annotation.NonNull;

/**
 * Created by Richard on 4/4/17.
 */

public class Player implements Comparable{
    private String userName;    //Holds the name of the player
    private int moves = 0;      //Holds the number of moves

    //Creates a new player based on just their name
    public Player(String userName) {
        this.userName = userName;
    }

    //Creates a new player based on the name and number of moves
    public Player(String userName, int moves) {
        this.userName = userName;
        this.moves = moves;
    }

    //Returns the name of the player
    public String getUserName() {return userName;}

    //Sets the name of the player
    public void setUserName(String userName) {this.userName = userName;}

    //Gets the number of moves for this player
    public int getMoves() {return moves;}

    //Sets the number of moves for this player
    public void setMoves(int moves) {this.moves = moves;}

    //Compares the two players so that we know who has the better score
    @Override
    public int compareTo(@NonNull Object o) {
        return this.getMoves() - ((Player)o).getMoves();
    }
}
