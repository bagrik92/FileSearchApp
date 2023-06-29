package org.example;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileSearchApp {


    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar myTest.jar <file name> <directory>");

        }

        String fileName = args[0];
        String directoryPath = args[1];

        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            System.out.println("Invalid directory path");

        }

        List<File> foundFiles = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        searchFiles(fileName, directory, foundFiles, executorService);

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (foundFiles.isEmpty()) {
            System.out.println("No files found with the given name in the specified directory");
        } else {
            System.out.println("Found files:");
            for (File file : foundFiles) {
                System.out.println(file.getAbsolutePath());
            }
        }
    }

    private static void searchFiles(String fileName, File directory, List<File> foundFiles, ExecutorService executorService) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    executorService.execute(() -> searchFiles(fileName, file, foundFiles, executorService));
                } else if (file.getName().matches(fileName)) {
                    synchronized (foundFiles) {
                        foundFiles.add(file);
                    }
                }
            }
        }
    }
}

