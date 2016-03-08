package tasknote.ui;

import static tasknote.ui.GuiConstant.COMMAND_ADD;
import static tasknote.ui.GuiConstant.DEFAULT_COMMAND;
import static tasknote.ui.GuiConstant.PROPERTY_FONT_SIZE;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tasknote.logic.TaskNoteControl;
import tasknote.shared.TaskObject;

public class GuiController extends Application {
    private final String APPLICATION_NAME = "TaskNote";
    
    private final double WINDOW_MIN_WIDTH = 900.0;
    private final double WINDOW_MIN_HEIGHT = 450.0;
    private static int FONT_SIZE_APPLICATION = 12;

    private static CommandLineContainer _commandLineContainer = CommandLineContainer.getInstance();
    private static TextField _commandLine = _commandLineContainer.getCommandLine();

    private static TasksContainer _tasksContainer = TasksContainer.getInstance();
    private static ObservableList<TaskObject> _tasksListToBeDisplayed = _tasksContainer.getTasksList();

    private static FloatingTasksContainer _floatingTasksContainer = FloatingTasksContainer.getInstance();
    private static ObservableList<TaskObject> _floatingTasksListToBeDisplayed = _floatingTasksContainer.getFloatingTasksList();
    
    private static SidebarContainer _sidebarContainer = SidebarContainer.getInstance();
    
    private static TaskNoteControl _tasknoteControl = new TaskNoteControl();
    
    private static Stage _primaryWindow = null;
    
    @Override
    public void start(Stage stage) {
        _primaryWindow = stage;
        BorderPane frame = new BorderPane();
        Scene scene = new Scene(frame);
        
        frame.setStyle(String.format(PROPERTY_FONT_SIZE, FONT_SIZE_APPLICATION));
        
        frame.setTop(MainMenuContainer.getInstance());
        frame.setLeft(_sidebarContainer);
        frame.setCenter(_tasksContainer);
        frame.setRight(_floatingTasksContainer);
        frame.setBottom(_commandLineContainer);
        
        // TODO
        displayUpdatedTaskList();
        
        stage.setTitle(APPLICATION_NAME);
        stage.setScene(scene);
        stage.show();
        
        focusOnCommandLine();
        
        stage.setMaximized(true);
        stage.setMinWidth(WINDOW_MIN_WIDTH);
        stage.setMinHeight(WINDOW_MIN_HEIGHT);
    }
    
    public static void retrieveCommand(TextField commandLine) {
        String command = commandLine.getText();
        
        if(command.trim().equals(COMMAND_ADD)) {
            return;
        }
        
        // TODO
        String feedback = _tasknoteControl.executeCommand(command);
        Notification.setupNotification(_primaryWindow, feedback);
        displayUpdatedTaskList();
        
        commandLine.setText(DEFAULT_COMMAND);
        commandLine.end();
        _commandLineContainer.clearLastModifiedCommand();
    }
    
    private void focusOnCommandLine() {
        if (_commandLine != null) {
            _commandLine.requestFocus();
            _commandLine.end();
        }
    }
    
    private static void displayUpdatedTaskList() {
        ArrayList<TaskObject> displayList = _tasknoteControl.getDisplayList();
        
        for(int index = 0; index < displayList.size(); index++) {
            displayList.get(index).setTaskID(index + 1);
        }
        
        ArrayList<TaskObject> tasksList = new ArrayList<TaskObject>();
        ArrayList<TaskObject> floatsList = new ArrayList<TaskObject>();
        
        for(TaskObject task: displayList) {
            if(task.getTaskType().equals("floating")) {
                floatsList.add(task);
            } else {
                tasksList.add(task);
            }
        }
        
        _tasksListToBeDisplayed.setAll(tasksList);
        _floatingTasksListToBeDisplayed.setAll(floatsList);
    }
    
    public static void main(String[] argv) {
        launch(argv);
    }
}