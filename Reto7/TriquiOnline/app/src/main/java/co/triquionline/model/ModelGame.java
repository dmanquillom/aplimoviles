package co.triquionline.model;

import java.util.ArrayList;
import java.util.List;

public class ModelGame {

    private String gameID, playerX, playerO, turn, winner;
    private List<String> board = new ArrayList() {{ add("0"); add("1"); add("2"); add("3"); add("4"); add("5"); add("6"); add("7"); add("8"); }};

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getPlayerX() {
        return playerX;
    }

    public void setPlayerX(String playerX) {
        this.playerX = playerX;
    }

    public String getPlayerO() {
        return playerO;
    }

    public void setPlayerO(String playerO) {
        this.playerO = playerO;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public List<String> getBoard() {
        return board;
    }

    public void setBoard(List<String> board) {
        this.board = board;
    }

    @Override
    public String toString() {
        return playerX;
    }
}
