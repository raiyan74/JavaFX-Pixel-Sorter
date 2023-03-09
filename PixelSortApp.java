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
import java.util.List;

public class PixelSortApp extends Application {

    private Image openImg;
    private ImageView view;
    public Double thresh;

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
        fileItem1.setOnAction(imgLoadEventListener);

        MenuItem fileItem2 = new MenuItem( "Save Image" );
        menu.getItems().add(fileItem2);
        fileItem2.setOnAction(imgSaveEventListener);

        MenuItem fileItem3 = new MenuItem( "Restore Original Image" );
        menu.getItems().add(fileItem3);
        fileItem3.setOnAction(imgRestoreEventListener);

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

        MenuItem editItem1 = new MenuItem( "Invert" );
        editMenu.getItems().add(editItem1);
        editItem1.setOnAction(imgInvertEventListener);

        MenuItem editItem2 = new MenuItem( "Random Sort" );
        editMenu.getItems().add(editItem2);
        editItem2.setOnAction(imgRandomSortEventListener);

        MenuItem editItem3 = new MenuItem( "Brightness Sort" );
        editMenu.getItems().add(editItem3);
        editItem3.setOnAction(imgBrightSortEventListener);

        MenuItem editItem4 = new MenuItem( "Saturation Sort" );
        editMenu.getItems().add(editItem4);
        editItem4.setOnAction(imgSaturationSortEventListener);

        MenuItem editItem5 = new MenuItem( "Hue Sort" );
        editMenu.getItems().add(editItem5);
        editItem5.setOnAction(imgHueSortEventListener);

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
    public void editSortImg(Image img, ImageView newView, int mode){

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

        if(mode == 2 || mode == 3 || mode == 4){
            thresh = InputBox.ThresholdInput();
        }

        //arrayList to store pixels and sort them
        ArrayList<Color> pixelList = new ArrayList<>();

        //sort pixels
        for(int j = 0; j < imgHEIGHT; j++){
            for (int i = 0; i < imgWIDTH; i++) {

                pixelList.add(pixelReader.getColor(i, j));//adding a row of pixels into pixel list

            }

            if(pixelList.size() == imgWIDTH) {

                ArrayList<Color> newRandomSortedList = new ArrayList<>();

                if(mode == 1){

                    newRandomSortedList = randomDivideSort(pixelList);

                } else if( mode == 2) {

                    newRandomSortedList = brightSort(pixelList, thresh);

                } else if( mode == 3) {

                    newRandomSortedList = saturationSort(pixelList, thresh);

                } else if( mode == 4) {

                    newRandomSortedList = hueSort(pixelList, thresh);

                }

                //pixelList.sort(Comparator.comparingDouble(Color::getBrightness)); //sorting the list which has a row of pixels


                for (int n = 0; n < newRandomSortedList.size(); n++) {

                    pixelWriter.setColor(n, j, newRandomSortedList.get(n));

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

    public ArrayList<Color> brightSort(ArrayList<Color> rawPixelList, Double thresh){

        ArrayList<Color> sortedPixelList = new ArrayList<>(); // Creates a new array list that will be filled and returned

        ArrayList<Color> nonBlackPixels = new ArrayList<>(); // arraylist that will hold the pixels that will get sorted

        //double threshold = thresh;


        for (int i = 0; i < rawPixelList.size(); i++){ // loops through the given pixel list

            //Color currPixel = rawPixelList.get(i);

            double currPixelBrightVal = rawPixelList.get(i).getBrightness();

            //System.out.println(currPixelBrightVal);

            //for(int m = 0; m < rawPixelList.size(); m++) {

                if (currPixelBrightVal >= thresh) {


                    nonBlackPixels.add(rawPixelList.get(i));// adds the bright pixel to the nonBlack arraylist which keep accumulating until loop encounters a black pixel


                } else {

                    nonBlackPixels.sort(Comparator.comparingDouble(Color::getBrightness));//sorts

                    sortedPixelList.addAll(nonBlackPixels);//adds sorted pixel to the returning arraylist

                    nonBlackPixels.clear();

                    sortedPixelList.add(rawPixelList.get(i)); // adds the low brightness pixel

                }

        }

        nonBlackPixels.sort(Comparator.comparingDouble(Color::getBrightness));//sorts

        sortedPixelList.addAll(nonBlackPixels);//adds sorted pixel to the returning arraylist

        nonBlackPixels.clear();

        return sortedPixelList;
    }

    public ArrayList<Color> saturationSort(ArrayList<Color> rawPixelList, Double thresh){

        ArrayList<Color> sortedPixelList = new ArrayList<>(); // Creates a new array list that will be filled and returned

        ArrayList<Color> nonSaturatedPixels = new ArrayList<>(); // arraylist that will hold the pixels that will get sorted

        //double threshold = thresh;


        for (int i = 0; i < rawPixelList.size(); i++){ // loops through the given pixel list

            //Color currPixel = rawPixelList.get(i);

            double currPixelBrightVal = rawPixelList.get(i).getSaturation();

            //System.out.println(currPixelBrightVal);

            //for(int m = 0; m < rawPixelList.size(); m++) {

            if (currPixelBrightVal >= thresh) {


                nonSaturatedPixels.add(rawPixelList.get(i));// adds the bright pixel to the nonBlack arraylist which keep accumulating until loop encounters a black pixel


            } else {

                nonSaturatedPixels.sort(Comparator.comparingDouble(Color::getSaturation));//sorts

                sortedPixelList.addAll(nonSaturatedPixels);//adds sorted pixel to the returning arraylist

                nonSaturatedPixels.clear();

                sortedPixelList.add(rawPixelList.get(i)); // adds the low brightness pixel

            }

        }

        nonSaturatedPixels.sort(Comparator.comparingDouble(Color::getSaturation));//sorts

        sortedPixelList.addAll(nonSaturatedPixels);//adds sorted pixel to the returning arraylist

        nonSaturatedPixels.clear();

        return sortedPixelList;
    }

    //sorts by hue threshold
    public ArrayList<Color> hueSort(ArrayList<Color> rawPixelList, Double thresh){

        ArrayList<Color> sortedPixelList = new ArrayList<>(); // Creates a new array list that will be filled and returned

        ArrayList<Color> nonHuedPixels = new ArrayList<>(); // arraylist that will hold the pixels that will get sorted

        //double threshold = thresh;


        for (int i = 0; i < rawPixelList.size(); i++){ // loops through the given pixel list

            //Color currPixel = rawPixelList.get(i);

            double currPixelBrightVal = rawPixelList.get(i).getHue();

            //System.out.println(currPixelBrightVal);

            //for(int m = 0; m < rawPixelList.size(); m++) {

            if (currPixelBrightVal >= thresh) {


                nonHuedPixels.add(rawPixelList.get(i));// adds the bright pixel to the nonBlack arraylist which keep accumulating until loop encounters a black pixel


            } else {

                nonHuedPixels.sort(Comparator.comparingDouble(Color::getHue));//sorts

                sortedPixelList.addAll(nonHuedPixels);//adds sorted pixel to the returning arraylist

                nonHuedPixels.clear();

                sortedPixelList.add(rawPixelList.get(i)); // adds the low brightness pixel

            }

        }

        nonHuedPixels.sort(Comparator.comparingDouble(Color::getHue));//sorts

        sortedPixelList.addAll(nonHuedPixels);//adds sorted pixel to the returning arraylist

        nonHuedPixels.clear();

        return sortedPixelList;
    }

    public ArrayList<Color> randomDivideSort(ArrayList<Color> rawPixelList){

        //System.out.println(rawPixelList.size());

        ArrayList<Color> randomPixelList = new ArrayList<>();


        int divideUpList = (int)(Math.random() * 60 - 17) + 17;

        //System.out.println(divideUpList);

        int dividedPixelCount = rawPixelList.size()/divideUpList;

        System.out.println(dividedPixelCount);

        int leftOverPixels = rawPixelList.size() % dividedPixelCount; //calculates how many pixels are left to iterate through

        //System.out.println(leftOverPixels);

        for(int i = 0; i < rawPixelList.size(); ) {//starts looping through the entire ArrayList and increases i by the random generated number on each increment

            if (leftOverPixels != 0) {

                if (i == (rawPixelList.size() - leftOverPixels)) { //checks if current value of i+(givenlistsize / random number) is greater than the list size. if yes, that means the left over pixel will not be able to be subListed and sorted and added back together.


                    for (int p = i; p < rawPixelList.size(); p++) {//since current value of i+(givenlistsize / random number) is smaller than the list size, So this loop adds the rest of the pixels back to the random pixel list

                        randomPixelList.add(rawPixelList.get(p));//this only happens after all the sub divided lists get added so it does need sorting and can be added as they are.(I HOPE)

                    }

                    i = i + dividedPixelCount; //increments i so that i becomes bigger than then the rawList size and the loop can stop.

                } else {// What happens when i+(givenlistsize / random number) is smaller than given list size, which means this happens when list can be properly sub divided and sorted and added back together.

                    int partitionEnd = i + dividedPixelCount;


                    List<Color> subPixelList = rawPixelList.subList(i, partitionEnd);//initializes List object which contains the sublist from i to i+dividedPixelCount

                    subPixelList.sort(Comparator.comparingDouble(Color::getBrightness));//sorts

                    randomPixelList.addAll(subPixelList);//adds whatever sorted pixels are in the sublist and adds that to the main ArrayList

                    i = i + dividedPixelCount;//increments the loop forward by portion of the divide

                }

            } else {

                int partitionEnd = i + dividedPixelCount;

                List<Color> subPixelList = rawPixelList.subList(i, partitionEnd);//initializes List object which contains the sublist from i to i+dividedPixelCount

                subPixelList.sort(Comparator.comparingDouble(Color::getBrightness));//sorts

                randomPixelList.addAll(subPixelList);//adds whatever sorted pixels are in the sublist and adds that to the main ArrayList

                i = i + dividedPixelCount;//increments the loop forward by portion of the divide

            }
        }

        //randomPixelList = shiftPixelList(randomPixelList);//offset method

        return randomPixelList;
    }

    /*//method to offset the pixel list
    public ArrayList<Color> shiftPixelList(ArrayList<Color> unShiftedList){

        ArrayList<Color> shiftedList = new ArrayList<>();

        int frst = unShiftedList.size()/7;
        int scnd = unShiftedList.size()/12;
        int thrd = unShiftedList.size()/15;
        int frth = unShiftedList.size()/22;
        int ffth = unShiftedList.size()/27;

        int[] shiftBy = new int [5];

        shiftBy[0] = frst;
        shiftBy[1] = scnd;
        shiftBy[2] = thrd;
        shiftBy[3] = frth;
        shiftBy[4] = ffth;

        int randPick = (int) (Math.random() * 4 - 0);

        List<Color> subShiftPixelList1 = unShiftedList.subList(shiftBy[randPick], unShiftedList.size());

        shiftedList.addAll(subShiftPixelList1);

        List<Color> subShiftPixelList2 = unShiftedList.subList(0,shiftBy[randPick]);

        shiftedList.addAll(subShiftPixelList2);

        return shiftedList;
    }*/

    //event handling methods

    EventHandler<ActionEvent> imgLoadEventListener = e -> {

        FileChooser fileChooser = new FileChooser();
        File openedFile = fileChooser.showOpenDialog(null);

        openImg = new Image(openedFile.toURI().toString());

        view.setImage(openImg);

    };

    EventHandler<ActionEvent> imgSaveEventListener = f -> {

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

    EventHandler<ActionEvent> imgRestoreEventListener = g -> {

        view.setImage(openImg);

    };

    EventHandler<ActionEvent> imgInvertEventListener = h -> {

        invertImg(view.getImage(), view);

    };

    EventHandler<ActionEvent> imgRandomSortEventListener = i -> {

        int mode = 1;

        editSortImg(view.getImage(), view, mode);

    };

    EventHandler<ActionEvent> imgBrightSortEventListener = j -> {

        int mode = 2;

        editSortImg(view.getImage(), view, mode);

    };

    EventHandler<ActionEvent> imgSaturationSortEventListener = k -> {

        int mode = 3;

        editSortImg(view.getImage(), view, mode);

    };

    EventHandler<ActionEvent> imgHueSortEventListener = l -> {

        int mode = 4;

        editSortImg(view.getImage(), view, mode);

    };

}