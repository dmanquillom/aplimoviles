package co.unal.triquipreferences;

import java.util.Random;

public class TicTacToeGame {

    // The computer's difficulty levels
    public enum DifficultyLevel {Easy, Harder, Expert};

    // Current difficulty level
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;

    public static final int BOARD_SIZE = 9;
    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';

    private char board[] = new char[BOARD_SIZE];
    private Random mRand = new Random();

    public char getBoardOccupant(int i){
        return board[i];
    }

    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        mDifficultyLevel = difficultyLevel;
    }

    public void clearBoard(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            board[i] = Character.forDigit(i + 1, 10);
        }
    }

    public boolean setUserMove(int i){
        if (board[i] != HUMAN_PLAYER && board[i] != COMPUTER_PLAYER) {
            board[i] = HUMAN_PLAYER;
            return true;
        }
        return false;
    }

    public int checkForWinner() {
        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (board[i] == HUMAN_PLAYER &&
                    board[i+1] == HUMAN_PLAYER &&
                    board[i+2]== HUMAN_PLAYER)
                return 2;
            if (board[i] == COMPUTER_PLAYER &&
                    board[i+1]== COMPUTER_PLAYER &&
                    board[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (board[i] == HUMAN_PLAYER &&
                    board[i+3] == HUMAN_PLAYER &&
                    board[i+6]== HUMAN_PLAYER)
                return 2;
            if (board[i] == COMPUTER_PLAYER &&
                    board[i+3] == COMPUTER_PLAYER &&
                    board[i+6]== COMPUTER_PLAYER)
                return 3;
        }

        // Check for diagonal wins
        if ((board[0] == HUMAN_PLAYER &&
                board[4] == HUMAN_PLAYER &&
                board[8] == HUMAN_PLAYER) ||
                (board[2] == HUMAN_PLAYER &&
                        board[4] == HUMAN_PLAYER &&
                        board[6] == HUMAN_PLAYER))
            return 2;
        if ((board[0] == COMPUTER_PLAYER &&
                board[4] == COMPUTER_PLAYER &&
                board[8] == COMPUTER_PLAYER) ||
                (board[2] == COMPUTER_PLAYER &&
                        board[4] == COMPUTER_PLAYER &&
                        board[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (board[i] != HUMAN_PLAYER && board[i] != COMPUTER_PLAYER)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    public void setComputerMove(){
        int move;

        // First see if there's a move O can make to win
        if(mDifficultyLevel == DifficultyLevel.Harder || mDifficultyLevel == DifficultyLevel.Expert) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (board[i] != HUMAN_PLAYER && board[i] != COMPUTER_PLAYER) {
                    char curr = board[i];
                    board[i] = COMPUTER_PLAYER;
                    if (checkForWinner() == 3) {
                        return;
                    }
                    else
                        board[i] = curr;
                }
            }
        }

        if(mDifficultyLevel == DifficultyLevel.Expert)  {
            // See if there's a move O can make to block X from winning
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (board[i] != HUMAN_PLAYER && board[i] != COMPUTER_PLAYER) {
                    char curr = board[i];   // Save the current number
                    board[i] = HUMAN_PLAYER;
                    if (checkForWinner() == 2) {
                        board[i] = COMPUTER_PLAYER;
                        return;
                    } else
                        board[i] = curr;
                }
            }
        }

        // Generate random move
        do
        {
            move = mRand.nextInt(BOARD_SIZE);
        } while (board[move] == HUMAN_PLAYER || board[move] == COMPUTER_PLAYER);
        board[move] = COMPUTER_PLAYER;
    }
}