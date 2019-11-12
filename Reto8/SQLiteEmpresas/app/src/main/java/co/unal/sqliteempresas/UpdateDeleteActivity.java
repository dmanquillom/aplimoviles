package co.unal.sqliteempresas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import co.unal.sqliteempresas.dao.COMPANIES;
import co.unal.sqliteempresas.dao.COMPANIESDao;
import co.unal.sqliteempresas.dao.ConexionSQLiteHelper;

public class UpdateDeleteActivity extends AppCompatActivity {

    private String area;
    private COMPANIES companies;
    private COMPANIESDao companiesDao;
    private ConexionSQLiteHelper conn;
    private EditText etUDName, etUDUrl, etUDPhone, etUDEmail, etUDListProduct;
    private CheckBox cbUDConsultory, cbUDCustomDevelopment, cbUDSoftwareFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete);

        etUDName = (EditText) findViewById(R.id.etUDName);
        etUDUrl = (EditText) findViewById(R.id.etUDUrl);
        etUDPhone = (EditText) findViewById(R.id.etUDPhone);
        etUDEmail = (EditText) findViewById(R.id.etUDEmail);
        etUDListProduct = (EditText) findViewById(R.id.etUDListProduct);
        cbUDConsultory = (CheckBox) findViewById(R.id.cbUDConsultory);
        cbUDCustomDevelopment = (CheckBox) findViewById(R.id.cbUDCustomDevelopment);
        cbUDSoftwareFactory = (CheckBox) findViewById(R.id.cbUDSoftwareFactory);

        Bundle bundle = getIntent().getExtras();
        companies = (COMPANIES) bundle.getSerializable(getString(R.string.kCompany));
        etUDName.setText(companies.getName());
        etUDUrl.setText(companies.getUrl());
        etUDPhone.setText(companies.getPhone());
        etUDEmail.setText(companies.getEmail());
        etUDListProduct.setText(companies.getListProduct());

        area = companies.getArea();
        if(area.contains(getString(R.string.cbConsultory))){
            cbUDConsultory.setChecked(true);
        }
        if(area.contains(getString(R.string.cbCustomDevelopment))){
            cbUDCustomDevelopment.setChecked(true);
        }
        if(area.contains(getString(R.string.cbSoftwareFactory))){
            cbUDSoftwareFactory.setChecked(true);
        }
        area = "";

        companiesDao = new COMPANIESDao();
        conn = new ConexionSQLiteHelper(UpdateDeleteActivity.this, COMPANIESDao.DB_COMPANY, null, 1);
    }

    public void onClick(View view) {
        AlertDialog.Builder builder;
        switch (view.getId()) {
            case R.id.bUpdate:
                String name = etUDName.getText().toString();
                String url = etUDUrl.getText().toString();
                String phone = etUDPhone.getText().toString();
                String email = etUDEmail.getText().toString();
                String listProduct = etUDListProduct.getText().toString();

                if (validateForm(name, url, phone, email, listProduct)) {
                    companies.setName(name);
                    companies.setUrl(url);
                    companies.setPhone(phone);
                    companies.setEmail(email);
                    companies.setListProduct(listProduct);
                    companies.setArea(area);

                    builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.mUQuestion)
                            .setCancelable(false)
                            .setPositiveButton(R.string.dYes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (companiesDao.save(conn, companies) > 0) {
                                        Toast.makeText(UpdateDeleteActivity.this, getString(R.string.mUSuccessful), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(UpdateDeleteActivity.this, MainActivity.class));
                                    } else {
                                        Toast.makeText(UpdateDeleteActivity.this, getString(R.string.mUFailure), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            })
                            .setNegativeButton(R.string.dNo, null);
                    builder.show();
                }
                break;

            case R.id.bDelete:
                builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.mDQuestion)
                        .setCancelable(false)
                        .setPositiveButton(R.string.dYes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (companiesDao.delete(conn, companies) > 0) {
                                    Toast.makeText(UpdateDeleteActivity.this, getString(R.string.mDSuccessful), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(UpdateDeleteActivity.this, MainActivity.class));
                                } else {
                                    Toast.makeText(UpdateDeleteActivity.this, getString(R.string.mDFailure), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.dNo, null);
                builder.show();
                break;
        }
    }

    private boolean validateForm(String name, String url, String phone, String email, String listProduct) {
        boolean valid = true;

        if (TextUtils.isEmpty(name)) {
            etUDName.setError(getString(R.string.mRequired));
            return false;
        }

        if (TextUtils.isEmpty(url)) {
            etUDUrl.setError(getString(R.string.mRequired));
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            etUDPhone.setError(getString(R.string.mRequired));
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            etUDEmail.setError(getString(R.string.mRequired));
            return false;
        }

        if (TextUtils.isEmpty(listProduct)) {
            etUDListProduct.setError(getString(R.string.mRequired));
            return false;
        }

        if(cbUDConsultory.isChecked()) {
            area = getString(R.string.cbConsultory) + getString(R.string.sSeparator);
        }
        if(cbUDCustomDevelopment.isChecked()) {
            area +=  getString(R.string.cbCustomDevelopment) + getString(R.string.sSeparator);
        }
        if(cbUDSoftwareFactory.isChecked()){
            area += getString(R.string.cbSoftwareFactory) + getString(R.string.sSeparator);
        }

        if(TextUtils.isEmpty(area)){
            Toast.makeText(UpdateDeleteActivity.this, getString(R.string.mCheckArea), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            area = area.substring(0, area.length() - getString(R.string.sSeparator).length());
        }

        return true;
    }
}
