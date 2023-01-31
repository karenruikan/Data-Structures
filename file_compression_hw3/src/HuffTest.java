import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;

import org.junit.Test;

public class HuffTest {

    Huff ht = new Huff();

    @Test
    public void testMakeHuffTree() throws IOException {
        InputStream ins = new ByteArrayInputStream("teststring".getBytes("UTF-8"));

        ht.makeHuffTree(ins);
        assertTrue(ht != null);
        assertEquals(3, ht.showCounts().get(116));
        assertEquals(2, ht.showCounts().get(115));
        assertEquals(1, ht.showCounts().get(101));
        assertEquals(HuffTree.class, ht.makeHuffTree(ins).getClass());

    }

    @Test
    public void testMakeTable() throws IOException {
        InputStream ins = new ByteArrayInputStream("teststring".getBytes("UTF-8"));

        ht.makeHuffTree(ins);
        ht.makeTable();
        assertEquals("001", ht.getCode(101)); // e
        assertEquals("110", ht.getCode(115)); // s
        assertEquals("10", ht.getCode(116)); // t
        assertEquals("000", ht.getCode(114)); // r
        assertEquals("011", ht.getCode(105)); // i
        assertEquals("1110", ht.getCode(110)); // n
        assertEquals("010", ht.getCode(103)); // g
        assertEquals("1111", ht.getCode(256)); // PSEUDO_EOF
    }

    @Test
    public void testShowCounts() throws IOException {
        InputStream ins = new ByteArrayInputStream("teststring".getBytes("UTF-8"));

        assertNull(ht.showCounts());
        ht.makeHuffTree(ins);
        assertTrue(ht.showCounts() != null);
        assertEquals(3, ht.showCounts().get(116));
        assertEquals(HashMap.class, ht.showCounts().getClass());
    }

    @Test
    public void testWrite() throws IOException {
        String outFile = "/autograder/submission/compressed.txt";
        String inFile = "/autograder/submission/ogfile1.txt";

        assertEquals(148, ht.write(inFile, outFile, true));
        
    }

    @Test
    public void testUncompress() {
        String outFile = "/autograder/submission/uncompressed.txt";
        String inFile = "/autograder/submission/compressed.txt";

        assertEquals(88, ht.uncompress(inFile, outFile));
        //this assert value needs to be updated
    }

    @Test
    public void testHeaderSize() throws IOException {
        InputStream ins = new ByteArrayInputStream("teststring".getBytes("UTF-8"));

        ht.makeHuffTree(ins);
        assertEquals(119, ht.headerSize());
    }

    @Test
    public void testWriteHeader() throws IOException {
        InputStream ins = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ht.makeHuffTree(ins);
        ht.makeTable();

        assertTrue(ht != null);
        assertEquals(119, ht.writeHeader(new BitOutputStream(out)));

        out.close();

    }

    //Not sure how to write this test
    @Test
    public void testReadHeader() throws IOException {
        String inFile = "/autograder/submission/compressed.txt";

        BitInputStream bitin = new BitInputStream(inFile);

        HuffTree readHT = ht.readHeader(bitin);
        assertEquals(HuffTree.class, readHT.getClass());
        assertEquals(87, readHT.size());
    }

}
