package co.unal.sqliteempresas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import co.unal.sqliteempresas.dao.COMPANIES;
import co.unal.sqliteempresas.dao.COMPANIESDao;
import co.unal.sqliteempresas.dao.ConexionSQLiteHelper;

public class RetrieveActivity extends AppCompatActivity {

    EditText etRName;
    private String area;
    private COMPANIES companies;
    private ListView lvRCompnies;
    private COMPANIESDao companiesDao;
    private ConexionSQLiteHelper conn;
    private CheckBox cbRConsultory, cbRCustomDevelopment, cbRSoftwareFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve);

        etRName = (EditText) findViewById(R.id.etRName);
        cbRConsultory = (CheckBox) findViewById(R.id.cbRConsultory);
        cbRCustomDevelopment = (CheckBox) findViewById(R.id.cbRCustomDevelopment);
        cbRSoftwareFactory = (CheckBox) findViewById(R.id.cbRSoftwareFactory);
        lvRCompnies = (ListView) findViewById(R.id.lvRCompanies);

        companiesDao = new COMPANIESDao();
        conn = new ConexionSQLiteHelper(this, COMPANIESDao.DB_COMPANY, null, 1);
    }

    public void onClick(View view) {

        String name = etRName.getText().toString();

        if (validateForm(name)) {
            companies = new COMPANIES();
            companies.setName(name);
            companies.setArea(area);

            ArrayAdapter adapter = new ArrayAdapter<COMPANIES>(RetrieveActivity.this, android.R.layout.simple_list_item_1, companiesDao.searchMatching(conn, companies));
            lvRCompnies.setAdapter(adapter);

            lvRCompnies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(RetrieveActivity.this, UpdateDeleteActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.kCompany), (COMPANIES) adapterView.getItemAtPosition(i));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

    private boolean validateForm(String name) {
        area = "";
        if (cbRConsultory.isChecked()) {
            area = getString(R.string.cbConsultory) + getString(R.string.sSeparator);
        }
        if (cbRCustomDevelopment.isChecked()) {
            area += getString(R.string.cbCustomDevelopment) + getString(R.string.sSeparator);
        }
        if (cbRSoftwareFactory.isChecked()) {
            area += getString(R.string.cbSoftwareFactory) + getString(R.string.sSeparator);
        }

        if (TextUtils.isEmpty(area)) {
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(RetrieveActivity.this, getString(R.string.mCheckRetrieve), Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            area = area.substring(0, area.length() - getString(R.string.sSeparator).length());
        }

        return true;
    }
}
