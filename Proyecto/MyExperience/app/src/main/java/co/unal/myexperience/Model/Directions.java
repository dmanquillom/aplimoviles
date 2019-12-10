package co.unal.myexperience.Model;

import java.util.List;

public class Directions {

    private String duration;
    private String distance;
    private List<String> polylinePointsList;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public List<String> getPolylinePointsList() {
        return polylinePointsList;
    }

    public void setPolylinePointsList(List<String> polylinePointsList) {
        this.polylinePointsList = polylinePointsList;
    }
}
