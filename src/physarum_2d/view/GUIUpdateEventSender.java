package physarum_2d.view;

/**
 * Enables implementing classes to send notifyGUIUpdateEventListeners orders to the listeners.
 * Enables the gui to notify the listeners when it has finished drawing the views
 * 
 * @author Alexis Cassion
 *
 */
public interface GUIUpdateEventSender {

	/**
	 * Adds a listener to the lists of event listeners
	 * @param listener
	 */
	public void addGUIUpdateEventListener(GUIUpdateEventListener listener);
	
	/**
	 * Sends an notifyGUIUpdateEventListeners order
	 */
	public void notifyGUIUpdateEventListeners();
}
