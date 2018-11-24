package java_texteditor;

import javafx.application.Application;
import javafx.event.EventHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Java_TextEditor extends Application {
    private int width = 300;
    private int height = 250;
    private String text = "Type here....";
    public static Settings settings;
    @Override
    public void start(Stage primaryStage) {
        File file = new File("data.json");
        TextArea textArea = new TextArea();
        Scene scene;
        //creates scene based on previously closed file 
        //ELSE creates a new scene with predefined settings
        if(file.exists()){
            create(file);
            textArea.setText(settings.getText());
            scene = new Scene(textArea, 
                    settings.getWidth(), settings.getHeight());
        }else{
            textArea.setPromptText(text);
            scene = new Scene(textArea, width, height);
        }
        
        primaryStage.setTitle("GUI Lab 5");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            //handles when the window is closed
            @Override
            public void handle(WindowEvent e){
                //takes the settings of the closed window and
                //adds to settings class
                settings = new Settings();
                settings.setHeight(scene.getHeight());
                settings.setWidth(scene.getWidth());
                settings.setText(textArea.getText());
                GsonBuilder builder = new GsonBuilder();
                //created to change java code to json
                Gson gson = builder.create();
                try{
                    //takes on json code and writes it to file 
                    FileWriter writer = new FileWriter("data.json");
                    String input = gson.toJson(settings);
                    writer.write(input);
                    writer.close();
                }catch(IOException ex){
                    System.out.println(ex);
                }
            }
        });
    }
    public static void create(File file){
           try{
               FileReader fr = new FileReader(file);
               BufferedReader br = new BufferedReader(fr);
               Gson gson = new Gson();
               settings = gson.fromJson(br.readLine(), Settings.class);
            }catch(IOException e){
                System.out.println(e);
           } 
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
