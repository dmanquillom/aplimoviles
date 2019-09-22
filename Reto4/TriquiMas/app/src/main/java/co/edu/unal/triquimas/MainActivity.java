package co.edu.unal.triquimas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Triqui triqui;
    private TextView tvScore;
    private int cont, level = 1;
    private Button mBoardButtons[];
    private ColorStateList oldColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBoardButtons = new Button[triqui.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.btn1);
        mBoardButtons[1] = (Button) findViewById(R.id.btn2);
        mBoardButtons[2] = (Button) findViewById(R.id.btn3);
        mBoardButtons[3] = (Button) findViewById(R.id.btn4);
        mBoardButtons[4] = (Button) findViewById(R.id.btn5);
        mBoardButtons[5] = (Button) findViewById(R.id.btn6);
        mBoardButtons[6] = (Button) findViewById(R.id.btn7);
        mBoardButtons[7] = (Button) findViewById(R.id.btn8);
        mBoardButtons[8] = (Button) findViewById(R.id.btn9);
        tvScore = (TextView) findViewById(R.id.tvScore);
        oldColors =  tvScore.getTextColors();
        triqui = new Triqui();
        triqui.clearBoard();
        setTextLevel();
    }

    //Reiniciar Juego
    public void resetGame(View view){
        cont = 0;
        triqui.clearBoard();
        tvScore.setTextColor(oldColors);
        setTextLevel();
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
        }
    }

    //Configurar Juego
    public void configGame(View view ) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.level)
                .setItems(R.array.level_item, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        level = which;
                        setTextLevel();
                    }
                });
        alertDialog.show();
    }

    //Salir Juego
    public void exitGame(View view){
        System.exit(0);
    }

    //Fijar Nivel
    public void setTextLevel(){
        if(level == 0){
            tvScore.setText("Nivel: Dificil");
        } else if (level == 2){
            tvScore.setText("Nivel: Facil");
        } else {
            tvScore.setText("Nivel: Medio");
        }
    }

    //Jugar
    public void Play(View view){
        int i;
        cont++;
        for (i = 0; i < mBoardButtons.length; i++) {
            if(view.getId() == getResources().getIdentifier("btn".concat(String.valueOf(i + 1)), "id", getPackageName())){
                //if(view.getId() == R.id.bOne){
                mBoardButtons[i].setTextColor(Color.rgb(0,255,0));
                mBoardButtons[i].setText("X");
                mBoardButtons[i].setEnabled(false);
                triqui.setUserMove(i, triqui.HUMAN_PLAYER);
            }
        }

        i = triqui.checkForWinner();
        if (i == 2){
            tvScore.setTextColor(Color.rgb(0,255,0));
            tvScore.setText("Ganador: X");
            disableBoard();
        } else if (cont == 5) {
            tvScore.setText("Empate");
        } else {
            i =  triqui.getComputerMove(level);
            mBoardButtons[i].setTextColor(Color.rgb(0,0,255));
            mBoardButtons[i].setText("O");
            mBoardButtons[i].setEnabled(false);
            i = triqui.checkForWinner();
            if(i == 3){
                tvScore.setTextColor(Color.rgb(0,0,255));
                tvScore.setText("Ganador: O");
                disableBoard();
            }
        }
    }

    //Deshabilitar tablero
    public void disableBoard(){
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setEnabled(false);
        }
    }
}
