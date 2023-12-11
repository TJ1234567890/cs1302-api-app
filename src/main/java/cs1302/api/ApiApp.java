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

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        this.stage = null;
        this.scene = null;

        this.root = new VBox();
        this.inputPane = new HBox();
        this.inputField = new TextField();
        this.loadButton = new Button("Lead");
        this.textPane = new ScrollPane();
        this.textFlow = new TextFlow();
    } // ApiApp



    @Override
    public void init() {
        HBox.setHgrow(this.inputField, Priority.ALWAYS);
        this.inputPane.getChildren().addAll(this.inputField, this.loadButton);

        this.textFlow.getChildren().add(new Text("hehe"));
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

    private void loadPage() {
        this.loadButton.setDisable(true);
        Platform.runLater(() -> this.textFlow.getChildren().clear());
        this.loadButton.setDisable(false);
    }

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
        }
    }

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
        } catch (Throwable e) {
            System.err.println(e);
        }

    }
} // ApiApp
