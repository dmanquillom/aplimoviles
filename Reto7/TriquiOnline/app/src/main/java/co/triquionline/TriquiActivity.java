package co.triquionline;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import co.triquionline.model.ModelGame;

public class TriquiActivity extends AppCompatActivity {

    private boolean mSoundOn;
    private BoardView mBoardView;
    private ModelGame mModelGame;
    private TextView tvInformation;
    private SharedPreferences mPrefs;
    private ColorStateList colorsDefault;
    MediaPlayer mHumanMediaPlayer, mComputerMediaPlayer;
    private String player, letter, gameID, playerX, playerO, turn = "", winner = "";

    public static final String soundKey = "sound";
    public static final String victoryMessageKey = "victory_message";
    public static final String difficultyLevelKey = "difficulty_level";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triqui);

        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setOnTouchListener(mTouchListener);

        tvInformation = (TextView) findViewById(R.id.information);
        colorsDefault = tvInformation.getTextColors();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean(soundKey, true);

        playGame();
    }

    //Nuevo juego
    private void playGame() {
        gameID = getIntent().getStringExtra(getString(R.string.kGameID));
        playerX = getIntent().getStringExtra(getString(R.string.kPlayerX));
        playerO = getIntent().getStringExtra(getString(R.string.kPlayerO));

        mModelGame = new ModelGame();
        if (gameID == null) {
            gameID = UUID.randomUUID().toString();
            player = playerX;
            letter = mBoardView.PLAYER_X;
            mModelGame.setGameID(gameID);
            mModelGame.setPlayerX(playerX);
            FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbToPlay)).child(gameID).setValue(mModelGame);
        } else {
            player = playerO;
            letter = mBoardView.PLAYER_O;
            turn = mBoardView.PLAYER_X;
            mModelGame.setGameID(gameID);
            mModelGame.setPlayerX(playerX);
            mModelGame.setPlayerO(playerO);
            mModelGame.setTurn(turn);
            FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbToPlay)).child(gameID).removeValue();
        }

        FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbGames)).child(gameID).setValue(mModelGame);
        FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbGames)).child(gameID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mModelGame = dataSnapshot.getValue(ModelGame.class);
                mBoardView.setBoard(mModelGame.getBoard());
                mBoardView.invalidate();

                if (mModelGame.getTurn() != null) {
                    turn = mModelGame.getTurn();
                }
                if (turn.isEmpty()) {
                    tvInformation.setText(getString(R.string.msWaitingPlayer));
                } else if (turn.compareTo(letter) == 0) {
                    tvInformation.setText(getString(R.string.msYourTurn));
                } else {
                    if (letter.compareTo(mBoardView.PLAYER_X) == 0) {
                        tvInformation.setText(getString(R.string.msTurnOf).concat(" ").concat(mModelGame.getPlayerO()));
                    } else {
                        tvInformation.setText(getString(R.string.msTurnOf).concat(" ").concat(playerX));
                    }
                }

                if (mModelGame.getWinner() != null) {
                    winner = mModelGame.getWinner();
                }
                if (!winner.isEmpty()) {
                    if (winner.compareTo(getString(R.string.msTie)) == 0) {
                        tvInformation.setText(getString(R.string.msTie));
                    } else if (winner.compareTo(player) == 0) {
                        if (letter.compareTo(mBoardView.PLAYER_X) == 0)
                            tvInformation.setTextColor(Color.rgb(0, 255, 0));
                        else
                            tvInformation.setTextColor(Color.rgb(0, 0, 255));
                        tvInformation.setText(getString(R.string.msWin));
                    } else {
                        if (letter.compareTo(mBoardView.PLAYER_X) == 0)
                            tvInformation.setTextColor(Color.rgb(0, 255, 0));
                        else
                            tvInformation.setTextColor(Color.rgb(0, 0, 255));
                        tvInformation.setText(getString(R.string.msLoss));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
        switch (item.getItemId()) {
            case R.id.new_game:
                //newGame();
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
        }
    }

    //Salir Juego
    private void exitGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.quit_question)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TriquiActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, null);
        builder.show();
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getCellSize();
            int row = (int) event.getY() / mBoardView.getCellSize();
            int pos = row * 3 + col;

            if (winner.isEmpty() && letter.compareTo(turn) == 0 && mBoardView.setBoardMove(pos, letter)) {
                //if (mSoundOn) {
                //mHumanMediaPlayer.start();
                //}
                if (mBoardView.checkForWinner() == 2) {
                    mModelGame.setWinner(player);
                } else if (mBoardView.checkForWinner() == 1) {
                    mModelGame.setWinner(getString(R.string.msTie));
                }

                if (letter.compareTo(mBoardView.PLAYER_X) == 0) {
                    turn = mBoardView.PLAYER_O;
                } else {
                    turn = mBoardView.PLAYER_X;
                }
                mModelGame.setTurn(turn);
                mModelGame.setBoard(mBoardView.getBoard());
                FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbGames)).child(gameID).setValue(mModelGame);
            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };
}
