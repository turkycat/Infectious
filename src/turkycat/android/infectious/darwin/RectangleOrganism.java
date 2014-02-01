package turkycat.android.infectious.darwin;

import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import android.graphics.Color;
import android.widget.Toast;


/**
 * The most basic implementation of an organism. This implementation takes the shape of a rectangle and bases evolution
 * 	of cells strictly from color
 * 
 * this organism supports all four players, and uses only States ZERO (organism) and ONE (player owned). INVALID will not occur.
 * 
 * @author Jesse Frush
 *
 */
public class RectangleOrganism extends Organism
{
	private final String TAG = "RectangleOrganism";
	
	private int height;
	private int width;
	
	
	/**
	 * a constructor which will build an instance of this class with the default size.
	 */
	public RectangleOrganism( int[] colors )
	{
		this( colors, 62, 40 );
	}
	
	
	/**
	 * a constructor which will build an instance of this class with a custom size.
	 */
	public RectangleOrganism( int[] colors, int height, int width )
	{
		this.height = height;
		this.width = width;
		init( colors );
	}
	
	
	private void init( int[] colors )
	{
		int n = colors.length;
		Random rnd = new Random();
		
		cells = new ColorBasedCell[height][width];
		for( int i = 0; i < height; i++ )
		{
			for( int j = 0; j < width; j++ )
			{
				cells[i][j] = new ColorBasedCell( this, i, j, colors[ rnd.nextInt( n ) ] );
			}
		}

//		ColorBasedCell.setupVonNeumannNeighbors( cells );
		ColorBasedCell.setupMooreNeighbors( cells );
	}
	
	
	
	
	
	@Override
	public Cell setupPlayer( Player player, int color )
	{
		int xpos = 0;
		int ypos = 0;
		
		// setup is pretty easy for a rectangle. just position players around it.
		switch( player )
		{
		case ORGANISM: return null;
		case PLAYER_ONE:
			xpos = 0;
			ypos = 0;
			break;
			
		case PLAYER_TWO:
			xpos = width - 1;
			ypos = height - 1;
			break;
			
		case PLAYER_THREE:
			xpos = 0;
			ypos = height - 1;
			break;
			
		case PLAYER_FOUR:
			xpos = width - 1;
			ypos = 0;
			break;
		}

		if( cells[xpos][ypos].getOwner() != Player.ORGANISM ) return null;
		cells[xpos][ypos].setOwner( player );
		cells[xpos][ypos].setState( State.ONE );

		processMove( cells[xpos][ypos], color );
		
		return cells[xpos][ypos];
	}
	
	
	
	

	/**
	 * performs a move for a player
	 */
	public void processMove( Cell cell, int color )
	{
		if( cell == null ) return;
		Player currentPlayer = cell.owner;
		boolean possibleWin = true;
		
		HashSet<Cell> visited = new HashSet<Cell>();
		Deque<Cell> que = new LinkedList<Cell>();
		visited.add( cell );
		que.add( cell );
		
		while( !que.isEmpty() )
		{
			Cell current = que.pop();
			
			//only continue with neighbors if processCell returns true
			if( current.processCell( currentPlayer, color ) )
			{
				if( current.owner != currentPlayer ) possibleWin = false;
				Collection<Cell> neighbors = current.getNeighbors();
				for( Cell neighbor : neighbors )
				{
					if( visited.contains( neighbor ) ) continue;
					visited.add( neighbor );
					que.add( neighbor );
				}
			}
		}
		//if( possibleWin )
		//{
		//	Toast.makeText( , "you win and stuff, yay", Toast.LENGTH_LONG ).show();
		//}
	}
	
	
	
	
	
	
	
	
	
	
	private class ColorBasedCell extends Cell
	{

		public ColorBasedCell( Organism org, int x, int y, int color )
		{
			super( org, x, y, color );
		}
		
		
		public ColorBasedCell( Organism org, int x, int y, int color, Player owner, State state )
		{
			super( org, x, y, color, owner, state );
		}
		
		
		
		/**
		 * this will switch a cell to the given color if already owned by the current player
		 * otherwise, takes control of the cell if the given color matches the cell's current color.
		 */
		public boolean processCell( Player currentPlayer, int color )
		{
			if( currentPlayer != owner )
			{
				if( color != this.color ) return false;
				
				//player doesn't own this cell, but the color matches
				infect( currentPlayer, color );
				return true;
			}
			
			//player already owns this cell, so it changes
			//this.color = color;
			
			//instead of immediately changing, cells will "evolve" to a color over a short period of time.
			this.color = evolveColor;
			this.evolving = true;
			this.evolvePercentage = 0.0f;
			this.evolveColor = color;
			
			return true;
		}
	}
}
