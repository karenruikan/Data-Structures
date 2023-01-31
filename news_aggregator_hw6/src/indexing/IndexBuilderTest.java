//package indexing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;

import java.util.*;
import java.util.Map.Entry;


public class IndexBuilderTest {

    IndexBuilder ib = new IndexBuilder();
    List<String> feeds = new ArrayList<String>();


    @Test
    public void testParseFeed() {

        feeds.add("https://www.seas.upenn.edu/~cit594/sample_rss_feed.xml");

        Map<String, List<String>> crawler = ib.parseFeed(feeds);
        // map has the correct number of files
        assertEquals(5, crawler.keySet().size());

        // map contains the names of the documents (URLs/keys)
        assertTrue(crawler.containsKey("https://www.seas.upenn.edu/~cit594/page1.html"));
        assertTrue(crawler.containsKey("https://www.seas.upenn.edu/~cit594/page4.html"));
        assertTrue(crawler.containsKey("https://www.seas.upenn.edu/~cit594/page5.html"));

        // map contains the correct number of terms in the lists (values)
        assertEquals(10, crawler.get("https://www.seas.upenn.edu/~cit594/page1.html").size()); 
        assertEquals(55, crawler.get("https://www.seas.upenn.edu/~cit594/page2.html").size());
        assertEquals(33, crawler.get("https://www.seas.upenn.edu/~cit594/page3.html").size());

    }


    @Test
    public void testBuildIndex() {

        feeds.add("https://www.seas.upenn.edu/~cit594/sample_rss_feed.xml");

        Map<String, List<String>> crawler = ib.parseFeed(feeds);
        Map<String, Map<String, Double>> forwardIndex = ib.buildIndex(crawler);

        assertEquals(5, forwardIndex.keySet().size());

        assertEquals(0.1021, forwardIndex.get("https://www.seas.upenn.edu/~cit594/page1.html").
                get("data"), 0.0001);
        assertEquals(0.183, forwardIndex.get("https://www.seas.upenn.edu/~cit594/page1.html").
                get("structures"), 0.001);
        assertEquals(0.046, forwardIndex.get("https://www.seas.upenn.edu/~cit594/page2.html").
                get("data"), 0.001);
        assertEquals(0.04877, forwardIndex.get("https://www.seas.upenn.edu/~cit594/page3.html").
                get("implement"), 0.00001);

    }

    @Test
    public void testBuildInvertedIndex() {
        feeds.add("https://www.seas.upenn.edu/~cit594/sample_rss_feed.xml");

        Map<String, List<String>> crawler = ib.parseFeed(feeds);
        Map<String, Map<String, Double>> forwardIndex = ib.buildIndex(crawler);
        
        Map<?,?> invertedIndex = ib.buildInvertedIndex(forwardIndex);
        
        //Test if all terms are captured
        assertEquals(92, invertedIndex.keySet().size());
        
        //Your map is of the correct type (of Map)
        assertTrue(invertedIndex instanceof HashMap);
        
        //Your map associates the correct files to a term
        //Your map stores the documents in the correct order
        TreeSet<Entry<String, Double>> tupleList1 = 
                (TreeSet<Entry<String, Double>>) invertedIndex.get("data");
        assertTrue(tupleList1.contains(new AbstractMap.SimpleEntry<>(
                "https://www.seas.upenn.edu/~cit594/page1.html",0.10216512475319815)));
        assertTrue(tupleList1.contains(new AbstractMap.SimpleEntry<>(
                "https://www.seas.upenn.edu/~cit594/page2.html",0.04643869306963552)));

        TreeSet<Entry<String, Double>> tupleList2 = (
                TreeSet<Entry<String, Double>>) invertedIndex.get("structures");
        assertTrue(tupleList2.contains(new AbstractMap.SimpleEntry<>(
                "https://www.seas.upenn.edu/~cit594/page1.html",0.18325814637483104)));
        assertTrue(tupleList2.contains(new AbstractMap.SimpleEntry<>(
                "https://www.seas.upenn.edu/~cit594/page2.html",0.066639325954484)));
        
        
        //inverse of a descending set 
        assertEquals(new AbstractMap.SimpleEntry<>(
                "https://www.seas.upenn.edu/~cit594/page1.html",
                0.10216512475319815), tupleList1.toArray()[0]);
        assertEquals(new AbstractMap.SimpleEntry<>(
                "https://www.seas.upenn.edu/~cit594/page2.html",
                0.04643869306963552), tupleList1.toArray()[1]);

        assertEquals(new AbstractMap.SimpleEntry<>(
                "https://www.seas.upenn.edu/~cit594/page1.html",
                0.18325814637483104), tupleList2.toArray()[0]);
        assertEquals(new AbstractMap.SimpleEntry<>(
                "https://www.seas.upenn.edu/~cit594/page2.html",
                0.066639325954484), tupleList2.toArray()[1]);

        
    }


    @Test
    public void testBuildHomePage() {
        feeds.add("https://www.seas.upenn.edu/~cit594/sample_rss_feed.xml");

        Map<String, List<String>> crawler = ib.parseFeed(feeds);
        Map<String, Map<String, Double>> forwardIndex = ib.buildIndex(crawler);
        
        Map<?,?> invertedIndex = ib.buildInvertedIndex(forwardIndex);
        
        Collection<Entry<String, List<String>>> homePage = ib.buildHomePage(invertedIndex);
        
        assertEquals(57, homePage.size());
        
        //Collection is of the correct type
        assertTrue(homePage instanceof TreeSet);
        
        
        //Test collection stores the entries are in the correct order
        Entry<String, List<String>> termData = (Entry<String, List<String>>) homePage.toArray()[0];
        List<String> linkData = new ArrayList<String>();
        linkData.add("https://www.seas.upenn.edu/~cit594/page1.html");
        linkData.add("https://www.seas.upenn.edu/~cit594/page2.html");
        linkData.add("https://www.seas.upenn.edu/~cit594/page3.html");
        assertEquals(new AbstractMap.SimpleEntry<>("data", linkData), termData);
        

      //Test collection stores the entries are in the correct order
        Entry<String, List<String>> termTree = (Entry<String, List<String>>) homePage.toArray()[1];
        List<String> linkTree = new ArrayList<String>();
        linkTree.add("https://www.seas.upenn.edu/~cit594/page3.html");
        linkTree.add("https://www.seas.upenn.edu/~cit594/page2.html");
        assertEquals(new AbstractMap.SimpleEntry<>("trees", linkTree), termTree);
        
    }

    @Test
    public void testCreateAutocompleteFile() {
        feeds.add("https://www.seas.upenn.edu/~cit594/sample_rss_feed.xml");

        Map<String, List<String>> crawler = ib.parseFeed(feeds);
        Map<String, Map<String, Double>> forwardIndex = ib.buildIndex(crawler);
        
        Map<?,?> invertedIndex = ib.buildInvertedIndex(forwardIndex);
        
        Collection<Entry<String, List<String>>> homePage = ib.buildHomePage(invertedIndex);
        
        
        Collection<?> auto = ib.createAutocompleteFile(homePage);
        
        assertTrue(auto instanceof TreeSet);
        assertEquals(57, auto.size());
        
    }

    @Test
    public void testSearchArticles() {
        feeds.add("https://www.seas.upenn.edu/~cit594/sample_rss_feed.xml");

        Map<String, List<String>> crawler = ib.parseFeed(feeds);
        Map<String, Map<String, Double>> forwardIndex = ib.buildIndex(crawler);
        
        Map<?,?> invertedIndex = ib.buildInvertedIndex(forwardIndex);
        
        Collection<Entry<String, List<String>>> homePage = ib.buildHomePage(invertedIndex);
        
        
        List<String> articles1 = ib.searchArticles("data", invertedIndex);
        assertEquals(3, articles1.size());
        assertTrue(articles1.contains("https://www.seas.upenn.edu/~cit594/page1.html"));
        assertTrue(articles1.contains("https://www.seas.upenn.edu/~cit594/page2.html"));
        assertTrue(articles1.contains("https://www.seas.upenn.edu/~cit594/page3.html"));
        
        List<String> articles2 = ib.searchArticles("structures", invertedIndex);
        assertEquals(2, articles2.size());
        assertTrue(articles2.contains("https://www.seas.upenn.edu/~cit594/page1.html"));
        assertTrue(articles2.contains("https://www.seas.upenn.edu/~cit594/page2.html"));
        
    }

}
