import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.*;

import org.junit.Test;

public class GameTest {
    
    Game game1 = new Game(2, Color.YELLOW);
    Game game2 = new Game(3, Color.GREEN);
    List<Color> listOfColors = Arrays.asList(IBlock.COLORS);
    
    @Test
    public void testGame() {
        assertTrue(game1 != null);
        assertTrue(game2 != null);
        
    }

    @Test
    public void testMax_depth() {
        assertEquals(2, game1.max_depth());
        assertEquals(3, game2.max_depth());
    }

    @Test
    public void testRandom_init() {
        Game game3 = new Game(4, Color.BLUE);
        assertEquals(4, game3.getRoot().children().size());
    }

    @Test
    public void testGetBlock() {
        assertTrue(game1.getBlock(2) != null);
        assertTrue(listOfColors.contains((game1.getBlock(5).getColor())));
        assertTrue(game1.getBlock(2) instanceof IBlock);
        assertEquals(1, game1.getBlock(2).depth());
       
    }

    @Test
    public void testGetRoot() {
        assertTrue(game1.getRoot() != null);
    }

    @Test
    public void testSwap() {

        game1.getRoot().smash(3);
        
        IBlock tl = game1.getRoot().getTopLeftTree();
        
        tl.smash(3);
        IBlock tltr = tl.getTopRightTree();
        tltr.smash(3);
        IBlock tltrbr = tltr.getBotRightTree();
        IBlock tltrtl = tltr.getTopLeftTree();
        
        game1.swap(9, 11);
        
        assertEquals(tltrbr, game1.getBlock(9));
        assertEquals(tltrtl, game1.getBlock(11));
        
    }

    @Test
    public void testFlatten() {
        return;
    }

    @Test
    public void testPerimeter_score() {
        assertEquals(0, game1.perimeter_score());
    }

}
