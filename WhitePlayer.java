class WhitePlayer extends Player {

    WhitePlayer(String playerName, int boardSize, int maxTimePerMove){
       super(playerName, boardSize);
    }

    public void update(Move opponentMove){
        gameBoard.update(opponentMove, "B");
    }

    public Move getMove(){
        return getMove("W");
    }
}
