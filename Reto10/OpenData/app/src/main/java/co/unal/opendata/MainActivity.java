package co.unal.opendata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CheckBox cbLike;
    private RecyclerView rvResultList;
    public List<OpenData> openDataList;
    public OpenDataAdapter openDataAdapter;
    private EditText etDepto, etMpio, etPuesto, etMesa;

    public static final String URL_PREFIX = "https://www.datos.gov.co/resource/9fg7-zvbs.json";
    public static final String PARAMETER_DPTO = "?$where=depto like %27%25";
    public static final String PARAMETER_SUFFIX = "%25%27";
    public static final String PARAMETER_AND = " and ";
    public static final String PARAMETER_MPIO = "mpio like %27%25";
    public static final String PARAMETER_PUESTO = "&puesto=";
    public static final String PARAMETER_PUESTO_LIKE = "puesto like %27%25";
    public static final String PARAMETER_MESA = "&mesa=";
    public static final String PARAMETER_ORDER = "&$order=votos ASC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDepto = (EditText) findViewById(R.id.etDepto);
        etMpio = (EditText) findViewById(R.id.etMpio);
        etPuesto = (EditText) findViewById(R.id.etPuesto);
        cbLike = (CheckBox) findViewById(R.id.cbLike);
        etMesa = (EditText) findViewById(R.id.etMesa);

        cbLike.setChecked(true);

        rvResultList = (RecyclerView) findViewById(R.id.rvResultList);
        rvResultList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvResultList.setLayoutManager(linearLayoutManager);

        openDataList = new ArrayList<>();
        openDataAdapter = new OpenDataAdapter(openDataList);
        rvResultList.setAdapter(openDataAdapter);
    }

    public void onClick(View view) {
        String dpto = etDepto.getText().toString().trim();
        String mpio = etMpio.getText().toString().trim();
        String puesto = etPuesto.getText().toString().trim();
        String mesa = etMesa.getText().toString();

        if (TextUtils.isEmpty(dpto)) {
            etDepto.setError(getString(R.string.required));
            return;
        } else {
            dpto = dpto.toUpperCase();
        }

        if (TextUtils.isEmpty(mpio)) {
            etMpio.setError(getString(R.string.required));
            return;
        } else {
            mpio = mpio.toUpperCase();
        }


        if (TextUtils.isEmpty(puesto)) {
            etPuesto.setError(getString(R.string.required));
            return;
        } else {
            puesto = puesto.toUpperCase();
        }

        if (TextUtils.isEmpty(mesa)) {
            mesa = "1";
        }

        openDataList.removeAll(openDataList);

        StringBuilder openDataUrl = new StringBuilder(URL_PREFIX);
        openDataUrl.append(PARAMETER_DPTO).append(dpto).append(PARAMETER_SUFFIX);
        openDataUrl.append(PARAMETER_AND).append(PARAMETER_MPIO).append(mpio).append(PARAMETER_SUFFIX);
        if (cbLike.isChecked()) {
            openDataUrl.append(PARAMETER_AND).append(PARAMETER_PUESTO_LIKE).append(puesto).append(PARAMETER_SUFFIX);
        } else {
            openDataUrl.append(PARAMETER_PUESTO).append(puesto);
        }
        openDataUrl.append(PARAMETER_MESA).append(mesa);
        openDataUrl.append(PARAMETER_ORDER);

        Object objAsyncTask[] = new Object[8];
        objAsyncTask[0] = this;
        objAsyncTask[1] = etDepto;
        objAsyncTask[2] = etMpio;
        objAsyncTask[3] = etPuesto;
        objAsyncTask[4] = etMesa;
        objAsyncTask[5] = openDataList;
        objAsyncTask[6] = openDataAdapter;
        objAsyncTask[7] = openDataUrl.toString().replaceAll(" ", "%20");
        new GetOpenData().execute(objAsyncTask);
    }
}
