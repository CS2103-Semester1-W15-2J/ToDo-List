/** @@author A0108371L */
package tasknote.logic.JUnitTests;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import tasknote.logic.TaskNoteControl;
import tasknote.shared.TaskObject;

public class DoneTests {

	TaskNoteControl tnc = new TaskNoteControl();
	ArrayList<TaskObject> obj;
	String command;
	String feedback;
	String output;
	
	@Test
	// @condition: Task File must be empty before executing this test
	public void testMarkAsComplete() {
		String taskName;
		ArrayList<TaskObject> list;
		TaskObject task;
		
		command = "add Buy Vegetables next tue";
		feedback = tnc.executeCommand(command);
		command = "add Complete Assignment by tonight 9pm";
		feedback = tnc.executeCommand(command);
		
		//First test case
		command = "done 1";
		feedback = tnc.executeCommand(command);
		
		list = tnc.getDisplayList();
		task = list.get(0);
		//System.out.println(task);
		
		output = "Task \"%s\" has been marked as completed Successfully";
		taskName = task.getTaskName();
		Assert.assertEquals(String.format(output, taskName), feedback);
		
		//Second test case
		command = "done 200";
		output = "Mark as complete failed";
		feedback = tnc.executeCommand(command);
		Assert.assertEquals(output, feedback);
		
		//Third test case
		command = "done 0";
		output = "Mark as complete failed";
		feedback = tnc.executeCommand(command);
		Assert.assertEquals(output, feedback);
		
		//Fourth test case
		command = "done 1";
		feedback = tnc.executeCommand(command);

		list = tnc.getDisplayList();
		task = list.get(0);
		//System.out.println(task);
		
		output = "Task \"%s\" has been marked as completed Successfully";
		taskName = task.getTaskName();
		Assert.assertEquals(String.format(output, taskName), feedback);
	}

}
