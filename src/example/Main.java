package example;

// apache commons import
import org.apache.commons.io.FilenameUtils;

// rgu-algorithms imports
import rgu.algorithms.*;
import rgu.algorithms.collections.*;

// rgu-transport imports
import rgu.transport.geospatial.*;
import rgu.transport.geospatial.multimodal.*;
import rgu.transport.util.*;

// standard library imports
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.function.*;

public class Main {

    public static void main(String[] args) throws IOException {
        // load the pre-constructed sample network (only needs to be done once)
        File file = new File("data/all-10m.transit");
        TransitNetwork network = null;
        try {
            System.out.println("loading network . . .");
            network = DataIO.readNetwork(file);
        } catch (IOException ex) {
            System.err.println("Unable to load data.");
            ex.printStackTrace();
            return;
        }


        // this is some random point somewhere near Inverness
        // we can't search from here thought, because it's not on the network
        GeoLocation examplePoint = GeoLocation.ofRounded(57.14981461633668, -2.094482424967341);
        System.out.println("example point: " + examplePoint);

        // setup used to find the nearest point on the network (only needs to be done once)
        NearestFinder<GeoLocation> finder = GeoLocation.nearestHaversine(network.locations());

        // find a point near to out example point, but actually on the network
        // we should find a point quite close to our example point
        GeoLocation startPoint = finder.nearest(examplePoint);
        System.out.println("start point: " + startPoint);

        // create a Dijkstra instance for inbound search (only needs to be done once)
        //SpatialTemporalReachabilityAlgorithm<Duration> dijkstra = Dijkstra.ofDuration();
        SpatialTemporalReachabilityAlgorithm<Duration> dijkstra = Dijkstra.ofNegativeDuration();
        // (* change to ofNegativeDuration() for inbound)


        // the parameters of our search (* becomes end time for inbound)
        LocalTime startTime = LocalTime.of(9, 0, 0);
        //Duration maxTravelTime = Duration.ofHours(1);
        Duration maxTravelTime = Duration.ofHours(-1);
        // (* change to -3 for inbound)


        // this is setup for passing our specific search parameters to the algorithm
        //Graph<JourneyState, Duration> graph = network.spatialTemporalGraph();
        Graph<JourneyState, Duration> graph = network.spatialTemporalGraph().reverse();
        // (* add .reverse() for inbound)

        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), startTime);
        JourneyState startState = JourneyState.of(startPoint, dateTime, false, true, "start");
        Function<JourneyState, JourneyPosition> toSpatial = state -> state.position();
        BiFunction<JourneyPosition, Duration, JourneyState> toSpatialTemporal
                = (position, cost) -> JourneyState.of(position, dateTime, cost);

        // ArrayList containing all the journeys from the analysis
        ArrayList<Journey> journeys = new ArrayList<>();

        // this runs the algorithm, printing the time taken to reach each reachable point
        // each point is printed as soon as it is discovered
        try {
            System.out.println("searching . . .");
            dijkstra.reachable(graph, startState, maxTravelTime, (state, duration) -> {

                // this is the callback code to process each value
                GeoLocation location = state.location();
                System.out.println(location + "\t" + duration);
                journeys.add(new Journey(location,duration));

            }, toSpatial, toSpatialTemporal);
            System.out.println("search complete.");
        } catch (InterruptedException ex) {
            System.err.println("Dijkstra thread was cancelled by an interrupt, aborting.");
            return;
        }

        // Load DataZones
        ArrayList<DataZone> zones = loadDataZoneMetrics();

        // Determine if inbound or inbound analysis for writing files
        boolean analysis = false;
        if(maxTravelTime.isNegative()){
            analysis = true;
        } else {
            analysis = false;
        }

        // double array containing start point latitude and longitude coordinates for writing files
        double[] sps = new double[]{startPoint.y(),startPoint.x()};

        PopulationCalculation inbound = new PopulationCalculation(zones,journeys,maxTravelTime.toHoursPart(),30);
        inbound.print();
        ExportFiles ef = new ExportFiles(inbound.getMap(),inbound.getMap2020(),inbound.getMap2016(),
                                            inbound.getGeoAcessMap2020(), inbound.getGeoAcessMap2016(),
                                            inbound.getMap2020Rank(),analysis,sps,inbound.getList(),
                                            inbound.getCheckJourneys());
    }

    // Static method to convert ArrayList to array
    public static double[] arrayList2Array(ArrayList<Double> arrayList){
        double[] array = new double[arrayList.size()];
        for(int i = 0; i < arrayList.size(); i++){
            array[i] = arrayList.get(i);
        }
        return array;
    }

    // Static method to load in SIMD DataZones

    public static ArrayList<DataZone> loadDataZones(){
        ArrayList<DataZone> zones = new ArrayList<>();
        File dir = new File("/Users/callumross/Documents/MSc Project/Project/Data/Working Data");

        for (File f : dir.listFiles()) {
            String name = f.getName();

            // Two ArrayLists to store the Data
            ArrayList<Double> longitude = new ArrayList<>();
            ArrayList<Double> latitude = new ArrayList<>();

            if (f.isFile() && FilenameUtils.isExtension(name,"csv")) {
                BufferedReader inputStream;
                String line;
                try {
                    inputStream = new BufferedReader(new FileReader(f));

                    // Skips header line
                    String headerline = inputStream.readLine();

                    while ((line = inputStream.readLine()) != null) {
                        String[] parts = line.split(",");

                        latitude.add(Double.parseDouble(parts[0]));
                        longitude.add(Double.parseDouble(parts[1]));
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
            // Checks lists arent empty and Converts ArrayList to Array
            if(!longitude.isEmpty() && !latitude.isEmpty()){
                double[] lats = arrayList2Array(latitude);
                double[] lons = arrayList2Array(longitude);
                zones.add(new DataZone(lats,lons,name));
            }
        }
        return zones;
    }

    // Static method to add population to SIMD DataZones
    public static ArrayList<DataZone> loadDataZoneMetrics(){
        ArrayList<DataZone> zones = loadDataZones();
        // Read in Populations
        File file3 = new File("/Users/callumross/Documents/MSc Project/Project/Geo Access Domain/" +
                "SIMD_Data_Zone_Metrics.csv");

        if (file3.isFile()) {
            BufferedReader inputStream;
            String line;
            try {
                inputStream = new BufferedReader(new FileReader(file3));
                // Skips header line
                String headerline = inputStream.readLine();

                while ((line = inputStream.readLine()) != null) {
                    String[] parts = line.split(",");
                    String name = parts[0].replace("\"","");

                    for(DataZone dz: zones){
                        if(dz.getName().equals(name)){
                            dz.setPopulation2020(Integer.valueOf(parts[1]));
                            dz.setPopulation2016(Integer.valueOf(parts[2]));
                            dz.setGeoAccessDomain2020(Integer.valueOf(parts[3]));
                            dz.setGeoAccessDomain2016(Integer.valueOf(parts[4]));
                            dz.setGeoAccess2020Rank(Integer.valueOf(parts[7]));
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        return zones;
    }
}
