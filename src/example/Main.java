package example;

// apache commons import
import org.apache.commons.io.FilenameUtils;

// standard library imports
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.function.*;

public class Main {

    public static void main(String[] args) throws IOException {
        // Please note a large section of code has been remove due to request
        // from the SDTC team at RGU and HITRANS (Highland and Island Transport).

        // ArrayList containing all the journeys from the analysis
        ArrayList<Journey> journeys = new ArrayList<>();

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
