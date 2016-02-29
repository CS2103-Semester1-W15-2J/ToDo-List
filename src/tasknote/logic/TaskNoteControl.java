package tasknote.logic;

import tasknote.logic.Commands.AddTask;
import tasknote.logic.Commands.DeleteTask;
import tasknote.logic.Commands.SearchTask;
import tasknote.logic.Commands.UpdateTask;
import tasknote.parser.Parser;
import tasknote.shared.TaskObject;
import tasknote.shared.COMMAND_TYPE;
import tasknote.shared.Constants;

import java.util.ArrayList;

public class TaskNoteControl {
	
	public static TaskNote taskNote;
	public static AddTask addTask;
	public static DeleteTask deleteTask;
	public static SearchTask searchTask;
	public static UpdateTask updateTask;
	
	
	public TaskNoteControl() {
		taskNote = new TaskNote();
		taskNote.loadTasks();
	}
	
	public ArrayList<TaskObject> getDisplayList() {
		return taskNote.getDisplayList();
	}
	
	/**
	 * This operation executes the user command
	 *
	 * @param User Command
	 * @return Status of Operation
	 */
	public String executeCommand(String userCommand){
		COMMAND_TYPE commandType = Parser.getCommandType(userCommand);
		String feedback = executeAction(commandType, userCommand);
		return feedback;
	}

	public static String executeAction(COMMAND_TYPE commandType, String userCommand){
		String response;
		switch (commandType) {
		case ADD:
			response = executeAdd(userCommand);
			break;
		case DELETE:
			response = executeDelete(userCommand);
			break;
		case SEARCH:
			response = executeSearch(userCommand);
			break;
		case UPDATE:
			response = executeUpdate(userCommand);
			break;
		case INVALID:
			response = Constants.WARNING_INVALID_COMMAND;
			break;
		case EXIT:
			System.exit(0);
		default:
			throw new Error("Unrecognized command type");
		}
		return response;
	}
	
	public static String executeAdd(String userCommand){
		TaskObject taskObject = Parser.parseAdd(userCommand);
		addTask = new AddTask(taskNote, taskObject);
		addTask.execute();
		addTask.refreshDisplay();
		String response = addTask.getFeedBack();
		return response;
	}
	
	public static String executeDelete(String userCommand){
		ArrayList<Integer> deleteIds = Parser.parseDelete(userCommand);
		deleteTask = new DeleteTask(taskNote, deleteIds);
		deleteTask.execute();
		deleteTask.refreshDisplay();
		String response = deleteTask.getFeedBack();
		return response;
	}
	
	public static String executeSearch(String userCommand){
		ArrayList<TaskObject> displayList = taskNote.getDisplayList();
		ArrayList<Integer> searchIds = Parser.parseSearch(userCommand, displayList);
		searchTask = new SearchTask(taskNote, searchIds);
		searchTask.execute();
		searchTask.refreshDisplay();
		String response = searchTask.getFeedBack();
		return response;
	}
	
	public static String executeUpdate(String userCommand){
		int updateTaskId = Parser.getUpdateTaskId(userCommand);
		ArrayList<TaskObject> displayList = taskNote.getDisplayList();
		TaskObject oldTaskObject = displayList.get(updateTaskId);
		TaskObject updatedTaskObject = Parser.parseUpdate(userCommand, oldTaskObject);
		updateTask = new UpdateTask(taskNote, updateTaskId, updatedTaskObject);
		updateTask.execute();
		updateTask.refreshDisplay();
		String response = updateTask.getFeedBack();
		return response;
	}
}
