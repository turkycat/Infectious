package turkycat.android.infectious.activities;

import java.util.Random;

import turkycat.android.infectious.R;
import turkycat.android.infectious.controllers.ButtonBar;
import turkycat.android.infectious.darwin.Cell;
import turkycat.android.infectious.darwin.Organism;
import turkycat.android.infectious.darwin.Player;
import turkycat.android.infectious.darwin.RectangleOrganism;
import turkycat.android.infectious.views.DisplayArea;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;

public class GameActivity extends Activity
{
	private final String TAG = "GameActivity";
	private int gameColors[];
	private LinearLayout activeColorBar;
	private ButtonBar buttonBar;
	
	private Cell playerOneStart;
	
	private DisplayArea displayArea;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_game );
		
		//grab the colors used from the XML file and convert them to color-ints for this application
		Resources r = getResources();
		int colors[] = new int[6];
		colors[0] = r.getColor( R.color.gold );
		colors[1] = r.getColor( R.color.blue );
		colors[2] = r.getColor( R.color.green );
		colors[3] = r.getColor( R.color.gum );
		colors[4] = r.getColor( R.color.metal );
		colors[5] = r.getColor( R.color.purple );
		
		//randomize the color order for each run
		Random rnd = new Random();
		gameColors = new int[6];
		for( int i = 6; i > 0; --i )
		{
			int randy = rnd.nextInt( i );
			gameColors[ 6 - i ] = colors[randy];
			swap( colors, i - 1, randy );
		}
		gameColors[5] = colors[0];
		
		
		buttonBar = new ButtonBar( gameColors, this );
		
		
		displayArea = (DisplayArea) findViewById( R.id.displayArea );
		displayArea.setupGame( 0, 1, gameColors );
		
		
		//retrieve the active color bar and set it to the active color
		activeColorBar = (LinearLayout) findViewById( R.id.controlLayout );
		activeColorBar.setBackgroundColor( gameColors[0] );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.game, menu );
		return true;
	}
	
	
	
	public void emitColorChosen( int colorIndex )
	{
		swap( gameColors, 0, colorIndex );
		displayArea.colorChosen( gameColors[0] );
		activeColorBar.setBackgroundColor( gameColors[0] );
	}
	
	
	
//	public Organism getCurrentOrganism()
//	{
//		return organism;
//	}

	
	
	private void swap( int[] arr, int i, int j )
	{
		int t = arr[i];
		arr[i] = arr[j];
		arr[j] = t;
	}
}
