package co.unal.sqliteempresas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import co.unal.sqliteempresas.dao.COMPANIES;
import co.unal.sqliteempresas.dao.COMPANIESDao;
import co.unal.sqliteempresas.dao.ConexionSQLiteHelper;

public class MainActivity extends AppCompatActivity {

    private ListView lvMCompnies;
    private COMPANIESDao companiesDao;
    private ConexionSQLiteHelper conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvMCompnies = (ListView) findViewById(R.id.lvMCompanies);

        companiesDao = new COMPANIESDao();
        conn = new ConexionSQLiteHelper(MainActivity.this, COMPANIESDao.DB_COMPANY, null, 1);
        ArrayAdapter adapter = new ArrayAdapter<COMPANIES>(MainActivity.this, android.R.layout.simple_list_item_1, companiesDao.loadAll(conn));
        lvMCompnies.setAdapter(adapter);

        lvMCompnies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, UpdateDeleteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.kCompany), (COMPANIES) adapterView.getItemAtPosition(i));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void onClick(View view){

        Intent intent = null;
        switch (view.getId()) {
            case R.id.bCreate:
                intent = new Intent(MainActivity.this, CreateActivity.class);
                break;
            case R.id.bRetrieve:
                intent = new Intent(MainActivity.this, RetrieveActivity.class);
                break;
        }
        startActivity(intent);
    }
}
