package co.edu.unal.triquicanvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BoardView extends View {

    private Paint mPaint;
    private int cellSize;
    private Triqui rTriqui;
    private boolean winner = false;
    public static final int GRID_WIDTH = 5;
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

    public void setRTriqui(Triqui rTriqui) {
        this.rTriqui = rTriqui;
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
        for (int i = 0; i < rTriqui.BOARD_SIZE; i++) {
            int col = i % 3;
            int row = i / 3;

            if (rTriqui.getBoardOccupant(i) == rTriqui.HUMAN_PLAYER) {
                canvas.drawBitmap(mHumanBitmap, null, new Rect(cellSize * col, cellSize * row, cellSize * (col + 1), cellSize * (row + 1)), null);
            } else if (rTriqui.getBoardOccupant(i) == rTriqui.COMPUTER_PLAYER) {
                canvas.drawBitmap(mComputerBitmap, null, new Rect(cellSize * col, cellSize * row, cellSize * (col + 1), cellSize * (row + 1)), null);
            }
        }

        canvas.drawLine(cellSize, 0, cellSize, boardSize, mPaint);
        canvas.drawLine(cellSize * 2, 0, cellSize * 2, boardSize, mPaint);
        canvas.drawLine(0, cellSize, boardSize, cellSize, mPaint);
        canvas.drawLine(0, cellSize * 2, boardSize, cellSize * 2, mPaint);
    }
}