package example;

import rgu.transport.geospatial.GeoLocation;

import java.time.Duration;

public class Journey {

    private GeoLocation location;
    private Duration duration;

    public Journey(GeoLocation location, Duration duration){
        this.location = location;
        this.duration = duration;
    }

    public double getDuration() {
        return duration.toMinutes();
    }

    public double getLon(){
        return this.location.x();
    }

    public double getLat(){
        return this.location.y();
    }

}
