public class EvaluateBoard {

    /**
     * Count the most in a row of any horizontal, vertical, or diagonal
     * @param board 2d array board being played
     * @param turn Name of the players turn. B for black and W for white
     * @return Most stones in a row
     */
    static int maxInARow(String[][] board, String turn){
        int horizontalCount = horizontalScore(board, turn).maxInARow;
        if(horizontalCount == 6){ return 6;}
        int verticalCount = verticalScore(board, turn).maxInARow;
        if(verticalCount == 6){ return 6;}
        return Math.max(diagonalScore(board, turn).maxInARow, Math.max(horizontalCount, verticalCount));
    }

    /**
     * Count the most in a row of any horizontal
     * @param board 2d array board being played
     * @param turn Name of the players turn. B for black and W for white
     * @return Most stones in a row horizontally
     */
    static Score horizontalScore(String[][] board, String turn){
        Score score = new Score(0, 0);

        for(int i=0; i<board.length; i++){
            String[] row = board[i];
            int stonesInARow = 0;
            int blankSpaces = 0;

            for(String tile: row){

                if(tile.equals("E")){
                    blankSpaces++;
                } else if(tile.equals(turn)){
                    stonesInARow++;
                } else {
                    stonesInARow = 0;
                    blankSpaces = 0;
                }
                if(stonesInARow == 6){
                    return new Score(6, 0);
                }
                if (stonesInARow >= score.maxInARow){
                    score.maxInARow = stonesInARow;
                    score.surroundingBlanks = blankSpaces;
                }
            }
        }
        return score;
    }

    /**
     * Count the most in a row of any vertical
     * @param board 2d array board being played
     * @param turn Name of the players turn. B for black and W for white
     * @return Most stones in a row vertically
     */
    static Score verticalScore(String[][] board, String turn){
        Score score = new Score(0, 0);

        for(int i=0; i<board.length; i++){
            int stonesInARow = 0;
            int blankSpaces = 0;

            for(int j=0; j<board.length; j++){

                if(board[j][i].equals("E")){
                    blankSpaces++;
                } else if(board[j][i].equals(turn)){
                    stonesInARow++;
                } else {
                    stonesInARow = 0;
                    blankSpaces = 0;
                }
                if(stonesInARow == 6){
                    return new Score(6, 0);
                }
                if (stonesInARow >= score.maxInARow){
                    score.maxInARow = stonesInARow;
                    score.surroundingBlanks = blankSpaces;
                }
            }
        }
        return score;
    }

    /**
     * Count the most in a row of both diagonals (right to left and left to right)
     * @param board 2d array board being played
     * @param turn Name of the players turn. B for black and W for white
     * @return Most stones in a row of any diagonal
     */
    static Score diagonalScore(String[][] board, String turn) {

        // Left to right diagonals
        Score leftToRight = diagonalCount(board, turn);

        // Only check the other diagonals if necessary
        if(leftToRight.maxInARow < 6){
            // Flip the board and repeat for right to left
            String [][] reverseBoard = GameBoard.getBoardReversed(board);
            Score rightToLeft = diagonalCount(reverseBoard, turn);
            if(rightToLeft.maxInARow > leftToRight.maxInARow){
                return rightToLeft;
            }
        }

        return leftToRight;
    }

    /**
     * Count the most in a row of the main diagonal
     * @param board 2d array board being played
     * @param turn Name of the players turn. B for black and W for white
     * @return Most stones in a row on the main diagonal
     */
    private static Score diagonalCount(String[][] board, String turn) {

        Score score = new Score(0, 0);
        int stonesInARow = 0;
        int blankSpaces = 0;

        // Top half diagonals starting from top left and working right
        for(int i=0; i< board.length * 2; i++){
            for(int j=0; j<=i; j++){
                int index = i -j;

                if(index < board.length && j < board.length){
                    String tile = board[index][j];
                    if(tile.equals("E")){
                        blankSpaces++;
                    }else if(tile.equals(turn)){
                        stonesInARow++;
                    } else {
                        stonesInARow = 0;
                        blankSpaces = 0;
                    }
                    if(stonesInARow == 6){
                        return new Score(6, 0);
                    }
                }
                if (stonesInARow >= score.maxInARow){
                    score.maxInARow = stonesInARow;
                    score.surroundingBlanks = blankSpaces;
                }
            }

            stonesInARow = 0;
            blankSpaces = 0;
        }
        return score;
    }
}

class Score{

    int maxInARow;
    int surroundingBlanks;

    Score(int maxInARow, int surroundingBlanks){
        this.maxInARow = maxInARow;
        this.surroundingBlanks = surroundingBlanks;
    }
}