package com.example.pixelsorter2;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;

public class PixelSortApp extends Application {

    private ImageView view;

    @Override
    public void start(Stage stage) throws Exception{
        stage.setTitle("Pixel Sorter");


        //stage.getIcons().add(new Image("file:src/icon.png"));
        URL url = getClass().getClassLoader().getResource("icon.png");

        assert url != null;
        stage.getIcons().add(new Image(url.toExternalForm()));


        Scene sceneBox = new Scene(createContent());
        stage.setScene(sceneBox);
        stage.show();

    }

    public static void main(String[] args) {
        launch( );
    }

    public Parent createContent(){
        BorderPane root = new BorderPane( );
        root.setPrefSize(800,600);
        root.setBackground(new Background(new BackgroundFill(Color.DIMGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        MenuBar bar = new MenuBar( );
        root.setTop(bar);

        //File options
        Menu menu = new Menu("File");
        MenuItem fileItem1 = new MenuItem( "Open Image" );
        menu.getItems().add(fileItem1);
        fileItem1.setOnAction(imgloadEventListener);
        MenuItem fileItem2 = new MenuItem( "Save Image" );
        menu.getItems().add(fileItem2);
        fileItem2.setOnAction(imgSaveEventListener);

        bar.getMenus().add(menu);

        //loaded image details
        view = new ImageView();
        root.setCenter(new HBox( view ));
        view.fitHeightProperty().bind(root.heightProperty());
        view.fitWidthProperty().bind(root.widthProperty());
        view.setPreserveRatio(true);
        root.setCenter(view);

        //Edit options
        Menu editMenu = new Menu( "Edit" );
        MenuItem editItem1 = new MenuItem( "Sort" );
        editMenu.getItems().add(editItem1);
        editItem1.setOnAction(imgEditEventListener);

        MenuItem editItem2 = new MenuItem( "invert" );
        editMenu.getItems().add(editItem2);
        editItem2.setOnAction(imgEditEventListener2);

        bar.getMenus().add(editMenu);


        return root;
    }

    //image editing methods

    //invert
    public void invertImg(Image img, ImageView newView){

        int imgWIDTH = (int)img.getWidth();
        int imgHEIGHT = (int)img.getHeight();

        WritableImage editableImg = new WritableImage(imgWIDTH, imgHEIGHT);
        PixelReader pixelReader = img.getPixelReader();
        PixelWriter pixelWriter = editableImg.getPixelWriter();

        for(int j = 0; j < imgHEIGHT; j++){
            for (int i = 0; i < imgWIDTH; i++){

                Color c1 = pixelReader.getColor(i, j);

                Color c2 = Color.color(c1.getRed(), c1.getGreen(), c1.getBlue());

                pixelWriter.setColor(i, j, c2.invert());
            }
        }

        Image finalEditedImg = SwingFXUtils.toFXImage(SwingFXUtils.fromFXImage(editableImg, null), null);
        newView.setImage(finalEditedImg);

    }

    //sort
    public void editImg(Image img, ImageView newView){

        int imgWIDTH = (int)img.getWidth();
        int imgHEIGHT = (int)img.getHeight();


        WritableImage editableImg = new WritableImage(imgWIDTH, imgHEIGHT);
        PixelReader pixelReader = img.getPixelReader();
        PixelWriter pixelWriter = editableImg.getPixelWriter();

        /*//set up 2D array
        Color[][] storedPixel = new Color[imgWIDTH][imgHEIGHT];

        //store all pixel's color data in a 2D array
        for(int j = 0; j < imgHEIGHT; j++){
            for (int i = 0; i < imgWIDTH; i++) {

                storedPixel[i][j] = pixelReader.getColor(i, j);

            }
        }*/

        //arrayList to store pixels and sort them
        ArrayList<Color> pixelList = new ArrayList<>();

        //sort pixels
        for(int j = 0; j < imgHEIGHT; j++){
            for (int i = 0; i < imgWIDTH; i++) {

                pixelList.add(pixelReader.getColor(i, j));//adding a row of pixels into pixel list

            }

            if(pixelList.size() == imgWIDTH) {

                pixelList.sort(Comparator.comparingDouble(Color::getBrightness)); //sorting the list which has a row of pixels

                for (int n = 0; n < pixelList.size(); n++) {

                    pixelWriter.setColor(n, j, pixelList.get(n));

                }
                pixelList.clear(); //clear the row of pixels from the list so it can add and sort the next row
            }

        }

        /*ArrayList<Color> pixelList = new ArrayList<>();

        int start_x = -1;
        int start_y = -1;

        for (int y = 0; y < imgHEIGHT; y++) {

            for (int x = 0; x < imgWIDTH; x++) {

                Color pixelColor = pixelReader.getColor(x,y);

                if(pixelColor != Color.BLACK){

                    if(start_x == -1 && start_y == -1){

                        start_x = x;
                        start_y = y;

                    }

                    pixelList.add(pixelColor);

                } else if(!pixelList.isEmpty()) {

                    pixelList.sort(Comparator.comparingDouble(Color::getBrightness));

                    for(int w = 0; w < pixelList.size(); w++){

                        pixelWriter.setColor(start_x,start_y,pixelList.get(w));

                        start_x++;

                    }

                    // Check if there are any pixels remaining in the pixelList
                    if(start_x < imgWIDTH - 1){
                        for(int w = 0; w < pixelList.size(); w++){
                            pixelWriter.setColor(start_x,start_y,pixelList.get(w));
                            start_x++;
                        }
                    }

                    pixelList.clear();

                    start_x = -1;
                    start_y = -1;

                } else {
                    // Set the color of the pixel writer to black
                    pixelWriter.setColor(x, y, Color.BLACK);
                }

            }
        }*/

        /*//checks if a pixel in the editable image object has no color, if yes, then sets that pixel to black
        for (int y = 0; y < imgHEIGHT; y++) {

            for (int x = 0; x < imgWIDTH; x++) {

                if(editableImg.getPixelReader().getColor(x,y) == null){

                    editableImg.getPixelWriter().setColor(x, y, Color.BLACK);

                }

            }
        }*/

        Image finalEditedImg = SwingFXUtils.toFXImage(SwingFXUtils.fromFXImage(editableImg, null), null);
        newView.setImage(finalEditedImg);
    }

    //event handling methods

    EventHandler<ActionEvent> imgloadEventListener = e -> {

        FileChooser fileChooser = new FileChooser();
        File openedFile = fileChooser.showOpenDialog(null);

        Image openImg = new Image(openedFile.toURI().toString());

        view.setImage(openImg);

    };

    EventHandler<ActionEvent> imgEditEventListener = f -> {

            editImg(view.getImage(), view);

    };

    EventHandler<ActionEvent> imgEditEventListener2 = g -> {

        invertImg(view.getImage(), view);

    };

    EventHandler<ActionEvent> imgSaveEventListener = h -> {

        Image saveImg = view.getImage();

        // create a file chooser dialog for saving the image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );

        // show the save file dialog and get the selected file
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                // add the selected file extension to the file name if it doesn't already have it
                String fileName = file.getName();
                String extension = fileChooser.getSelectedExtensionFilter().getExtensions().get(0).replace("*", "");
                if (!fileName.toLowerCase().endsWith(extension.toLowerCase())) {
                    fileName += extension;
                    file = new File(file.getParentFile(), fileName);
                }

                // save the image to the selected file
                if (!file.exists()) {
                    file.createNewFile();
                }

                BufferedImage bImage = SwingFXUtils.fromFXImage(saveImg, null);

                String formatName = extension.substring(1);

                ImageIO.write(bImage, formatName, file);

                System.out.println("Image saved successfully to " + file.getAbsolutePath());

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("No file selected.");
        }

    };

}