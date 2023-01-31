import java.util.Comparator;

/**
 * @author ericfouh
 */
public interface ITerm extends Comparable<ITerm> {

    /**
     * Compares the two terms in descending order by weight.
     * 
     * @return comparator Object
     */
    public static Comparator<ITerm> byReverseWeightOrder() {

        return new Comparator<ITerm>() {

            public int compare(ITerm x, ITerm y) {
                if (((Term)x).getWeight() > ((Term)y).getWeight()) {
                    return -1;
                } else if (((Term)x).getWeight() < ((Term)y).getWeight()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }


    /**
     * Compares the two terms in lexicographic order, but using only the first r
     * characters of each query.
     * 
     * @param r
     * @return comparator Object
     */
    public static Comparator<ITerm> byPrefixOrder(int r) {
        if (r < 0) {
            throw new java.lang.IllegalArgumentException();
        }

        final int x = r;
        return new Comparator<ITerm>() {
            public int compare(ITerm x, ITerm y) {
                String string1 = ((Term) x).getTerm();
                String string2 = ((Term) y).getTerm();


                //Find min length of either text input or the term
                int minlength;

                if (string1.length() < string2.length()) {
                    minlength = string1.length();
                } else {
                    minlength = string2.length();
                }


                //Compare the substring of the larger string with the smaller string
                //Returns -1 if the two strings lengths are equal, returns 1 else wise
                if (minlength >= r) {
                    return string1.substring(0, r).compareToIgnoreCase(string2.substring(0, r));
                }  else if (string1.substring(0, minlength).compareToIgnoreCase(
                        string2.substring(0, minlength)) == 0) {
                    if (string1.length() == minlength) {
                        return -1;
                    } else {
                        return 1;
                    }
                }  else {
                    return string1.substring(0, minlength).compareToIgnoreCase(
                            string2.substring(0, minlength));
                }
            }
        };

    }

    // Compares the two terms in lexicographic order by query.
    public int compareTo(ITerm that);


    // Returns a string representation of this term in the following format:
    // the weight, followed by a tab, followed by the query.
    public String toString();

}
