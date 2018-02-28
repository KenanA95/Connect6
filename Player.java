public class Player {

    private final int MAX_DEPTH = 3;

    String playerName;
    private int boardSize;
    GameBoard gameBoard;

    Player(String playerName, int boardSize){
        this.playerName = playerName;
        this.boardSize = boardSize;
        this.gameBoard = new GameBoard(boardSize);
    }

    /**
     * Select two moves with alpha beta pruning + min max
     * @param turn Name of the players turn. B for black and W for white
     * @return Move found
     */
    Move getMove(String turn){
        AlphaBeta.minMax(gameBoard, turn, MAX_DEPTH, 0, -1000, 1000, true, 0);
        Coordinates firstMove = AlphaBeta.bestMove;
        gameBoard.availableMoves = gameBoard.updateAvailableMoves(firstMove.x, firstMove.y);
        gameBoard.board[firstMove.y][firstMove.x] = turn;

        AlphaBeta.minMax(gameBoard, turn, MAX_DEPTH, 0, -1000, 1000, true, 0);
        Coordinates secondMove = AlphaBeta.bestMove;
        gameBoard.availableMoves = gameBoard.updateAvailableMoves(secondMove.x, secondMove.y);
        gameBoard.board[secondMove.y][secondMove.x] = turn;

        firstMove = Coordinates.toDisplayCoordinates(firstMove, boardSize);
        secondMove = Coordinates.toDisplayCoordinates(secondMove, boardSize);

        return new Move(firstMove.x, firstMove.y, secondMove.x, secondMove.y);
    }

}
