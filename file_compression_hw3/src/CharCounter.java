import java.io.*;
import java.util.*;

public class CharCounter implements ICharCounter, IHuffConstants {

    private Map<Integer, Integer> charTable = new HashMap<Integer, Integer>();

    @Override
    public int getCount(int ch) {
        if (ch < 0) {
            throw new IllegalArgumentException();
        } else if (ch > ALPH_SIZE) {
            throw new IllegalArgumentException();
        } else if (charTable.get(ch) == null) {
            return 0;
        } else {
            return charTable.get(ch);
        }
    }

    @Override
    public int countAll(InputStream stream) throws IOException {

        int ch;
        int count = 0;

        BitInputStream bits = new BitInputStream(stream);
        ch = bits.read(BITS_PER_WORD);

        while (ch != -1) {

            add(ch);
            count++;
            ch = bits.read(BITS_PER_WORD);
        }
        return count;

    }


    @Override
    public void add(int i) {
        if (!charTable.containsKey(i)) {
            charTable.put(i, 1);
        } else {
            charTable.put(i, charTable.get(i) + 1);
        }

    }

    @Override
    public void set(int i, int value) {
        charTable.put(i, value);
    }

    @Override
    public void clear() {
        charTable.clear();

    }

    @Override
    public Map<Integer, Integer> getTable() {
        return this.charTable;
    }

}