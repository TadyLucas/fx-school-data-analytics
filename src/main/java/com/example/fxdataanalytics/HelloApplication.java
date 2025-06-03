package com.example.fxdataanalytics;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        HBox root = new HBox();
        Scene scene = new Scene(root, 600, 600);
        VBox buttons = new VBox();
        VBox btnsKraje = new VBox();
        ToggleGroup groupKraje = new ToggleGroup();
        VBox btnsOkresy = new VBox();

        Canvas canvas = new Canvas(500, 600);

        ToggleGroup groupOkresy = new ToggleGroup();
        Label krajeLablel = new Label("Kraj:");
        Label okresLabel = new Label("Okres:");
        Label vyberLabel = new Label("Vyber:");
        VBox dataVbox = new VBox();
        Label startLabel = new Label("Obyvatelé na začátku roku:");
        Label midLabel = new Label("Obyvatelé k 1. červenci:");
        Label endLabel = new Label("Obyvatelé na konci roku:");
        Label startRes = new Label("");
        Label midRes = new Label("");
        Label endRes = new Label("");
        VBox vyberVbox = new VBox();
        ToggleGroup groupVyber = new ToggleGroup();
        dataVbox.getChildren().addAll(startLabel, startRes, midLabel, midRes, endLabel, endRes);



        for(int i=0; i<Config.numberOfKraje; i++){
            RadioButton kraj = new RadioButton(Config.KRAJE[i].getNazev());
            kraj.setUserData(Config.KRAJE[i].getId());
            kraj.setToggleGroup(groupKraje);
            if (!btnsKraje.getChildren().contains(kraj)) {
                btnsKraje.getChildren().add(kraj);
            }
        }

        groupKraje.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                int selectedKrajId = (Integer) newValue.getUserData();

                // Clear previous okres buttons so we start fresh
                btnsOkresy.getChildren().clear();
                vyberVbox.getChildren().clear();

                // Loop through all okresy
                int length = Config.OKRESY.length;
                for (int i = 0; i <length; i++) {
                    Okres okres = Config.OKRESY[i];
                    // If krajId matches the selected kraj id, add a radio button
                    if (okres.getId() == selectedKrajId) {
                        RadioButton okresButton = new RadioButton(okres.getNazev());
                        okresButton.setUserData(okres.getKrajId());
                        okresButton.setToggleGroup(groupOkresy);
                        btnsOkresy.getChildren().add(okresButton);
                    }
                }
            }
        });

        groupOkresy.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                int selectedOkresId = (Integer) newValue.getUserData();
                vyberVbox.getChildren().clear();
                Okres okres = Config.OKRESY[selectedOkresId - 1];


                for(int i=0; i<Config.VYBERY.length; i++) {
//                    System.out.println(okres.getNazev());
//                    System.out.printf("Okres: %d, %d, %s\n", i, okres.getId(), okres.getNazev());
                    RadioButton vyber = new RadioButton(Config.VYBERY[i]);
                    vyberVbox.getChildren().add(vyber);
                    vyber.setUserData(new VyberData(selectedOkresId-1, i*3));
                    vyber.setToggleGroup(groupVyber);
                }

                if (!buttons.getChildren().contains(vyberVbox)) {
                    buttons.getChildren().add(vyberVbox);
                }
            }
        });
        groupVyber.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                VyberData data = (VyberData) newValue.getUserData();

                int selectedOkresId = data.getId();
                int n = data.getVyber();
//                System.out.println(n);
                Okres okres = Config.OKRESY[selectedOkresId];
                int[] value = okres.getHodnoty();
                // Make sure the array has enough values
                startRes.setText(String.valueOf(value[n]));
                midRes.setText(String.valueOf(value[n+1]));
                endRes.setText(String.valueOf(value[n+2]));
                startRes.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
                midRes.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
                endRes.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
                if (!buttons.getChildren().contains(dataVbox)) {
                    buttons.getChildren().add(dataVbox);
                }
                GraphicsContext gc = canvas.getGraphicsContext2D();
                drawBars(canvas, gc, value[n], value[n+1], value[n+2]);
            }
        });

        buttons.getChildren().addAll(krajeLablel,btnsKraje, okresLabel, btnsOkresy, vyberLabel);
        root.getChildren().addAll(buttons ,canvas);
        stage.setTitle("Data analytics app");
        stage.setScene(scene);
        stage.show();
    }
    private void drawBars(Canvas canvas, GraphicsContext gc, int v1, int v2, int v3) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double DX = Config.DX;
        double DISTANCE_X = Config.DISTANCE_X;
        double SPACE = Config.SPACE;

        int[] values = {v1, v2, v3};
        int[] sorted = values.clone();
        Arrays.sort(sorted); // sorted[0] = min, sorted[1] = mid, sorted[2] = max

        double minHeight = 0.2;
        double maxHeight = 0.8;

        // Calculate scaled height for each value
        double[] scaledHeights = new double[3];
        for (int i = 0; i < 3; i++) {
            if (sorted[2] == sorted[0]) {
                scaledHeights[i] = 0.5; // fallback if all values equal
            } else {
                scaledHeights[i] = minHeight + ((values[i] - sorted[0]) * (maxHeight - minHeight)) / (sorted[2] - sorted[0]);
            }
        }

        gc.setStroke(Color.BLACK);
        double canvasHeight = canvas.getHeight();

        for (int i = 0; i < scaledHeights.length; i++) {
            double totalHeight = canvasHeight * scaledHeights[i];
            double x = DISTANCE_X * (i + 1);
            double y = canvasHeight - totalHeight;

            // Draw 3 nested rectangles
            for (int level = 0; level < Config.NUMBER_LINES; level++) {
                double inset = SPACE * level;
                double width = DX - inset * 2;
                double height = totalHeight - inset * 2;
                double rectX = x + inset;
                double rectY = y + inset;

                if (width > 0 && height > 0) {
                    gc.strokeRect(rectX, rectY, width, height);
                }
            }
        }
    }



    private Okres findOkresById(int id) {
        for (Okres o : Config.OKRESY) {
            if (o.getId() == id) {
                return o;
            }
        }
        return null;  // or throw exception if you want
    }


    public static void main(String[] args) {
        launch();
    }
}