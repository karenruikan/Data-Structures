import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocumentIteratorTest {

    @Test
    public void testNext() {
	Reader r = new StringReader("This is the first HW kkkk 1 @$704958.,; jlksji haha");
	DocumentIterator ws = new DocumentIterator(r, 5);
	assertEquals("thisisthefirsthw", ws.next());
	assertEquals("isthefirsthwkkkk", ws.next());
	assertEquals("thefirsthwkkkkjlksji", ws.next());
	assertEquals("firsthwkkkkjlksjihaha", ws.next());
	assertEquals("", ws.next());
	assertFalse(ws.hasNext());
    }

}
