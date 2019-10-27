package co.triquionline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.triquionline.model.ModelGame;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private ListView lvGame;
    private ArrayAdapter<ModelGame> arrayAdapterGame;
    private List<ModelGame> listModelGame = new ArrayList<ModelGame>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etName = (EditText) findViewById(R.id.etName);
        lvGame = (ListView) findViewById(R.id.lvGame);
        listGame();

        lvGame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = etName.getText().toString();
                if (!name.isEmpty()) {
                    Intent mIntent = new Intent(MainActivity.this, TriquiActivity.class);
                    ModelGame modelGame = (ModelGame) adapterView.getItemAtPosition(i);
                    mIntent.putExtra(getString(R.string.kGameID), modelGame.getGameID());
                    mIntent.putExtra(getString(R.string.kPlayerX), modelGame.getPlayerX());
                    mIntent.putExtra(getString(R.string.kPlayerO), name);
                    startActivity(mIntent);
                } else {
                    etName.setError(getString(R.string.erRequired));
                }
            }
        });
    }

    public void onClick(View v) {
        String name = etName.getText().toString();

        if (!name.isEmpty()) {
            Intent mIntent = new Intent(MainActivity.this, TriquiActivity.class);
            mIntent.putExtra(getString(R.string.kPlayerX), name);
            startActivity(mIntent);
        } else {
            etName.setError(getString(R.string.erRequired));
        }
    }

    private void listGame() {
        FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbToPlay))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listModelGame.clear();
                        for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                            ModelGame modelTeacher = objSnapshot.getValue(ModelGame.class);
                            listModelGame.add(modelTeacher);
                            arrayAdapterGame = new ArrayAdapter<ModelGame>(MainActivity.this, android.R.layout.simple_list_item_1, listModelGame);
                            lvGame.setAdapter(arrayAdapterGame);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
