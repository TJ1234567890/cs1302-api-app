package cs1302.api;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest;

import javafx.event.ActionEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.io.IOException;
import java.net.URL;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {

    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();                                     // builds and returns a HttpClient object

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object

    /** This is the beginning of Api1 and the key for Api1. */
    private final String airportApi = "https://api.api-ninjas.com/v1/airports?";
    private final String airportApiKey = "k78n1Pfwf3a187lpaKSAJQ==obe3ESp43nX7LAGL";

    /** This is the beginning of Api2 and the key for Api2. */
    private final String iqAirApi = "http://api.airvisual.com/v2/nearest_city?";
    private final String iqAirApiKey = "4e1e0d5d-e9ba-45af-9c11-f61a94c13c49";

    /** These variables create the aplication and how it looks. */
    Stage stage;
    Scene scene;
    VBox root;
    HBox inputPane;
    HBox container;
    TextField inputField;
    Button loadButton;
    ScrollPane textPane;
    TextFlow textFlow;
    Text prompt;
    Insets pads;
    Text msg;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        this.stage = null;
        this.scene = null;

        this.root = new VBox(3);
        this.inputPane = new HBox(2.5);
        this.container = new HBox();
        this.inputField = new TextField();
        this.loadButton = new Button("Load");
        this.textPane = new ScrollPane();
        this.textFlow = new TextFlow();
        this.prompt = new Text("Enter name of an Airport: ");
        this.msg = new Text("Forecast Information about the location will be displayed below");
        this.pads = new Insets(3, 3, 3, 3);
    } // ApiApp


    /** {@inheritDoc} */
    @Override
    public void init() {
        HBox.setHgrow(this.inputField, Priority.ALWAYS);
        this.inputPane.getChildren().addAll(this.prompt, this.inputField, this.loadButton);
        this.inputPane.setPadding(pads);
        this.inputPane.setAlignment(Pos.CENTER_LEFT);

        this.container.getChildren().addAll(this.msg);
        this.container.setAlignment(Pos.CENTER);
        this.container.setPadding(pads);

        Text pri = new Text("");
        this.textFlow.getChildren().add(pri);
        this.textFlow.setMaxWidth(650);
        this.textPane.setPrefHeight(450);
        this.textPane.setPrefWidth(650);
        this.textPane.setContent(this.textFlow);

        this.root.getChildren().addAll(this.inputPane, this.container, this.textPane);
        this.root.setPadding(pads);
        this.loadButton.setOnAction(event -> {
            Thread t = new Thread(() -> this.createAirportLink());
            t.setDaemon(true);
            t.start();
        });
    }

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;
        this.scene = new Scene(root);
        stage.setTitle("ApiApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start

    /**
     *  This method takes in {@code cause} which is the type of error.
     *  It also takes in the {@code URI} to see which link caused the error
     *  Using the two parameters a error message is created to display
     *  The created error message is displayed on a {@code Alert} window
     *
     *  @param cause is the Exception that was caught, will be displyes
     *  @param URI is the link that created the request that caused an error
     *  will be used to display message
     */
    public static void alertError(Throwable cause, String URI) {
        TextArea text = new TextArea("URI: " + URI + "\n\n" +
                                     "Exception: " + cause.toString());
        text.setEditable(false);
        Alert alert = new Alert(AlertType.ERROR);
        alert.getDialogPane().setContent(text);
        alert.setResizable(true);
        alert.showAndWait();
    } // alertError

    /**
     *  This method gets the requested search item from {@code inputField}.
     *  Uses the values that it recieves and encodes it through {@code URLEncoder.encode()}
     *  Uses {@code StandardCharsets.UTF_8} to encode
     *  creates a uri string using {@code airportApi} and the formated information
     *  creates a request to the api and parses it to a class that can access the values
     *  if not result is provided the method throws an error.
     *  If there are multiple results to a airport name inputed
     *  the user is prompted to search specifically from the list provided
     * once only one result shows up it calls the {@code creatIQAirLink()} method.
     *
     *  @throw new IllegalArgumentException
    */
    private void createAirportLink() {
        String text = this.inputField.getText();

        String name = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String query = String.format("name=%s", name);
        String uri = airportApi + query;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("X-Api-Key", airportApiKey)
                .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());

            String jsonString = response.body();
            this.loadButton.setDisable(true);
            AirportDetails[] details = GSON.fromJson(jsonString, AirportDetails[].class);
            if (details.length == 0) {
                throw new IllegalArgumentException("Provided input resulted in no results");
            }
            Platform.runLater(() -> this.msg.setText("Loading..."));
            if (details.length == 1) {
                AirportDetails airport = details[0];
                String latTra = URLEncoder.encode(airport.latitude, StandardCharsets.UTF_8);
                String lonTra = URLEncoder.encode(airport.longitude, StandardCharsets.UTF_8);
                String quer = String.format("lat=%s&lon=%s&key=%s", latTra, lonTra, iqAirApiKey);
                String url = iqAirApi + quer;
                createIQAirLink(url, airport);
            } else {
                Platform.runLater(() -> this.textFlow.getChildren().clear());
                String print = "There is no airport named \"" + text + "\" you were looking for:\n";
                Text msg = new Text(print);
                Text inst = new Text("Please input the proper name from below once found.\n");
                Text line = new Text("_________________________\n");
                Platform.runLater(() -> this.textFlow.getChildren().addAll(msg, inst, line));
                for (int i = 0; i < details.length; i++) {
                    AirportDetails airport = details[i];
                    Text p1 = new Text("Airport name: " + airport.name + "\n");
                    Text p2 = new Text( "City: " + airport.city + "\n");
                    Text p3 = new Text( "Region: " + airport.region + "\n");
                    Platform.runLater(() -> this.textFlow.getChildren()
                                      .addAll(p1, p2, p3, new Text("_________________________\n")));
                }
                Platform.runLater(() -> this.msg.setText("Please Re-Input the Airport you desire"));
                this.loadButton.setDisable(false);
            }
        } catch (Throwable e) {
            System.err.println(e);
            this.loadButton.setDisable(false);
            Platform.runLater(() -> this.alertError(e, uri));
        }
    }

    /**
     *  This method takes the paramater {@code airport} and takes the lat and lon of it.
     *  Uses the lat and lon that it recieves and encodes it through {@code URLEncoder.encode()}
     *  Uses {@code StandardCharsets.UTF_8} to encode
     *  creates a uri string using {@code iqAirApi} and the formated information
     *  after recieving json. if it has status: success then the code will not throw error
     *  the code then takes parsed information and displayed it in easy, readable format
     *
     *  @param airport is the specific information from one airport that was provided
     *  @param uri is the link used to request the HTTP
     *  @throw IllegalArgumentException
     */
    private void createIQAirLink(String uri, AirportDetails airport) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
            String jsonString = response.body();
            AirQuality result = GSON.fromJson(jsonString, AirQuality.class);
            if (result.status.equals("success")) {
                Platform.runLater(() -> this.textFlow.getChildren().clear());
                Data da  = result.data;
                Current ct  = da.current;
                Pollution pol = ct.pollution;
                Weather cast = ct.weather;
                String t = "\nForecast: \n";
                String temp = "\tTemperature: " + cast.tp + " Celsius\n";
                String pres = "\tAtmospheric Pressure: " + cast.pr + " hPa\n";
                String hum = "\tHumidity: " + cast.hu + "%\n";
                String win = "\tWind Speed: " + cast.ws + " m/s\n";
                String dir = "\tWind Direction: " + cast.wd + " degrees (N=0, E=90, W=270, S=360)";
                String nam = "Airport: " + airport.name + "\n";
                String cit = "City: " + airport.city + "\n";
                String cou = "Country: " + da.country + "\n";
                String coor = "Latitude: " + airport.latitude + "\n";
                String cor = "Longitude: " + airport.longitude + "\n";
                Platform.runLater(() -> this.textFlow.getChildren()
                                  .addAll(new Text(nam), new Text(cit), new Text(cou),
                                          new Text(coor), new Text(cor), new Text(t),
                                          new Text(temp), new Text(pres), new Text(hum),
                                          new Text(win), new Text(dir)));
                String p = "\nPollutants: \n";
                String aqu = "\tAir Quality: " + pol.aqius + " on the AQI with US Standards\n";
                String mus = "\tMain Pollutant: " + classify(pol.mainus) + "Based on US AQI)\n";
                String aqi = "\tAir Quality: " + pol.aqicn + " on the AQI with Chinese Standards\n";
                String mcn = "\tMain Pollutant: " + classify(pol.maincn) + "Based on Chinese AQI)";

                String table = "\n\n\n\t  Air Quality Index (AQI)\n";
                String Gd = "\t     0-50 = Good\n";
                String Md = "\t   51-100 = Moderate\n";
                String Ug = "  101-150 = Unhealthy for Sensitive people\n";
                String Uh = "\t   151-200 = Unhealthy\n";
                Platform.runLater(() -> this.textFlow.getChildren()
                                  .addAll(new Text(p), new Text(aqu), new Text(mus),
                                          new Text(aqi), new Text(mcn), new Text(table),
                                          new Text(Gd), new Text(Md), new Text(Ug),
                                          new Text(Uh)));
                String fin = "Forecast Information Provided Below";
                Platform.runLater(() -> this.msg.setText(fin));
                this.loadButton.setDisable(false);
            } else {
                throw new IllegalArgumentException("status: failed");
            }
        } catch (Throwable e) {
            System.err.println(e);
            this.loadButton.setDisable(false);
            Platform.runLater(() -> this.alertError(e, uri));
        }

    }

    /**
     *  This helps expand the type of pollutant that the JsonString gives.
     *  The parameter {@code pollutant} can only be 6 possible strings.
     *  So if the pollutant is not one of the 6 possibel strings then it does not
     *  get expanded uppon.
     *
     *  @param pollutant - type of pollutant shortened due to API documentation
     *  @return String that is the expanded version of the shortened pollutant
     */
    private String classify(String pollutant) {
        if (pollutant.equals("p2")) {
            return "pm2.5 (";
        } else if (pollutant.equals("p1")) {
            return "pm10 (";
        } else if (pollutant.equals("o3")) {
            return "Ozone (O3) (";
        } else if (pollutant.equals("n2")) {
            return "Nitrogen dioxide (NO2) (";
        } else if (pollutant.equals("s2")) {
            return "Sulfur dioxide (SO2) (";
        } else if (pollutant.equals("co")) {
            return "Carbon monoxide (CO) (";
        }
        return pollutant;
    }
} // ApiApp
