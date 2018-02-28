/**
 * Class Name: AlphaBeta.java
 * Author's Name: Kenan Alkiek and Zhenpeng Li
 * Date: 2/25/18
 * Description of the class: Alpha beta pruning algorithm to select next move
 */

public class AlphaBeta {

    static Coordinates bestMove = new Coordinates(0, 0);

    /**
     * Min max algorithm to pick the best move available
     * @param gameBoard Current game board being played on
     * @param turn Name of the players turn. B for black and W for white
     * @param maxDepth The recursive limit
     * @param depth The current depth
     * @param alpha Current alpha value
     * @param beta Current beta value
     * @param maxPlayer true if its
     * @param index Keeps track of how many times we've called each method. Used to alternate between players every 2 moves
     * @return The best utility score found
     */
    public static int minMax(PlayerBoard gameBoard, String turn, int maxDepth, int depth, int alpha,
                             int beta, boolean maxPlayer, int index){

        // If its a terminal node return the utility value
        if(gameBoard.isGameOver() || depth==maxDepth){
            return utilityScore(gameBoard, turn, maxPlayer);
        }

        if(maxPlayer){
            return getMax(gameBoard, turn, maxDepth, depth, alpha, beta, index);

        } else{
            return getMin(gameBoard, turn, maxDepth, depth, alpha, beta, index);
        }

    }

    /**
     * Get the maximum utility value at a given level
     * @param gameBoard Current game board being played on
     * @param turn Name of the players turn. B for black and W for white
     * @param maxDepth The recursive limit
     * @param depth The current depth
     * @param alpha Current alpha value
     * @param beta Current beta value
     * @param index Keeps track of how many times we've called each method. Used to alternate between players every 2 moves
     * @return The maximum utility value for boards at a given depth
     */
     private static int getMax(PlayerBoard gameBoard, String turn, int maxDepth, int depth, int alpha, int beta, int index){

        int best = Integer.MIN_VALUE;
        boolean maxPlayer = false;

        for(Coordinates move: gameBoard.availableMoves){

            if(index % 2 == 0){
                turn = opponentTurn(turn);
                maxPlayer = true;
            }

            String [][] boardCopy = PlayerBoard.copyBoard(gameBoard.board);
            boardCopy[move.y][move.x] = turn;
            PlayerBoard childBoard = new PlayerBoard(boardCopy, gameBoard.availableMoves);
            int val = minMax(childBoard, turn, maxDepth,depth+1, alpha, beta, maxPlayer, index++);

            if(val > best){
                best = val;
                bestMove = new Coordinates(move.x, move.y);
            }

            alpha = Math.max(alpha, best);

            // Pruning
            if(beta <= alpha){
                break;
            }
        }

        return best;
    }

    /**
     * Get the minimum utility value at a given level
     * @param gameBoard Current game board being played on
     * @param turn Name of the players turn. B for black and W for white
     * @param maxDepth The recursive limit
     * @param depth The current depth
     * @param alpha Current alpha value
     * @param beta Current beta value
     * @param index Keeps track of how many times we've called each method. Used to alternate between players every 2 moves
     * @return The minimum utility value for boards at a given depth
     */
    private static int getMin(PlayerBoard gameBoard, String turn, int maxDepth, int depth, int alpha, int beta, int index){
        int best = Integer.MAX_VALUE;
        boolean maxPlayer = true;

        for(Coordinates move: gameBoard.availableMoves){

            if(index % 2 == 0){
                turn = opponentTurn(turn);
                maxPlayer = false;
            }

            String [][] boardCopy = GameBoard.copyBoard(gameBoard.board);
            boardCopy[move.y][move.x] = turn;
            PlayerBoard childBoard = new PlayerBoard(boardCopy, gameBoard.availableMoves);

            int val = minMax(childBoard, turn, maxDepth,depth+1, alpha, beta, maxPlayer, index++);

            if (val < best){
                best = val;
                bestMove = new Coordinates(move.x, move.y);
            }

            beta = Math.min(beta, best);

            // Pruning
            if(beta <= alpha){
                break;
            }
        }
        return best;
    }

    /**
     * Evaluate how much a board is worth
     * @param gameBoard Current game board being played on
     * @param turn Name of the players turn. B for black and W for white
     * @return The utility score of a board
     */
    private static int utilityScore(PlayerBoard gameBoard, String turn, boolean maxPlayer){

        int utility = 0;
        Score score = maxScore(gameBoard.board, turn);
        boolean canWin = (score.maxInARow + score.surroundingBlanks) >= 6;

        // May have to adjust these scores later
        if(canWin){
            switch (score.maxInARow){
                case 6:
                    utility = 500;
                    break;
                case 5:
                    utility = 50;
                    break;
                case 4:
                    utility = 40;
                    break;
                case 3:
                    utility = 30;
                    break;
                case 2:
                    utility = 20;
                    break;
                case  1:
                    utility = 10;
                    break;
            }
        }

        // Return the opposite score for the opponent's turn
        if(!maxPlayer){
            utility *= -1;
        }

        return utility;
    }

    /**
     * Compute the best score (most in a row) of the horizontal, vertical, and diagonals
     * @param board 2d array board being played
     * @param turn Name of the players turn. B for black and W for white
     * @return Vest score (most in a row) of the horizontal, vertical, and diagonals
     */
    private static Score maxScore(String [][] board, String turn){
        Score horizontalScore = EvaluateBoard.horizontalScore(board, turn);
        Score verticalScore = EvaluateBoard.verticalScore(board, turn);
        Score diagonalScore = EvaluateBoard.diagonalScore(board, turn);

        if(horizontalScore.maxInARow > verticalScore.maxInARow){
            if(horizontalScore.maxInARow > diagonalScore.maxInARow){
                return horizontalScore;
            }
        } else {
            if(verticalScore.maxInARow > diagonalScore.maxInARow){
                return verticalScore;
            }
        }

        return diagonalScore;
    }

    /**
     * Alternate the turns
     * @param turn Name of the players turn. B for black and W for white
     * @return B=>W and W=>B
     */
    private static String opponentTurn(String turn){
        if(turn.equals("B")){
            return "W";
        } else {
            return "B";
        }
    }
}
