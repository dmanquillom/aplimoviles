package co.unal.sqliteempresas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import co.unal.sqliteempresas.dao.COMPANIES;
import co.unal.sqliteempresas.dao.COMPANIESDao;
import co.unal.sqliteempresas.dao.ConexionSQLiteHelper;

public class CreateActivity extends AppCompatActivity {

    private String area = "";
    private COMPANIES companies;
    private COMPANIESDao companiesDao;
    private ConexionSQLiteHelper conn;
    private EditText etCName, etCUrl, etCPhone, etCEmail, etCListProduct;
    private CheckBox cbCConsultory, cbCCustomDevelopment, cbCSoftwareFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        etCName = (EditText) findViewById(R.id.etCName);
        etCUrl = (EditText) findViewById(R.id.etCUrl);
        etCPhone = (EditText) findViewById(R.id.etCPhone);
        etCEmail = (EditText) findViewById(R.id.etCEmail);
        etCListProduct = (EditText) findViewById(R.id.etCListProduct);
        cbCConsultory = (CheckBox) findViewById(R.id.cbCConsultory);
        cbCCustomDevelopment = (CheckBox) findViewById(R.id.cbCCustomDevelopment);
        cbCSoftwareFactory = (CheckBox) findViewById(R.id.cbCSoftwareFactory);

        companiesDao = new COMPANIESDao();
        conn = new ConexionSQLiteHelper(this, COMPANIESDao.DB_COMPANY, null, 1);
    }

    public void onClick(View v) {

        String name = etCName.getText().toString();
        String url = etCUrl.getText().toString();
        String phone = etCPhone.getText().toString();
        String email = etCEmail.getText().toString();
        String listProduct = etCListProduct.getText().toString();

        if (validateForm(name, url, phone, email, listProduct)) {
            companies = new COMPANIES();
            companies.setName(name);
            companies.setUrl(url);
            companies.setPhone(phone);
            companies.setEmail(email);
            companies.setListProduct(listProduct);
            companies.setArea(area);
            if (companiesDao.create(conn, companies) > 0) {
                Toast.makeText(CreateActivity.this, getString(R.string.mCSuccessful), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateActivity.this, MainActivity.class));
            } else {
                Toast.makeText(CreateActivity.this, getString(R.string.mCFailure), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateForm(String name, String url, String phone, String email, String listProduct) {

        if (TextUtils.isEmpty(name)) {
            etCName.setError(getString(R.string.mRequired));
            return false;
        }

        if (TextUtils.isEmpty(url)) {
            etCUrl.setError(getString(R.string.mRequired));
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            etCPhone.setError(getString(R.string.mRequired));
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            etCEmail.setError(getString(R.string.mRequired));
            return false;
        }

        if (TextUtils.isEmpty(listProduct)) {
            etCListProduct.setError(getString(R.string.mRequired));
            return false;
        }

        if(cbCConsultory.isChecked()) {
            area = getString(R.string.cbConsultory) + getString(R.string.sSeparator);
        }
        if(cbCCustomDevelopment.isChecked()) {
            area +=  getString(R.string.cbCustomDevelopment) + getString(R.string.sSeparator);
        }
        if(cbCSoftwareFactory.isChecked()){
            area += getString(R.string.cbSoftwareFactory) + getString(R.string.sSeparator);
        }

        if(TextUtils.isEmpty(area)){
            Toast.makeText(CreateActivity.this, getString(R.string.mCheckArea), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            area = area.substring(0, area.length() - getString(R.string.sSeparator).length());
        }

        return true;
    }
}
