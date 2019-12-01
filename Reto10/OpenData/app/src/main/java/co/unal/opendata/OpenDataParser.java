package co.unal.opendata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OpenDataParser {

    public static final String JSON_KEY_DEPTO = "depto";
    public static final String JSON_KEY_MPIO = "mpio";
    public static final String JSON_KEY_PUESTO = "puesto";
    public static final String JSON_KEY_MESA = "mesa";
    public static final String JSON_KEY_PARTIDO = "partido";
    public static final String JSON_KEY_CANDIDATO = "candidato";
    public static final String JSON_KEY_VOTOS = "votos";


    public String getJsonString(String url) {
        String jsonString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL nearbyPlacesURL = new URL(url);
            httpURLConnection = (HttpURLConnection) nearbyPlacesURL.openConnection();
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            jsonString = stringBuffer.toString();

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonString;
    }

    public List<OpenData> listOpenData(String jsonOpenDataString) {
        List<OpenData> openDataList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonOpenDataString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectOpenData = jsonArray.getJSONObject(i);

                OpenData openData = new OpenData();
                openData.setDepto(jsonObjectOpenData.getString((JSON_KEY_DEPTO)));
                openData.setMpio(jsonObjectOpenData.getString(JSON_KEY_MPIO));
                openData.setPuesto(jsonObjectOpenData.getString(JSON_KEY_PUESTO));
                openData.setMesa(jsonObjectOpenData.getInt(JSON_KEY_MESA));
                try {
                    openData.setPartido(jsonObjectOpenData.getString(JSON_KEY_PARTIDO));
                } catch (JSONException ex){
                    openData.setPartido("");
                }
                openData.setCandidato(jsonObjectOpenData.getString(JSON_KEY_CANDIDATO));
                openData.setVotos(jsonObjectOpenData.getInt(JSON_KEY_VOTOS));
                openDataList.add(openData);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return openDataList;
    }
}
