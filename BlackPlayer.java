class BlackPlayer extends Player {

    BlackPlayer(String playerName, int boardSize, int maxTimePerMove){
        super(playerName, boardSize);
    }

    public void update(Move opponentMove){
        gameBoard.update(opponentMove, "W");
    }


    public Move getMove(){
        return getMove("B");
    }
}