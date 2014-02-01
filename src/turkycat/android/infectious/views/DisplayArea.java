package turkycat.android.infectious.views;

import turkycat.android.infectious.controllers.DrawProperties;
import turkycat.android.infectious.darwin.Cell;
import turkycat.android.infectious.darwin.Organism;
import turkycat.android.infectious.darwin.Player;
import turkycat.android.infectious.darwin.RectangleOrganism;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DisplayArea extends SurfaceView implements SurfaceHolder.Callback
{
	private final static int CLASSIC_MODE = 0;
	private final static String TAG = "DisplayArea";
	
	private int areaHeight;
	private int areaWidth;
	private DrawThread drawThread;
	private RectangleOrganism organism;
	private DrawProperties drawProperties;
	
	Cell playerRoot[];
	private int players;
	
	private boolean running;
	
	//------------------------------------------------------------------constructors

	public DisplayArea( Context context )
	{
		super( context );
		init( context );
	}

	public DisplayArea( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		init( context );
	}

	public DisplayArea( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		init( context );
	}
	
	
	private void init( Context context )
	{
		//initialize the DrawProperties controller which will allow us to modify the scale and position of the draw calls at any time.
		this.drawProperties = new DrawProperties();

		//get the surface holder, which we will use to draw directly to the screen
		SurfaceHolder holder = getHolder();
		holder.addCallback( this );
		
		//initialize the drawThread which handles drawing to the screen using the surface holder
		this.drawThread = new DrawThread( holder, context );
	}
	
	
	
	
	public void setupGame( int mode, int players, int[] gameColors )
	{
		switch( mode )
		{
		case 0:
		default:
			this.organism = new RectangleOrganism( gameColors );
		}
		
		//lol nested ternary statements
		//this.players = players < 1 ? 1 : players > 4 ? 4 : players;
		this.players = Math.max( 1, Math.min( 4, players ) );
		
		playerRoot = new Cell[this.players];
		switch( this.players )
		{
		case 4:
			playerRoot[3] = organism.setupPlayer( Player.PLAYER_FOUR, gameColors[0] );
		case 3:
			playerRoot[2] = organism.setupPlayer( Player.PLAYER_THREE, gameColors[0] );
		case 2:
			playerRoot[1] = organism.setupPlayer( Player.PLAYER_TWO, gameColors[0] );
		case 1:
			default:
			playerRoot[0] = organism.setupPlayer( Player.PLAYER_ONE, gameColors[0] );
		}
		
		drawThread.setOrganism( this.organism );
		drawThread.setDirty();
	}
	
	
	
	public void colorChosen( int color )
	{
		organism.processMove( playerRoot[0], color );
		drawThread.setDirty();
	}
	
	
	
	///-------------------------------------------methods for handling surface calls
	


	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
	}
	

	@Override
	public void surfaceChanged( SurfaceHolder holder, int format, int width, int height )
	{
		Log.d( TAG,  "surface changed" );
	}

	@Override
	public void surfaceCreated( SurfaceHolder holder )
	{
		Log.i( TAG, "Surface Created." );
		
		drawThread.start();
	}

	@Override
	public void surfaceDestroyed( SurfaceHolder holder )
	{

		Log.i( TAG, "Surface destroyed." );
		
		try
		{
			drawThread.running = false;
			drawThread.join( 50 );
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
	}
	


	
	@Override
	protected void onSizeChanged( int w, int h, int oldw, int oldh )
	{
		super.onSizeChanged( w, h, oldw, oldh );
		
		areaHeight = h;
		areaWidth = w;
		Log.i( TAG, "sup from sized changed" + areaHeight + " " + areaWidth );
	}

	
	@Override
	public boolean onTouchEvent( MotionEvent event )
	{
		Log.i( TAG, "Touch Detected in display area." );
		
		int masked = event.getActionMasked();
		switch( masked )
		{
		case MotionEvent.ACTION_POINTER_DOWN:
		}
		
		return true;
	}

	
	
	//---------------------------------------------------------------draw thread
	public class DrawThread extends Thread
	{
		private SurfaceHolder holder;
		private Context context;
		private Organism organism;
		
		private boolean dirty;
		volatile public boolean running;
		
		
		
		public DrawThread( SurfaceHolder holder, Context context )
		{
			this.holder = holder;
			this.context = context;
			this.organism = null;
			this.running = false;
		}
		
		
		
		@Override
		public void run()
		{
			//using the java-recommended thread death method
			this.running = true;
			while( running )
			{
				synchronized( this )
				{
					if( dirty )
					{
						dirty = false;
						organism.draw( holder, drawProperties );
					}
				}
			}
		}
		
		
		//private synchronized void drawOrganism( Canvas canvas )
		//{
			//organism.draw( canvas, areaHeight, areaWidth, 1.0f, 0, 0 );
		//}
		
		
		private void setOrganism( Organism org )
		{
			this.organism = org;
		}
		
		
		
		
		/**
		 * sets the state of the display to dirty, which will trigger a draw
		 */
		public synchronized void setDirty()
		{
			if( organism == null ) return;
			dirty = true;
		}
	}

	
	
}
