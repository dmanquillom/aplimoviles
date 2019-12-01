package co.unal.opendata;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class GetOpenData extends AsyncTask<Object, String, String> {

    private Context context;
    private List<OpenData> openDataList;
    private OpenDataAdapter openDataAdapter;
    private EditText etDepto, etMpio, etPuesto, etMesa;
    private OpenDataParser openDataParser = new OpenDataParser();

    @Override
    protected String doInBackground(Object... objects) {
        context = (Context) objects[0];
        etDepto = (EditText) objects[1];
        etMpio = (EditText) objects[2];
        etPuesto = (EditText) objects[3];
        etMesa = (EditText) objects[4];
        openDataList = (List<OpenData>) objects[5];
        openDataAdapter = (OpenDataAdapter) objects[6];
        return openDataParser.getJsonString((String) objects[7]) ;
    }

    @Override
    protected void onPostExecute(String s) {
        openDataList.addAll(openDataParser.listOpenData(s));
        if(openDataList.size() > 0) {
            OpenData openData = openDataList.get(0);
            etDepto.setText(openData.getDepto());
            etMpio.setText(openData.getMpio());
            etPuesto.setText(openData.getPuesto());
            etMesa.setText(String.valueOf(openData.getMesa()));
        } else {
            Toast.makeText(context, R.string.msgNoResult, Toast.LENGTH_SHORT).show();
        }
        openDataAdapter.notifyDataSetChanged();
    }
}
