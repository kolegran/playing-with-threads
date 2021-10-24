package com.github.kolegran;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConcurrentResourcesAccess {

    private static final String FILE = "example.txt";

    public static void main(String[] args) throws IOException, InterruptedException {
        Files.deleteIfExists(Paths.get(FILE));

        final File file = new File(FILE);
        file.createNewFile();

        final String helloString = "Hello world!\n";
        final Thread hello = new Thread(() -> writeToFile(helloString, file), "hello");

        final String nameString = "My name is Kolegran.\n";
        final Thread name = new Thread(() -> {
            writeToFile(nameString, file);
        }, "name");

        name.start();
        hello.start();

        name.join();
        hello.join();

        System.out.println(Files.readString(Paths.get(FILE)));
    }

    private static synchronized void writeToFile(String content, File file) {
        System.out.println(Thread.currentThread().getName());

        char[] chars = readFileAndAppendNewContent(content).toCharArray();
        try (final FileOutputStream out = new FileOutputStream(file)) {
            for (char c : chars) {
                out.write(c);
            }
            System.out.println("TEST - " + Files.readString(Paths.get(FILE)));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static String readFileAndAppendNewContent(String content) {
        try {
            return Files.readString(Paths.get(FILE)) + content;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

}
