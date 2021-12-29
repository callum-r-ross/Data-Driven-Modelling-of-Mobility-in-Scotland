package example;

import org.apache.lucene.geo.Polygon;
import org.apache.lucene.geo.Polygon2D;

/**
 * DataZone represents SIMD data zone.
 */
public class DataZone {

    private double[] lons;
    private double[] lats;
    private String name;
    private Polygon polygon;
    private Polygon2D polygon2D;
    private int population2020;
    private int population2016;
    private int geoAccessDomain2020;
    private int geoAccessDomain2016;
    private int geoAccess2020Rank;


    /**
     * Constructor for creating DataZone.
     * @param latitude - double array containing latitude coordinates for the polygon which represents the data zone.
     *                  The first and last elements of the array must be equal.
     * @param longitude - double array containing longitude coordinates for the polygon which represents the data zone.
     *                  The first and last elements of the array must be equal.
     * @param name - The name of the data zone which is being represented. Follows the format of S.........
     */
    public DataZone(double[] latitude, double[] longitude,String name) {
        this.lons = longitude;
        this.lats = latitude;
        setName(name);
        this.polygon = new Polygon(this.lats,this.lons);
        setPolygon2D();
    }

    /**
     * Returns the SIMD data zone name.
     * @return SIMD data zone name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the 2020 population for the SIMD data zone.
     * @return Population for SIMD data zone in 2020.
     */
    public int getPopulation2020() {
        return population2020;
    }

    /**
     * Returns the 2016 population for the SIMD data zone.
     * @return Population for SIMD data zone in 2016.
     */
    public int getPopulation2016(){
        return population2016;
    }

    /**
     * Returns 2020 geographical access score.
     * @return 2020 geographical access score for data zone.
     */
    public int getGeoAccessDomain2020(){
        return this.geoAccessDomain2020;
    }

    /**
     * Returns 2016 geographical access score for data zone.
     * @return 2016 geographical access score for data zone.
     */
    public int getGeoAccessDomain2016(){
        return this.geoAccessDomain2016;
    }


    /**
     * Returns geographical access score for data zone.
     * @return 2020 geographical access rank
     */
    public int getGeoAccess2020Rank(){
        return this.geoAccess2020Rank;
    }

    /**
     * Sets the data zones 2020 geographical access rank
     * @param geoAccess2020Rank 2020 geographical access rank
     */
    public void setGeoAccess2020Rank(int geoAccess2020Rank){
        this.geoAccess2020Rank = geoAccess2020Rank;
    }

    // Sets DataZones name by removing .csv file extension and -part-n from non-self-enclosing data zones
    private void setName(String name){
        this.name = name.substring(0,name.lastIndexOf("."));
        this.name = name.substring(0,9);
    }

    // Creates the Polygon2D object from the Polygon object
    private void setPolygon2D(){
        this.polygon2D = Polygon2D.create(this.polygon);
    }

    /**
     * Sets the 2020 SIMD population for the data zone.
     * @param population2020 SIMD 2020 population for corresponding data zone.
     */
    public void setPopulation2020(int population2020) {
        this.population2020 = population2020;
    }

    /**
     * Sets the 2016 SIMD population for the data zone.
     * @param population2016 SIMD 2016 population for corresponding data zone.
     */
    public void setPopulation2016(int population2016) {
        this.population2016 = population2016;
    }

    /**
     * Sets the 2020 SIMD geographical access score for data zone
     * @param geoAccessDomain2020
     */
    public void setGeoAccessDomain2020(int geoAccessDomain2020){
        this.geoAccessDomain2020 = geoAccessDomain2020;
    }

    /**
     * Sets the 2016 SIMD geographical access score for data zone
     * @param geoAccessDomain2016
     */
    public void setGeoAccessDomain2016(int geoAccessDomain2016){
        this.geoAccessDomain2016 = geoAccessDomain2016;
    }

    /**
     * Calls the contains method of the Polygon2D class. Determines if the point
     * is contained within the polygon.
     * @param latitude - Latitude (y) coordinate of the point being searched
     * @param longitude - Longitude (x) coordinate of the point being searched.
     * @return - Whether the point is contained within the polygon.
     */
    public boolean contains(double latitude,double longitude){
        return this.polygon2D.contains(latitude,longitude);
    }
}
