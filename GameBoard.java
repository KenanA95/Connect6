import java.util.*;

/**
 * Class Name: GameBoard.java
 * Author's Name: Kenan Alkiek and Zhenpeng Li
 * Date: 2/25/18
 * Description of the class: Board used by the driver to play the game
 */

class GameBoard {

    int boardSize;
    String[][] board;

    GameBoard(int boardSize){
        this.boardSize = boardSize;
        this.board = initBoard(boardSize);
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
    }

    /**
     * Check if the user selected move is allowed
     * @param currentMove Board dimensions
     * @return true if the move is illegal and false if its legal
     */
    public boolean isIllegalMove(Move currentMove){
        String [][] boardCopy = copyBoard(board);
        Move move = Coordinates.toArrayCoordinates(currentMove, boardSize);

        // Have to play the first move on a copy of the board to check if the second move is legal
        if(isIllegalMove(board, move.x1, move.y1)){
            return true;
        } else{
            boardCopy[move.y1][move.x1] = "B";
            return isIllegalMove(boardCopy, move.x2, move.y2);
        }

    }

    /**
     * Check if the user selected move is allowed
     * @param board Board dimensions
     * @param x The x coordinate
     * @param y The y coordinate
     * @return true if the move is illegal and false if its legal
     */
    private boolean isIllegalMove(String [][] board, int x, int y){
        // Have to check if its in bounds before trying to access any values
        if(inBounds(x, y)){
            boolean isEmpty = board[y][x].equals("E");
            boolean isAdjacent = adjacentToTile(board, x, y);
            return !(isEmpty && isAdjacent);
        }

        return true;
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

    private boolean adjacentToTile(String [][] board, int x, int y){
        ArrayList<Coordinates> neighbors = getNeighborCoordinates(x, y);

        for(Coordinates neighbor: neighbors){
            if(!board[neighbor.y][neighbor.x].equals("E")){
                return true;
            }
        }
        return false;
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
    boolean boardIsFull(){
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
     * Output the board for user display
     */
    public void printBoard(){
        // Format each row with the row number - [board values] - newline
        String rowFormat = " %s\t[ %s ] \n";

        for(int i=0; i<boardSize; i++){
            // The row numbers start at 1 and are the reverse of the standard array index
            int rowNumber = Math.abs(i-boardSize);
            StringBuilder boardRow = new StringBuilder();

            // Create a string with the values of one row
            for(int j=0; j < boardSize; j++) {
                boardRow.append(this.board[i][j]).append("  ");
            }

            System.out.format(rowFormat, Integer.toString(rowNumber), boardRow.toString());
        }

        // Output the column indices at the bottom of the board. Also start at 1 but are not reversed
        System.out.print("   \t  ");
        for(int i=1; i<boardSize+1; i++){

            System.out.print(Integer.toString(i));

            // If the integer is in the double digits only print one space after to keep consistent formatting
            if(String.valueOf(i).length() == 1){
                System.out.print("  ");
            } else {
                System.out.print(" ");
            }
        }
        System.out.println();
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
