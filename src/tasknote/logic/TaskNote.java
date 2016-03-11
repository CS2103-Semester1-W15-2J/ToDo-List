package tasknote.logic;

import tasknote.storage.Storage;
import tasknote.shared.TaskObject;
import tasknote.shared.COMMAND_TYPE;
import tasknote.shared.Constants;
import tasknote.logic.History.CommandHistory;
import tasknote.logic.History.CommandObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TaskNote {

	/*
	 * These are the various lists that will be used to store the TaskObjects,
	 * Search IDs of Tasks and Search results which will then be displayed to
	 * user
	 */
	private static ArrayList<TaskObject> taskList;
	private static ArrayList<TaskObject> searchList;
	private static ArrayList<TaskObject> displayList;

	private static CommandHistory history;

	/*
	 * This is the storage object that will be used to load tasks into the
	 * taskList and it will be called to save the tasks after each user
	 * operation
	 */
	private static Storage storage = new Storage();

	/*
	 * These integers are used to store the number of results retrieved upon
	 * user's search (searchIdSize) and the number of tasks to be deleted
	 * (deleteIdSize)
	 */
	private static int searchIdSize;
	private static int deleteIdSize;

	public TaskNote() {
		taskList = new ArrayList<TaskObject>();
		searchList = new ArrayList<TaskObject>();
		displayList = new ArrayList<TaskObject>();
		history = new CommandHistory();
	}

	/**
	 * This operation loads the tasks from the storage after each time the
	 * application is opened
	 *
	 */
	public void loadTasks() {
		try {
			storage = new Storage();
			taskList = storage.loadTasks();
		} catch (Exception e) {
			taskList = new ArrayList<TaskObject>();
		}
		refreshDisplay(taskList);
	}

	/**
	 * This operation returns the taskList containing Task Objects
	 * 
	 * @return List of Tasks
	 */
	public ArrayList<TaskObject> getTaskList() {
		return taskList;
	}

	/**
	 * This operation returns the Search List containing Task Objects that
	 * matched the User's search
	 * 
	 * @return List of Tasks that matched User's search
	 */
	public ArrayList<TaskObject> getSearchList() {
		return searchList;
	}

	/**
	 * This method is called by UI after each User Operation to display the list
	 * of tasks to the User
	 *
	 * @return List of Tasks to be displayed to the User
	 */
	public ArrayList<TaskObject> getDisplayList() {
		return displayList;
	}

	/**
	 * This operation reinitializes the Search List to a new list
	 * 
	 */
	public void reIntializeSearchList() {
		searchList = new ArrayList<TaskObject>();
	}

	/**
	 * This operation refreshes the list of task to be displayed to the user
	 * after each user operation
	 *
	 * @param List
	 *            of Tasks to be displayed to the User
	 */
	public void refreshDisplay(ArrayList<TaskObject> list) {
		displayList = new ArrayList<TaskObject>();
		for (int i = 0; i < list.size(); i++) {
			displayList.add(list.get(i));
		}
	}

	/**
	 * This operation adds a taskObject to the ArrayList of TaskObjects, sorts
	 * it based on Date and Time and saves it in the Storage
	 *
	 * @param task
	 *            object
	 * @return Status of Operation
	 */
	public String addTask(TaskObject taskObject) {
		boolean isSuccess = true;
		try {
			taskList.add(taskObject);
			sortAndSave(taskList);
			history.pushAddToUndo(taskObject);
		} catch (Exception e) {
			isSuccess = false;
		}
		return showFeedback(COMMAND_TYPE.ADD, isSuccess, taskObject);
	}

	/**
	 * This operation deletes a task in the ArrayList of TaskObjects and saves
	 * it in the Storage
	 *
	 * @param Id
	 *            of the Task stored in ArrayList
	 * @return Status of the operation
	 */
	public String deleteTask(ArrayList<Integer> deleteIds) {
		deleteIdSize = deleteIds.size();
		boolean isSuccess = isValidIdList(deleteIds);
		if (isSuccess) {
			try {
				deleteFromTaskList(deleteIds);
				storage.saveTasks(taskList);
			} catch (Exception e) {
				isSuccess = false;
			}
		}
		return showFeedback(COMMAND_TYPE.DELETE, isSuccess, null);
	}

	/**
	 * This operation searches retrieves all relevant tasks based on the given
	 * IDs from the ArrayList of TaskObjects.
	 *
	 * @param userCommand
	 * @return status of the operation
	 */
	public String searchTasks(ArrayList<Integer> searchIds) {
		boolean isSuccess = true;
		searchIdSize = searchIds.size();
		try {
			for (int i = 0; i < searchIds.size(); i++) {
				searchList.add(taskList.get(searchIds.get(i)));
			}
		} catch (Exception e) {
			isSuccess = false;
		}
		return showFeedback(COMMAND_TYPE.SEARCH, isSuccess, null);
	}

	/**
	 * This operation removes the old task from the taskList, adds the updated
	 * task into the taskList, sorts and saves the list
	 *
	 * @param userCommand
	 * @return status of the operation
	 */
	public String updateTask(int updateTaskId, TaskObject updatedTaskObject) {
		boolean isSuccess = isValidTaskId(updateTaskId);
		if (isSuccess && updatedTaskObject != null) {
			try {
				TaskObject oldTaskObject = taskList.remove(updateTaskId);
				taskList.add(updateTaskId, updatedTaskObject);
				sortAndSave(taskList);
				history.pushUpdateToUndo(oldTaskObject, updatedTaskObject);
			} catch (Exception e) {
				isSuccess = false;
			}
		}
		return showFeedback(COMMAND_TYPE.UPDATE, isSuccess, updatedTaskObject);
	}

	/**
	 * This operation reverts the action executed by the last command
	 * 
	 * @return status of the operation
	 */
	public String undoLastCommand() {
		boolean isSuccess = true;
		int undoCount = 0;
		try {	
			CommandObject commandObject = history.peekUndoStack();
			int numPrecedingObjects = commandObject.getPrecedingObjects();

			while(undoCount <= numPrecedingObjects) {
				commandObject = history.popUndoStack();
				COMMAND_TYPE commandType = commandObject.getRevertCommandType();
				if(commandType == COMMAND_TYPE.ADD) {
					TaskObject taskObject = commandObject.getTaskObject();
					history.pushDeleteToRedo(taskObject);
					taskList.add(taskObject);
				}else if(commandType == COMMAND_TYPE.DELETE) {
					TaskObject taskObject = commandObject.getTaskObject();
					history.pushAddToRedo(taskObject);
					taskList.remove(taskObject);
				}else if(commandType == COMMAND_TYPE.UPDATE) {
					CommandObject oldObject = history.popUndoStack();
					CommandObject newObject = history.popUndoStack();
					TaskObject oldTaskObject = oldObject.getTaskObject();
					TaskObject newTaskObject = newObject.getTaskObject();
					history.pushUpdateToRedo(oldTaskObject, newTaskObject);
					history.pushAddToUndo(newTaskObject);
					history.pushDeleteToUndo(oldTaskObject);
				}

				undoCount++;
			}
			history.peekRedoStack().setPrecedingObjects(numPrecedingObjects);
			
			sortAndSave(taskList);
		} catch (Exception e) {
			isSuccess = false;
		}
		
		return showFeedback(COMMAND_TYPE.UNDO, isSuccess, null);
	}
	
	public String redoLastUndoCommand() {
		boolean isSuccess = true;
		int redoCount = 0;
		try {
			CommandObject commandObject = history.peekRedoStack();
			int numPrecedingObjects = commandObject.getPrecedingObjects();
			while(redoCount <= numPrecedingObjects) {
				commandObject = history.popRedoStack();
				COMMAND_TYPE commandType = commandObject.getRevertCommandType();
				if(commandType == COMMAND_TYPE.ADD) {
					TaskObject taskObject = commandObject.getTaskObject();
					taskList.add(taskObject);
				}else if(commandType == COMMAND_TYPE.DELETE) {
					TaskObject taskObject = commandObject.getTaskObject();
					taskList.remove(taskObject);
				}else if(commandType == COMMAND_TYPE.UPDATE) {
					//pass - do nothing
				}
				redoCount++;
			}
			
			sortAndSave(taskList);
		} catch (Exception e) {
			isSuccess = false;
		}
		return showFeedback(COMMAND_TYPE.REDO, isSuccess, null);
	}

	/**
	 * This operation sets the completion status of the task to be true
	 *
	 * @param Task
	 *            Object
	 * @return status of the operation
	 */
	public String markTaskAsCompleted(TaskObject taskObject) {
		boolean isSuccess = true;
		try {
			taskObject.setIsMarkedDone(isSuccess);
			sortAndSave(taskList);
		} catch (Exception e) {
			isSuccess = false;
		}
		return showFeedback(COMMAND_TYPE.DONE, isSuccess, taskObject);
	}

	public boolean isValidIdList(ArrayList<Integer> idList) {
		boolean isValid = true;
		if (deleteIdSize > 0) {
			for (int i = 0; i < idList.size(); i++) {
				int taskId = idList.get(i);
				if (!isValidTaskId(taskId)) {
					isValid = false;
					break;
				}
			}
		} else {
			isValid = false;
		}
		return isValid;
	}

	public static void deleteFromTaskList(ArrayList<Integer> deleteIds) {
		for (int i = 0; i < deleteIds.size(); i++) {
			TaskObject taskObject = displayList.get(deleteIds.get(i));
			int index = taskList.indexOf(taskObject);
			taskList.remove(index);
			history.pushDeleteToUndo(taskObject);
		}
		CommandObject commandObject = history.peekUndoStack();
		commandObject.setPrecedingObjects(deleteIdSize - Constants.DECREMENT_PRECEDING_OBJECTS);
	}

	public boolean isValidTaskId(int taskId) {
		boolean isValid = true;
		if (taskId >= displayList.size() || taskId < Constants.EMPTY_LIST_SIZE) {
			isValid = false;
		}
		return isValid;
	}

	/**
	 * This operation sorts the list of Tasks and saves them to Storage
	 *
	 * @param Task
	 *            List
	 * @throws Exception
	 */
	private static void sortAndSave(ArrayList<TaskObject> taskList) throws Exception {
		try {
			sortByDate(taskList);
			storage.saveTasks(taskList);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * This operation sorts all tasks based on the date-time of the task
	 *
	 * @param list
	 *            to be sorted
	 * @throws Exception
	 */
	private static void sortByDate(ArrayList<TaskObject> list) throws Exception {
		// TODO
		try {
			// Sort by Date - Currently sorted by Task Name
			Collections.sort(list, new Comparator<TaskObject>(){
			    public int compare(TaskObject taskObject1, TaskObject taskObject2) {
			        return taskObject1.getTaskName().compareToIgnoreCase(taskObject2.getTaskName());
			    }
			});
		} catch (Exception e) {
			System.out.println("Sort by date has an error");
			throw e;
		}
	}

	/**
	 * This operation constructs the feedback to be displayed to the User after
	 * each User Operation
	 *
	 * @param Command
	 *            type, isSuccess(true if User operation is executed
	 *            successfully; Otherwise false), Task Object
	 *
	 * @return Feedback to the User
	 */
	private static String showFeedback(COMMAND_TYPE commandType, boolean isSuccess, TaskObject task) {

		switch (commandType) {
		case ADD:
			if (isSuccess && task != null) {
				int taskIndex = taskList.indexOf(task);
				String taskName = task.getTaskName();
				return String.format(Constants.MESSAGE_ADD_SUCCESSFUL, ++taskIndex, taskName);
			} else {
				return Constants.MESSAGE_ADD_UNSUCCESSFUL;
			}
		case DELETE:
			if (isSuccess) {
				return String.format(Constants.MESSAGE_DELETE_SUCCESSFUL, deleteIdSize);
			} else {
				return Constants.MESSAGE_DELETE_UNSUCCESSFUL;
			}
		case SEARCH:
			if (isSuccess) {
				return String.format(Constants.MESSAGE_SEARCH_SUCCESSFUL, searchIdSize);
			} else {
				return Constants.MESSAGE_SEARCH_UNSUCCESSFUL;
			}
		case UPDATE:
			if (isSuccess && task != null) {
				// TODO: Feedback which fields were updated
				return Constants.MESSAGE_UPDATE_SUCCESSFUL;
			} else {
				// TODO
				return Constants.MESSAGE_UPDATE_UNSUCCESSFUL;
			}
		case UNDO:
			if (isSuccess) {
				// TODO: Feedback what was undone
				return Constants.MESSAGE_UNDO_SUCCESSFUL;
			} else {
				// TODO
				return Constants.MESSAGE_UNDO_UNSUCCESSFUL;
			}
		case REDO:
			if (isSuccess) {
				// TODO: Feedback what was re-did
				return Constants.MESSAGE_REDO_SUCCESSFUL;
			} else {
				// TODO
				return Constants.MESSAGE_REDO_UNSUCCESSFUL;
			}
		case DONE:
			if (isSuccess && task != null) {
				String taskName = task.getTaskName();
				return String.format(Constants.MESSAGE_DONE_SUCCESSFUL, taskName);
			} else {
				return Constants.MESSAGE_DONE_UNSUCCESSFUL;
			}
		default:
			throw new Error("Unrecognized command type");
		}
	}
}
