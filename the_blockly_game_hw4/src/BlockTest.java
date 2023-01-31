import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;

import java.awt.Color;
import java.util.*;

public class BlockTest {
    Point pointA = new Point(0, 0);
    Point pointB = new Point(8, 8);
    List<Color> listOfColors = Arrays.asList(IBlock.COLORS);
    
    @Test
    public void testBlock() {
        Block block = new Block(pointA, pointB, 0, null);
        assertTrue(block != null);
    }

    @Test
    public void testSmash() {
        Block block = new Block(pointA, pointB, 0, null);
        block.smash(2);
        assertEquals(1, block.getTopLeftTree().depth());
        assertEquals(1, block.getTopRightTree().depth());
        
        assertNotNull(block.getBotRightTree());
        assertFalse(block.isleaf());
        assertTrue(block.getTopLeftTree().isleaf());
        
//        assertEquals(new Point(4,8), block.getBotLeftTree().getBotRight());
//        assertEquals(new Point(8,4), block.getTopRightTree().getBotRight());
    }

    @Test
    public void testDepth() {
        Block block = new Block(pointA, pointB, 0, null);

        assertEquals(0,block.depth());
    }

    @Test
    public void testChildren() {
        Block block = new Block(pointA, pointB, 0, null);
        block.smash(2);
        assertEquals(4, block.children().size());
        assertTrue(listOfColors.contains(block.children().get(0).getColor()));
        assertTrue(listOfColors.contains(block.children().get(1).getColor()));
    }

    @Test
    public void testRotate() {
        Block block = new Block(pointA, pointB, 0, null);

        IBlock ogTopLeft = block.getTopLeftTree();
        block.rotate();
        assertEquals(ogTopLeft, block.getTopRightTree());
    }

    @Test
    public void testGetColor() {
        Block block = new Block(pointA, pointB, 0, null);
        assertTrue(listOfColors.contains(block.getColor()));
        block.smash(2);
        assertTrue(listOfColors.contains(block.children().get(0).getColor()));
        assertTrue(listOfColors.contains(block.children().get(1).getColor()));
    }

    @Test
    public void testSetColor() {
        Block block = new Block(pointA, pointB, 0, null);
        block.setColor(Color.BLUE);
        assertEquals(Color.BLUE, block.getColor());
    }

    @Test
    public void testGetTopLeft() {
        Block block = new Block(pointA, pointB, 0, null);
        block.smash(1);
        
        assertTrue(block.getTopLeft() != null);
        assertEquals(0, block.getTopLeft().getX());
        assertEquals(0, block.getTopLeft().getY());
        assertEquals(4, block.children().get(1).getTopLeft().getX());
        assertEquals(0, block.children().get(1).getTopLeft().getY());
    }

    @Test
    public void testGetBotRight() {
        Block block = new Block(pointA, pointB, 0, null);
        block.smash(1);
        
        assertTrue(block.getBotRight() != null);
        assertEquals(8, block.getBotRight().getX());
        assertEquals(8, block.getBotRight().getY());
        IBlock x = block.children().get(1);
        assertEquals(8, block.children().get(1).getBotRight().getX());
        assertEquals(4, block.children().get(1).getBotRight().getY());
    }

    @Test
    public void testSetTopLeft() {
        Block block = new Block(pointA, pointB, 0, null);
        block.smash(1);
        
        Point topleft = new Point(4,4);
        block.setTopLeft(topleft);
        assertEquals(4, block.getTopLeft().getX());
        
    }

    @Test
    public void testSetBotRight() {
        Block block = new Block(pointA, pointB, 0, null);
        block.smash(1);
        
        Point botright = new Point(4,4);
        block.setBotRight(botright);
        assertEquals(4, block.getBotRight().getX());
    }

    @Test
    public void testIsleaf() {
        Block block = new Block(pointA, pointB, 0, null);
        assertTrue(block.isleaf());
        block.smash(1);
        assertFalse(block.isleaf());
    }

    @Test
    public void testGetTopLeftTree() {
        Block block = new Block(pointA, pointB, 0, null);
        block.smash(2);
        assertTrue(block.getTopLeftTree() != null);
        assertEquals(1,block.getTopLeftTree().depth());
        
    }

    @Test
    public void testGetTopRightTree() {
        Block block = new Block(pointA, pointB, 0, null);
        block.smash(2);
        assertTrue(block.getTopRightTree() != null);
        assertEquals(1, block.getTopRightTree().depth());
    }

    @Test
    public void testGetBotLeftTree() {
        Block block = new Block(pointA, pointB, 0, null);
        block.smash(2);
        assertTrue(block.getBotLeftTree() != null);
        assertEquals(1,block.getBotLeftTree().depth());
    }

    @Test
    public void testGetBotRightTree() {
        Block block = new Block(pointA, pointB, 0, null);
        block.smash(2);
        assertTrue(block.getBotRightTree() != null);
        assertEquals(1,block.getBotRightTree().depth());
    }

}
