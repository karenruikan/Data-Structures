import java.io.*;
import java.util.*;

public class Huff implements ITreeMaker, IHuffEncoder, IHuffHeader, IHuffModel {

    private HuffTree tree;
    private CharCounter cc;
    private Map<Integer, String> encTable;
    private Map<Integer, Integer> freqTable;
    private MinHeap minHeap;

    // Constructor
    public Huff() {
        this.tree = null;
        this.cc = new CharCounter();
        this.encTable = new HashMap<Integer, String>();
    }

    public HuffTree buildTree() {

        HuffTree tmp1, tmp2 = null;

        while (minHeap.heapsize() > 1) { // While two items left
            tmp1 = (HuffTree) minHeap.removemin();
            tmp2 = (HuffTree) minHeap.removemin();
            tree = new HuffTree(tmp1.root(), tmp2.root(), tmp1.weight() + tmp2.weight());
            minHeap.insert(tree); // Return new tree to heap
        }
        return tree; // Return the tree
    }

    @Override
    public HuffTree makeHuffTree(InputStream stream) throws IOException {

        BitInputStream bits = new BitInputStream(stream);

        // Get the counts of all characters, and put into the hashmap
        cc.countAll(bits);
        freqTable = cc.getTable();

        // Create an array of HuffTree the size of your frequency table + 1 (for the
        // PSEUDO_EOF).

        HuffTree[] htArray = new HuffTree[cc.getTable().size() + 1];

        // Add all HuffTrees into the array.
        int i = 0;
        for (int ch : cc.getTable().keySet()) {
            HuffTree leaf = new HuffTree(ch, cc.getTable().get(ch));
            htArray[i] = leaf;
            i++;
        }
        htArray[i] = new HuffTree(PSEUDO_EOF, 1);

        // Call the MinHeap constructor with the array of HuffTree and num and max
        // both equals to the length of the array.
        // minHeap is the PriorityQueue created

        minHeap = new MinHeap(htArray, htArray.length, htArray.length);

        buildTree();
        return tree;

    }

    private Map<Integer, String> encoding(Map<Integer, 
            String> encTable, IHuffBaseNode x, String s) {

        if (x.isLeaf()) {
            encTable.put(((HuffLeafNode) x).element(), s);
        } else {
            encoding(encTable, ((HuffInternalNode) x).left(), s + "0");
            encoding(encTable, ((HuffInternalNode) x).right(), s + "1");
        }
        return encTable;
    }

    @Override
    public Map<Integer, String> makeTable() {

        String s = "";
        encoding(encTable, tree.root(), s);

        return encTable;
    }

    @Override
    public String getCode(int i) {
        return encTable.get(i);
    }

    @Override
    public Map<Integer, Integer> showCounts() {
        return freqTable;
    }

    @Override
    public int write(String inFile, String outFile, boolean force) {

        int newbitCount = 0;
        int oldbitCount = 0;

        // Calculate the size of the uncompressed file, just count the number of ch and
        // multiply by BIT_PER_WORD
        // Calculate the size of the compressed file, first create freqTable & encTable
        // and sumproduct.
        // This method does not create a new file therefore does not take space.

        try {
            BitInputStream bitin = new BitInputStream(inFile);

            oldbitCount = cc.countAll(bitin) * BITS_PER_WORD;

            makeHuffTree(bitin);
            makeTable();

            bitin = new BitInputStream(inFile);
            int chCount = bitin.read(BITS_PER_WORD);

            while (chCount != -1) {
                newbitCount += encTable.get(chCount).length();
                chCount = bitin.read(BITS_PER_WORD);
            }

            newbitCount += this.headerSize();

            // check if the newbitCount is smaller than oldbitCount
            if (newbitCount <= oldbitCount || force) {
                // write Header
                BitOutputStream bitout = new BitOutputStream(outFile);
                this.writeHeader(bitout);

                // write actual file bit by bit, compress, and write

                bitin = new BitInputStream(inFile);
                int ch = bitin.read(BITS_PER_WORD);
                while (ch != -1) {
                    //bitout.write(Integer.parseInt(encTable.get(ch), 2));
                    String code = encTable.get(ch);
                    for (int i = 0; i < code.length(); i++) {
                        if (code.charAt(i) == '0') {
                            bitout.write(1, 0);
                        } else {
                            bitout.write(1, 1);
                        }
                    }

                    ch = bitin.read(BITS_PER_WORD);
                }
                // write an PEUSDO_EOF at the end
                //bitout.write(PSEUDO_EOF);
                String code = encTable.get(PSEUDO_EOF);
                for (int i = 0; i < code.length(); i++) {
                    if (code.charAt(i) == '0') {
                        bitout.write(1, 0);
                    } else {
                        bitout.write(1, 1);
                    }
                }
                bitout.close();
            }

            bitin.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return newbitCount;
    }

    @Override
    public int uncompress(String inFile, String outFile) {

        int fileSize = 0;
        try {
            BitInputStream bitin = new BitInputStream(inFile);
            BitOutputStream bitout = new BitOutputStream(outFile);

            HuffTree headerTree = readHeader(bitin);
            IHuffBaseNode current = headerTree.root();

            // when you read a 0, go left, when you read a 1, go right,
            // when you reach a leaf node, you would know you finished
            // read a character, so you can update your counter and write
            // the character you just read, start at your root again and keep reading
            int ch = bitin.read(1);
            while (ch != -1) {
                if (current.isLeaf()) {
                    if (((HuffLeafNode) current).element() == PSEUDO_EOF) {
                        break;
                    }
                    bitout.write(BITS_PER_WORD, ((HuffLeafNode) current).element());
                    fileSize += BITS_PER_WORD;
                    current = headerTree.root();
                }

                if ((ch & 1) == 0) {
                    current = ((HuffInternalNode) current).left();
                } else if ((ch & 1) == 1) {
                    current = ((HuffInternalNode) current).right();
                }

                ch = bitin.read(1);
            }
            //bitout.write(PSEUDO_EOF);
            bitin.close();
            bitout.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fileSize;
    }

    @Override
    public int headerSize() {

        // return the size of the HuffTree + Magic Number
        int magicNumSize = BITS_PER_INT;
        return magicNumSize + tree.size();
    }

    private int writeHeaderHelper(BitOutputStream out, IHuffBaseNode x) throws IOException {
        int count = 0;

        if (x == null) {
            return 0;
        } else if (x.isLeaf()) {
            // update count
            // output stream write leaf path and value
            out.write(1, 1);
            out.write(BITS_PER_WORD, ((HuffLeafNode) x).element());
            // count = count + 1 + ((HuffLeafNode) x).weight();
            count = count + 10;

        } else {
            out.write(1, 0);

            // count = count + writeHeaderHelper(out, ((HuffInternalNode) x).left()) +
            // writeHeaderHelper(out, ((HuffInternalNode) x).right());
            count = count + 1 + writeHeaderHelper(out, ((HuffInternalNode) x).left())
                    + writeHeaderHelper(out, ((HuffInternalNode) x).right());
        }

        return count;
    }

    @Override
    public int writeHeader(BitOutputStream out) {

        int treeSize = 0;
        int magicNumSize = BITS_PER_INT;

        out.write(BITS_PER_INT, MAGIC_NUMBER);

        try {
            treeSize += writeHeaderHelper(out, tree.root());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return magicNumSize + treeSize;

    }

    private HuffTree readHeaderHelper(BitInputStream in) throws IOException {
        IHuffBaseNode left, right;

        int ch = in.read(1);

        if ((ch & 1) == 1) {
            int leaf = in.read(BITS_PER_WORD);
            return (new HuffTree(leaf, 0));
        } else {
            left = readHeaderHelper(in).root();
            right = readHeaderHelper(in).root();
            return (new HuffTree(left, right, 0));
        }
    }

    @Override
    public HuffTree readHeader(BitInputStream in) throws IOException {
        HuffTree headerTree = null;

        int magicNumber = in.read(BITS_PER_INT);

        if (magicNumber == MAGIC_NUMBER) {
            headerTree = readHeaderHelper(in);
        } else {
            throw new IOException("Magic Number not right");
        }
        return headerTree;
    }

}
