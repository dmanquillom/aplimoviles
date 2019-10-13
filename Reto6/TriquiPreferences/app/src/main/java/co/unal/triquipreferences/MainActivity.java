package co.unal.triquipreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TicTacToeGame mGame;
    private BoardView mBoardView;
    private TextView mInfoTextView;
    private SharedPreferences mPrefs;
    private ColorStateList defaultColors;
    private boolean mSoundOn, winner = false;
    MediaPlayer mHumanMediaPlayer, mComputerMediaPlayer;

    public static final String soundKey = "sound";
    public static final String victoryMessageKey = "victory_message";
    public static final String difficultyLevelKey = "difficulty_level";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setOnTouchListener(mTouchListener);

        mGame = new TicTacToeGame();
        mBoardView.setRTriqui(mGame);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean(soundKey, true);
        String difficultyLevel = mPrefs.getString(difficultyLevelKey, getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);

        defaultColors = mInfoTextView.getTextColors();
        newGame();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_game:
                newGame();
                break;
            case R.id.ai_difficulty:
                startActivityForResult(new Intent(this, SettingsActivity.class), 0);
                break;
            case R.id.quit:
                exitGame();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings
            mSoundOn = mPrefs.getBoolean(soundKey, true);
            String difficultyLevel = mPrefs.getString(difficultyLevelKey, getString(R.string.difficulty_harder));
            if (difficultyLevel.equals(getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
            setTextLevel();
        }
    }

    //Reiniciar juego
    private void newGame() {
        winner = false;
        setTextLevel();
        mGame.clearBoard();
        mBoardView.invalidate();
    }

    //Salir Juego
    private void exitGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.quit_question)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, null);
        builder.show();
    }

    //Mostrar nivel
    public void setTextLevel() {
        mInfoTextView.setTextColor(defaultColors);
        String difficultyLevel = mPrefs.getString(difficultyLevelKey, getString(R.string.difficulty_harder));
        mInfoTextView.setText(getString(R.string.level).concat(" ").concat(difficultyLevel));
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getCellSize();
            int row = (int) event.getY() / mBoardView.getCellSize();
            int pos = row * 3 + col;

            if(!winner && mGame.setUserMove(pos)) {
                if(mSoundOn){
                    mHumanMediaPlayer.start();
                }
                if (mGame.checkForWinner() == 0) {
                    mGame.setComputerMove();
                    if(mSoundOn) {
                        mComputerMediaPlayer.start();
                    }
                    if (mGame.checkForWinner() == 3) {
                        winner = true;
                        mInfoTextView.setText(R.string.result_computer_wins);
                        mInfoTextView.setTextColor(Color.rgb(0, 0, 255));
                    }
                } else if (mGame.checkForWinner() == 2) {
                    winner = true;
                    String defaultMessage = getString(R.string.result_human_wins);
                    mInfoTextView.setText(mPrefs.getString(victoryMessageKey, defaultMessage));
                    mInfoTextView.setTextColor(Color.rgb(0, 255, 0));
                } else {
                    mInfoTextView.setText(R.string.result_tie);
                }
                mBoardView.invalidate();
            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };
}
