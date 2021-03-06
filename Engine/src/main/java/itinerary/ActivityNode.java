package itinerary;

import com.google.maps.model.TravelMode;
import generator.GUID;
import template.Attraction;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ActivityNode {

    private final boolean showTransportationIcon; // For free time only
    private Types type;
    private String startTime;
    private String endTime;
    private Attraction attraction;
    private String uniqueKey;
    //    private Map<Types, Long> transDuration;
    private TransportationObject transportation;

    public ActivityNode(LocalTime startTime, LocalTime endTime, Types type, Attraction attraction) {
        this.startTime = startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        this.endTime = endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        this.type = type;
        this.attraction = attraction;
        this.uniqueKey = GUID.generate();
        this.showTransportationIcon = false;

        if (type != Types.ATTRACTION && type != Types.FREE_TIME) {
            //            this.transDuration = new HashMap<>();
        }
    }

    public static Types googleTravelToActivityType(TravelMode travelMode) {
        if (travelMode.equals(TravelMode.WALKING)) {
            return Types.WALK;
        } else if (travelMode.equals(TravelMode.DRIVING)) {
            return Types.CAR;
        } else if (travelMode.equals(TravelMode.TRANSIT)) {
            return Types.TRANSIT;
        } else {
            return Types.BICYCLE;
        }
    }

    public boolean isShowTransportationIcon() {
        return showTransportationIcon;
    }

    public TransportationObject getTransportation() {
        return transportation;
    }

    public void setTransportation(Types type, HashMap<Types, Long> durations, double srcLng, double srcLat) {
        this.transportation = new TransportationObject(type, durations, srcLng, srcLat);
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }

    public Types getType() {
        return this.type;
    }

    public void setType(Types type) {
        this.type = type;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public enum Types {
        FREE_TIME, CAR, BICYCLE, WALK, TRANSIT, ATTRACTION, FLIGHT
    }

    private static class TransportationObject {
        private final Types type;
        private Map<Types, Long> data;
        private sourceData sourceData;

        public TransportationObject(Types type, Map<Types, Long> data, double srcLng, double srcLat) {
            this.type = type;
            this.data = data;
            this.sourceData = new sourceData(srcLng, srcLat);
        }

        public Types getType() {
            return type;
        }

        public Map<Types, Long> getData() {
            return data;
        }

        public void setData(Map<Types, Long> data) {
            this.data = data;
        }

        public TransportationObject.sourceData getSourceData() {
            return sourceData;
        }

        public void setSourceData(TransportationObject.sourceData sourceData) {
            this.sourceData = sourceData;
        }

        private static class sourceData {
            private final double srcLng;
            private final double srcLat;

            public sourceData(double srcLng, double srcLat) {
                this.srcLng = srcLng;
                this.srcLat = srcLat;
            }

            public double getSrcLng() {
                return srcLng;
            }

            public double getSrcLat() {
                return srcLat;
            }
        }
    }
}
