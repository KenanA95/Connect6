/**
 * Class Name: Player.java
 * Author's Name: Kenan Alkiek and Zhenpeng Li
 * Date: 2/25/18
 */

class WhitePlayer extends Player{

    WhitePlayer(String playerName, int boardSize, int maxTimePerMove){
       super(playerName, boardSize);
    }

    /**
     * Keep track of the black players move
     * @param opponentMove Move selected by the black player
     */
    public void update(Move opponentMove){
        gameBoard.update(opponentMove, "B");
    }

    /**
     * Select a move using alpha beta pruning
     * @return The move found using alpha beta + min max
     */
    public Move getMove(){
        return getMove("W");
    }
}
