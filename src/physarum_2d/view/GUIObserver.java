package physarum_2d.view;

import java.awt.Graphics;

/**
 * Observes a model and paints it in a panel
 * Observers in the banner should implement this class for the SwingGUI to place them correctly.
 * 
 * [Inspired by the LI260 course on racing simulation by teacher Vincent Guigue - UPMC]
 * @author Alexis Cassion
 *
 */
public interface GUIObserver {

	/**
	 * prints a representation of the model on the graphic given in argument
	 * @param g
	 */
	public void print(Graphics g);
}
