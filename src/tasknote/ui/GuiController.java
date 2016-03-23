package tasknote.ui;

import static tasknote.ui.GuiConstant.COMMAND_ADD;
import static tasknote.ui.GuiConstant.DEFAULT_COMMAND;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tasknote.logic.TaskNoteControl;
import tasknote.shared.TaskObject;
import tasknote.shared.TaskObject.TASK_STATUS;

public class GuiController extends Application {
    private final String APPLICATION_NAME = "TaskNote";
    
    private final String APPLICATION_ICON_PATH = "resources/image/tasknote-icon.png";
    
    private final double WINDOW_MIN_WIDTH = 450.0;
    private final double WINDOW_MIN_HEIGHT = 450.0;

    private static CommandLineContainer _commandLineContainer = CommandLineContainer.getInstance();
    private static TextField _commandLine = _commandLineContainer.getCommandLine();
    private static TasksContainer _tasksContainer = TasksContainer.getInstance();
    private static ObservableList<TaskObject> _tasksListToBeDisplayed = _tasksContainer.getTasksList();
    private static FloatingTasksContainer _floatingTasksContainer = FloatingTasksContainer.getInstance();
    private static ObservableList<TaskObject> _floatingTasksListToBeDisplayed = _floatingTasksContainer.getFloatingTasksList();
    private static SidebarContainer _sidebarContainer = SidebarContainer.getInstance();
    private static ListView<String> _sidebarNavigation = _sidebarContainer.getNavigationList();
    
    private static TaskNoteControl _tasknoteControl = new TaskNoteControl();
    
    private static Stage _primaryWindow = null;
    
    @Override
    public void start(Stage stage) {
        _primaryWindow = stage;
        BorderPane frame = new BorderPane();
        Scene scene = new Scene(frame);
        
        scene.getStylesheets().add(getClass().getResource("resources/css/theme-monotone-dark.css").toExternalForm());
        
        frame.setTop(MainMenuContainer.getInstance());
        frame.setLeft(_sidebarContainer);
        frame.setCenter(_tasksContainer);
        frame.setRight(_floatingTasksContainer);
        frame.setBottom(_commandLineContainer);
        
        
        setSidebarNavigationBehaviour();
        // TODO
        _sidebarContainer.selectNavigationCell(1);
        displayOutstandingTaskList();
        
        stage.setTitle(APPLICATION_NAME);
        stage.setScene(scene);
        stage.getIcons().add(new Image(GuiController.class.getResourceAsStream(APPLICATION_ICON_PATH))); 
        stage.show();
        
        focusOnCommandLine();
        
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
        _sidebarContainer.selectNavigationCell(1);
        displayUpdatedTaskList();
        
        commandLine.setText(DEFAULT_COMMAND);
        commandLine.end();
        _commandLineContainer.clearLastModifiedCommand();
    }
    
    public static void executeCommand(String command) {
        if(command == null || command.trim().equals(COMMAND_ADD) || command.isEmpty()) {
            return;
        }
        
        // TODO
        String feedback = _tasknoteControl.executeCommand(command);
        Notification.setupNotification(_primaryWindow, feedback);
        _sidebarContainer.selectNavigationCell(1);
        displayUpdatedTaskList();
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
    
    private static void displayOutstandingTaskList() {
        ArrayList<TaskObject> displayList = _tasknoteControl.getDisplayList();
        
        for(int index = 0; index < displayList.size(); index++) {
            displayList.get(index).setTaskID(index + 1);
        }
        
        ArrayList<TaskObject> tasksList = new ArrayList<TaskObject>();
        ArrayList<TaskObject> floatsList = new ArrayList<TaskObject>();
        
        for(TaskObject task: displayList) {
            if(task.getTaskType().equals("floating") && task.getTaskStatus() == TASK_STATUS.TASK_DEFAULT) {
                floatsList.add(task);
            } else if (task.getTaskStatus() == TASK_STATUS.TASK_DEFAULT){
                tasksList.add(task);
            }
        }
        
        _tasksListToBeDisplayed.setAll(tasksList);
        _floatingTasksListToBeDisplayed.setAll(floatsList);
    }
    
    private static void displayOverdueTaskList() {
        ArrayList<TaskObject> displayList = _tasknoteControl.getDisplayList();
        
        for(int index = 0; index < displayList.size(); index++) {
            displayList.get(index).setTaskID(index + 1);
        }
        
        ArrayList<TaskObject> tasksList = new ArrayList<TaskObject>();
        ArrayList<TaskObject> floatsList = new ArrayList<TaskObject>();
        
        for(TaskObject task: displayList) {
            if(task.getTaskType().equals("floating") && task.getTaskStatus() == TASK_STATUS.TASK_OUTSTANDING) {
                floatsList.add(task);
            } else if (task.getTaskStatus() == TASK_STATUS.TASK_OUTSTANDING){
                tasksList.add(task);
            }
        }
        
        _tasksListToBeDisplayed.setAll(tasksList);
        _floatingTasksListToBeDisplayed.setAll(floatsList);
    }
    
    private static void displayCompletedTaskList() {
        ArrayList<TaskObject> displayList = _tasknoteControl.getDisplayList();
        
        for(int index = 0; index < displayList.size(); index++) {
            displayList.get(index).setTaskID(index + 1);
        }
        
        ArrayList<TaskObject> tasksList = new ArrayList<TaskObject>();
        ArrayList<TaskObject> floatsList = new ArrayList<TaskObject>();
        
        for(TaskObject task: displayList) {
            if(task.getTaskType().equals("floating") && task.getTaskStatus() == TASK_STATUS.TASK_COMPLETED) {
                floatsList.add(task);
            } else if (task.getTaskStatus() == TASK_STATUS.TASK_COMPLETED){
                tasksList.add(task);
            }
        }
        
        _tasksListToBeDisplayed.setAll(tasksList);
        _floatingTasksListToBeDisplayed.setAll(floatsList);
    }
    
    private void setSidebarNavigationBehaviour() {
        _sidebarNavigation.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                changeView(newSelection);
            }
        });
    }
    
    private void changeView(String selected){
        switch(selected) {
            case "View All":
                displayUpdatedTaskList();
                break;
            case "Outstanding":
                displayOutstandingTaskList();
                break;
            case "Overdue":
                displayOverdueTaskList();
                break;
            case "Completed":
                displayCompletedTaskList();
                break;
        }
    }
    
    public static void main(String[] argv) {
        launch(argv);
    }
}