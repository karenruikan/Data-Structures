import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;

import org.junit.Test;

public class CharCounterTest {

    String filePath = "/autograder/submission";
    ICharCounter cc = new CharCounter();

    @Test
    public void testGetCount() throws IOException {

        InputStream ins = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
        cc.countAll(ins);
        assertEquals(3, cc.getCount(116));
        assertEquals(0, cc.getCount(119));

        assertThrows(IllegalArgumentException.class, () -> {
            cc.getCount(-1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            cc.getCount(257);
        });
    }

    @Test
    public void testCountAll() throws IOException {

        InputStream ins = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
        assertEquals(10, cc.countAll(ins));
        assertEquals(3, cc.getTable().get(116));
        assertEquals(2, cc.getTable().get(115));
        assertEquals(1, cc.getTable().get(101));

    }

    @Test
    public void testClear() throws IOException {

        InputStream ins = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
        cc.countAll(ins);
        cc.clear();
        assertEquals(0, cc.getTable().size());

    }

    @Test
    public void testGetTable() throws IOException {

        InputStream ins = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
        cc.countAll(ins);
        Map table = cc.getTable();
        assertEquals(7, table.size());
        assertEquals(3, table.get(116));

    }

}
