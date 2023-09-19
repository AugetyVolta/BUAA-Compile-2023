package utils;

import java.io.*;
import java.util.Scanner;
import java.util.StringJoiner;

public class MyIO {
    public static String readFile(String filePath) {
        InputStream stream;
        try {
            stream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Scanner reader = new Scanner(stream);
        StringJoiner stringJoiner = new StringJoiner("\n"); //通过\n进行分割
        while (reader.hasNextLine()) {
            stringJoiner.add(reader.nextLine());
        }
        reader.close();
        try {
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stringJoiner.toString().replace("\r", "");
    }

    public static void writeFile(String filePath, String content) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        writer.print(content);
        writer.close();
    }
}
