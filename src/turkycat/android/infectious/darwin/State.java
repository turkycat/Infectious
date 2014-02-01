package turkycat.android.infectious.darwin;


/**
 * The all-powerful State enum dictates the evolution of the cells of an organism.
 * 
 * 	- for example:
 * 			- each cell will have an owner. This is the Organism it belongs to until it is taken over by a virus (player).
 * 				cells may be manipulated only by the owner. Typically, a cell in state zero may be used to represent a 'healthy' cell
 * 				that most likely not change in the next round.
 * 			- additional states may be used to transition cells for varying game types.
 * 			
 * 
 * @author Jesse
 *
 */
public enum State
{
	INVALID,
	ZERO,
	ONE,
	TWO,
	THREE,
	FOUR,
	FIVE,
	SIX,
	SEVEN,
	EIGHT,
	NINE,
	TEN
}
