package java_texteditor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.Pair;

public class Java_TextEditor extends Application {
    private int width = 600;
    private int height = 500;
    private String text = "Type here....";
    private String fileName = "null";
    public static Settings settings;
    TextArea textArea = new TextArea();
    Scene scene;
    Stage primaryStage;
    BorderPane root = new BorderPane();
    File file;
    Label datetime;
    Label name;
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    ListView<String> list_of_fonts = new ListView<String>();
    ListView<String> list_of_styles = new ListView<String>();
    ListView<Double> list_of_sizes = new ListView<Double>();
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        MenuBar menuBar = createMenus();
        ToolBar toolBar = createToolBar();
        HBox statusBar = getStatusBar();
        createScene();
        VBox vBox = new VBox();
        vBox.getChildren().add(menuBar);
        vBox.getChildren().add(toolBar);
        root.setBottom(statusBar);
        root.setTop(vBox);
        root.setCenter(textArea);
        
        primaryStage.setTitle("Text Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
        //handles when the editor window is closed
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override
            public void handle(WindowEvent e){
                onClose(e);
            }
        });
    }
    private HBox getStatusBar(){
        HBox statusBar = new HBox();
        Label caretPosition = getCaretPosition();
        datetime = new Label();
        name = new Label(fileName);
        initClock();
        statusBar.getChildren().addAll(name, new Separator(), datetime, 
                new Separator(), caretPosition);
        return statusBar;
    }
    private Label getCaretPosition(){
        Label caretPosition = new Label();
        textArea.caretPositionProperty().addListener((param1, param2, param3)->{
            String s = textArea.getText().toString();
            String[] arr = s.split("\\n", -1);
            int caret = textArea.getCaretPosition();
            int count = 0, row = 0, col = 0, temp = 0;
            //finds the row that the user is on
            if(param2 != param3){
                for(int i = 0; i < arr.length; i++){
                    count += arr[i].length();
                    if(caret <= count){
                        temp = count - caret;
                        col = arr[i].length() - temp;
                    }
                    if(caret <= count){
                        row = i + 1;
                        break;
                    }
                    count++;
                }
            }
            caretPosition.setText(row + ":" + col);
        });
        return caretPosition;
    }
    public void initClock() {
        Timeline timeline = new Timeline(new KeyFrame(
        Duration.millis(1000),
            ae -> setDateTime()));
        timeline.setCycleCount( Animation.INDEFINITE );
        timeline.play();
    }
    //Sets the text for the dateTime Label
    public void setDateTime(){
          Format formatter = new SimpleDateFormat("HH:mm:ss");
          Date date = new Date();
          datetime.setText(formatter.format(date));
    }
    //creates a toolbar with icons for common operations
    public ToolBar createToolBar(){
        //creates buttons for toolbar
        Button cutButton = createCutButton();
        Button copyButton = createCopyButton();
        Button pasteButton = createPasteButton();
        Button saveButton = createSaveButton();
        Button openButton = createOpenButton();

        ToolBar toolBar = new ToolBar(cutButton, copyButton, new Separator(),
                pasteButton, saveButton, openButton);
        return toolBar;
    }
    public Button createCutButton(){
        Button button = new Button();
        Image cut_icon = new Image(getClass().getResourceAsStream("cut.png"), 
            20, 20, true, true);
        button.setGraphic(new ImageView(cut_icon));
        button.setTooltip(new Tooltip("Cut"));
        DropShadow shadow = new DropShadow();
        //Adding the shadow when the mouse cursor is on
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, 
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    button.setEffect(shadow);
                }
        });
        //Removing the shadow when the mouse cursor is off
        button.addEventHandler(MouseEvent.MOUSE_EXITED, 
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    button.setEffect(null);
                }
        });
        //Cut selected text 
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            @Override 
            public void handle(MouseEvent e){
                cut();
            }
        });
        return button;
    }
    public Button createCopyButton(){
        Button button = new Button();
        Image copy_icon = new Image(getClass().getResourceAsStream("copy.png"), 
            20, 20, true, true);
        button.setGraphic(new ImageView(copy_icon));
        button.setTooltip(new Tooltip("Copy"));
        DropShadow shadow = new DropShadow();
        //Adding the shadow when the mouse cursor is on
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, 
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    button.setEffect(shadow);
                }
        });
        //Removing the shadow when the mouse cursor is off
        button.addEventHandler(MouseEvent.MOUSE_EXITED, 
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    button.setEffect(null);
                }
        });
        //Copy contents to clipboard
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            @Override 
            public void handle(MouseEvent e){
                String selectedText = textArea.getSelectedText();
                copy(selectedText);
            }
        });
       
        return button;
    }
    public Button createPasteButton(){
        Button button = new Button();
        Image paste_icon = new Image(getClass().getResourceAsStream("paste.png"), 
            20, 20, true, true);
        button.setGraphic(new ImageView(paste_icon));
        button.setTooltip(new Tooltip("Paste"));
        DropShadow shadow = new DropShadow();
        //Adding the shadow when the mouse cursor is on
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, 
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    button.setEffect(shadow);
                }
        });
        //Removing the shadow when the mouse cursor is off
        button.addEventHandler(MouseEvent.MOUSE_EXITED, 
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    button.setEffect(null);
                }
        });
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            @Override 
            public void handle(MouseEvent e){
                textArea.replaceSelection(clipboard.getString());
            }
        });
        return button;
    }
    public Button createSaveButton(){
        Button button = new Button();
        Image save_icon = new Image(getClass().getResourceAsStream("save.png"), 
            20, 20, true, true);
        button.setGraphic(new ImageView(save_icon));
        button.setTooltip(new Tooltip("Save"));
        DropShadow shadow = new DropShadow();
        //Adding the shadow when the mouse cursor is on
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, 
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    button.setEffect(shadow);
                }
        });
        //Removing the shadow when the mouse cursor is off
        button.addEventHandler(MouseEvent.MOUSE_EXITED, 
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    button.setEffect(null);
                }
        });
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            @Override 
            public void handle(MouseEvent e){
                try{
                    if(file != null){
                        saveFile(textArea.getText(), file);
                    }else{
                        saveAsFile(textArea.getText(), file);
                    }
               } catch(IOException ev){
                   System.out.println(ev);
               }
                
            }
        });
        return button;
    }
    public Button createOpenButton(){
        Button button = new Button();
        Image open_icon = new Image(getClass().getResourceAsStream("open.png"), 
            20, 20, true, true);
        button.setGraphic(new ImageView(open_icon));
        button.setTooltip(new Tooltip("Open"));
        DropShadow shadow = new DropShadow();
        //Adding the shadow when the mouse cursor is on
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, 
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    button.setEffect(shadow);
                }
        });
        //Removing the shadow when the mouse cursor is off
        button.addEventHandler(MouseEvent.MOUSE_EXITED, 
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    button.setEffect(null);
                }
        });
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            @Override 
            public void handle(MouseEvent e){
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open");
                File file1 = fileChooser.showOpenDialog(primaryStage);
                if(file1 != null){
                    openFile(file1);
                    file = file1;
                }
            }
        });
        return button;
    }
    
    public Menu createFileMenu(){
        Menu menuFile = new Menu("_File");
        MenuItem newItem = new MenuItem("_New");
        MenuItem openItem = new MenuItem("_Open");
        MenuItem saveItem = new MenuItem("_Save");
        MenuItem saveAsItem = new MenuItem("Save _As");
        MenuItem printItem = new MenuItem("_Print");
        MenuItem pageSetupItem = new MenuItem("Page Set_up");
        MenuItem exitItem = new MenuItem("E_xit");
        
        //add file item accelerators
        newItem.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        openItem.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        saveItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        printItem.setAccelerator(KeyCombination.keyCombination("Ctrl+P"));
        
        //create action handlers for Save, Open, Exit 
        openItem.setOnAction(new EventHandler<ActionEvent>(){ 
           @Override
           public void handle(ActionEvent event){
               FileChooser fileChooser = new FileChooser();
               fileChooser.setTitle("Open");
               File file1 = fileChooser.showOpenDialog(primaryStage);
               if(file1 != null){
                   openFile(file1);
                   file = file1;
               }
           }
        });
        saveItem.setOnAction(new EventHandler<ActionEvent>(){ 
           @Override
           public void handle(ActionEvent event){
               try{
                    if(file != null){
                        saveFile(textArea.getText(), file);
                    }else{
                        saveAsFile(textArea.getText(), file);
                    }
               } catch(IOException e){
                   System.out.println(e);
               }
               
           }
        });
        saveAsItem.setOnAction(new EventHandler<ActionEvent>(){ 
           @Override
           public void handle(ActionEvent event){
                try{
                    saveAsFile(textArea.getText(), file);
                } catch(IOException e){
                    System.out.println(e);
                }
           }
        });
        exitItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                System.exit(0);
            }
        });
        
        //add all file menu items to the menu
        menuFile.getItems().addAll(newItem, openItem, saveItem, 
                saveAsItem, new SeparatorMenuItem(),  pageSetupItem,
                printItem, new SeparatorMenuItem(), exitItem);
        return menuFile;
    }
    public Menu createEditMenu(){
        Menu menuEdit = new Menu("_Edit");
        MenuItem undoItem = new MenuItem("_Undo");
        MenuItem redoItem = new MenuItem("Redo");
        MenuItem cutItem = new MenuItem("Cu_t");
        MenuItem copyItem = new MenuItem("_Copy");       
        MenuItem pasteItem = new MenuItem("_Paste");
        MenuItem deleteItem = new MenuItem("De_lete");
        MenuItem findItem = new MenuItem("_Find...");
        MenuItem findNextItem = new MenuItem("Find _Next");
        MenuItem replaceItem = new MenuItem("_Replace...");
        MenuItem goToItem = new MenuItem("_Go To...");
        MenuItem selectAllItem = new MenuItem("Select _All");
        MenuItem timeDateItem = new MenuItem("Time/_Date");

        deleteItem.setOnAction(new EventHandler<ActionEvent>(){ 
            @Override
            public void handle(ActionEvent event){
                textArea.replaceSelection("");
            }
         });
        cutItem.setOnAction(new EventHandler<ActionEvent>(){ 
            @Override 
            public void handle(ActionEvent e){
                cut();
            }
         });
        copyItem.setOnAction(new EventHandler<ActionEvent>(){ 
            @Override 
            public void handle(ActionEvent e){
                String selectedText = textArea.getSelectedText();
                copy(selectedText);
            }
         });
        pasteItem.setOnAction(new EventHandler<ActionEvent>(){ 
            @Override 
            public void handle(ActionEvent e){
                textArea.replaceSelection(clipboard.getString());
            }
         });
        //add edit item accelerators
        undoItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Z"));
        cutItem.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
        copyItem.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
        pasteItem.setAccelerator(KeyCombination.keyCombination("Ctrl+V"));
        deleteItem.setAccelerator(KeyCombination.keyCombination("Del"));
        findItem.setAccelerator(KeyCombination.keyCombination("Ctrl+F"));
        findNextItem.setAccelerator(KeyCombination.keyCombination("F3"));
        replaceItem.setAccelerator(KeyCombination.keyCombination("Ctrl+H"));
        goToItem.setAccelerator(KeyCombination.keyCombination("Ctrl+G"));
        selectAllItem.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));
        timeDateItem.setAccelerator(KeyCombination.keyCombination("F5"));

        menuEdit.getItems().addAll(undoItem, new SeparatorMenuItem(), redoItem, 
                cutItem, copyItem, pasteItem, deleteItem, new SeparatorMenuItem(),
                findItem, findNextItem, replaceItem, goToItem, new SeparatorMenuItem(),
                selectAllItem, timeDateItem);
        return menuEdit;
    }
    public Menu createFormatMenu(){
        Menu menuFormat = new Menu("_Format");
        MenuItem wordWrapItem = new MenuItem("_Word Wrap");
        MenuItem fontItem = new MenuItem("_Font...");
        fontItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                fontPane();
            }
        });
        menuFormat.getItems().addAll(wordWrapItem, fontItem);
        return menuFormat;
    }
 
    public void fontPane(){
        DialogPane dip = new DialogPane();
        Dialog<Pair<String, String>> dialog = new Dialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialog.setTitle("Font");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        TextArea sample = new TextArea("AaBbYyZz");
        sample.setEditable(false);
        dip.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setDialogPane(dip);
        dialog.getDialogPane().setContent(grid);
        
        //gets list of available fonts
        ObservableList<String> fonts = FXCollections.observableArrayList (
            javafx.scene.text.Font.getFamilies());
        //gets list of font styles
        ObservableList<String> fontStyle = FXCollections.observableArrayList (
            javafx.scene.text.Font.getFontNames(textArea.getFont().getFamily()));
        //gets list of sizes
        ObservableList<Double> size = getFontSizes();
        
        list_of_sizes = new ListView<Double>(size);
        
        list_of_fonts.setItems(fonts);
        list_of_styles.setItems(fontStyle);
        
        list_of_fonts.getSelectionModel().select(fonts.indexOf(textArea.getFont().getFamily()));
        list_of_fonts.scrollTo(fonts.indexOf(textArea.getFont().getFamily()));
        
        list_of_styles.getSelectionModel().select(fontStyle.indexOf(textArea.getFont().getName()));
        list_of_styles.scrollTo(fontStyle.indexOf(textArea.getFont().getName()));

        list_of_sizes.getSelectionModel().select(size.indexOf(textArea.getFont().getSize()));
        list_of_sizes.scrollTo(size.indexOf(textArea.getFont().getSize()));
        
        //gets list of font styles form selected font
        list_of_fonts.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, 
                    String old_val, String new_val) {
                        ObservableList<String> fontStyle = FXCollections.observableArrayList (
                             javafx.scene.text.Font.getFontNames(new_val)); 
                        list_of_styles.setItems(fontStyle);
                        list_of_styles.getSelectionModel().select(fontStyle.get(0));
                        Font new_font = new Font(list_of_styles.getSelectionModel().getSelectedItem(),
                            list_of_sizes.getSelectionModel().getSelectedItem());
                        sample.setFont(new_font);
                        stage.show();
                }
        });

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                Font new_font = new Font(list_of_styles.getSelectionModel().getSelectedItem(),
                        list_of_sizes.getSelectionModel().getSelectedItem());
                sample.setFont(new_font);
                textArea.setFont(new_font);
            }
            return null;
        });
        //adds search bars for font, style, size to gridpane
        addSearchBars(grid, fonts, fontStyle, size);
        grid.add(list_of_fonts, 0, 1);
        grid.add(list_of_styles, 1, 1);
        grid.add(list_of_sizes, 2, 1);
        grid.add(sample, 2, 2);
        dialog.showAndWait();
    }
    private void addSearchBars(GridPane grid, ObservableList<String> fonts, 
            ObservableList<String> styles, ObservableList<Double> sizes){
        TextField search_fonts = getSearchFontBar(fonts);
        TextField search_styles = getSearchStyleBar(styles);
        TextField search_sizes = getSearchSizeBar(sizes);
        grid.add(search_fonts, 0, 0);
        grid.add(search_styles, 1, 0);
        grid.add(search_sizes, 2, 0);
    }
    private TextField getSearchFontBar(ObservableList<String> fonts){
        TextField search_fonts = new TextField();
        search_fonts.textProperty().addListener(
            new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, 
                    String old_val, String new_val) {
                    boolean stop = false;
                    for(int i = 0; i < fonts.size();i++){
                        if(fonts.get(i).toLowerCase().contains(new_val)){
                            for(int j = 0; j < new_val.length(); j++){
                                if(fonts.get(i).toLowerCase().charAt(j) == new_val.charAt(j)){
                                    list_of_fonts.getSelectionModel().select(i);
                                    list_of_fonts.getFocusModel().focus(i);
                                    list_of_fonts.scrollTo(i);
                                    stop = true;
                                }
                                break;
                            }
                            if(stop){break;}
                        }
                    }    

                }
        });
        return search_fonts;
    }
    private TextField getSearchStyleBar(ObservableList<String> styles){
        TextField search_styles = new TextField();
        search_styles.textProperty().addListener(
            new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, 
                    String old_val, String new_val) {
                    boolean stop = false;
                    for(int i = 0; i < styles.size();i++){
                        if(styles.get(i).toLowerCase().contains(new_val)){
                            for(int j = 0; j < new_val.length(); j++){
                                if(styles.get(i).toLowerCase().charAt(j) == new_val.charAt(j)){
                                    list_of_styles.getSelectionModel().select(i);
                                    list_of_styles.getFocusModel().focus(i);
                                    list_of_styles.scrollTo(i);
                                    stop = true;
                                }
                                break;
                            }
                            if(stop){break;}
                        }
                    }    

                }
        });
        return search_styles;
    }
    private TextField getSearchSizeBar(ObservableList<Double> sizes){
        TextField search_sizes = new TextField();
        search_sizes.textProperty().addListener(
            new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, 
                    String old_val, String new_val) {
                    try{
                        double entry = Double.parseDouble(new_val);
                        list_of_sizes.getSelectionModel().select(sizes.indexOf(entry));
                        list_of_sizes.getFocusModel().focus(sizes.indexOf(entry));
                        list_of_sizes.scrollTo(sizes.indexOf(entry));
                    }catch(NumberFormatException e){
                        System.out.println(e);
                    }

                }
        });
        return search_sizes;
    }
    private ObservableList getFontSizes(){
        List<Double> list = new ArrayList();
        for(double i = 1; i < 420; i++){
            list.add(i);
        }
        ObservableList<Double> size = (FXCollections.observableArrayList(list));
        return size;
    }
    public Menu createViewMenu(){
        Menu menuView = new Menu("_View");
        MenuItem statusBarItem = new MenuItem("_Status Bar");
        menuView.getItems().add(statusBarItem);
        return menuView;
    }
    public Menu createHelpMenu(){
        Menu menuHelp = new Menu("_Help");
        MenuItem viewHelpItem = new MenuItem("_View Help");
        MenuItem aboutItem = new MenuItem("_About Lab 7 Editor");
        menuHelp.getItems().addAll(viewHelpItem, new SeparatorMenuItem(), aboutItem);
        return menuHelp;
    }
    public MenuBar createMenus(){
        MenuBar menuBar = new MenuBar();
        //creates the File Menu and all sub-menus
        Menu menuFile = createFileMenu();
        //creates the Edit Menu and all sub-menus
        Menu menuEdit = createEditMenu();
        //creates the Format Menu and all sub-menus
        Menu menuFormat = createFormatMenu();
        //creates the View Menu and all sub-menus
        Menu menuView = createViewMenu();
        //creates the Help Menu and all sub-menus
        Menu menuHelp = createHelpMenu();
        
        menuBar.getMenus().addAll(menuFile, menuEdit,
                menuFormat, menuView, menuHelp);
        
        return menuBar;
    }
    
    public void createScene(){
        textArea.setPromptText(text);
        textArea.setFocusTraversable(false);
        scene = new Scene(root, width, height);
    }
    public void onClose(WindowEvent e){
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
    public static void displayPreviousFile(File file){
        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            Gson gson = new Gson();
            settings = gson.fromJson(br.readLine(), Settings.class);
         }catch(IOException e){
             System.out.println(e);
        } 
    }
    public void openFile(File file) {
        Gson gson = new Gson();
        try(FileReader fr = new FileReader(file)){
            fileName = file.getName();
            name.setText(fileName);
            BufferedReader br = new BufferedReader(fr);
            settings = gson.fromJson(br.readLine(), Settings.class);
            textArea.setText(settings.getText());
            primaryStage.setHeight(settings.getHeight());
            primaryStage.setWidth(settings.getWidth());
        }catch(IOException e){
            System.out.println(e);
        }
    }
    public void copy(String selectedText){
        content.putString(selectedText);
        clipboard.setContent(content);
    }
    
    public void saveAsFile(String contents, File file) 
            throws IOException{
        settings = new Settings();
        settings.setHeight(scene.getHeight());
        settings.setWidth(scene.getWidth());
        settings.setText(textArea.getText());
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = 
                new FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Save As");
        file = fileChooser.showSaveDialog(primaryStage);
        Gson gson = new Gson();
        String new_file = gson.toJson(settings);
            try(FileWriter fw = new FileWriter(file)){
                fw.write(new_file);
                fw.close();
            }catch(IOException e){
                System.out.println(e);
            } 
    }
    public void saveFile(String contents, File file)
            throws IOException{
        settings = new Settings();
        settings.setHeight(scene.getHeight());
        settings.setWidth(scene.getWidth());
        settings.setText(textArea.getText());
        Gson gson = new Gson();
        String new_file = gson.toJson(settings);
            try(FileWriter fw = new FileWriter(file)){
                fw.write(new_file);
                fw.close();
            }catch(IOException e){
                System.out.println(e);
            } 
    }
    public void cut(){
        String selectedText = textArea.getSelectedText();
        textArea.replaceSelection("");
        copy(selectedText);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
