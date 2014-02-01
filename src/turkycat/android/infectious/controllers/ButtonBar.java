package turkycat.android.infectious.controllers;

import turkycat.android.infectious.R;
import turkycat.android.infectious.activities.GameActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

public class ButtonBar
{
	private final String TAG = "ButtonBar";
	
	private GameActivity parent;
	private Button buttons[];
	private ButtonController buttonControllers[];
	private int[] gameColors;
	private int activePointer;
	private int activeButton;

	public ButtonBar( int[] gameColors, GameActivity parent )
	{
		this.parent = parent;
		this.gameColors = gameColors;
		this.activePointer = -1;
		this.activeButton = -1;
		
		//for ease of indexing between classes, there are six button indices for the six colors, but 0 is never used. ( 5 buttons )
		buttons = new Button[6];
		buttonControllers = new ButtonController[6];
		
		init();
	}

	private void init()
	{
		//need to grab each button and background item individually and add button controllers to them.

		buttons[1] = (Button) parent.findViewById( R.id.controlButton1 );
		buttons[1].setBackgroundColor( gameColors[1] );
		LinearLayout layout = (LinearLayout) parent.findViewById( R.id.buttonHighlight1 );
		//ButtonController class automatically attaches itself to the given button and layout.
		buttonControllers[1] = new ButtonController( 1, buttons[1], layout, this );

		buttons[2] = (Button) parent.findViewById( R.id.controlButton2 );
		buttons[2].setBackgroundColor( gameColors[2] );
		layout = (LinearLayout) parent.findViewById( R.id.buttonHighlight2 );
		buttonControllers[2] = new ButtonController( 2, buttons[2], layout, this );

		buttons[3] = (Button) parent.findViewById( R.id.controlButton3 );
		buttons[3].setBackgroundColor( gameColors[3] );
		layout = (LinearLayout) parent.findViewById( R.id.buttonHighlight3 );
		buttonControllers[3] = new ButtonController( 3, buttons[3], layout, this );

		buttons[4] = (Button) parent.findViewById( R.id.controlButton4 );
		buttons[4].setBackgroundColor( gameColors[4] );
		layout = (LinearLayout) parent.findViewById( R.id.buttonHighlight4 );
		buttonControllers[4] = new ButtonController( 4, buttons[4], layout, this );

		buttons[5] = (Button) parent.findViewById( R.id.controlButton5 );
		buttons[5].setBackgroundColor( gameColors[5] );
		layout = (LinearLayout) parent.findViewById( R.id.buttonHighlight5 );
		buttonControllers[5] = new ButtonController( 5, buttons[5], layout, this );
	}
	
	
	/*
	 * sets the active pointer to guarantee that no multitouch shenanigans occur
	 */
	public void setActivePointerID( int id )
	{
		this.activePointer = id;
	}
	
	
	
	public boolean isActivePointer( int id )
	{
		if( activePointer == -1 ) return true;
		return id == activePointer;
	}
	

	
	/*
	 * called by the button controllers when a button is selected (but not confirmed).
	 */
	public void emitSelected( int id )
	{
		/*
		for( int i = 0; i < buttons.length; i++ )
		{
			if( buttons[i] == button ) continue;
			buttonControllers[i].deselect();
		}
		*/
		if( activeButton > 0 ) buttonControllers[activeButton].deselect();
		activeButton = id;
	}
	
	
	
	/*
	 * called by the button controllers when a button is selected (but not confirmed).
	 */
	public void emitConfirmed( int id )
	{
		if( id != activeButton )
		{
			//this shouldn't happen. For safety, we reset the controller state.
			Log.d( TAG, "Confirmed button does not match active button. Resetting controller state." );
			reset();
		}

		activeButton = -1;
		
		//tell the parent Activity that a color was selected. This will swap the game colors and perform necessary actions
		parent.emitColorChosen( id );
		
		//reset the button to it's new color (the old active color)
		buttonControllers[id].setButtonColor( gameColors[id] );
	}
	
	
	
	
	
	
	private void reset()
	{
		Log.d( TAG, "For some reason, reset() was called. likely invalid state reached." );
		for( int i = 0; i < 6; i++ )
		{
			buttonControllers[i].deselect();
			buttonControllers[i].setButtonColor( gameColors[i] );
		}
	}
}
