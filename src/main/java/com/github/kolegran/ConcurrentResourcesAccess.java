package com.github.kolegran;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ConcurrentResourcesAccess {

    private static final String FILE = "example.txt";

    public static void main(String[] args) throws IOException, InterruptedException {
        Files.deleteIfExists(Paths.get(FILE));

        final String helloString = "Hello world!\n";
        final Thread hello = new Thread(() -> writeToFile(helloString), "hello");

        final String nameString = "My name is Kolegran.\n";
        final Thread name = new Thread(() -> writeToFile(nameString), "name");

        hello.start();
        name.start();

        name.join();
        hello.join();
        // problem is with this implementation there is no guarantee of order in which lines will be written to the file

        System.out.println(Files.readString(Paths.get(FILE)));
    }

    private static void writeToFile(String content) {
        try {
            Files.writeString(Paths.get(FILE), content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

}
