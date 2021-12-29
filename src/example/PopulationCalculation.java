package example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Calculates reachable for reachability analysis performed.
 */
public class PopulationCalculation {
    private ArrayList<DataZone> dataZones;
    private ArrayList<Journey> journeys;
    private ArrayList<Integer> travelTimeBins = new ArrayList<>();
    private HashMap<Integer, Integer> map2020 = new HashMap<>();
    private HashMap<Integer, Integer> map2016 = new HashMap<>();
    private int bin;
    private int endNum;
    private int calculationConstant;
    private ArrayList <Journey> checkJourneys = new ArrayList<>();
    private ArrayList<String> checkDataZones = new ArrayList<>();

    /**
     * Creates Population class object.
     * @param dataZones - ArrayList containing all SIMD DataZones
     * @param journeys - ArrayList containing all Journeys created from performing inbound/outbound analysis.
     * @param maxTravelTime - maxTravelTime from inbound/outbound analysis performed.
     * @param bin - Desired time increment in minutes for travel intervals.
     */
    public PopulationCalculation(ArrayList<DataZone> dataZones, ArrayList<Journey> journeys,
                                 int maxTravelTime, int bin){
        this.dataZones = dataZones;
        this.journeys = journeys;
        this.bin = bin;
        setCalculation(maxTravelTime);
        this.endNum = maxTravelTime * (calculationConstant*60);
    }

    /**
     * Prints the population for each Journey duration interval along with the total population for the journey.
     */
    public void print(){
        setBins();
        printPopulations(map2020,"2020 Population Calculation");
        System.out.println();
        printPopulations(map2016,"2016 Population Calculation");
    }

    // Determines if inbound or outbound reachability analysis has been performed.
    private void setCalculation(int maxTravelTime){
        if(maxTravelTime < 0){
            this.calculationConstant = -1;
        } else {
            this.calculationConstant = 1;
        }
    }

    // Prints the contents of the passed HashMap
    private void printPopulations(HashMap<Integer, Integer> map, String yearPopulation){
        int totalPopulation = 0;
        TreeMap<Integer, Integer> sorted = new TreeMap<>();
        sorted.putAll(map);
        System.out.println(yearPopulation);
        for (Map.Entry<Integer, Integer> entry : sorted.entrySet()) {
            totalPopulation += entry.getValue();
            System.out.println("In " + entry.getKey() +
                    " minutes or under " + entry.getValue() +
                    " can make the journey.");
        }
        System.out.println("A total of " + totalPopulation +
                " can make the journey.");
    }

    // Sets the travel time interval bins for HashMaps and ArrayList
    private void setBins(){
        int startNum = 0;
        while (startNum < endNum) {
            startNum += this.bin;
            map2020.put(startNum, 0);
            map2016.put(startNum,0);
            travelTimeBins.add(startNum);
        }
        calculatePopulations();
    }

    // Calculates population for each data zone
    private void calculatePopulations() {
        for (DataZone dataZone : dataZones) {
            for (Journey journey : journeys) {
                checkZone(dataZone,journey);
            }
        }
    }

    // Determines if journey is contained within data zone
    private void checkZone(DataZone dataZone,Journey journey){
        if (dataZone.contains(journey.getLat(),journey.getLon())
                && !checkDataZones.contains(dataZone.getName())) {
            checkDataZones.add(dataZone.getName());
            checkJourney(dataZone,journey);
        }
    }

    // Determines which travel time interval bin the journey belongs to
    private void checkJourney(DataZone dataZone, Journey journey){
        for(Integer bin: travelTimeBins){
            if ((journey.getDuration()*calculationConstant) <= bin
                    && !checkJourneys.contains(journey)) {
                checkJourneys.add(journey);
                map2020.put(bin, map2020.get(bin) +
                        dataZone.getPopulation2020());
                map2016.put(bin, map2016.get(bin) +
                        dataZone.getPopulation2016());
            }
        }
    }

    /**
     * Returns HashMap for writing file containing SIMD DataZones and corresponding
     * Journey time from analysis.
     * @return HashMap, key = DataZone, value = travel time interval
     */
    public HashMap<String,Double> getMap(){
        HashMap<String,Double> map = new HashMap<>();
        ArrayList<DataZone> theseZones = getDataZoneList();
        for(DataZone dataZone: theseZones){
            for(Journey journey: checkJourneys){
                if(dataZone.contains(journey.getLat(),journey.getLon())){
                    if(journey.getDuration() < 0){
                        map.put(dataZone.getName(),journey.getDuration() * -1);
                    } else {
                        map.put(dataZone.getName(),journey.getDuration());
                    }
                }
            }
        }
        return map;
    }

    /**
     * Returns HashMap containing analysis data zones and corresponding 2020 geographical access score
     * @return HashMap, key = SIMD data zone, value = 2020 geographical access score
     */
    public HashMap<String,Integer> getGeoAcessMap2020(){
        HashMap<String,Integer> map = new HashMap<>();
        ArrayList<DataZone> theseZones = getDataZoneList();

        for(DataZone dataZone: theseZones){
            for(Journey journey: checkJourneys){
                if(dataZone.contains(journey.getLat(),journey.getLon())){
                    map.put(dataZone.getName(),dataZone.getGeoAccessDomain2020());
                }
            }
        }
        return map;
    }

    /**
     * Returns HashMap containing analysis data zones and corresponding 2016 geographical access score
     * @return HashMap, key = SIMD data zone, value = 2016 geographical access score
     */
    public HashMap<String,Integer> getGeoAcessMap2016(){
        HashMap<String,Integer> map = new HashMap<>();
        ArrayList<DataZone> theseZones = getDataZoneList();

        for(DataZone dataZone: theseZones){
            for(Journey journey: checkJourneys){
                if(dataZone.contains(journey.getLat(),journey.getLon())){
                    map.put(dataZone.getName(),dataZone.getGeoAccessDomain2016());
                }
            }
        }
        return map;
    }

    // Creates a ArrayList of DataZone objects whose name is contained in the checkZones list
    private ArrayList<DataZone> getDataZoneList(){
        ArrayList<DataZone> theseZones = new ArrayList<>();
        for(DataZone dz: dataZones){
            for(String zoneName: checkDataZones){
                if(dz.getName().equals(zoneName) && !theseZones.contains(dz)){
                    theseZones.add(dz);
                }
            }
        }
        return theseZones;
    }

    /**
     * Returns an ArrayList containing coordinates of all locations produced by reachability analysis.
     * @return ArrayList containing coordinates of all locations produced by reachability analysis.
     */
    public ArrayList<double[]> getList(){
        ArrayList<double[]> allLocations = new ArrayList<>();
        for(Journey j: journeys){
            allLocations.add(new double[]{j.getLat(),j.getLon()});
        }
        return allLocations;
    }

    /**
     * Returns HashMap containing travel time intervals and corresponding 2020 SIMD population.
     * @return HashMap, key = travel time interval, value = 2020 SIMD population
     */
    public HashMap<Integer,Integer> getMap2020(){
        return map2020;
    }

    /**
     * Returns HashMap containing travel time intervals and corresponding 2016 SIMD population.
     * @return HashMap, key = travel time interval, value = 2016 SIMD population
     */
    public HashMap<Integer, Integer> getMap2016(){
        return map2016;
    }

    /**
     * Returns HashMap containing data zone and 2020 geographical access domain
     * @return HashMap, key = SIMD data zone, value = 2020 geographical access domain
     */
    public HashMap<String, Integer> getMap2020Rank(){
        HashMap<String,Integer> map = new HashMap<>();
        ArrayList<DataZone> theseZones = getDataZoneList();

        for(DataZone dataZone: theseZones){
            for(Journey journey: checkJourneys){
                if(dataZone.contains(journey.getLat(),journey.getLon())){
                    map.put(dataZone.getName(),dataZone.getGeoAccess2020Rank());
                }
            }
        }
        return map;
    }

    /**
     * Returns an ArrayList containing the location of journeys used in the population calculation
     * @return ArrayList containing locations of journeys used in population calculation.
     */
    public ArrayList<double[]> getCheckJourneys(){
        ArrayList<double[]> locations = new ArrayList<>();
        for(Journey journey: checkJourneys){
            locations.add(new double[]{journey.getLat(),journey.getLon()});
        }
        return locations;
    }
}
