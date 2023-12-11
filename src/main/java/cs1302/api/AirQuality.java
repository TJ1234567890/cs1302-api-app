package cs1302.api;

public class AirQuality {
    String status;
    Data data;

    public static class Data {
        Current current;
    }

    public static class Current {
        Pollution pollution;
        Weather weather;
    }

    public static class Pollution {
        String ts;
        int aquis;
        String mainus;
        int aqicn;
        String maincn;
    }

    public static class Weather {
        int tp;
        int pr;
        int hu;
        double ws;
        int wd;
        String ic;
    }
}
