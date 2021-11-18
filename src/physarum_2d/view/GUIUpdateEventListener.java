package physarum_2d.view;

/**
 * Enables implementing classes to listen to update orders from the senders.
 * Enables the listener to be notiefied when the gui has finished drawing
 * 
 * @author Alexis Cassion
 *
 */
public interface GUIUpdateEventListener {

	/**
	 * Manages an update order
	 */
	public void onGUIUpdateEventTriggered();
}
