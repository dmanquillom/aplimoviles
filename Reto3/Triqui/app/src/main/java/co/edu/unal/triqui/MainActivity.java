package co.edu.unal.triqui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int cont;
    private Triqui triqui;
    private Button btnReset;
    private TextView tvScore;
    private Button mBoardButtons[];
    private CheckBox cbPlayer2;
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
        cbPlayer2 = (CheckBox) findViewById(R.id.cbPlayer2);

        btnReset = (Button) findViewById(R.id.btnReset);


        triqui = new Triqui();
        triqui.setMBoard();
    }

    public void onClick(View view) {
        if(view.getId() == R.id.btnReset){
            resetGame();
        } else {
            cbPlayer2.setEnabled(false);
            if(cbPlayer2.isChecked()){
                SecondPlayer(view);
            } else {
                Computer(view);
            }
        }
    }

    //Deshabilitar todos los botones
    public void disableBoard(){
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setEnabled(false);
        }
    }

    //Limpiar Juego
    public void resetGame(){
        cont = 0;
        triqui.setMBoard();
        tvScore.setText("");
        tvScore.setTextColor(oldColors);
        cbPlayer2.setEnabled(true);
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
        }
    }

    public void SecondPlayer(View view){
        int i;
        cont++;
        for (i = 0; i < mBoardButtons.length; i++) {
            if(view.getId() == getResources().getIdentifier("btn".concat(String.valueOf(i + 1)), "id", getPackageName())){
                //if(view.getId() == R.id.bOne){
                if(cont % 2 == 1){
                    mBoardButtons[i].setTextColor(Color.rgb(0,255,0));
                    mBoardButtons[i].setText("X");
                    triqui.setUserMove(i, triqui.HUMAN_PLAYER);
                } else {
                    mBoardButtons[i].setTextColor(Color.rgb(0,0,255));
                    mBoardButtons[i].setText("O");
                    triqui.setUserMove(i, triqui.COMPUTER_PLAYER);
                }
                mBoardButtons[i].setEnabled(false);
            }
        }

        i = triqui.checkForWinner();
        if(i == 3){
            tvScore.setTextColor(Color.rgb(255,0,0));
            tvScore.setText("Ganador: Jugador O");
            disableBoard();
        } else if (i == 2){
            tvScore.setTextColor(Color.rgb(255,0,0));
            tvScore.setText("Ganador: Jugador X");
            disableBoard();
        } else if(cont == 9){
            tvScore.setText("Empate");
            disableBoard();
        } else {
            if(cont % 2 == 1){
                tvScore.setText("Turno: Jugador O");
            } else {
                tvScore.setText("Turno: Jugador X");
            }
        }
    }

    //Jugar contra el computador
    public void Computer(View view){
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
            tvScore.setTextColor(Color.rgb(255,0,0));
            tvScore.setText("Ganador: Jugador X");
            disableBoard();
        } else if (cont == 5) {
            tvScore.setText("Empate");
        } else {
            i =  triqui.getComputerMove();
            mBoardButtons[i].setTextColor(Color.rgb(0,0,255));
            mBoardButtons[i].setText("O");
            mBoardButtons[i].setEnabled(false);
            i = triqui.checkForWinner();
            if(i == 3){
                tvScore.setTextColor(Color.rgb(255,0,0));
                tvScore.setText("Ganador: Jugador O");
                disableBoard();
            }
        }
    }
}
