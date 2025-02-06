package ru.home.app;

import java.io.*;
import java.nio.file.*;
import java.util.*;
ы
public class FileFilterUtility {

    private static final String DEFAULT_OUTPUT_DIR = ".";
    private static final String DEFAULT_PREFIX = "";
    private static final String INTEGERS_FILE = "integers.txt";
    private static final String FLOATS_FILE = "floats.txt";
    private static final String STRINGS_FILE = "strings.txt";

    private static String outputDir = DEFAULT_OUTPUT_DIR;
    private static String prefix = DEFAULT_PREFIX;
    private static boolean appendMode = false;
    private static boolean shortStats = false;
    private static boolean fullStats = false;

    private static int integerCount = 0;
    private static long integerSum = 0;
    private static long integerMin = Long.MAX_VALUE;
    private static long integerMax = Long.MIN_VALUE;

    private static int floatCount = 0;
    private static double floatSum = 0;
    private static double floatMin = Double.MAX_VALUE;
    private static double floatMax = Double.MIN_VALUE;

    private static int stringCount = 0;
    private static int stringMinLength = Integer.MAX_VALUE;
    private static int stringMaxLength = 0;

    public static void main(String[] args) {
        List<String> inputFiles = new ArrayList<>();
        parseArguments(args, inputFiles);

        if (inputFiles.isEmpty()) {
            System.err.println("No input files specified.");
            return;
        }

        try {
            processFiles(inputFiles);
            printStatistics();
        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }

    private static void parseArguments(String[] args, List<String> inputFiles) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                    outputDir = args[++i];
                    break;
                case "-p":
                    prefix = args[++i];
                    break;
                case "-a":
                    appendMode = true;
                    break;
                case "-s":
                    shortStats = true;
                    break;
                case "-f":
                    fullStats = true;
                    break;
                default:
                    if (args[i].endsWith(".txt")) {
                        inputFiles.add(args[i]);
                    }
                    break;
            }
        }
    }

    private static void processFiles(List<String> inputFiles) throws IOException {
        Path integersPath = Paths.get(outputDir, prefix + INTEGERS_FILE);
        Path floatsPath = Paths.get(outputDir, prefix + FLOATS_FILE);
        Path stringsPath = Paths.get(outputDir, prefix + STRINGS_FILE);

        try (BufferedWriter integersWriter = createWriter(integersPath);
             BufferedWriter floatsWriter = createWriter(floatsPath);
             BufferedWriter stringsWriter = createWriter(stringsPath)) {

            for (String inputFile : inputFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        processLine(line, integersWriter, floatsWriter, stringsWriter);
                    }
                } catch (IOException e) {
                    System.err.println("Error reading file " + inputFile + ": " + e.getMessage());
                }
            }
        }
    }

    private static BufferedWriter createWriter(Path path) throws IOException {
        if (appendMode) {
            return Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } else {
            return Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    private static void processLine(String line, BufferedWriter integersWriter, BufferedWriter floatsWriter, BufferedWriter stringsWriter) throws IOException {
        try {
            long integerValue = Long.parseLong(line);
            integersWriter.write(line);
            integersWriter.newLine();
            updateIntegerStats(integerValue);
        } catch (NumberFormatException e1) {
            try {
                double floatValue = Double.parseDouble(line);
                floatsWriter.write(line);
                floatsWriter.newLine();
                updateFloatStats(floatValue);
            } catch (NumberFormatException e2) {
                stringsWriter.write(line);
                stringsWriter.newLine();
                updateStringStats(line);
            }
        }
    }

    private static void updateIntegerStats(long value) {
        integerCount++;
        integerSum += value;
        integerMin = Math.min(integerMin, value);
        integerMax = Math.max(integerMax, value);
    }

    private static void updateFloatStats(double value) {
        floatCount++;
        floatSum += value;
        floatMin = Math.min(floatMin, value);
        floatMax = Math.max(floatMax, value);
    }

    private static void updateStringStats(String value) {
        stringCount++;
        int length = value.length();
        stringMinLength = Math.min(stringMinLength, length);
        stringMaxLength = Math.max(stringMaxLength, length);
    }

    private static void printStatistics() {
        if (shortStats || fullStats) {
            System.out.println("Statistics:");
            if (integerCount > 0) {
                System.out.println("Integers: " + integerCount);
                if (fullStats) {
                    System.out.println("  Min: " + integerMin);
                    System.out.println("  Max: " + integerMax);
                    System.out.println("  Sum: " + integerSum);
                    System.out.println("  Avg: " + (integerSum / (double) integerCount));
                }
            }
            if (floatCount > 0) {
                System.out.println("Floats: " + floatCount);
                if (fullStats) {
                    System.out.println("  Min: " + floatMin);
                    System.out.println("  Max: " + floatMax);
                    System.out.println("  Sum: " + floatSum);
                    System.out.println("  Avg: " + (floatSum / floatCount));
                }
            }
            if (stringCount > 0) {
                System.out.println("Strings: " + stringCount);
                if (fullStats) {
                    System.out.println("  Min Length: " + stringMinLength);
                    System.out.println("  Max Length: " + stringMaxLength);
                }
            }
        }
    }
}