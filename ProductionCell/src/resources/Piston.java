package resources;

import utils.Addeable;
import utils.ProbabilisticSensor;
import utils.QueryRangeSensor;
import utils.Item;
import utils.MessagesHelpers;
import utils.WorkSimulator;

public class Piston implements Addeable {
	
	WorkSimulator moveACycleSimulator;
	WorkSimulator waitInPositionSimulator;
	ProbabilisticSensor endOfRoad;
	QueryRangeSensor positioned;
	QueryRangeSensor beginOfRoad;
	String name;
	Item itemHandled;
	Addeable next; //It could be another piston or maybe the final container
	
	public Piston(String name, Addeable next, int moveCycleMaxTime, int moveCycleMinTime, int maxQueriesToPositionate, int minQueriesToPositionate) {
		this.moveACycleSimulator = new WorkSimulator(moveCycleMaxTime, moveCycleMinTime);
		this.waitInPositionSimulator = new WorkSimulator(300, 100);
		this.positioned = new QueryRangeSensor(maxQueriesToPositionate, minQueriesToPositionate);
		this.beginOfRoad = new QueryRangeSensor(maxQueriesToPositionate, minQueriesToPositionate); //it will take almost the same to go from the begin to well than from well to begin
		this.endOfRoad = new ProbabilisticSensor( 0.001 );
		this.name = name;
		this.next = next;
		this.itemHandled = null;
	}
	
	public void moveForward() throws InterruptedException {
		MessagesHelpers.infoMessage(generatePistonMsg("Moving forward."));
		this.positioned.startCapturing();
		while(!this.positioned.read()) {
			this.moveACycleSimulator.work();
		}
		MessagesHelpers.infoMessage(generatePistonMsg("Moving forward ended. The item " + this.itemHandled.getItemName() + " is in place."));
	}

	public void waitInPosition() throws InterruptedException {
		MessagesHelpers.infoMessage(generatePistonMsg("Waiting for timeout in position."));
		this.waitInPositionSimulator.work();
		MessagesHelpers.infoMessage(generatePistonMsg("Waiting ended."));
	}
	
	public void moveBackward() throws InterruptedException {
		MessagesHelpers.infoMessage(generatePistonMsg("Moving backward. The item " + this.itemHandled.getItemName() + " is not longer in this piston domain."));
		this.next.addItem(this.itemHandled);
		this.itemHandled = null;
		this.beginOfRoad.startCapturing();
		while(!this.beginOfRoad.read()) {
			this.moveACycleSimulator.work();
		}
		MessagesHelpers.infoMessage(generatePistonMsg("Moving backward ended. The piston is in home position."));
	}
	
	public void errorChecker() throws InterruptedException {
		MessagesHelpers.infoMessage(generatePistonMsg("Starting error checking."));
		while(!this.endOfRoad.read());
		MessagesHelpers.warningMessage(generatePistonMsg("End of road reached. It could lead to an error."));
		
		//while(true); //For no errors use this and comment the above code.
	}
	
	public void errorHandler() {
		MessagesHelpers.errorMessage(generatePistonMsg("End of road reached while moving forward. The system is going down!"));
	}
	
	public void addItem(Item item) {
		MessagesHelpers.infoMessage(generatePistonMsg("Adding the item " + item.getItemName() + " to the piston domain."));
		MessagesHelpers.checkCondition(this.itemHandled != null, generatePistonMsg("There was another item handled in the domains of this piston."));
		this.itemHandled = item;
		MessagesHelpers.infoMessage(generatePistonMsg("The item " + item.getItemName() + " has been successfully added to the piston domain."));
	}
	
	private String generatePistonMsg(String msg) {
		return ">> Piston " + this.name + " << " + msg;
	}
}