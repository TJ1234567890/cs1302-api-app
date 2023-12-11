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

    private final String airportApi = "https://api.api-ninjas.com/v1/airports?";
    private final String airportApiKey = "k78n1Pfwf3a187lpaKSAJQ==obe3ESp43nX7LAGL";

    private final String iqAirApi = "http://api.airvisual.com/v2/nearest_city?";
    private final String iqAirApiKey = "4e1e0d5d-e9ba-45af-9c11-f61a94c13c49";

    Stage stage;
    Scene scene;
    VBox root;
    HBox inputPane;
    TextField inputField;
    Button loadButton;
    ScrollPane textPane;
    TextFlow textFlow;
    Text prompt;
    Insets pads;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        this.stage = null;
        this.scene = null;

        this.root = new VBox(3);
        this.inputPane = new HBox(2.5);
        this.inputField = new TextField();
        this.loadButton = new Button("Lead");
        this.textPane = new ScrollPane();
        this.textFlow = new TextFlow();
        this.prompt = new Text("Search for the forecast at an airport: ");
        this.pads = new Insets(3, 3, 3, 3);
    } // ApiApp



    @Override
    public void init() {
        HBox.setHgrow(this.inputField, Priority.ALWAYS);
        this.inputPane.getChildren().addAll(this.prompt, this.inputField, this.loadButton);
        this.inputPane.setPadding(pads);
        this.inputPane.setAlignment(Pos.CENTER_LEFT);

        this.textFlow.getChildren().add(new Text("Information will displayed here"));
        this.textFlow.setMaxWidth(630);
        this.textPane.setPrefHeight(480);
        this.textPane.setContent(this.textFlow);

        this.root.getChildren().addAll(this.inputPane, this.textPane);
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

        // demonstrate how to load local asset using "file:resources/"
        Image bannerImage = new Image("file:resources/readme-banner.png");
        ImageView banner = new ImageView(bannerImage);
        banner.setPreserveRatio(true);
        banner.setFitWidth(640);

        // some labels to display information
        Label notice = new Label("Modify the starter code to suit your needs.");

        // setup scene
        root.getChildren().addAll(banner, notice);
        scene = new Scene(root);

        // setup stage
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

        System.out.println(uri);

        try {

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("X-Api-Key", airportApiKey)
                .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());

            String jsonString = response.body();
            System.out.println(jsonString.trim());
            AirportDetails[] details = GSON.fromJson(jsonString, AirportDetails[].class);
            System.out.println(GSON.toJson(details));
            System.out.println(details.length);
            if (details.length == 0) {
                throw new IllegalArgumentException("Provided input resulted in no results");
            }
            if (details.length == 1) {
                AirportDetails airport = details[0];
                System.out.println("1");
                createIQAirLink(airport);
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
            }
        } catch (Throwable e) {
            System.err.println(e);
            Platform.runLater(() -> this.alertError(e, uri));
        }
    }

    /**
     *  This method takes the paramater {@code airport} and takes the lat and lon of it}.
     *  Uses the lat and lon that it recieves and encodes it through {@code URLEncoder.encode()}
     *  Uses {@code StandardCharsets.UTF_8} to encode
     *  creates a uri string using {@code iqAirApi} and the formated information
     *  after recieving json. if it has status: success then the code will not throw error
     *  the code then takes parsed information and displayed it in easy, readable format
     *
     *  @param airport is the specific information from one airport that was provided
     *  @throw IllegalArgumentException
     */
    private void createIQAirLink(AirportDetails airport) {
        String lat =  airport.latitude;
        String lon =  airport.longitude;

        String latTra = URLEncoder.encode(lat, StandardCharsets.UTF_8);
        String lonTra = URLEncoder.encode(lon, StandardCharsets.UTF_8);
        String query = String.format("lat=%s&lon=%s&key=%s", latTra, lonTra, iqAirApiKey);
        String uri = iqAirApi + query;

        System.out.println(uri);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());

            String jsonString = response.body();
            System.out.println(jsonString.trim());
            AirQuality result = GSON.fromJson(jsonString, AirQuality.class);
            System.out.println(GSON.toJson(result));
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
                String dir = "\tWind Direction: " + cast.wd + " degrees (N=0, E=90, ...)\n";
                String aqu = "\tAir Quality: " + pol.aqius + " on the AQI with US Standards\n";
                String aqi = "\tAir Quality: " + pol.aqicn + " on the AQI with Chinese Standards\n";
                String nam = "Airport: " + airport.name + "\n";
                String cit = "City: " + airport.city + "\n";
                String cou = "Country: " + da.country + "\n";
                Platform.runLater(() -> this.textFlow.getChildren()
                                  .addAll(new Text(nam), new Text(cit), new Text(cou),
                                          new Text(t), new Text(temp), new Text(pres),
                                          new Text(hum), new Text(win), new Text(dir)));
            } else {
                throw new IllegalArgumentException("status: failed");
            }
        } catch (Throwable e) {
            System.err.println(e);
            Platform.runLater(() -> this.alertError(e, uri));
        }

    }
} // ApiApp
