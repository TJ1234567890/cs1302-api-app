package cs1302.api;

/**
 * Represents a response from the Airport search API. This is called on by Gson to
 * create an object from the JSON response body. This class is to help in the AppApi file
 */
public class AirportDetails {
    String name;
    String city;
    String region;
    String latitude;
    String longitude;
} // AirportDetails
