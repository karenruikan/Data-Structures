import java.awt.Color;
import java.util.Queue;
import java.util.Random;
import java.util.LinkedList;

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
 * Game(int maxDepth, Color target)
 * 
 * @author ericfouh
 *
 */

public class Game implements IGame {

    private int maxDepth;
    private Color target;
    private IBlock root;

    public Game(int maxDepth, Color target) {
        this.maxDepth = maxDepth;
        this.target = target;
        this.root = this.random_init();
    }

    /**
     * @return the maximum dept of this blocky board.
     */
    public int max_depth() {
        return this.maxDepth;
    }

    /**
     * initializes a random board game
     * 
     * @return the root of the tree of blocks
     */
    public IBlock random_init() {
        this.root = new Block(new Point(0, 0), new Point(8, 8), 0, null);

        this.root.smash(this.maxDepth);

        int curDepth = 1;
        int cap = 5;
        int newBlock;

        while (curDepth < this.maxDepth) {
            Random rand = new Random();
            newBlock = rand.nextInt(cap);

            if (newBlock == 0) {
                continue;
            }

            IBlock temp = this.getBlock(newBlock);

            //Smash and update depth for exit condition
            if (temp.isleaf() && temp.depth() < maxDepth) {
                temp.smash(this.maxDepth);

                cap += 4;
                curDepth = temp.depth() + 1;
            } 
        }
        return this.root;
    }

    /**
     * Traverse the tree of blocks to find a sub block based on its id 
     * 
     * @param pos the id of the block
     * @return the block with id pos or null
     */
    public IBlock getBlock(int pos) {
        Queue<IBlock> q = new LinkedList<>();
        q.add(this.root);
        int curPos = 0;

        while (!q.isEmpty() && curPos <= pos) {
            IBlock curBlock = q.remove();

            if (pos == curPos) {
                return curBlock;
            }

            if (!curBlock.isleaf()) {
                for (IBlock child: curBlock.children()) {
                    q.add(child);
                }
            }
            curPos += 1; 
        }
        return null;
    }

    /**
     * @return the root of the quad tree representing this
     * blockly board
     */
    public IBlock getRoot() {
        return this.root;
    }

    /**
     * The two blocks  must be at the same level / have the same size. 
     * We should be able to swap a block with no sub blocks with
     * one with sub blocks.
     * 
     * 
     * @param x the block to swap
     * @param y the other block to swap
     */
    public void swap(int x, int y) {
        Block b1 = (Block) this.getBlock(x);
        Block b2 = (Block) this.getBlock(y);


        if (b1.getParent() != null && b2.getParent() != null) {

            Block p1 = (Block) b1.getParent();
            Block p2 = (Block) b2.getParent();
            
            if (b1.depth() == b2.depth()) {
                
                if (p1.getTopLeftTree() == b1) {
                    p1.setTopLeftTree(null);
                } else if (p1.getTopRightTree() == b1) {
                    p1.setTopRightTree(null);
                } else if (p1.getBotRightTree() == b1) {
                    p1.setBotRightTree(null);
                } else {
                    p1.setBotLeftTree(null);
                }

                if (p2.getTopLeftTree() == b2) {
                    p2.setTopLeftTree(b1);
                } else if (p2.getTopRightTree() == b2) {
                    p2.setTopRightTree(b1);
                } else if (p2.getBotRightTree() == b2) {
                    p2.setBotRightTree(b1);
                } else {
                    p2.setBotLeftTree(b1);
                }

                if (p1.getTopLeftTree() == null) {
                    p1.setTopLeftTree(b2);
                } else if (p1.getTopRightTree() == null) {
                    p1.setTopRightTree(b2);
                } else if (p1.getBotRightTree() == null) {
                    p1.setBotRightTree(b2);
                } else {
                    p1.setBotLeftTree(b2);
                }

                swapHelper(b1, b2.getTopLeft().getX() - b1.getTopLeft().getX(), 
                        b2.getTopLeft().getY() - b1.getTopLeft().getY());
                swapHelper(b2, b1.getTopLeft().getX() - b2.getTopLeft().getX(), 
                        b1.getTopLeft().getY() - b2.getTopLeft().getY());
            }
        }

    }
    
    public void swapHelper(Block block, int x, int y) {

        if (block.isleaf()) {

            block.setTopLeft(new Point(block.getTopLeft().getX() + x, 
                    block.getTopLeft().getY() + y));
            block.setBotRight(new Point(block.getBotRight().getX() + x, 
                    block.getBotRight().getY() + y));

        } else {
            this.swapHelper((Block) block.getTopLeftTree(), x, y);
            this.swapHelper((Block) block.getTopRightTree(), x, y);
            this.swapHelper((Block) block.getBotLeftTree(), x, y);
            this.swapHelper((Block) block.getBotRightTree(), x, y);

            block.setBotRight(block.getBotRightTree().getBotRight());
            block.setTopLeft(block.getTopLeftTree().getTopLeft());
           
        }

    }

    /**
     * Turns (flattens) the quadtree into a 2D-array of blocks.
     * Each cell in the array represents a unit cell.
     * This method should not mutate the tree.
     * @return a 2D array of the tree
     */

    public IBlock[][] flatten() {
        return null;
    }



    /**
     * computes the scores based on perimeter blocks of the same color
     * as the target color.
     * The quadtree must be flattened first
     * 
     * @return the score of the user (corner blocs count twice)
     */
    public int perimeter_score() {
        return 0;
    }


    /**
     * This method will be useful to test your code
     * @param root the root of this blockly board
     */
    public void setRoot(IBlock root) {
        this.root = root;
    }

}
