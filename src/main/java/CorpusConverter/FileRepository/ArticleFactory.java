package CorpusConverter.FileRepository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

/**
 * Create an Article From a File
 */
public class ArticleFactory {

    static Integer counter = 0;

    public static Article createArticleFromFile(String topic, File file) {
        Article article = new Article(counter++, topic, extractTitle(file), extractArticle(file));
        return article;
    }

    /**
     * Extracts the Title from the text
     *
     * @param file the file to extract the
     * @return
     */
    public static String extractTitle(File file) {

        String title = null;
        try {
            title =  Files.readAllLines(file.toPath(), Charset.defaultCharset()).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return title;
    }

    /**
     * Returns the Article from the text
     * @param file the file to extract the article from
     * @return The Article String.
     */
    public static String extractArticle(File file) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int stringIndex = 0;
        lines = lines.subList(1, lines.size());

        // Concatenate everything after the first two indicies.
        String string =  lines.stream().map(e -> e.toString()).reduce("", String::concat);
        // Replace any occurence of end of sentence.Start of sentence with end of sentence. Start of sentence.
        string = string.replaceAll("\\.", ". ");
        string = string.replaceAll("\\?", "? ");
        string = string.replaceAll("!", "! ");
        string = string.replaceAll("/", " ");


        return string;
    }

}
