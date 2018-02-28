public class AlphaBeta {

    static Coordinates bestMove = new Coordinates(0, 0);

    public static int minMax(GameBoard gameBoard, String turn, int maxDepth, int depth, int alpha,
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

    private static int getMax(GameBoard gameBoard, String turn, int maxDepth, int depth, int alpha, int beta, int index){

        int best = Integer.MIN_VALUE;
        boolean maxPlayer = false;

        for(Coordinates move: gameBoard.availableMoves){

            // Connect6 each player has two turns before alternating so do the same thing for getMax
            if(index % 2 == 0){
                turn = opponentTurn(turn);
                maxPlayer = true;
            }

            String [][] boardCopy = GameBoard.copyBoard(gameBoard.board);
            boardCopy[move.y][move.x] = turn;
            GameBoard childBoard = new GameBoard(boardCopy, gameBoard.availableMoves);
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

    private static int getMin(GameBoard gameBoard, String turn, int maxDepth, int depth, int alpha, int beta, int index){
        int best = Integer.MAX_VALUE;
        boolean maxPlayer = true;

        for(Coordinates move: gameBoard.availableMoves){

            // Connect6 each player has two turns before alternating so do the same thing for getMin
            if(index % 2 == 0){
                turn = opponentTurn(turn);
                maxPlayer = false;
            }

            String [][] boardCopy = GameBoard.copyBoard(gameBoard.board);
            boardCopy[move.y][move.x] = turn;
            GameBoard childBoard = new GameBoard(boardCopy, gameBoard.availableMoves);

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

    private static int utilityScore(GameBoard gameBoard, String turn, boolean maxPlayer){

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
