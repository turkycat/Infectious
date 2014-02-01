package turkycat.android.infectious.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class ControlArea extends SurfaceView implements SurfaceHolder.Callback
{
	private final String TAG = "ControlArea";
	
	//size parameters for controlling the screen from this class
	private int areaHeight;
	private int areaWidth;
	private int windowHeight;
	private int windowWidth;
	
	
	//------------------------------------------------------------------constructors

	public ControlArea( Context context )
	{
		super( context );
		init( context );
	}

	public ControlArea( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		init( context );
	}

	public ControlArea( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		init( context );
	}
	
	
	private void init( Context context )
	{
		SurfaceHolder holder = getHolder();
		holder.addCallback( this );
		
		//get the maximum pixels of the screen
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point p = new Point();
		display.getSize( p );
		windowHeight = p.y;
		windowWidth = p.x;
		Log.i( TAG, "INIT: window height: " + windowHeight + " width: " + windowWidth );
	}
	
	
	
	
	
	
	///-------------------------------------------unimplemented methods for handling surface calls

	@Override
	public void surfaceChanged( SurfaceHolder holder, int format, int width, int height )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated( SurfaceHolder holder )
	{
		/* interestingly enough, onSizeChanged is called before surfaceCreated (implies surfaceCreated is called when
		 * everything is ready). This means I can rely on my areaHeight and areaWidth values to draw the control area now.
		Log.i( TAG, "sup from surface created " + areaHeight + " " + areaWidth );
		 */
		
		//Canvas canvas = holder.lockCanvas( null );
		//initializeControlArea( canvas );
		
		//holder.unlockCanvasAndPost( canvas );
	}
	
	
	private void initializeControlArea( Canvas canvas )
	{
		//draw a border. Begin by clearing to border color
		canvas.drawColor( Color.WHITE );
		
		//draw another smaller rectangle over the border color with the background color
		Paint paint = new Paint();
		paint.setColor( Color.BLACK );
		int borderSize = 2;
		
		//draw the rectangle with given border size
//		canvas.drawRect( borderSize, windowHeight - areaHeight + borderSize, areaWidth - borderSize, windowHeight - borderSize, paint );
		canvas.drawRect( 0.1f, 0.97f, 0.3f, 0.99f, paint );
	}

	@Override
	public void surfaceDestroyed( SurfaceHolder holder )
	{
		// TODO Auto-generated method stub
		
	}

	
	@Override
	protected void onSizeChanged( int w, int h, int oldw, int oldh )
	{
		super.onSizeChanged( w, h, oldw, oldh );
		
		areaHeight = h;
		areaWidth = w;
		Log.i( TAG, "sup from sized changed " + areaHeight + " " + areaWidth );
	}

	
	@Override
	public boolean onTouchEvent( MotionEvent event )
	{
		Log.i( TAG, "Touch Detected in control area." );
		return true;
		//return gameThread.handleTouchEvent( event );
	}
}