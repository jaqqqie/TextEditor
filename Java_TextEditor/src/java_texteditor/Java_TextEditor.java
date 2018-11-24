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
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Java_TextEditor extends Application {
    private int width = 600;
    private int height = 500;
    private String text = "Type here....";
    public static Settings settings;
    TextArea textArea = new TextArea();
    Scene scene;
    Stage primaryStage;
    BorderPane root = new BorderPane();
    File file;
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        MenuBar menuBar = createMenus();
        root.setTop(menuBar);
        root.setCenter(textArea);
        createScene();
       
        ToolBar toolBar = createToolBar();
        
        root.setLeft(toolBar);
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
    
    public ToolBar createToolBar(){
        //creates buttons for toolbar
        Button cutButton = createCutButton();
        Button copyButton = createCopyButton();
        Button pasteButton = createPasteButton();
        Button saveButton = createSaveButton();
        Button openButton = createOpenButton();

        ToolBar toolBar = new ToolBar(cutButton, copyButton, new Separator(),
                pasteButton, saveButton, openButton);
        toolBar.setOrientation(Orientation.VERTICAL);
        return toolBar;
    }
    public Button createCutButton(){
        Button button = new Button();
        Image cut_icon = new Image(getClass().getResourceAsStream("cut.png"));
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
        Image copy_icon = new Image(getClass().getResourceAsStream("copy.png"));
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
        Image paste_icon = new Image(getClass().getResourceAsStream("paste.png"));
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
        Image save_icon = new Image(getClass().getResourceAsStream("save.png"));
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
        Image open_icon = new Image(getClass().getResourceAsStream("open.png"));
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
        menuFormat.getItems().addAll(wordWrapItem, fontItem);
        return menuFormat;
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

