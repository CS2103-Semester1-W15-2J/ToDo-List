package tasknote.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import tasknote.logic.TaskNoteControl;
import tasknote.shared.TaskObject;
import tasknote.shared.TaskObject.TASK_STATUS;

public class Scheduler {
    private TaskNoteControl _logic = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMMM yyyy");
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd MMMMM yyyy hh:mma"); 
    
    private int INTERVAL_SECOND_CHECK_OVERDUE_TASK = 5;
    
    public Scheduler(TaskNoteControl logic) {
        this._logic = logic;
    }
    
    public void runOutstandingTaskCheck() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(INTERVAL_SECOND_CHECK_OVERDUE_TASK), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                runCheck();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    private void runCheck() {
        ArrayList<TaskObject> displayList = _logic.getDisplayList();

        try {
            checkForOutstandingTasks(displayList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    private void checkForOutstandingTasks(ArrayList<TaskObject> taskObjectList) throws ParseException {
        // Iterate through array and check if it is outstanding
        long currentTimeInMillisecond = System.currentTimeMillis();
        
        for(TaskObject task : taskObjectList) {
            if(task.getTaskType() == TaskObject.TASK_TYPE_FLOATING || task.getTaskStatus() == TASK_STATUS.TASK_COMPLETED) {
                continue;
            }
            // TODO Use millisecond comparison in future
            String date = task.getFormattedDate();
            String time = task.getFormattedTime();
            
            Date dateObject = null;
            
            if(date.isEmpty() && time.isEmpty()) {
                continue;
            } else if(!date.isEmpty() && time.isEmpty()) {
                dateObject = dateFormat.parse(date);
            } else if(!date.isEmpty() && !time.isEmpty()) {
                dateObject = dateTimeFormat.parse(date + " " + time);
            }
            
            if(dateObject != null && dateObject.getTime() < currentTimeInMillisecond) {
                task.setTaskStatus(TASK_STATUS.TASK_OUTSTANDING);
            }
        }
    }
}
