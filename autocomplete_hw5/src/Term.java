
import java.util.*;
import java.util.Comparator;

public class Term implements ITerm {

    private long weight;
    private String term;


    /*Initializes a term with the given query string and weight*/
    public Term(String term, long w) {
        //exception will be thrown if query word is null OR if weight of the word is less than 0
        if (term == null || w < 0) {
            throw new IllegalArgumentException();
        }
        this.weight = w; //Initialize weight
        this.term = term; //Initialize query
    }

    /*Compares the two terms in descending order by weight*/
    public static Comparator<Term> byReverseWeightOrder() {
        return new Comparator<Term>() {
            public int compare(Term x, Term y) {
                if (x.weight > y.weight) {
                    return -1;
                } else if (x.weight < y.weight) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }

    /*Compares the two terms in descending order but using only the first r characters
    of each query.*/
    public static Comparator<Term> byPrefixOrder(int r) {
        if (r < 0) {
            throw new java.lang.IllegalArgumentException();
        }
        return new Comparator<Term>() {
            public int compare(Term term1, Term term2) {
                String string1 = term1.term;
                String string2 = term2.term;


                //Find min length of either text input or the term
                int minlength = Math.min(string1.length(), string2.length());
                return string1.substring(0, minlength).compareToIgnoreCase(
                        string2.substring(0, minlength));
            }
        };
    }


    /*Compares the two terms in lexicographic order by query. */
    @Override
    public int compareTo(ITerm that) {
        return this.term.compareToIgnoreCase(((Term) that).getTerm());

    }

    /*Returns a string representation of this term in the following format: the 
     * weight, followed by a tab, followed by the query*/
    public String toString() {
        return Long.toString(this.weight) + "\t" + this.term;
    }


    public long getWeight() {
        return this.weight;
    }


    public String getTerm() {
        return this.term;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
