package example;

// Imports
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExportFiles {

    private HashMap<String, Double> durationsMap;
    private HashMap<Integer,Integer> pop2020;
    private HashMap<Integer,Integer> pop2016;
    private HashMap<String,Integer> geo2020;
    private HashMap<String,Integer> geo2016;
    private HashMap<String,Integer> geo2020Rank;

    private Timestamp timeStamp;
    private File fileDestination;
    private double[] startingPoint;
    private ArrayList<double[]> allLocations;
    private ArrayList<double[]> locations;


    /**
     * Writes files needed for visualisations in QGIS to chosen directory 
     * @param durationsMap HashMap containing SIMD data zones and travel times
     * @param pop2020 HashMap containing travel time intervals and 2020 SIMD population
     * @param pop2016 HashMap containing travel time intervals and 2016 SIMD population
     * @param geo2020 HashMap containing SIMD data zone and 2020 SIMD geographical access score
     * @param geo2016 HashMap containing SIMD data zone and 2016 SIMD geographical access score
     * @param geo2020Rank HashMap containing SIMD data zone and 2020 SIMD geographical access rank
     * @param inOut Boolean variable determining if inbound or outbound analysis has been performed
     * @param startingPoint double array containing coordinates of starting point
     * @param allLocations ArrayList containing coordinates of all locations produced by reachability analysis
     * @param locations ArrayList containing coordinates of location used in population calculations
     */
    public ExportFiles(HashMap<String, Double> durationsMap,HashMap<Integer,Integer> pop2020,
                       HashMap<Integer, Integer> pop2016, HashMap<String,Integer> geo2020 ,
                       HashMap<String,Integer> geo2016, HashMap<String,Integer> geo2020Rank,
                       boolean inOut, double[] startingPoint, ArrayList<double[]> allLocations,
                       ArrayList<double[]> locations){

        this.timeStamp = new Timestamp(System.currentTimeMillis());
        this.durationsMap = durationsMap;
        this.startingPoint = startingPoint;
        this.allLocations = allLocations;
        this.locations = locations;
        this.pop2020 = pop2020;
        this.pop2016 = pop2016;
        this.geo2020 = geo2020;
        this.geo2016 = geo2016;
        this.geo2020Rank = geo2020Rank;

        analysis(inOut);
        exportDurations();
        exportSP();
        exportAllLocations();
        exportLocations();
        export2020Population();
        export2016Population();
        export2020GeoAccessDomain();
        export2016GeoAccessDomain();
        export2020GeoAccessDomainRank();
    }

    // Method determines folder directory and creates new folder with current time Timestamp
    private void analysis(boolean inOut){
        if(inOut){
           fileDestination = new File("/Users/callumross/Documents/MSc Project/Project/Data/Census Module Output" +
                    "/Inbound Analysis/" + timeStamp);
        } else {
            fileDestination = new File("/Users/callumross/Documents/MSc Project/Project/Data/Census Module Output" +
                    "/Outbound Analysis/" + timeStamp);
        }
        fileDestination.mkdir();
    }

    // Method writes file to folder containing SIMD DataZone and corresponding travel time from analysis
    private void exportDurations(){
        String fileName = " Analysis_DataZones" + ".csv";
        try (PrintWriter writer = new PrintWriter(new File(fileDestination
                + "/" + fileName))) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"DataZone\"");
            sb.append(',');
            sb.append("\"Duration\"");
            sb.append('\n');
            for(Map.Entry<String, Double> entry : durationsMap.entrySet()){
                sb.append(entry.getKey());
                sb.append(',');
                sb.append(entry.getValue());
                sb.append('\n');
            }
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method writes file to folder containing starting point from analysis
    private void exportSP(){
        String fileName = " Analysis_StartingPoint" + ".csv";
        try (PrintWriter writer = new PrintWriter(new File(fileDestination + "/" + fileName))) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"Staring Point Latitude\"");
            sb.append(",");
            sb.append("\"Staring Point Longitude\"");
            sb.append('\n');
            sb.append(this.startingPoint[0]);
            sb.append(',');
            sb.append(this.startingPoint[1]);
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method write file to folder containing coordinates of all allLocations from analysis
    private void exportAllLocations(){
        String fileName = " Analysis_All_Locations" + ".csv";
        try (PrintWriter writer = new PrintWriter(new File(fileDestination + "/" + fileName))) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"Location Latitude\"");
            sb.append(",");
            sb.append("\"Location Longitude\"");
            sb.append('\n');
            for(double[] d: allLocations){
                sb.append(d[0]);
                sb.append(",");
                sb.append(d[1]);
                sb.append('\n');
            }
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // Writes file containing locations used in population calculation
    private void exportLocations(){
        String fileName = " Analysis_Locations" + ".csv";
        try (PrintWriter writer = new PrintWriter(new File(fileDestination + "/" + fileName))) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"Location Latitude\"");
            sb.append(",");
            sb.append("\"Location Longitude\"");
            sb.append('\n');
            for(double[] d: locations){
                sb.append(d[0]);
                sb.append(",");
                sb.append(d[1]);
                sb.append('\n');
            }
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method writes 2020 population for each time interval
    private void export2020Population(){
        String fileName = " Analysis_2020Population" + ".csv";
        try (PrintWriter writer = new PrintWriter(new File(fileDestination + "/" + fileName))) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"Travel Interval\"");
            sb.append(",");
            sb.append("\"2020 Population\"");
            sb.append('\n');
            for(Map.Entry<Integer, Integer> entry : this.pop2020.entrySet()){
                sb.append(entry.getKey());
                sb.append(',');
                sb.append(entry.getValue());
                sb.append('\n');
            }
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method writes 2016 population for each time interval
    private void export2016Population(){
        String fileName = " Analysis_2016Population" + ".csv";
        try (PrintWriter writer = new PrintWriter(new File(fileDestination + "/" + fileName))) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"Travel Interval\"");
            sb.append(",");
            sb.append("\"2016 Population\"");
            sb.append('\n');
            for(Map.Entry<Integer, Integer> entry : this.pop2016.entrySet()){
                sb.append(entry.getKey());
                sb.append(',');
                sb.append(entry.getValue());
                sb.append('\n');
            }
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method writes 2016 population for each time interval
    private void export2020GeoAccessDomain(){
        String fileName = " Analysis_2020GeoAccess" + ".csv";
        try (PrintWriter writer = new PrintWriter(new File(fileDestination + "/" + fileName))) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"Data_Zone\"");
            sb.append(",");
            sb.append("\"2020_Geographic_Access_Domain\"");
            sb.append('\n');
            for(Map.Entry<String, Integer> entry : this.geo2020.entrySet()){
                sb.append(entry.getKey());
                sb.append(',');
                sb.append(entry.getValue());
                sb.append('\n');
            }
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method writes 2016 population for each time interval
    private void export2016GeoAccessDomain(){
        String fileName = " Analysis_2016GeoAccess" + ".csv";
        try (PrintWriter writer = new PrintWriter(new File(fileDestination + "/" + fileName))) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"Data_Zone\"");
            sb.append(",");
            sb.append("\"2016_Geographic_Access_Domain\"");
            sb.append('\n');
            for(Map.Entry<String, Integer> entry : this.geo2016.entrySet()){
                sb.append(entry.getKey());
                sb.append(',');
                sb.append(entry.getValue());
                sb.append('\n');
            }
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method writes 2016 population for each time interval
    private void export2020GeoAccessDomainRank(){
        String fileName = " Analysis_2020GeoAccess_Domain_Rank" + ".csv";
        try (PrintWriter writer = new PrintWriter(new File(fileDestination + "/" + fileName))) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"Data_Zone\"");
            sb.append(",");
            sb.append("\"2020_GeoAccess_Domain_Rank\"");
            sb.append('\n');
            for(Map.Entry<String, Integer> entry : this.geo2020Rank.entrySet()){
                sb.append(entry.getKey());
                sb.append(',');
                sb.append(entry.getValue());
                sb.append('\n');
            }
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
