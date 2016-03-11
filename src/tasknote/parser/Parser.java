package tasknote.parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import tasknote.shared.COMMAND_TYPE;
import tasknote.shared.TaskObject;

public class Parser {

	// Here are the valid commands accepted by
	// the program
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_RENAME = "rename";
	private static final String COMMAND_DONE = "done";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_UNDO = "undo";
	private static final String COMMAND_REDO = "redo";
	private static final String COMMAND_EXIT = "exit";
	private static final String COMMAND_SEARCH = "search";

	// Here are the valid keywords accepted by
	// the program
	private static final String KEYWORD_BY = "by";
	private static final String KEYWORD_AT = "at";
	private static final String KEYWORD_ON = "on";
	private static final String KEYWORD_FROM = "from";
	private static final String KEYWORD_TO = "to";
	private static final String KEYWORD_IN = "in";
	private static final String KEYWORD_NOTIFY = "notify";

	// Here are the regex that are used by the
	// parser for parsing all user commands
	private static final String REGEX_WHITESPACE = " ";
	private static final String REGEX_QUOTATION = "\"";

	/**
	 * This method accepts an entire String passed from the user through his
	 * command line, and returns the matching COMMAND_TYPE
	 * 
	 * @param userCommand
	 *            This refers to the entire String that is passed by the user
	 *            through the command line
	 * 
	 * @return A COMMAND_TYPE enum value is returned. All the COMMAND_TYPE enums
	 *         can be found in the tasknote.shared package
	 * 
	 *         In the event where the command supplied by the user, does not
	 *         match any of the valid COMMAND_TYPES, the INVALID COMMAND_TYPE
	 *         value is returned instead
	 */
	public static COMMAND_TYPE getCommandType(String userCommand) {

		ParserFirstPass firstPassCommand = new ParserFirstPass(userCommand);
		ArrayList<String> allPhrase = firstPassCommand.getFirstPassParsedResult();
		
		int allPhraseCount = allPhrase.size();
		
		String userCommandWord = allPhrase.get(0);

		// Finally, we check to see which COMMAND_TYPE
		// matches the command given by the user
		// If none of the COMMAND_TYPE matches, the
		// INVALID COMMAND_TYPE is returned instead
		if (userCommandWord.equalsIgnoreCase(COMMAND_ADD)) {

			if (allPhraseCount == 1) {
				return COMMAND_TYPE.INVALID;
			} else {
				return COMMAND_TYPE.ADD;
			}

		} else if (userCommandWord.equalsIgnoreCase(COMMAND_EDIT)) {
			return COMMAND_TYPE.UPDATE;
		} else if (userCommandWord.equalsIgnoreCase(COMMAND_DELETE)) {
			return COMMAND_TYPE.DELETE;
		} else if (userCommandWord.equalsIgnoreCase(COMMAND_SEARCH)) {
			return COMMAND_TYPE.SEARCH;
		} else if (userCommandWord.equalsIgnoreCase(COMMAND_EXIT)) {
			return COMMAND_TYPE.EXIT;
		} else if (userCommandWord.equalsIgnoreCase(COMMAND_DONE)) {
			return COMMAND_TYPE.DONE;
		} else if (userCommandWord.equalsIgnoreCase(COMMAND_REDO)) {
			return COMMAND_TYPE.REDO;
		} else if (userCommandWord.equalsIgnoreCase(COMMAND_UNDO)) {
			return COMMAND_TYPE.UNDO;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}

	// Fixed command structure: add taskname <on date> <by time> <notify time>
	// <at location>
	public static TaskObject parseAdd(String userCommand) {

		ParserFirstPass parseAllWords = new ParserFirstPass(userCommand);
		ArrayList<String> allPhrases = parseAllWords.getFirstPassParsedResult();

		int phraseCount = allPhrases.size();

		String switchString = "name";
		String taskType = "floating";

		if (phraseCount == 2) {
			return new TaskObject(allPhrases.get(1));
		}

		StringBuilder name = new StringBuilder(allPhrases.get(1));
		StringBuilder location = new StringBuilder();
		int dateDay = -1;
		int dateMonth = -1;
		int dateYear = -1;
		int dateHour = -1;
		int dateMinute = -1;
		int hourBefore = 0;

		for (int i = 2; i < phraseCount; i++) {

			String currentPhrase = allPhrases.get(i);

			if (currentPhrase.equalsIgnoreCase(KEYWORD_ON)) {
				switchString = "date";
				continue;
			} else if (currentPhrase.equalsIgnoreCase(KEYWORD_BY)) {
				switchString = "time";
				continue;
			} else if (currentPhrase.equalsIgnoreCase(KEYWORD_NOTIFY)) {
				switchString = "notify";
				continue;
			} else if (currentPhrase.equalsIgnoreCase(KEYWORD_AT)) {
				switchString = "at";
				continue;
			}

			if (switchString.equals("name")) {
				name.append(REGEX_WHITESPACE);
				name.append(currentPhrase);
				continue;
			}

			if (switchString.equals("date")) {

				String[] dayMonthYear = currentPhrase.split("/");

				try {
					dateDay = Integer.parseInt(dayMonthYear[0]);
					dateMonth = Integer.parseInt(dayMonthYear[1]);
					dateYear = Integer.parseInt(dayMonthYear[2]);
					taskType = "deadline";
					continue;
				} catch (NumberFormatException e) {
					System.out.println(currentPhrase
							+ " is not a valid date format");
					switchString = "name";
					continue;
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println(currentPhrase
							+ " is not a valid date format");
					switchString = "name";
					continue;
				}
			}

			if (switchString.equals("time")) {

				String[] hourMinute = currentPhrase.split(":");

				try {
					dateHour = Integer.parseInt(hourMinute[0]);
					dateMinute = Integer.parseInt(hourMinute[1]);
					taskType = "deadline";
					continue;
				} catch (NumberFormatException e) {
					System.out.println(currentPhrase
							+ " is not a valid date format");
					switchString = "name";
					continue;
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println(currentPhrase
							+ " is not a valid date format");
					switchString = "name";
					continue;
				}
			}

			if (switchString.equals("notify")) {

				try {
					hourBefore = Integer.parseInt(currentPhrase);
					continue;
				} catch (NumberFormatException e) {
					System.out.println(currentPhrase
							+ " is not a valid date format");
					switchString = "name";
					continue;
				}
			}

			if (switchString.equals("location")) {
				location.append(REGEX_WHITESPACE);
				location.append(currentPhrase);
				continue;
			}
		}

		if (dateHour > 0 || dateMinute > 0) {
			if (dateDay == -1 || dateMonth == -1 || dateYear == -1) {
				GregorianCalendar todayCalendar = new GregorianCalendar();
				int todayHour = todayCalendar.get(Calendar.HOUR_OF_DAY);
				int todayMinute = todayCalendar.get(Calendar.MINUTE);
				
				if (todayHour > dateHour || 
						(todayHour == dateHour && todayMinute > dateMinute)) {
					todayCalendar.roll(Calendar.DAY_OF_MONTH, 1);
					
					if (todayCalendar.get(Calendar.DAY_OF_MONTH) == 1) {
						todayCalendar.roll(Calendar.MONTH, 1);
						
						if (todayCalendar.get(Calendar.MONTH) == 1) {
							todayCalendar.roll(Calendar.YEAR, 1);
						}
					}
				}
				
				dateDay = todayCalendar.get(Calendar.DAY_OF_MONTH);
				dateMonth = todayCalendar.get(Calendar.MONTH) + 1;
				dateYear = todayCalendar.get(Calendar.YEAR);
			}
		}

		// Set name
		TaskObject taskObjectToBuild = new TaskObject(name.toString());

		// Set date
		taskObjectToBuild.setDateYear(dateYear);
		taskObjectToBuild.setDateMonth(dateMonth);
		taskObjectToBuild.setDateDay(dateDay);

		// Set time
		taskObjectToBuild.setDateHour(dateHour);
		taskObjectToBuild.setDateMinute(dateMinute);
		taskObjectToBuild.setNotifyTime(hourBefore);

		// Set location
		taskObjectToBuild.setLocation(location.toString());
		
		// Set taskType
		taskObjectToBuild.setTaskType(taskType);

		return taskObjectToBuild;
	}

	public static TaskObject parseUpdate(String userCommand,
			TaskObject reallyOldTaskObject) {

		ParserFirstPass parseAllWords = new ParserFirstPass(userCommand);
		ArrayList<String> allPhrases = parseAllWords.getFirstPassParsedResult();

		int phraseCount = allPhrases.size();

		StringBuilder name = new StringBuilder(reallyOldTaskObject.getTaskName());
		StringBuilder location = new StringBuilder(reallyOldTaskObject.getLocation());
		int dateDay = reallyOldTaskObject.getDateDay();
		int dateMonth = reallyOldTaskObject.getDateMonth();
		int dateYear = reallyOldTaskObject.getDateYear();
		int dateHour = reallyOldTaskObject.getDateHour();
		int dateMinute = reallyOldTaskObject.getDateMinute();
		int hourBefore = reallyOldTaskObject.getNotifyTime();
		
		boolean alteringName = false;
		boolean alteringLocation = false;

		String switchString = "name";
		String taskType = reallyOldTaskObject.getTaskType();

		for (int i = 2; i < phraseCount; i++) {

			String currentPhrase = allPhrases.get(i);

			if (currentPhrase.equalsIgnoreCase(KEYWORD_ON)) {
				switchString = "date";
				continue;
			} else if (currentPhrase.equalsIgnoreCase(KEYWORD_BY)) {
				switchString = "time";
				continue;
			} else if (currentPhrase.equalsIgnoreCase(KEYWORD_NOTIFY)) {
				switchString = "notify";
				continue;
			} else if (currentPhrase.equalsIgnoreCase(KEYWORD_AT)) {
				switchString = "at";
				continue;
			}

			if (switchString.equals("name")) {
				if (!alteringName) {
					alteringName = true;
					name.delete(0, name.length());
					name.append(currentPhrase);
				} else {
					name.append(REGEX_WHITESPACE);
					name.append(currentPhrase);
				}
				continue;
			}

			if (switchString.equals("date")) {

				String[] dayMonthYear = currentPhrase.split("/");

				try {
					dateDay = Integer.parseInt(dayMonthYear[0]);
					dateMonth = Integer.parseInt(dayMonthYear[1]);
					dateYear = Integer.parseInt(dayMonthYear[2]);
					taskType = "deadline";
					continue;
				} catch (NumberFormatException e) {
					System.out.println(currentPhrase
							+ " is not a valid date format");
					switchString = "name";
					continue;
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println(currentPhrase
							+ " is not a valid date format");
					switchString = "name";
					continue;
				}
			}

			if (switchString.equals("time")) {

				String[] hourMinute = currentPhrase.split(":");

				try {
					dateHour = Integer.parseInt(hourMinute[0]);
					dateMinute = Integer.parseInt(hourMinute[1]);
					taskType = "deadline";
					continue;
				} catch (NumberFormatException e) {
					System.out.println(currentPhrase
							+ " is not a valid date format");
					switchString = "name";
					continue;
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println(currentPhrase
							+ " is not a valid date format");
					switchString = "name";
					continue;
				}
			}

			if (switchString.equals("notify")) {

				try {
					hourBefore = Integer.parseInt(currentPhrase);
					continue;
				} catch (NumberFormatException e) {
					System.out.println(currentPhrase
							+ " is not a valid date format");
					switchString = "name";
					continue;
				}
			}

			if (switchString.equals("location")) {
				if (!alteringLocation) {
					alteringLocation = true;
					location.delete(0, location.length());
					location.append(currentPhrase);
				} else {
					location.append(REGEX_WHITESPACE);
					location.append(currentPhrase);
				}
				continue;
			}
		}
		
		if (dateHour > 0 || dateMinute > 0) {
			if (dateDay == -1 || dateMonth == -1 || dateYear == -1) {
				GregorianCalendar todayCalendar = new GregorianCalendar();
				int todayHour = todayCalendar.get(Calendar.HOUR_OF_DAY);
				int todayMinute = todayCalendar.get(Calendar.MINUTE);
				
				if (todayHour > dateHour || 
						(todayHour == dateHour && todayMinute > dateMinute)) {
					todayCalendar.roll(Calendar.DAY_OF_MONTH, 1);
					
					if (todayCalendar.get(Calendar.DAY_OF_MONTH) == 1) {
						todayCalendar.roll(Calendar.MONTH, 1);
						
						if (todayCalendar.get(Calendar.MONTH) == 1) {
							todayCalendar.roll(Calendar.YEAR, 1);
						}
					}
				}
				
				dateDay = todayCalendar.get(Calendar.DAY_OF_MONTH);
				dateMonth = todayCalendar.get(Calendar.MONTH) + 1;
				dateYear = todayCalendar.get(Calendar.YEAR);
			}
		}

		// Set name
		TaskObject taskObjectToBuild = new TaskObject(name.toString());

		// Set date
		taskObjectToBuild.setDateYear(dateYear);
		taskObjectToBuild.setDateMonth(dateMonth);
		taskObjectToBuild.setDateDay(dateDay);

		// Set time
		taskObjectToBuild.setDateHour(dateHour);
		taskObjectToBuild.setDateMinute(dateMinute);
		taskObjectToBuild.setNotifyTime(hourBefore);

		// Set location
		taskObjectToBuild.setLocation(location.toString());
		
		// Set taskType
		taskObjectToBuild.setTaskType(taskType);

		return taskObjectToBuild;
	}

	public static ArrayList<Integer> parseDelete(String userCommand) {
		// TODO Auto-generated method stub

		String[] splitCommand = userCommand.trim().split(REGEX_WHITESPACE);
		int idCount = splitCommand.length;

		ArrayList<Integer> list = new ArrayList<Integer>();

		for (int i = 1; i < idCount; i++) {

			String nextCommand = splitCommand[i];

			try {

				int nextID = Integer.parseInt(nextCommand) - 1;

				list.add(nextID);

			} catch (NumberFormatException e) {
				// No Exceptions handling supported yet
				// TODO
			}
		}

		return list;
	}

	public static ArrayList<Integer> parseSearch(String userCommand,
			ArrayList<TaskObject> displayList) {
		// TODO Auto-generated method stub

		// Magic number: Search is of length 7
		String searchString = userCommand.trim().substring(7,
				userCommand.length());

		Iterator taskObjectIterator = displayList.iterator();

		ArrayList<Integer> allSelectedTaskIDs = new ArrayList<>();

		int i = 0;

		while (taskObjectIterator.hasNext()) {

			TaskObject currentTaskObject = (TaskObject) taskObjectIterator
					.next();

			String currentTaskName = currentTaskObject.getTaskName();

			if (currentTaskName.contains(searchString)) {

				System.out.println(currentTaskName);
				System.out.println(currentTaskObject.getTaskID());

				allSelectedTaskIDs.add(i);
			}

			i++;
		}

		return allSelectedTaskIDs;
	}

	public static int getUpdateTaskId(String userCommand) {

		String[] splitUserCommand = userCommand.trim().split(REGEX_WHITESPACE);

		try {

			String givenID = splitUserCommand[1];

			int returnValue = Integer.parseInt(givenID) - 1;

			return returnValue;
		} catch (NumberFormatException e) {

			return -1;
		}

	}
}
