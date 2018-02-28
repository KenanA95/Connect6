import java.util.*;

class GameBoard {

    int boardSize;
    String[][] board;
    ArrayList<Coordinates> availableMoves;

    GameBoard(int boardSize){
        this.boardSize = boardSize;
        this.board = initBoard(boardSize);
        this.availableMoves = new ArrayList<>();

        int x = (boardSize-1)/2;
        int y = (boardSize-1)/2;
        ArrayList<Coordinates> neighbors = getNeighborCoordinates(x, y);
        this.availableMoves.addAll(neighbors);
    }

    GameBoard(String[][] board, ArrayList<Coordinates> availableMoves){
        this.board = board;
        this.boardSize = board.length;
        this.availableMoves = availableMoves;
    }

    /**
     * Initialize the 2d array with the center tile set to black
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

    public boolean isGameOver(){
        if(boardIsFull()){
            return true;
        }

        boolean whiteWins = EvaluateBoard.maxInARow(board, "W") == 6;
        boolean blackWins = EvaluateBoard.maxInARow(board, "B") == 6;

        return (blackWins || whiteWins);
    }

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

    static String[][] copyBoard(String[][] board){
        String[][] copy = new String[board.length][board.length];

        for(int i=0; i<board.length; i++){
            System.arraycopy(board[i], 0, copy[i], 0, board.length);
        }

        return copy;

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