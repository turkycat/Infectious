package turkycat.android.infectious.darwin;

import java.util.Collection;
import java.util.LinkedList;

import android.graphics.Color;

/**
 * The cell is the basic building block of larger organisms. The Cell class is particularly self-aware, 
 * 	providing and controlling the knowledge of itself, it's role, and its place in the big picture of the organism.
 * 
 * The cell class can be implemented to extend, direct & control the behavior of individual cells and their behavior within
 * 	the organism which it plays host to
 * 
 * @author Jesse Frush
 * 
 */
public abstract class Cell implements Cloneable
{
	private final static String TAG = "Cell";
	protected final static float EVOLUTION_GROWTH_RATE = 0.03f;
	
	protected Organism organism;
	protected Player owner;
	public State state;
	
	public int color;

	public boolean evolving;
	public float evolvePercentage;
	public int evolveColor;
	
	protected Collection<Cell> neighbors;
	
	
	//----------------------------------------------------------------------------constructors
	
	public Cell( Organism org, int x, int y, int color )
	{
		this( org, x, y, color, Player.ORGANISM, State.ZERO );
	}
	
	
	public Cell( Organism org, int x, int y, int color, Player owner, State state )
	{
		neighbors = new LinkedList<Cell>();
		this.color = color;
		this.evolving = false;
		this.evolveColor = color;
		this.evolvePercentage = 1.0f;
		this.organism = org;
		this.owner = owner;
		this.state = state;
	}
	
	
	
	//---------------------------------------------------------traversal-related methods
	
	
	/**
	 * adds the given cell to the list of neighbor cells
	 *
	 * @return true if newly added, false otherwise
	 */
	public boolean addNeighbor( Cell cell )
	{
		if( neighbors.contains( cell ) ) return false;
		neighbors.add( cell );
		return true;
	}
	
	
	
	/**
	 * a getter for the collection of neighbors
	 */
	public Collection<Cell> getNeighbors()
	{
		return neighbors;
	}
	
	
	
	/**
	 * determines if a given cell is a neighbor of this cell
	 * 
	 * @param cell - the cell to be compared against this cell
	 * @return true if they are the same object or equivalent objects
	 */
	public boolean isNeighbor( Cell cell )
	{
		for( Cell c : neighbors )
		{
			if( cell == c || cell.equals( c ) ) return true;
		}
		
		return false;
	}
	
	
	
	/**
	 * this method controls how the cell evolves between each draw cycle.
	 * this method will effectively do nothing if the cell is not evolving.
	 * @return - true if the cell is still evolving after the phase
	 * 		   - false if the cell is not evolving or completes the final phase this cycle.
	 */
	public boolean phase()
	{
		evolvePercentage += EVOLUTION_GROWTH_RATE;
		if( evolvePercentage >= 1.0f )
		{
			evolving = false;
			evolvePercentage = 1.0f;
			color = evolveColor;
		}
		
		return evolving;
	}
	
	
	
	protected void infect( Player currentPlayer, int color )
	{
		//if the color matches, but the player doesn't. Take control of this cell.
		owner = currentPlayer;
		this.state = State.ONE;
		
		//set the evolution of this cell to take place
		this.evolving = true;
		this.color = Color.WHITE;
		this.evolveColor = color;
		this.evolvePercentage = 0.0f;
	}
	

	
	public abstract boolean processCell( Player currentPlayer, int color );
	
	
	//----------------------------------------------------------getters & setters
	
	public int getColor()
	{
		return color;
	}
	
	
	/**
	 * this is a public field. Access the variable directly to improve performance on mobile devices.
	 * @return
	 */
	public State getState()
	{
		return state;
	}
	
	
	public Player getOwner()
	{
		return owner;
	}
	
	
//	public int getX()
//	{
//		return xPos;
//	}
//	
//	
//	public int getY()
//	{
//		return yPos;
//	}
	
	
	public void setColor( int color )
	{
		this.color = color;
	}
	
	
	public void setState( State state )
	{
		this.state = state;
	}
	
	
	public void setOwner( Player owner )
	{
		this.owner = owner;
	}
	
	
	
	
	
	//-------------------------------------------------------------------------------static methods
	
	
	
	/*
	 * this method accepts a 2d array of cells and sets up the neighbors for game traversal logic.
	 * 	- a setup method should ALWAYS be ran after creating each cell for the individual organism
	 *  - do not pass null cells in the arrays. If you wish not to use a cell in a position, 
	 *  		correct usage is to create a cell with State.INVALID, which will never be drawn or processed.
	 *  
	 * this method uses VonNeumann neighbors:
	 * 
	 * ex) x is vonNeumann neighbor of O
	 *           x
	 *         x O x
	 *           x
	 */
	public static void setupVonNeumannNeighbors( Cell[][] cells )
	{
		setupNeighbors( cells, false );
	}
	
	
	/*
	 * this method accepts a 2d array of cells and sets up the neighbors for game traversal logic.
	 * 	- a setup method should ALWAYS be ran after creating each cell for the individual organism
	 *  - do not pass null cells in the arrays. If you wish not to use a cell in a position, 
	 *  		correct usage is to create a cell with State.INVALID, which will never be drawn or processed.
	 *  
	 * this method uses VonNeumann neighbors:
	 * 
	 * ex) x is vonNeumann neighbor of O
	 *           x
	 *         x O x
	 *           x
	 */
	public static void setupMooreNeighbors( Cell[][] cells )
	{
		setupNeighbors( cells, true );
	}
	
	
	
	/**
	 * set's up the neighborhood by creating edges between cells
	 * 
	 * @param cells to be parsed and setup
	 * @param Moore - determines whether Moore neighborhood is used
	 * 				- true = Moore
	 * 				- false = Von Neuman
	 */
	private static void setupNeighbors( Cell[][] cells, boolean Moore )
	{
		if( cells == null || cells.length == 0 ) return;
		
		synchronized( cells )
		{
			int height = cells.length;
			int width = cells[0].length;

			for( int i = 0; i < height; i++ )
			{
				for( int j = 0; j < width; j++ )
				{
					if( cells[i][j] == null ) continue;
					if( cells[i][j].getState() == State.INVALID ) continue;

					//check below
					if( i > 0 ) verifyAndAddNeighbor( cells[i][j], cells[i - 1][j] );

					//above
					if( i < height - 1 ) verifyAndAddNeighbor( cells[i][j], cells[i + 1][j] );

					//to the left
					if( j > 0 ) verifyAndAddNeighbor( cells[i][j], cells[i][j - 1] );

					//to the right
					if( j < width - 1 ) verifyAndAddNeighbor( cells[i][j], cells[i][j + 1] );

					//diagonals
					if( Moore )
					{
						//upper
						if( i < height - 1 )
						{
							//upper left
							if( j > 0 ) verifyAndAddNeighbor( cells[i][j], cells[i + 1][j - 1] );

							//upper right
							if( j < width - 1 ) verifyAndAddNeighbor( cells[i][j], cells[i + 1][j + 1] );
						}

						//lower
						if( i > 0 )
						{
							//lower left
							if( j > 0 ) verifyAndAddNeighbor( cells[i][j], cells[i - 1][j - 1] );

							//lower right
							if( j < width - 1 ) verifyAndAddNeighbor( cells[i][j], cells[i - 1][j + 1] );
						}
					}
				}
			}
		}
	}
	
	
	/*
	 * verifies that the neighbor cell is a valid Cell in a valid State
	 * 	-if so, the neighbor is added to the current cell's neighbor list ( the edge [ toVerify -> neighbor ] is created )
	 */
	private static void verifyAndAddNeighbor( Cell currentCell, Cell neighbor )
	{
		if( currentCell == null || neighbor == null ) return;
		if( neighbor.getState() == State.INVALID ) return;
		currentCell.addNeighbor( neighbor );
	}
	
	
	
	/**
	 * clone method
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		Cell cell = (Cell) super.clone();
		
		cell.organism = this.organism;
		cell.color = this.color;
		cell.owner = this.owner;
		cell.state = this.state;
		cell.evolving = this.evolving;
		cell.evolvePercentage = this.evolvePercentage;
		
		return cell;
	}
}
