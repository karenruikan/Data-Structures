import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Block implements IBlock {

    private Color color;
    private int depth;

    private Point topLeft;
    private Point botRight;

    private IBlock topLeftTree;
    private IBlock topRightTree;
    private IBlock botLeftTree;
    private IBlock botRightTree;
    private IBlock parentRoot;
    private Random rand;


    public Block(Point topLeft, Point botRight, int depth, IBlock parentroot) {
        this.depth = depth;
        this.rand = new Random();
        this.color = genRandomColor();

        this.topLeftTree = null;
        this.topRightTree = null;
        this.botLeftTree = null;
        this.botRightTree = null;
        this.parentRoot = parentroot;

        this.topLeft = topLeft;
        this.botRight = botRight;
    }

    /**
     * smash this block into 4 sub block of random color. the depth of the new
     * blocks should be less than maximum depth
     * 
     * @param maxDepth the max depth of this board/quadtree
     */
    public void smash(int maxDepth) {

        if (maxDepth == this.depth) {
            return;
        }

        this.setColor(null);

        Point center = new Point(
                (topLeft.getX() + botRight.getX()) / 2,
                (topLeft.getY() + botRight.getY()) / 2
                );

        // TopLeft
        topLeftTree = new Block(
                this.getTopLeft(),
                center,
                this.depth + 1, 
                this
                );
        this.setTopLeftTree(topLeftTree);

        // TopRight
        topRightTree = new Block(
                new Point(center.getX(), topLeft.getY()),
                new Point(botRight.getX(), center.getY()),
                this.depth + 1, 
                this
                );
        this.setTopRightTree(topRightTree);

        // BotRight
        botRightTree = new Block(
                center,
                this.getBotRight(),
                this.depth + 1, 
                this

                );
        this.setBotRightTree(botRightTree);

        // BotLeft
        botLeftTree = new Block(
                new Point(topLeft.getX(), center.getY()),
                new Point(center.getX(), botRight.getY()),
                this.depth + 1, 
                this

                );
        this.setBotLeftTree(botLeftTree);
    }

    /**
     * @return the depth of this block
     */
    public int depth() {
        return this.depth;
    }

    /**
     * used by {@link IGame#random_init()} random_init 
     * to keep track of sub blocks.
     * 
     * The children are returned in this order: 
     * upper-left child (NW),
     * upper-right child (NE), 
     * lower-right child (SE), 
     * lower-left child (SW).
     * 
     * @return the list of all the children of this block (clockwise order,
     *         starting with top left block)
     */
    public List<IBlock> children() {
        if (this.getBotLeftTree() == null && this.getBotRightTree() == null
                && this.getTopLeftTree() == null && this.getTopRightTree() == null) {
            return null;
        }
        return Arrays.asList(
                this.getTopLeftTree(), this.getTopRightTree(),
                this.getBotRightTree(), this.getBotLeftTree()
                );
    }

    /**
     * rotate this block clockwise.
     * 
     *  To rotate, first move the children's pointers
     * then recursively update the top left and
     *  bottom right points of each child.
     */
    public void rotate() {
        // short-circuit
        if (this.isleaf()) {
            return;
        }

        // rotate the trees
        IBlock temp = this.getBotLeftTree();
        this.setBotLeftTree(this.getBotRightTree());
        this.setBotRightTree(this.getTopRightTree());
        this.setTopRightTree(this.getTopLeftTree());
        this.setTopLeftTree(temp);

        //NW 
        int nwTLx = this.topLeftTree.getTopLeft().getX();
        int nwTLy = this.topLeftTree.getTopLeft().getY();
        int nwBRx = this.topLeftTree.getBotRight().getX();
        int nwBRy = this.topLeftTree.getBotRight().getY();
        //NE
        int neTLx = this.topRightTree.getTopLeft().getX();
        int neTLy = this.topRightTree.getTopLeft().getY();
        int neBRx = this.topRightTree.getBotRight().getX();
        int neBRy = this.topRightTree.getBotRight().getY();
        //SE
        int seTLx = this.botRightTree.getTopLeft().getX();
        int seTLy = this.botRightTree.getTopLeft().getY();
        int seBRx = this.botRightTree.getBotRight().getX();
        int seBRy = this.botRightTree.getBotRight().getY();
        //SW
        int swTLx = this.botLeftTree.getTopLeft().getX();
        int swTLy = this.botLeftTree.getTopLeft().getY();
        int swBRx = this.botLeftTree.getBotRight().getX();
        int swBRy = this.botLeftTree.getBotRight().getY();

        // set topleft / botright recursively
        this.getBotLeftTree().getTopLeft().setX(swTLx);
        this.getBotLeftTree().getTopLeft().setY(swTLy);
        this.getBotLeftTree().getBotRight().setX(swBRx);
        this.getBotLeftTree().getBotRight().setY(swBRy);

        this.getBotRightTree().getTopLeft().setX(seTLx);
        this.getBotRightTree().getTopLeft().setY(seTLy);
        this.getBotRightTree().getBotRight().setX(seBRx);
        this.getBotRightTree().getBotRight().setX(seBRy);

        this.getTopRightTree().getTopLeft().setX(neTLx);
        this.getTopRightTree().getTopLeft().setY(neTLy);
        this.getTopRightTree().getBotRight().setX(neBRx);
        this.getTopRightTree().getBotRight().setY(neBRy);

        this.getTopLeftTree().getTopLeft().setX(nwTLx);
        this.getTopLeftTree().getTopLeft().setY(nwTLy);
        this.getTopLeftTree().getBotRight().setX(nwBRx);
        this.getTopLeftTree().getBotRight().setY(nwBRy);

        this.rotateHelper(
                this.getTopLeftTree(), 
                this.getTopLeftTree().getTopLeft(), 
                this.getTopLeftTree().getBotRight()
        );
        this.rotateHelper(
                this.getTopRightTree(), 
                this.getTopRightTree().getTopLeft(), 
                this.getTopRightTree().getBotRight()
        );
        this.rotateHelper(
                this.getBotRightTree(), 
                this.getBotRightTree().getTopLeft(), 
                this.getBotRightTree().getBotRight()
        );
        this.rotateHelper(
                this.getBotLeftTree(), 
                this.getBotLeftTree().getTopLeft(), 
                this.getBotLeftTree().getBotRight()
        );
    }

    // Similar to smash but used in a recursive way to update topleft/ botright for all sub blocks
    private void rotateHelper(IBlock block, Point topLeft, Point botRight) {
        if (block.isleaf()) {
            return;
        }

        Point center = new Point(
                (topLeft.getX() + botRight.getX()) / 2,
                (topLeft.getY() + botRight.getY()) / 2
                );

        // TopLeft
        block.getTopLeftTree().getTopLeft().setX(topLeft.getX());
        block.getTopLeftTree().getTopLeft().setY(topLeft.getY());
        block.getTopLeftTree().getBotRight().setX(center.getX());
        block.getTopLeftTree().getBotRight().setY(center.getY());
        // recurse
        rotateHelper(
                block.getTopLeftTree(), 
                block.getTopLeftTree().getTopLeft(), 
                block.getTopLeftTree().getBotRight()
        );

        // TopRight
        block.getTopRightTree().getTopLeft().setX(center.getX());
        block.getTopRightTree().getTopLeft().setY(topLeft.getY());
        block.getTopRightTree().getBotRight().setX(botRight.getX());
        block.getTopRightTree().getBotRight().setY(center.getY());
        // recurse
        rotateHelper(
                block.getTopRightTree(), 
                block.getTopRightTree().getTopLeft(), 
                block.getTopRightTree().getBotRight()
        );

        // BotRight
        block.getBotRightTree().getTopLeft().setX(center.getX());
        block.getBotRightTree().getTopLeft().setY(center.getY());
        block.getBotRightTree().getBotRight().setX(botRight.getX());
        block.getBotRightTree().getBotRight().setY(botRight.getY());
        // recurse
        rotateHelper(
                block.getBotRightTree(), 
                block.getBotRightTree().getTopLeft(), 
                block.getBotRightTree().getBotRight()
        );

        // BotLeft
        block.getBotLeftTree().getTopLeft().setX(topLeft.getX());
        block.getBotLeftTree().getTopLeft().setY(center.getY());
        block.getBotLeftTree().getBotRight().setX(center.getX());
        block.getBotLeftTree().getBotRight().setY(botRight.getY());
        // recurse
        rotateHelper(
                block.getBotLeftTree(), 
                block.getBotLeftTree().getTopLeft(), 
                block.getBotLeftTree().getBotRight()
        );
    }

    /**
     * @return a random color
     */
    private Color genRandomColor() {
        return COLORS[this.rand.nextInt(COLORS.length)];
    }


    /**
     * @return the color of this block
     */
    public Color getColor() {
        return this.color;
    }


    /**
     * Changes the color of this block
     * 
     * @param c the new color
     */
    public void setColor(Color c) {
        this.color = c;
    }


    /**
     * @return top left point
     */
    public Point getTopLeft() {
        return this.topLeft;
    }


    /**
     * @return Bottom right point
     */
    public Point getBotRight() {
        return this.botRight;
    }


    /**
     * @return top left point
     */
    public void setTopLeft(Point topLeft) {
        this.topLeft = topLeft;
    }


    /**
     * @return Bottom right point
     */
    public void setBotRight(Point botRight) {
        this.botRight = botRight;
    }


    /**
     * @return true if this block has no children/sub blocks
     */
    public boolean isleaf() {
        return this.children() == null;
    }

    /**
     * @return the top left sub block (NE)
     */
    public IBlock getTopLeftTree() {
        return this.topLeftTree;
    }

    /**
     * @return the top right sub block (SE)
     */
    public IBlock getTopRightTree() {
        return this.topRightTree;
    }

    /**
     * @return the bottom left sub block (SE)
     */
    public IBlock getBotLeftTree() {
        return this.botLeftTree;
    }

    /**
     * @return the bottom right sub block (SW)
     */
    public IBlock getBotRightTree() {
        return this.botRightTree;
    }

    /**
     * Changes the top left tree
     * 
     * @param tree the new tree
     */
    public void setTopLeftTree(IBlock tree) {
        this.topLeftTree = tree;
    }

    /**
     * Changes the top right tree
     * 
     * @param tree the new tree
     */
    public void setTopRightTree(IBlock tree) {
        this.topRightTree = tree;
    }

    /**
     * Changes the bot right tree
     * 
     * @param tree the new tree
     */
    public void setBotRightTree(IBlock tree) {
        this.botRightTree = tree;
    }

    /**
     * Changes the bot left tree
     * 
     * @param tree the new tree
     */
    public void setBotLeftTree(IBlock tree) {
        this.botLeftTree = tree;
    }

    /**
     * check if two blocks are the same -- used for testing
     * 
     * @param another block
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IBlock)) {
            return false;
        }
        IBlock block = (IBlock) o;
        return block.depth() == this.depth() && 
                block.getColor() == this.getColor() &&
                block.getTopLeft() == this.getTopLeft() &&
                block.getBotRight() == this.getBotRight();
    }

    public IBlock getParent() {
        return this.parentRoot;
    }

    public void setParent(Block pBlock) {
        parentRoot = pBlock;

    }
}
