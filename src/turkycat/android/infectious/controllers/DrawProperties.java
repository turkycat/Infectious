package turkycat.android.infectious.controllers;

/**
 * the use of the DrawProperties class will allow the DisplayArea surface view to modify the properties of the drawing events
 * 	at any time.
 * 	- A reference to a DrawProperties object must be supplied to an organism when it is asked to draw itself.
 * 
 *  - The manipulation of this object by a thread while an organism's draw method is executing within another thread
 *  	will allow animations to continue to play out while the user shifts or scales the view.
 *  
 *  - the receiving organism should duplicate any values to be used at the beginning of each draw iteration
 *  	to guarantee uniformity with each cycle and to improve performance by reducing object references.
 *
 * @author Jesse Frush
 *
 */
public class DrawProperties
{
	public float lockedScale = 1.0f;
	public volatile float currentScale = 1.0f;
	public volatile float shift = 5.0f;
	
	public synchronized void lockScale()
	{
		lockedScale = lockedScale * currentScale;
		currentScale = 1.0f;
	}
}
