package co.edu.unal.triquicanvas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Triqui rTriqui;
    private TextView tvScore;
    private BoardView mBoardView;
    private ImageButton btnSound;
    private ColorStateList oldColors;
    private boolean winner = false, sound = true;
    MediaPlayer mHumanMediaPlayer, mComputerMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvScore = (TextView) findViewById(R.id.tvScore);
        oldColors =  tvScore.getTextColors();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setOnTouchListener(mTouchListener);
        btnSound = (ImageButton) findViewById(R.id.ibtnSound);

        rTriqui = new Triqui();
        mBoardView.setRTriqui(rTriqui);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.computer);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    //Reiniciar Juego
    public void resetGame(View view){
        winner = false;
        rTriqui.clearBoard();
        mBoardView.invalidate();
        tvScore.setTextColor(oldColors);
        setTextLevel();
    }

    //Configurar Juego
    public void configGame(View view ) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.level)
                .setItems(R.array.level_item, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        rTriqui.setLevel(which);
                        tvScore.setTextColor(oldColors);
                        setTextLevel();
                    }
                });
        alertDialog.show();
    }

    //Salir Juego
    public void exitGame(View view){
        System.exit(0);
    }

    public void soundEnable(View view){
        if(sound){
            sound = false;
            btnSound.setImageResource(android.R.drawable.ic_lock_silent_mode);
        } else {
            sound = true;
            btnSound.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
        }
    }

    //Fijar Nivel
    public void setTextLevel(){
        tvScore.setTextColor(oldColors);
        if(rTriqui.getLevel() == 0){
            tvScore.setText("Nivel: Dificil");
        } else if (rTriqui.getLevel()
                == 2){
            tvScore.setText("Nivel: Facil");
        } else {
            tvScore.setText("Nivel: Medio");
        }
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getCellSize();
            int row = (int) event.getY() / mBoardView.getCellSize();
            int pos = row * 3 + col;

            if(!winner && rTriqui.setUserMove(pos)) {
                if(sound){
                    mHumanMediaPlayer.start();
                }
                if (rTriqui.checkForWinner() == 0) {
                    rTriqui.setComputerMove();
                    if(sound) {
                        mComputerMediaPlayer.start();
                    }
                    if (rTriqui.checkForWinner() == 3) {
                        winner = true;
                        tvScore.setTextColor(Color.rgb(0, 0, 255));
                        tvScore.setText("Ganador: O");
                    }
                } else if (rTriqui.checkForWinner() == 2) {
                    winner = true;
                    tvScore.setTextColor(Color.rgb(0, 255, 0));
                    tvScore.setText("Ganador: X");
                } else {
                    tvScore.setText("Empate");
                }
                mBoardView.invalidate();
            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };
}
