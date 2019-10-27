package co.triquionline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class BoardView extends View {

    private int cellSize;
    private Paint mPaint;
    private List<String> board;
    public static final int GRID_WIDTH = 5;
    public static final String PLAYER_X = "X";
    public static final String PLAYER_O = "O";
    private Bitmap mHumanBitmap, mComputerBitmap;

    public BoardView(Context context) {
        super(context);
        initialize();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public void initialize() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.human);
        mComputerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.computer);
    }

    public List<String> getBoard() {
        return board;
    }

    public void setBoard(List<String> board) {
        this.board = board;
    }

    public boolean setBoardMove(int i, String letter){
        if((board.get(i).compareTo(PLAYER_X) != 0) && (board.get(i).compareTo(PLAYER_O) != 0)){
            board.set(i, letter);
            return true;
        }
        return false;
    }

    public int getCellSize(){
        return getWidth()/3;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Make thick, light gray lines
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStrokeWidth(GRID_WIDTH);

        // Determine the width and height of the View
        int boardSize = getWidth();
        cellSize = boardSize / 3;

        // Draw the two vertical board lines
        for (int i = 0; i < board.size(); i++) {
            int col = i % 3;
            int row = i / 3;

            if (board.get(i).compareTo(PLAYER_X) == 0) {
                canvas.drawBitmap(mHumanBitmap, null, new Rect(cellSize * col, cellSize * row, cellSize * (col + 1), cellSize * (row + 1)), null);
            } else if (board.get(i).compareTo(PLAYER_O) == 0) {
                canvas.drawBitmap(mComputerBitmap, null, new Rect(cellSize * col, cellSize * row, cellSize * (col + 1), cellSize * (row + 1)), null);
            }
        }

        canvas.drawLine(cellSize, 0, cellSize, boardSize, mPaint);
        canvas.drawLine(cellSize * 2, 0, cellSize * 2, boardSize, mPaint);
        canvas.drawLine(0, cellSize, boardSize, cellSize, mPaint);
        canvas.drawLine(0, cellSize * 2, boardSize, cellSize * 2, mPaint);
    }

    public int checkForWinner() {
        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (board.get(i).compareTo(PLAYER_X) == 0 && board.get(i+1).compareTo(PLAYER_X) == 0 && board.get(i+2).compareTo(PLAYER_X) == 0)
                return 2;
            if (board.get(i).compareTo(PLAYER_O) == 0 && board.get(i+1).compareTo(PLAYER_O) == 0 && board.get(i+2).compareTo(PLAYER_O) == 0)
                return 2;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (board.get(i).compareTo(PLAYER_X) == 0 && board.get(i+3).compareTo(PLAYER_X) == 0 && board.get(i+6).compareTo(PLAYER_X) == 0)
                return 2;
            if (board.get(i).compareTo(PLAYER_O) == 0 && board.get(i+3).compareTo(PLAYER_O) == 0 && board.get(i+6).compareTo(PLAYER_O) == 0)
                return 2;
        }

        // Check for diagonal wins
        if ((board.get(0).compareTo(PLAYER_X) == 0 && board.get(4).compareTo(PLAYER_X) == 0 && board.get(8).compareTo(PLAYER_X) == 0) ||
                (board.get(2).compareTo(PLAYER_X) == 0 && board.get(4).compareTo(PLAYER_X) == 0 && board.get(6).compareTo(PLAYER_X) == 0))
            return 2;
        if ((board.get(0).compareTo(PLAYER_O) == 0 && board.get(4).compareTo(PLAYER_O) == 0 && board.get(8).compareTo(PLAYER_O) == 0) ||
                (board.get(2).compareTo(PLAYER_O) == 0 && board.get(4).compareTo(PLAYER_O) == 0 && board.get(6).compareTo(PLAYER_O) == 0))
            return 2;

        // Check for tie
        for (int i = 0; i < board.size(); i++) {
            // If we find a number, then no one has won yet
            if (board.get(i) != PLAYER_X && board.get(i) != PLAYER_O)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }
}