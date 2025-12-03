import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/***********************************************
 * @file Proj4.java
 * @description This class reads SP500 data, builds
 *   sorted, shuffled, and reversed lists, and measures
 *   hash table insert, search, and delete performance
 *   for each ordering.
 * @author Alex Warren
 * @date December 3, 2025
 ***********************************************/

public class Proj4 {
    public static void main(String[] args) throws IOException {
        // Use command line arguments to specify the input file
        if (args.length != 2) {
            System.err.println("Usage: java Proj4 <input file> <number of lines>");
            System.exit(1);
        }

        String inputFileName = args[0];
        int numLines = Integer.parseInt(args[1]);

        // Read the dataset file
        FileInputStream fis = new FileInputStream(inputFileName);
        Scanner fileScanner = new Scanner(fis);

        ArrayList<SP500> data = new ArrayList<>();
        int count = 0;

        // Read line by line with the format symbol, price
        while (fileScanner.hasNextLine() && count < numLines) {
            String line = fileScanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(",");
            if (parts.length < 2) {
                continue;
            }

            String symbol = parts[0].trim();
            double price = Double.parseDouble(parts[1].trim());

            data.add(new SP500(symbol, price));
            count++;
        }
        fileScanner.close();


        // Build sorted, shuffled, and reversed lists
        ArrayList<SP500> sorted = new ArrayList<>(data);
        Collections.sort(sorted);

        ArrayList<SP500> shuffled = new ArrayList<>(sorted);
        Collections.shuffle(shuffled);

        ArrayList<SP500> reversed = new ArrayList<>(sorted);
        Collections.sort(reversed, Collections.reverseOrder());

        // Prepare CSV output file in append mode
        PrintWriter csvWriter = new PrintWriter(new FileOutputStream("analysis.txt", true));

        int N = sorted.size();
        System.out.println("Number of lines evaluated: " + N);
        System.out.println();

        // Sorted test:
        SeparateChainingHashTable<SP500> table =
                new SeparateChainingHashTable<>(2 * N + 1);

        // track start and end times for insert, search, and delete
        long startInsert = System.nanoTime();
        for (SP500 s : sorted) {
            table.insert(s);
        }
        long endInsert = System.nanoTime();

        long startSearch = System.nanoTime();
        for (SP500 s : sorted) {
            table.contains(s);
        }
        long endSearch = System.nanoTime();

        long startDelete = System.nanoTime();
        for (SP500 s : sorted) {
            table.remove(s);
        }
        long endDelete = System.nanoTime();

        // concert times to milliseconds
        double insertSortedMs = (endInsert - startInsert) / 1_000_000.0;
        double searchSortedMs = (endSearch - startSearch) / 1_000_000.0;
        double deleteSortedMs = (endDelete - startDelete) / 1_000_000.0;

        // print sorted results
        System.out.println("SORTED");
        System.out.printf("Insert: %.3f ms%n", insertSortedMs);
        System.out.printf("Search: %.3f ms%n", searchSortedMs);
        System.out.printf("Delete: %.3f ms%n%n", deleteSortedMs);

        // Write to output file
        csvWriter.printf("%d,sorted,%.3f,%.3f,%.3f%n",
                N, insertSortedMs, searchSortedMs, deleteSortedMs);

        //Shuffled test:
        table = new SeparateChainingHashTable<>(2 * N + 1);

        // track start and end times for each operation
        startInsert = System.nanoTime();
        for (SP500 s : shuffled) {
            table.insert(s);
        }
        endInsert = System.nanoTime();

        startSearch = System.nanoTime();
        for (SP500 s : shuffled) {
            table.contains(s);
        }
        endSearch = System.nanoTime();

        startDelete = System.nanoTime();
        for (SP500 s : shuffled) {
            table.remove(s);
        }
        endDelete = System.nanoTime();

        // convert each shuffled time to ms
        double insertShufMs = (endInsert - startInsert) / 1_000_000.0;
        double searchShufMs = (endSearch - startSearch) / 1_000_000.0;
        double deleteShufMs = (endDelete - startDelete) / 1_000_000.0;

        // Print results
        System.out.println("SHUFFLED");
        System.out.printf("Insert: %.3f ms%n", insertShufMs);
        System.out.printf("Search: %.3f ms%n", searchShufMs);
        System.out.printf("Delete: %.3f ms%n%n", deleteShufMs);

        // Write to output file
        csvWriter.printf("%d,shuffled,%.3f,%.3f,%.3f%n",
                N, insertShufMs, searchShufMs, deleteShufMs);

        // Reversed list:
        table = new SeparateChainingHashTable<>(2 * N + 1);

        // Track start and end times for each operation
        startInsert = System.nanoTime();
        for (SP500 s : reversed) {
            table.insert(s);
        }
        endInsert = System.nanoTime();

        startSearch = System.nanoTime();
        for (SP500 s : reversed) {
            table.contains(s);
        }
        endSearch = System.nanoTime();

        startDelete = System.nanoTime();
        for (SP500 s : reversed) {
            table.remove(s);
        }
        endDelete = System.nanoTime();

        // convert to ms
        double insertRevMs = (endInsert - startInsert) / 1_000_000.0;
        double searchRevMs = (endSearch - startSearch) / 1_000_000.0;
        double deleteRevMs = (endDelete - startDelete) / 1_000_000.0;

        // Print reversed results
        System.out.println("REVERSED");
        System.out.printf("Insert: %.3f ms%n", insertRevMs);
        System.out.printf("Search: %.3f ms%n", searchRevMs);
        System.out.printf("Delete: %.3f ms%n%n", deleteRevMs);

        // Write to output file
        csvWriter.printf("%d,reversed,%.3f,%.3f,%.3f%n",
                N, insertRevMs, searchRevMs, deleteRevMs);

        csvWriter.close();
    }
}