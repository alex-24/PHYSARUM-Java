package physarum_2d.view;

/**
 * Enables implementing classes to send notifySimuUpdateEventListeners orders to the listeners.
 * Used to have the game engine to command an notifySimuUpdateEventListeners of the GUI
 * 
 * @author Alexis Cassion
 *
 */
public interface SimuUpdateEventSender {

	/**
	 * Adds a listener to the lists of event listeners
	 * @param listener
	 */
	public void addSimuUpdateEventListener(SimuUpdateEventListener listener);
	
	/**
	 * Sends an notifySimuUpdateEventListeners order
	 */
	public void notifySimuUpdateEventListeners();
}
