//package indexing;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IndexBuilder implements IIndexBuilder {


    @Override
    public Map<String, List<String>> parseFeed(List<String> feeds) {

        Map<String, List<String>> parsed = new HashMap<String, List<String>>();
        // for loop to iterate through all the RSS feed inputs
        for (String feed : feeds) {

            try {
                // jsoup to read the RSS link and obtain the URL within each RSS in linkText
                Document doc = Jsoup.connect(feed).get();
                Elements links = doc.getElementsByTag("link");

                for (Element link : links) {

                    String linkText = link.text();

                    // jsoup to read the HTML text "body" within linkText and store in body

                    Document docHtml = Jsoup.connect(linkText).get();
                    String body = docHtml.getElementsByTag("body").text();
                    body = body.replaceAll("\\p{Punct}", "").toLowerCase();

                    List<String> bodyText = new ArrayList<String>();
                    // remove punctuation, convert to lowercase, and parse text, store in arraylist


                    bodyText = Arrays.asList(body.split(" "));
                    // add to parsedRSSFeedsMap

                    parsed.put(linkText, bodyText);

                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return parsed;

    }

    @Override
    public Map<String, Map<String, Double>> buildIndex(Map<String, List<String>> docs) {

        Map<String, Map<String, Double>> forwardIndex = new HashMap<String, Map<String, Double>>();


        //Calculate the total number of documents, will be used for IDF calculation
        double idfN = docs.size();

        //each document
        for (Map.Entry<String, List<String>> mapEntry: docs.entrySet()) {

            String link = mapEntry.getKey();
            List<String> body = mapEntry.getValue();

            //get TF's denominator: total number of terms in the document
            double tfD = body.size();

            //initialize a map that stores the TF value of each word
            TreeMap<String, Double> tfIdfMap = new TreeMap<String, Double>();

            for (String word: body) {
                double tfN = 0; 

                Object[] wordArr = body.toArray();

                for (int i = 0; i < body.size(); i++) {
                    if (wordArr[i].toString().equals(word.toString())) {
                        tfN++;
                    }
                }

                //Calculate the tf for each word
                double tf = tfN / tfD;

                double idfD = 0;
                //get each document and check if the word is in it
                for (Map.Entry<String, List<String>> mapEntry1: docs.entrySet()) {
                    List<String> body1 = mapEntry1.getValue();
                    if (body1.contains(word)) {
                        idfD ++;
                    }
                }

                //Calculate the idf for each word
                double idf = Math.log(idfN / idfD);

                //Calculate the TF-IDF for each word
                double tfIdf = tf * idf;

                tfIdfMap.put(word, tfIdf);
            }

            forwardIndex.put(link, tfIdfMap);
        }

        return forwardIndex;
    }

    @Override
    public Map<?, ?> buildInvertedIndex(Map<String, Map<String, Double>> index) {
        Map<String,TreeSet<Entry<String, Double>>> invertedIndex = 
                new HashMap<String,TreeSet<Entry<String, Double>>>();

        for (Map.Entry<String, Map<String, Double>> ind: index.entrySet()) {

            String link = ind.getKey();
            Map<String, Double> value = ind.getValue();

            for (Map.Entry<String, Double> val: value.entrySet()) {
                String term = val.getKey();
                Double tfidf = val.getValue();

                //If the term has not beed added to the inverted Index yet

                if (!invertedIndex.containsKey(term)) {
                    Entry<String, Double> tuple = new AbstractMap.SimpleEntry<>(link,tfidf);
                    TreeSet<Entry<String, Double>> tupleList = 
                            new TreeSet<Entry<String, Double>>(compareInvertedIndex());
                    tupleList.add(tuple);
                    invertedIndex.put(term, tupleList);
                } else {
                    //If the term is already added to the inverted Index
                    TreeSet<Entry<String, Double>> tempTup = invertedIndex.get(term);
                    tempTup.add(new AbstractMap.SimpleEntry<String, Double>(link, tfidf));
                }
            }
        }
        return invertedIndex;
    }

    public static Comparator<Entry<String, Double>> compareInvertedIndex() {
        return new Comparator<Entry<String, Double>>() {
            @Override
            public int compare(Entry<String, Double> tuple1, Entry<String, Double> tuple2) {
                if (tuple1.getValue() > tuple2.getValue()) {
                    return -1;
                } else if (tuple1.getValue() < tuple2.getValue()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }

    @Override
    public Collection<Entry<String, List<String>>> buildHomePage(Map<?, ?> invertedIndex) {
        TreeSet<Entry<String, List<String>>> homePage = 
                new TreeSet<Entry<String, List<String>>>(compareHomePage());

        for (Map.Entry<?,?> ind: invertedIndex.entrySet()) {
            String term = (String) ind.getKey();
            TreeSet<Entry<String, Double>> tupleList = 
                    (TreeSet<Entry<String, Double>>) ind.getValue();

            List<String> links = new ArrayList<String>();
            for (Entry<String, Double> tuple: tupleList) {
                //if the link is not already in the List, add it
                if (!links.contains(tuple.getKey())) {
                    links.add(tuple.getKey());
                }
            }

            if (!Arrays.stream(STOPW).anyMatch(term :: equals)) {
                Entry<String, List<String>> terms = new AbstractMap.SimpleEntry<>(term,links);
                homePage.add(terms);
            }

        }

        return homePage;
    }

    public static Comparator<Entry<String, List<String>>> compareHomePage() {
        return new Comparator<Entry<String, List<String>>>() {
            @Override
            public int compare(Entry<String, List<String>> tuple1, 
                    Entry<String, List<String>> tuple2) {
                if (tuple1.getValue().size() > tuple2.getValue().size()) {
                    return -1;
                } else if (tuple1.getValue().size() < tuple2.getValue().size()) {
                    return 1;
                } else {
                    return tuple1.getKey().compareTo(tuple2.getKey()) * (-1);
                }
            }
        };
    }

    @Override
    public Collection<?> createAutocompleteFile(Collection<Entry<String, List<String>>> homepage) {
        TreeSet<String> autocomplete = new TreeSet<String>(compareAutocomplete());

        FileWriter output;
        try {
            output = new FileWriter("autocomplete.txt");
            BufferedWriter writer = new BufferedWriter(output);

            //Build the TreeSet that will be returned
            for (Entry<String, List<String>> term : homepage) {
                String word = term.getKey();
                if (!autocomplete.contains(word)) {
                    autocomplete.add(word);
                }
            }
            //Write out the autocomplete.txt file

            writer.write(Integer.toString(autocomplete.size()) + "\r\n");
            
            for (String word : autocomplete) {
                writer.write("0 " + word + "\r\n");
            }

            writer.close();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return autocomplete;
    }

    public static Comparator<String> compareAutocomplete() {
        return new Comparator<String>() {
            @Override
            public int compare(String word1, String word2) {
                return word1.compareTo(word2);
            }
        };
    }


    @Override
    public List<String> searchArticles(String queryTerm, Map<?, ?> invertedIndex) {
        List<String> articles = new ArrayList<String>();
        TreeSet<Entry<String, List<String>>> homePage = 
                (TreeSet<Entry<String, List<String>>>) buildHomePage(invertedIndex);

        for (Entry<String, List<String>> tuple: homePage)  {
            if (tuple.getKey().toString().equals(queryTerm.trim().toLowerCase())) {
                articles = tuple.getValue();
            }
        }
        return articles;
    }


}