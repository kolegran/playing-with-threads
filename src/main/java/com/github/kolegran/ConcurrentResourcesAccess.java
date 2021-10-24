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

        final String helloString = "Hello world!\n";
        final Thread hello = new Thread(() -> writeToFile(helloString, file), "hello");

        final String nameString = "My name is Kolegran.\n";
        final Thread name = new Thread(() -> writeToFile(nameString, file), "name");

        name.start();
        hello.start();

        name.join();
        hello.join();

        System.out.println(Files.readString(Paths.get(FILE)));
    }

    private static void writeToFile(String content, File file) {
        char[] chars = content.toCharArray();
        try (final FileOutputStream out = new FileOutputStream(file)) {
            for (char c : chars) {
                out.write(c);
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

}
