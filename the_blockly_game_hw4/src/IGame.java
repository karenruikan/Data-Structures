import java.awt.Color;

/**
 * the IGame class creates the blocky board 
 * It maintains the max_depth
 * === Class Attributes === 
 * maximum depth: 
 *      -   max_depth
 *          The deepest level/depth allowed in the overall block
 *          structure. 
 * 
 * The root of the quadtree
 *      -   root
 * 
 *The target color
 *      -   color
 * 
 * ===Constructor === 
 *
 * The Game class constructor has the following signature 
 * Game(int max_depth, Color target)
 * 
 * @author ericfouh
 *
 */

public interface IGame {    
    /**
     * @return the maximum dept of this blocky board.
     */
    public int max_depth();
    
    /**
     * initializes a random board game
     * 
     * @return the root of the tree of blocks
     */
    public IBlock random_init();

    /**
     * Traverse the tree of blocks to find a sub block based on its id 
     * 
     * @param pos the id of the block
     * @return the block with id pos or null
     */
    public IBlock getBlock(int pos);

    /**
     * @return the root of the quad tree representing this
     * blockly board
     */
    public IBlock getRoot();
    
    /**
     * The two blocks  must be at the same level / have the same size. 
     * We should be able to swap a block with no sub blocks with
     * one with sub blocks.
     * 
     * 
     * @param x the block to swap
     * @param y the other block to swap
     */
    public void swap(int x, int y);
    
    
    
    /**
     * Turns (flattens) the quadtree into a 2D-array of blocks.
     * Each cell in the array represents a unit cell.
     * This method should not mutate the tree.
     * @return a 2D array of the tree
     */
    
    public IBlock[][] flatten();

    
 
    /**
     * computes the scores based on perimeter blocks of the same color
     * as the target color.
     * The quadtree must be flattened first
     * 
     * @return the score of the user (corner blocs count twice)
     */
    public int perimeter_score();
    
    
    /**
     * This method will be useful to test your code
     * @param root the root of this blockly board
     */
    public void setRoot(IBlock root);
    
}
