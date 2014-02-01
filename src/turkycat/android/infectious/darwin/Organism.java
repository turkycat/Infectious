package turkycat.android.infectious.darwin;

import turkycat.android.infectious.controllers.DrawProperties;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

/**
 * an organism is the original owner of cells. The organism may have many types of cells and therefore many different
 * defenses or abilities. Therefore, the organism is the control center for the behavior of all cells for a given turn.
 * 
 * @author Jesse Frush
 *
 */
public abstract class Organism
{
	private final String TAG = "Organism";
	private final float CELLSIZE = 23.0f;
	private final float CELLSPACING = 4.0f;
	
	protected Cell[][] cells;
	
	public abstract Cell setupPlayer( Player player, int color );
	public abstract void processMove( Cell cell, int color );
	
	

	//public void draw( Canvas canvas, int areaHeight, int areaWidth, float scale, int xShift, int yShift )
	public void draw( SurfaceHolder holder, DrawProperties drawProperties )
	{
		if( holder == null || drawProperties == null ) return;
		int height = cells.length;
		int width = cells[0].length;
		float spacing = CELLSIZE + CELLSPACING;
		
		Paint paint = new Paint();
		
		boolean animating = false;
		long fin = 0;
		do
		{
			long initial = System.currentTimeMillis();
			if( animating )
			{
				try
				{
					Thread.sleep( 20 - ( fin - initial ) );
				}
				catch( InterruptedException e )
				{
					//do nothing
				}
			}
				
			//one pass unless otherwise specified by the cells.
			animating = false;
			
			//lock the canvas and retrieve a reference to the next canvas to be drawn.
			Canvas canvas = holder.lockCanvas();
			canvas.drawColor( Color.BLACK );
			
			//store the values to be used for the current draw iteration
			float scale = drawProperties.lockedScale * drawProperties.currentScale;
			float shift = drawProperties.shift;
			
			//go through each cell tell them to draw at their respective location
			for( int i = 0; i < height; i++ )
			{
				for( int j = 0; j < width; j++ )
				{
					Cell current = cells[i][j];
					
					if( current.state == State.INVALID ) continue;

					//paint.setAlpha( 200 );
					float left = shift + ( ( spacing * j ) * scale );
					float right = shift + ( ( ( spacing * j ) + CELLSIZE ) * scale );
					float top = shift + ( ( spacing * i ) * scale );
					float bottom = shift + ( ( ( spacing * i ) + CELLSIZE ) * scale );
					
					//add a white border to cells that are owned by the player. accomplished by drawing a full size rectangle and shrinking the dimensions.
//					if( current.owner != Player.ORGANISM && !current.evolving )
//					{
//						paint.setColor( Color.WHITE );
//						canvas.drawRect( left, top, right, bottom, paint );
//						
//						//reduce dimensions of the rectangle to be drawn over the white, forming a border.
//						left++;
//						right--;
//						top++;
//						bottom--;
//					}

					

					if( current.evolving )
					{
						//any cell still animating means we need to continue drawing after this iteration
						animating = true;
						
						current.phase();
						
						//determine the size of the smaller square based on the stage of evolution
						float outerSquare = right - left;
						float innerSquare = outerSquare * current.evolvePercentage;
						
						//an offset is needed to center the element
						float offset = ( outerSquare - innerSquare ) / 2.0f;
						
						//draw the outer square
						paint.setColor( current.color );
						canvas.drawRect( left, top, right, bottom, paint );
						
						//draw the inner square
						paint.setColor( current.evolveColor );
						canvas.drawRect( left + offset, top + offset, right - offset, bottom - offset, paint );
					}
					else
					{
						//to draw the cell, set paint to the color we're drawing
						paint.setColor( current.color );
						canvas.drawRect( left, top, right, bottom, paint );
					}
				}
			}
			
			//finished drawing. hand the canvas back to the holder
			holder.unlockCanvasAndPost( canvas );
			fin = System.currentTimeMillis();
			
		} while( animating );
	}
}
