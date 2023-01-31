import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DocumentIterator implements Iterator<String> {

    private Reader r;
    private int c = -1;
    private int n;

    public DocumentIterator(Reader r, int n) {
        this.r = r;
        this.n = n;
        skipNonLetters();
    }

    private void skipNonLetters() {
        try {
            this.c = this.r.read();
            while (!Character.isLetter(this.c) && this.c != -1) {
                this.c = this.r.read();
            }
        } catch (IOException e) {
            this.c = -1;
        }
    }

    @Override
    public boolean hasNext() {
        return (c != -1);
    }

    @Override
    public String next() {
        int numWords = 0;

        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        java.lang.String answer = "";
        java.lang.String tmpans = "";
        try {
            while (numWords < this.n && hasNext()) {
                tmpans = answer;
                while (Character.isLetter(this.c)) {
                    answer = answer + (char) this.c;
                    this.c = this.r.read();
                }

                if (numWords == 0) {
                    this.r.mark(1000);
                }
                
                if (!tmpans.equals(answer)) {
                    numWords++;
                }
                skipNonLetters();
            }
            this.r.reset();
            this.c = this.r.read();
        } catch (IOException e) {
            e.printStackTrace();
            throw new NoSuchElementException();
        }

        if (numWords < this.n) {
            answer = "";
            this.c = -1;
        }

        return (String) answer.toLowerCase();
    }

}
