package java_texteditor;

import javafx.application.Application;;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class Java_TextEditor extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        //creates a new text area for the user
        TextArea text = new TextArea();
        //adding a prompt to the inital text box
        text.setPromptText("Type here....");
        
        Scene scene = new Scene(text, 300, 250);
        //setting the title of the textarea
        primaryStage.setTitle("Text Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
