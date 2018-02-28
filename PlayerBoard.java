import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ListIterator;

/**
 * Class Name: PlayerBoard.java
 * Author's Name: Kenan Alkiek and Zhenpeng Li
 * Date: 2/25/18
 * Description of the class: Board used by the players to keep track of the game
 */

public class PlayerBoard {
    ArrayList<Coordinates> availableMoves;
    private int boardSize;
    String [][] board;

    PlayerBoard(int boardSize){
        this.boardSize = boardSize;
        this.board = initBoard(boardSize);
        this.availableMoves = new ArrayList<>();

        int x = (boardSize-1)/2;
        int y = (boardSize-1)/2;
        ArrayList<Coordinates> neighbors = getNeighborCoordinates(x, y);
        this.availableMoves.addAll(neighbors);
    }

    PlayerBoard(String[][] board, ArrayList<Coordinates> availableMoves){
        this.board = board;
        this.boardSize = board.length;
        this.availableMoves = availableMoves;
    }

    /**
     * Run the game between the two players
     * @param boardSize Board dimensions
     * @return 2d array representing the board with center tile set to black
     */
    private static String[][] initBoard(int boardSize) {
        String[][] board = new String[boardSize][boardSize];

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = "E";
            }
        }

        // Place a black stone in the middle of the board
        int center = (boardSize-1)/2;
        board[center][center] = "B";

        return board;
    }

    /**
     * Update the board with the move chosen by the player
     * @param currentMove Board dimensions
     * @param turn Name of the player's turn. B for black and W for white
     */
    public void update(Move currentMove, String turn){

        currentMove = Coordinates.toArrayCoordinates(currentMove, boardSize);

        if(turn.equals("B")){
            board[currentMove.y1][currentMove.x1] = "B";
            board[currentMove.y2][currentMove.x2] = "B";
        } else {
            board[currentMove.y1][currentMove.x1] = "W";
            board[currentMove.y2][currentMove.x2] = "W";
        }

        availableMoves = updateAvailableMoves(currentMove.x1, currentMove.y1);
        availableMoves = updateAvailableMoves(currentMove.x2, currentMove.y2);
    }

    /**
     * Update the move available to a player
     * @param x The x coordinate
     * @param y The y coordinate
     * @return List of the moves available for player
     */
    ArrayList<Coordinates> updateAvailableMoves(int x, int y){

        // Make a copy to avoid concurrency issues
        ArrayList<Coordinates> moves = new ArrayList<>(availableMoves);

        // The move made is no longer available
        for(Coordinates coordinates: moves){
            if(coordinates.equalTo(x, y)){
                moves.remove(coordinates);
                break;
            }
        }

        // The newly available moves are the empty neighboring tiles of the stone placed. Also don't
        // add duplicates
        ArrayList<Coordinates> neighbors = getNeighborCoordinates(x, y);

        for(Coordinates neighbor: neighbors){
            if(board[neighbor.y][neighbor.x].equals("E") && Coordinates.getIndex(availableMoves, neighbor) == -1){
                moves.add(neighbor);
            }
        }

        return moves;
    }

    /**
     * Get all the coordinate neighbors of a point
     * @param x The x coordinate
     * @param y The y coordinate
     * @return List of the neighbor coordinates of the point given
     */
    private ArrayList<Coordinates> getNeighborCoordinates(int x, int y){

        ArrayList<Coordinates> neighborCoordinates = new ArrayList<>();
        int[][] neighbors = new int[][]{{x-1, y-1}, {x, y-1}, {x+1, y-1}, {x-1, y}, {x+1, y}, {x-1, y+1},
                {x, y+1}, {x+1, y+1}};

        for(int[] neighbor: neighbors){
            int xCoord = neighbor[0];
            int yCoord = neighbor[1];

            if(inBounds(xCoord, yCoord)){
                neighborCoordinates.add(new Coordinates(xCoord, yCoord));
            }
        }

        return neighborCoordinates;
    }

    /**
     * Reverse a 2d array. Used to check for a diagonal win instead of having two separate diagonal checks
     * @param board The 2d board to flip
     * @return The reversed/flipped board
     */
    static String[][] getBoardReversed(String[][] board){
        String [][] reversedBoard = new String[board.length][board.length];

        for(int i=0; i<board.length; i++){
            String[] row = board[i];
            Collections.reverse(Arrays.asList(row));
            reversedBoard[i] = row;
        }
        return reversedBoard;
    }

    /**
     * Check if a point is in bounds on the board
     * @param x The x coordinate
     * @param y The y coordinate
     * @return True if the coordinates are in bounds. False if they are out of bounds
     */
    private boolean inBounds(int x, int y){
        return (x > -1 && x < boardSize && y > -1 && y < boardSize);
    }

    /**
     * Check if the game is over
     * @return True if the game is over from a draw or player win
     */
    public boolean isGameOver(){
        if(boardIsFull()){
            return true;
        }

        boolean whiteWins = EvaluateBoard.maxInARow(board, "W") == 6;
        boolean blackWins = EvaluateBoard.maxInARow(board, "B") == 6;

        return (blackWins || whiteWins);
    }

    /**
     * Check if the board is full
     * @return True if the board is full and false otherwise
     */
    private boolean boardIsFull(){
        for(int i=0; i<this.boardSize; i++){
            for(int j=0; j<this.boardSize; j++){
                if(this.board[i][j].equals("E")){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Get a copy of the 2d board array
     * @return A copy of the 2d board array
     */
    static String[][] copyBoard(String[][] board){
        String[][] copy = new String[board.length][board.length];

        for(int i=0; i<board.length; i++){
            System.arraycopy(board[i], 0, copy[i], 0, board.length);
        }

        return copy;
    }
}


class Coordinates {

    int x;
    int y;

    Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * Search a list of coordinates
     * @param coordinatesList The list of the coordinates to search through
     * @param coordinate The coordinate to search for
     * @return The index of the found coordinates. -1 if it does not exist
     */
    public static int getIndex(ArrayList<Coordinates> coordinatesList, Coordinates coordinate){

        ListIterator<Coordinates> iterator = coordinatesList.listIterator();

        while(iterator.hasNext()){
            if(iterator.next().equalTo(coordinate.x, coordinate.y)){
                return iterator.nextIndex();
            }
        }

        return -1;
    }

    /**
     * Check if the points are equal
     * @param x The x coordinate
     * @param y The y coordinate
     * @return True if the coordinates are equal and false otherwise
     */
    public boolean equalTo(int x, int y){
        return (this.x == x && this.y == y);
    }

    /**
     * Game play coordinates starts with the bottom left corner as (1. 1) convert to 2d array indices
     * @param move Move to convert from the display coordinates to the array indices
     * @param boardSize Size of the board
     * @return The move represented as standard array indices
     */
    static Move toArrayCoordinates(Move move, int boardSize){
        Coordinates firstCoordinates = toArrayCoordinates(new Coordinates(move.x1, move.y1), boardSize);
        Coordinates secondCoordinates = toArrayCoordinates(new Coordinates(move.x2, move.y2), boardSize);
        return new Move(firstCoordinates.x, firstCoordinates.y, secondCoordinates.x, secondCoordinates.y);
    }

    /**
     * Game play coordinates starts with the bottom left corner as (1. 1) convert to 2d array indices
     * @param coordinates Coordinates to convert from the display coordinates to the array indices
     * @param boardSize Size of the board
     * @return The move represented as standard array indices
     */
    private static Coordinates toArrayCoordinates(Coordinates coordinates, int boardSize){
        return new Coordinates(coordinates.x -1, Math.abs(coordinates.y-boardSize));
    }

    /**
     * 2d array indices to game play coordinates starts that have the bottom left corner as (1. 1)
     * @param coordinates Coordinates to convert from array indices to the display coordinates
     * @param boardSize Size of the board
     * @return The move represented as the display / game play coordinates
     */
    static Coordinates toDisplayCoordinates(Coordinates coordinates, int boardSize){
        return new Coordinates(coordinates.x + 1, Math.abs(coordinates.y-boardSize));
    }

}