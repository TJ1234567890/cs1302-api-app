package cs1302.api;

/**
 * Represents a response from the IQAir search API. This is called on by Gson to
 * create an object from the JSON response body. This class is to help in the AppApi file.
 * This is a class under AirQuality
 */
public class Weather {
    int tp;
    int pr;
    int hu;
    double ws;
    int wd;
    String ic;
}
