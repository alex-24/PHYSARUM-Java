package physarum_2d.view;

/**
 * Enables implementing classes to listen to update orders from the senders.
 * Used to have the GUI listen to update commands an update from the game engine
 * 
 * @author Alexis Cassion
 *
 */
public interface SimuUpdateEventListener {

	/**
	 * Manages an update order
	 */
	public void onSimuUpdateEventTriggered();
}
