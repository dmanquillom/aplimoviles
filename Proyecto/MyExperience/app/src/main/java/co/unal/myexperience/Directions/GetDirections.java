package co.unal.myexperience.Directions;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import co.unal.myexperience.Model.Directions;
import co.unal.myexperience.R;

public class GetDirections extends AsyncTask<Object, String, String> {

    private GoogleMap mMap;
    private Context context;
    private TextView distance;
    private DirectionsParser directionsParser;

    @Override
    protected String doInBackground(Object... objects) {
        context = (Context) objects[0];
        mMap = (GoogleMap) objects[1];
        distance = (TextView) objects[2];
        directionsParser = new DirectionsParser(context);
        return directionsParser.getJsonString((String) objects[3]) ;
    }

    @Override
    protected void onPostExecute(String s) {
        Directions directions = directionsParser.getDirections(s);
        distance.setText(directions.getDuration().concat(" ").concat(context.getString(R.string.distance_separator)).concat(" ").concat(directions.getDistance()));
        for(String point : directions.getPolylinePointsList()){
            PolylineOptions options = new PolylineOptions();
            options.color(context.getColor(R.color.colorPrimaryDark));
            options.width(10);
            options.addAll(PolyUtil.decode(point));
            mMap.addPolyline(options);
        }
    }
}
