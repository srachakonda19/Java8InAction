import java.io.*;
import java.util.concurrent.*;
import java.util.*;
import java.nio.file.*;

public class WordSearchMultithreading {

    // ExecutorService for managing threads
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java WordSearchMultithreading <directory> <word>");
            System.exit(1);
        }

        String directoryPath = args[0];
        String wordToSearch = args[1];

        try {
            int totalOccurrences = searchWordInDirectory(directoryPath, wordToSearch);
            System.out.println("Total occurrences of '" + wordToSearch + "': " + totalOccurrences);
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

    private static int searchWordInDirectory(String directoryPath, String word) throws Exception {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        // List to hold Future objects for each file task
        List<Future<Integer>> futures = new ArrayList<>();

        // Recursively process all files and subdirectories
        Files.walk(Paths.get(directoryPath)).filter(Files::isRegularFile).forEach(filePath -> {
            futures.add(executor.submit(() -> countWordInFile(filePath.toFile(), word)));
        });

        int totalOccurrences = 0;

        // Aggregate results from all threads
        for (Future<Integer> future : futures) {
            totalOccurrences += future.get();
        }

        return totalOccurrences;
    }

    private static int countWordInFile(File file, String word) {
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                count += countOccurrences(line, word);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + file.getName());
        }

        return count;
    }

    private static int countOccurrences(String line, String word) {
        int count = 0;
        int index = 0;

        while ((index = line.indexOf(word, index)) != -1) {
            count++;
            index += word.length();
        }

        return count;
    }
}
