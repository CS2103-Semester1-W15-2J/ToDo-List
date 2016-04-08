package tasknote.ui;

import static tasknote.ui.GuiConstant.PROPERTY_FONT_SIZE;
import static tasknote.ui.GuiConstant.PROPERTY_FONT_WEIGHT;
import static tasknote.ui.GuiConstant.SPACING_BETWEEN_COMPONENTS;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import tasknote.shared.TaskObject;
import tasknote.shared.TaskObject.TASK_STATUS;

public class TasksContainer extends HBox {
    
    private String CSS_CLASS_TASKS_CONTAINER = "tasks-container";
    private String CSS_CLASS_TASKS_LIST = "tasks-list";
    private String CSS_CLASS_TASKS_LIST_CELL = "tasks-list-cell";
    
    private static final String FORMAT_TASK_DATE_TIME_PROPERTY = "%n\t%1$s, %2$s";
    private static final String FORMAT_TASK_DATE_PROPERTY = "%n\t%1$s";
    private static final String FORMAT_TASK_LOCATION_PROPERTY = "%n\t%1$s";
    
    private static TasksContainer _tasksContainer = null;
    private ListView<TaskObject> _observableListRepresentation = new ListView<TaskObject>();
    private ObservableList<TaskObject> _tasksList = FXCollections.observableArrayList(
            taskobject-> new Observable[] {
                    taskobject.getObservableTaskStatus()
                });
    
    private static final Color LIGHT_GRAY = Color.rgb(150,141,143);
    private static final Color RED = Color.rgb(240, 100, 100);
    
    private TasksContainer() {
        // Only one instance of TasksContainer is permitted
    }
    
    /**
     * getInstance() allows user to get an instance of TasksContainer.
     * 
     * @return The one instance of TasksContainer.
     */
    public static TasksContainer getInstance() {
        if (_tasksContainer == null) {
            _tasksContainer = new TasksContainer();
            _tasksContainer.setupTasksContainer();
        }
        return _tasksContainer;
    }

    /**
     * getTasksList() allows user to get the observable list contained within
     * TasksContainer.
     * 
     * @return The ObservableList in TasksContainer.
     */
    public ObservableList<TaskObject> getTasksList() {
        return _tasksList;
    }

    /*
     * As per name, set up tasks container.
     */
    private void setupTasksContainer() {
        setTasksContainerPresentation();
        setTaskListPresentation();

        setTaskListBehaviour();

        this.getChildren().addAll(_observableListRepresentation);
    }

    /*
     * Set up the presentation of the tasks container.
     */
    private void setTasksContainerPresentation() {
        this.getStyleClass().add(CSS_CLASS_TASKS_CONTAINER);
        this.setSpacing(SPACING_BETWEEN_COMPONENTS);
    }

    /*
     * Set up the presentation of the (observable) list containing all the
     * tasks.
     */
    private void setTaskListPresentation() {
        _observableListRepresentation.getStyleClass().add(CSS_CLASS_TASKS_LIST);
        _observableListRepresentation.setItems(_tasksList);
        HBox.setHgrow(_observableListRepresentation, Priority.ALWAYS);
    }

    /*
     * Set up the behaviour of the (observable) list.
     */
    private void setTaskListBehaviour() {
        _observableListRepresentation.setCellFactory(new Callback<ListView<TaskObject>, ListCell<TaskObject>>() {
            @Override
            public ListCell<TaskObject> call(ListView<TaskObject> param) {
                return new ListCell<TaskObject>() {
                    @Override
                    public void updateItem(TaskObject task, boolean empty) {
                        super.updateItem(task, empty);
                        this.getStyleClass().add(CSS_CLASS_TASKS_LIST_CELL);
                        if (!isEmpty()) {
                            setGraphic(getFormattedText(task));
                        } else {
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };
            }
        });
    }
    
    public static TextFlow getFormattedText(TaskObject task) {        
        TASK_STATUS taskStatus = task.getTaskStatus();
        Text taskIndex = null;
        Text taskNameValue = new Text(task.getTaskName());
        Text taskDateTimeValue = null;
        Text taskLocationValue = null;
        Text taskEndDateTimeValue = null;
        
        String taskDate = task.getFormattedDate();
        String taskTime = task.getFormattedTime();
        String taskLocation = task.getLocation();
        String taskEndDate = task.getFormattedEndDate();
        String taskEndTime = task.getFormattedEndTime();
        
        if(task.getTaskID() > 0) {
            taskIndex = new Text(task.getTaskID() + ". ");
            taskIndex.setStyle(String.format(PROPERTY_FONT_WEIGHT, "bold"));
        }
        
        
        
        if(!taskDate.isEmpty() && !taskTime.isEmpty()) {
            taskDateTimeValue = new Text(String.format(FORMAT_TASK_DATE_TIME_PROPERTY, taskDate, taskTime));
            taskDateTimeValue.setStyle(String.format(PROPERTY_FONT_SIZE, 10));
        } else if (!taskDate.isEmpty() && taskTime.isEmpty()){
            taskDateTimeValue = new Text(String.format(FORMAT_TASK_DATE_PROPERTY, taskDate));
            taskDateTimeValue.setStyle(String.format(PROPERTY_FONT_SIZE, 10));
        }
        
        if(taskLocation == null || !taskLocation.isEmpty()) {
            taskLocationValue = new Text(String.format(FORMAT_TASK_LOCATION_PROPERTY, taskLocation));
            taskLocationValue.setStyle(String.format(PROPERTY_FONT_SIZE, 10));
        }
        
        if(!taskEndDate.isEmpty() && !taskEndTime.isEmpty()) {
            taskEndDateTimeValue = new Text(String.format(FORMAT_TASK_DATE_TIME_PROPERTY, taskEndDate, taskEndTime));
            taskEndDateTimeValue.setStyle(String.format(PROPERTY_FONT_SIZE, 10));
        } else if (!taskEndDate.isEmpty() && taskEndTime.isEmpty()){
            taskEndDateTimeValue = new Text(String.format(FORMAT_TASK_DATE_PROPERTY, taskEndDate));
            taskEndDateTimeValue.setStyle(String.format(PROPERTY_FONT_SIZE, 10));
        }
        
        return colorise(taskStatus, taskIndex, taskNameValue, taskDateTimeValue, taskLocationValue, taskEndDateTimeValue);
    }
    
    private static TextFlow colorise(TASK_STATUS status, Text taskIndex, Text taskNameValue, Text taskDateTimeValue, Text taskLocationValue, Text taskEndDateTimeValue) {
        TextFlow colorisedText = new TextFlow();
        
        colorisedText.setPrefWidth(0);
        
        switch(status) {
            case TASK_OUTSTANDING:
                if(taskIndex != null) {
                    taskIndex.setFill(RED);
                }
                taskNameValue.setFill(RED);
                if(taskDateTimeValue != null) {
                    taskDateTimeValue.setFill(RED);
                }
                if(taskLocationValue != null) {
                    taskLocationValue.setFill(RED);
                }
                if(taskEndDateTimeValue != null) {
                    taskEndDateTimeValue.setFill(RED);
                }
                break;
            case TASK_COMPLETED:
                if(taskIndex != null) {
                    taskIndex.setFill(Color.GRAY);
                }
                taskNameValue.setFill(Color.GRAY);
                if(taskDateTimeValue != null) {
                    taskDateTimeValue.setFill(Color.GRAY);
                }
                if(taskLocationValue != null) {
                    taskLocationValue.setFill(Color.GRAY);
                }
                if(taskEndDateTimeValue != null) {
                    taskEndDateTimeValue.setFill(Color.GRAY);
                }
                break;
            case TASK_DEFAULT:
            default:
                if(taskIndex != null) {
                    taskIndex.setFill(LIGHT_GRAY);
                }
                taskNameValue.setFill(Color.BLACK);
                if(taskDateTimeValue != null) {
                    taskDateTimeValue.setFill(Color.MAROON);
                }
                if(taskLocationValue != null) {
                    taskLocationValue.setFill(Color.MAROON);
                }
                if(taskEndDateTimeValue != null) {
                    taskEndDateTimeValue.setFill(Color.MAROON);
                }
                break;
        }
        
        if(taskIndex != null) {
            colorisedText.getChildren().addAll(taskIndex);
        } 
        colorisedText.getChildren().addAll(taskNameValue);
        
        if(taskDateTimeValue != null) {
            colorisedText.getChildren().addAll(taskDateTimeValue);
        } 
        
        if(taskEndDateTimeValue != null) {
            colorisedText.getChildren().addAll(taskEndDateTimeValue);
        } 
        
        if (taskLocationValue != null) {
            colorisedText.getChildren().addAll(taskLocationValue);
        }
        
        colorisedText.autosize();
        
        return colorisedText;
    }
}
