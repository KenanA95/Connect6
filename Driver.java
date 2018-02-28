import java.util.*;

/*
Driver program for 2-person game of Connect6

Input arguments
1. Size of the board
2. -h for human -c for computer (the black player)
3. First player name
4. -h for human -c for computer (the white player)
5. Second player name

Example  "java Driver 7 -h Kenan -h Tim"
*/


class Driver {
    private static final int MAX_TIME_PER_MOVE = 500;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args){

        boolean blackComputer = false;
        boolean whiteComputer = false;

        // Parse input arguments
        int boardSize = Integer.parseInt(args[0]);
        int maxTimePerMove = MAX_TIME_PER_MOVE;

        // Create new board and players
        GameBoard gameBoard = new GameBoard(boardSize);
        BlackPlayer blackPlayer = new BlackPlayer(args[2], boardSize, maxTimePerMove);
        WhitePlayer whitePlayer = new WhitePlayer(args[4], boardSize, maxTimePerMove);

        if (args[1].equals("-c")){
            blackComputer = true;
        }

        if (args[3].equals("-c")) {
           whiteComputer = true;
        }

        runGame(gameBoard, blackPlayer, whitePlayer, blackComputer, whiteComputer);
    }

    /**
     * Read in where the user wants to play their two moves
     * @param name The player that the AI will identify as
     * @return Move containing two user choices
     */
    private static Move getHumanMove(String name){
        System.out.println("Hello  " + name + "\nProvide x1 y1 x2 y2");
        int x1 = sc.nextInt();
        int y1 = sc.nextInt();
        int x2 = sc.nextInt();
        int y2 = sc.nextInt();
        return new Move(x1, y1, x2, y2);
    }

    /**
     * Request the move from the white player
     * @param whitePlayer White player (either computer or human)
     * @param whiteComputer true if white is a computer and false otherwise
     * @return Move chosen by the white player
     */
    private static Move getWhiteMove(WhitePlayer whitePlayer, boolean whiteComputer){
        System.out.println("White player: " + whitePlayer.playerName + "'s turn!");
        Move currentMove;

        if (whiteComputer){
            currentMove = whitePlayer.getMove();
        } else {
            currentMove = getHumanMove(whitePlayer.playerName);
        }
        return currentMove;
    }

    /**
     * Request the move from the white player
     * @param blackPlayer Black player (either computer or human)
     * @param blackComputer true if black is a computer and false otherwise
     * @return Move chosen by the black player
     */
    private static Move getBlackMove(BlackPlayer blackPlayer, boolean blackComputer){
        System.out.println("Black player: " + blackPlayer.playerName + "'s turn!");
        Move currentMove;

        if (blackComputer){
           currentMove = blackPlayer.getMove();
        } else {
            currentMove = getHumanMove(blackPlayer.playerName);
        }

        return currentMove;
    }

    /**
     * Output a message if a player makes an illegal move
     * @param turn Turn of the player who made the illegal move
     */
    private static void illegalMoveWinner(String turn){
        System.out.println("Illegal move has been made");

        if(turn.equals("black")){
            System.out.println("White player wins!");
        } else {
            System.out.println("Black player wins!");
        }
    }

    /**
     * Output winner or draw message at the end of the game
     * @param gameBoard The board the players are using
     */
    private static void gameOverMessage(GameBoard gameBoard){
        boolean whiteWin = EvaluateBoard.maxInARow(gameBoard.board, "W") == 6;
        boolean blackWin = EvaluateBoard.maxInARow(gameBoard.board, "B") == 6;

        if(gameBoard.boardIsFull()){
            System.out.println("The game has ended in a draw!");
        } else if(whiteWin){
            System.out.println("White player wins!");
        } else if(blackWin){
            System.out.println("Black player wins!");
        }
    }

    /**
     * Run the game between the two players
     * @param gameBoard The board the players are using
     * @param blackPlayer Black player (either computer or human)
     * @param whitePlayer White player (either computer or human)
     * @param blackComputer true if black is a computer and false otherwise
     * @param whiteComputer true if black is a computer and false otherwise
     */
    private static void runGame(GameBoard gameBoard, BlackPlayer blackPlayer, WhitePlayer whitePlayer,
                                boolean blackComputer, boolean whiteComputer){

        // Same start settings for every game
        boolean done = false;
        String turn = "W";
        Move currentMove;
        gameBoard.printBoard();
        System.out.println("Welcome " + blackPlayer.playerName + "! You are the black player");
        System.out.println("Welcome " + whitePlayer.playerName + "! You are the white player");

        while (!done) {
            if (turn.equals("B")) {
                currentMove = getBlackMove(blackPlayer, blackComputer);
            } else {
                currentMove = getWhiteMove(whitePlayer, whiteComputer);
            }

            if (gameBoard.isIllegalMove(currentMove)) {
                done = true;
                illegalMoveWinner(turn);
            } else {
                gameBoard.update(currentMove, turn);
                gameBoard.printBoard();
                if (gameBoard.isGameOver()) {
                    done = true;
                    gameOverMessage(gameBoard);
                } else {
                    if (turn.equals("B")) {
                        whitePlayer.update(currentMove);
                        turn = "W";
                    } else {
                        blackPlayer.update(currentMove);
                        turn = "B";
                    }
                }
            }
        }
    }
}

