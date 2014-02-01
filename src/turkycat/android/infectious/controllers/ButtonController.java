package turkycat.android.infectious.controllers;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ButtonController implements View.OnTouchListener
{
	private final String TAG = "ButtonController";
	private final boolean DISABLE_XML_BACKGROUND_HIGHLIGHT = true;
	
	private int id;
	private ButtonBar parent;
	
	private Button button;
	private LinearLayout background;
	private int backgroundColor;
	private int foregroundColor;
	
	private final int selectedColor = Color.WHITE;
	private boolean selected;
	
	public ButtonController( int id, Button button, LinearLayout background, ButtonBar parent )
	{
		this.id = id;
		this.parent = parent;
		this.button = button;
		this.background = background;
		this.selected = false;
		this.backgroundColor = 0;
		this.foregroundColor = 0;
		button.setOnTouchListener( this );
		
		Drawable d = background.getBackground();
		if ( d instanceof ColorDrawable ) backgroundColor = ((ColorDrawable) d).getColor();
		
		d = button.getBackground();
		if ( d instanceof ColorDrawable ) foregroundColor = ((ColorDrawable) d).getColor();
		
		//i did this when I decided not to outline deselected buttons in blacknot. the xml still defines this for now.
		if( DISABLE_XML_BACKGROUND_HIGHLIGHT )
		{
			background.setBackgroundColor( foregroundColor );
			backgroundColor = foregroundColor;
		}
	}

	
	
	@Override
	public boolean onTouch( View view, MotionEvent event )
	{
		//we only care about events from the first active pointer. ButtonBar keeps track of the active pointer.
		int currentPointer = event.getPointerId( event.getActionIndex() );
		if( !parent.isActivePointer( currentPointer ) )
				return true;		//consume event, do nothing.
		
		int eventMasked = event.getActionMasked();
		switch( eventMasked )
		{
		case MotionEvent.ACTION_DOWN:

			parent.setActivePointerID( currentPointer );
			background.setBackgroundColor( foregroundColor );
			button.setBackgroundColor( selected ? foregroundColor : selectedColor );
			break;
			
		case MotionEvent.ACTION_UP:

			//invert the 'selected' property and emit the proper signal based on the state of the button
			selected = !selected;
			
			if( selected ) parent.emitSelected( id );
			else parent.emitConfirmed( id );
			
			button.setBackgroundColor( foregroundColor );
			background.setBackgroundColor( selected ? selectedColor : backgroundColor );
			//else parent.emitAction( button );
			break;
		}

		return true;
	}
	
	

	
	/*
	 * forces this button into it's normal state
	 */
	public void deselect()
	{
		if( selected )
		{
			background.setBackgroundColor( backgroundColor );
			button.setBackgroundColor( foregroundColor );
		}
		
		this.selected = false;
	}
	
	
	
	public void setButtonColor( int color )
	{
		foregroundColor = color;
		if( DISABLE_XML_BACKGROUND_HIGHLIGHT )
		{
			backgroundColor = color;
		}
	}
	
}
