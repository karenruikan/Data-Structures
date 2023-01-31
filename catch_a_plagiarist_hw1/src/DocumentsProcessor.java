import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.io.*;

public class DocumentsProcessor implements IDocumentsProcessor {

    @Override
    public Map<String, List<String>> processDocuments(String directoryPath, int n) {

        // Creating a File object for directory
        File directory = new File(directoryPath);

        // List of all files and directories
        File[] filesList = directory.listFiles();

        Map<String, List<String>> map = new HashMap<String, List<String>>();

        for (File file : filesList) {

            if (file.isFile() && file.getName().contains(".txt")) {

                BufferedReader bReader = null;
                try {
                    bReader = new BufferedReader(new FileReader(file));
                    DocumentIterator di = new DocumentIterator(bReader, n);

                    List<String> nWordStrings = new ArrayList<String>();
                    String fileName = file.getName();

                    while (di.hasNext()) {
                        String nAnswers = di.next();

                        if (!nAnswers.isEmpty()) {
                            nWordStrings.add(nAnswers);
                        }

                    }
                    map.put(fileName, nWordStrings);

                } catch (FileNotFoundException e) {

                }

            }
        }
        return map;
    }

    @Override
    public List<Tuple<String, Integer>> storeNWordSequences(Map<String, List<String>> docs, String nwordFilePath) {

        List<Tuple<String, Integer>> fileIndex = new ArrayList<>();

        try {
            RandomAccessFile byteFile = new RandomAccessFile(nwordFilePath, "rw");

            for (String fName : docs.keySet()) {
                List<String> nWordStrings = docs.get(fName);

                int byteCount = 0;
                for (String singleString : nWordStrings) {

                    try {
                        byteFile.writeBytes(singleString + " ");
                    } catch (IOException e) {
                    }

                    byteCount = byteCount + singleString.length() + 1;
                }

                Tuple<String, Integer> tuple = new Tuple<String, Integer>(fName, byteCount);

                fileIndex.add(tuple);
            }

            byteFile.close();

        } catch (IOException e) {
        }

        return fileIndex;

    }

    @Override
    public TreeSet<Similarities> computeSimilarities(String nwordFilePath, List<Tuple<String, Integer>> fileindex) {
        TreeSet<Similarities> similaritiesTree = new TreeSet<Similarities>();
        Map<String, List<String>> tmp = new HashMap<>();

        int count = 0;
        int bytesReadCount = 0;

        try {
            FileInputStream fileIS = new FileInputStream(nwordFilePath);
            BufferedInputStream bufferIS = new BufferedInputStream(fileIS);

            // Loop over each test file
            for (int i = 0; i < fileindex.size(); i++) {

                String fileName = fileindex.get(i).getLeft();
                int numOfBytes = fileindex.get(i).getRight();

                // Read the portion of nwordFilePath that belongs to this test file
                byte[] bytes = new byte[numOfBytes];
                try {
                    bytesReadCount = bufferIS.read(bytes, 0, numOfBytes);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String bString = new String(bytes);
                String[] stringList = bString.split(" ");

                // Loop over each n-word string in newStringBuffer
                for (int j = 0; j < stringList.length; j++) {

                    if (tmp.containsKey(stringList[j])) {
                        // case1: newStringBuffer[j] appeared previously
                        List<String> listOfNames = tmp.get(stringList[j]);

                        // Loop over all previous test files that contain newStringBuffer[j]
                        for (String name : listOfNames) {
                            if (name.compareTo(fileName) == 0) {
                                continue;
                            }

                            if (similaritiesTree.contains(new Similarities(name, fileName))) {
                                // Update an existing record in the returning TreeSet

                                Similarities ceiling = similaritiesTree.ceiling(new Similarities(name, fileName));
                                int n = ceiling.getCount();
                                ceiling.setCount(n + 1);

                            } else {
                                // Add a new record into the returning TreeSet
                                Similarities sim = new Similarities(name, fileName);
                                sim.setCount(1);
                                similaritiesTree.add(sim);
                            }
                        }

                        listOfNames.add(fileName);
                        // tempCollection.get(newStringBuffer[j]).add(fileName);
                    } else {
                        // case2: newStringBuffer[j] NOT appeared previously
                        List<String> fileList = new ArrayList<String>();

                        tmp.put(stringList[j], fileList);
                        fileList.add(fileName);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return similaritiesTree;
    }

    @Override
    public void printSimilarities(TreeSet<Similarities> sims, int threshold) {
        Comparator<Similarities> newComparator = new Comparator<Similarities>() {

            @Override
            public int compare(Similarities o1, Similarities o2) {
                return o2.getCount() - o1.getCount();
            }
        };

        TreeSet<Similarities> simTree = new TreeSet<Similarities>(newComparator);
        simTree.addAll(sims);

        Similarities cutoff = new Similarities("file1", "file2");
        cutoff.setCount(threshold);

        simTree.headSet(cutoff);

        for (Similarities a : simTree) {
            System.out.println(
                    "{file1=" + a.getFile1() + ", " + "file2=" + a.getFile2() + ", " + "count=" + a.getCount() + "}");
        }

    }

    public List<Tuple<String, Integer>> processAndStore(String directoryPath, String sequenceFile, int n) {

        // Creating a File object for directory
        File directory = new File(directoryPath);

        // List of all files and directories
        File[] filesList = directory.listFiles();
        Arrays.sort(filesList);
        List<Tuple<String, Integer>> fileIndex = new ArrayList<>();

        RandomAccessFile byteFile;
        try {
            byteFile = new RandomAccessFile(sequenceFile, "rw");

            for (File file : filesList) {

                if (file.isFile() && file.getName().contains(".txt")) {

                    BufferedReader bReader = null;
                    try {
                        bReader = new BufferedReader(new FileReader(file));
                        DocumentIterator di = new DocumentIterator(bReader, n);

                        List<String> nWordStrings = new ArrayList<String>();
                        String fileName = file.getName();

                        while (di.hasNext()) {
                            String nAnswers = di.next();

                            if (!nAnswers.isEmpty()) {
                                nWordStrings.add(nAnswers);
                            }

                        }

                        int byteCount = 0;
                        for (String singleString : nWordStrings) {

                            try {
                                byteFile.writeBytes(singleString + " ");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            byteCount = byteCount + singleString.length() + 1;
                        }

                        Tuple<String, Integer> tuple = new Tuple<String, Integer>(fileName, byteCount);

                        fileIndex.add(tuple);

                    } catch (FileNotFoundException e) {

                    }
                }
            }
            try {
                byteFile.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        return fileIndex;

    }

}
