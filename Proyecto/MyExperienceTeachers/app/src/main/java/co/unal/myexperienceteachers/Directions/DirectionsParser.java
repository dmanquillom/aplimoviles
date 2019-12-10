package co.unal.myexperienceteachers.Directions;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import co.unal.myexperienceteachers.Model.Directions;
import co.unal.myexperienceteachers.R;

public class DirectionsParser {

    private Context context;

    public DirectionsParser(Context context) {
        this.context = context;
    }

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

    public Directions getDirections(String jsonOpenDataString) {
        Directions directions = new Directions();

        try {
            JSONObject jsonObject = new JSONObject(jsonOpenDataString);
            jsonObject = jsonObject.getJSONArray(context.getString(R.string.json_routes)).getJSONObject(0).getJSONArray(context.getString(R.string.json_legs)).getJSONObject(0);
            directions.setDuration(jsonObject.getJSONObject(context.getString(R.string.json_duration)).getString(context.getString(R.string.json_text)));
            directions.setDistance(jsonObject.getJSONObject(context.getString(R.string.json_distance)).getString(context.getString(R.string.json_text)));

            List<String> polylinePointsList = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray(context.getString(R.string.json_steps));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectSteps = jsonArray.getJSONObject(i);
                polylinePointsList.add(jsonObjectSteps.getJSONObject(context.getString(R.string.json_polyline)).getString(context.getString(R.string.json_points)));
            }
            directions.setPolylinePointsList(polylinePointsList);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return directions;
    }
}
