package CorpusConverter.ArticleConverters;

import CorpusConverter.FileRepository.Article;

import java.io.File;
import java.util.Scanner;

/**
 * The sentiment converter for the already converted values provided in the text file.
 */
public class PreCalculatedSentimentConverter extends DoubleConverter {

    /** File path to the sentiment document */
    static String PATH_TO_FILE = System.getProperty("user.dir") + "/bbc/sentiment.txt";

    /**
     * Open the file and find the article in the text file.
     *
     * @param article the article to convert
     * @return
     */
    @Override
    public Double convert(Article article) {
        File sentimentFile = new File(PATH_TO_FILE);

        // Assume the sentiment file contains the title
        // and in the format Title : Score
        // It should also be in order.
        if (sentimentFile.exists()) {
            Scanner scanner;
            try {
                scanner = new Scanner(sentimentFile);
            } catch (Exception e) {
                throw new IllegalArgumentException("I checked this. How did you get here. ");
            }
            String currentLine;

            while(scanner.hasNextLine())
            {
                currentLine = scanner.nextLine();
                if(currentLine.contains(article.getTitle()))
                {
                    int indexOf = currentLine.lastIndexOf(":");
                    String score = currentLine.substring(indexOf + 1);
                    return Double.parseDouble(score);
                }
            }
        }
        return 0.0;
    }
}
