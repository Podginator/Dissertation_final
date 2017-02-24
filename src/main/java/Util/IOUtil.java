package Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Simple IO stuff. We don't currently care about these exceptions, but in future we should
 * Do something with them other than just print stack traces.
 */
public class IOUtil {

    /**
     * Write to File wrapper
     * @param filepath the file to write to
     * @param write stuff to the write.
     */
    public static void WriteToFile(String filepath, String write) {
        try {
            Files.write(Paths.get(filepath), write.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Create And Write To File
     * @param filepath filepath to write to
     * @param write the stuff to write.
     */
    public static void CreateAndWriteToFile(String filepath, String write) {
        try {
            Files.write(Paths.get(filepath), write.getBytes(), StandardOpenOption.CREATE_NEW);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Create a file
     * @param filepath the file to write to
     */
    public static void CreateFile(String filepath) {
        try {
            Files.write(Paths.get(filepath), "".getBytes(), StandardOpenOption.CREATE_NEW);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
