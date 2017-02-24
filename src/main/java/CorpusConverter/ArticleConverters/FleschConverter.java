package CorpusConverter.ArticleConverters;

import CorpusConverter.FileRepository.Article;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts an article to a FleschKincaid Readbility Index
 *
 * Formula:  206.835 - 1.015(totalWords/totalSentence) - 84.6(totalSyllables/TotalWord)
 *
 */
public class FleschConverter extends DoubleConverter {

    private static double START_CONST = 206.835;

    private static double WORD_MODIFIER = 1.015;

    private static double SYLLABLE_MODIFIER = 84.6;


    /**
     * Convert to the Flesch score
     * @param article the article to convert
     * @return The Flesch Score of the article.
     */
    public Double convert(Article article) {
        String articleContents = article.getArticle().replaceAll("[^\\s]*\\d[^\\s]*", "");
        double wordCount = ArticleUtilities.GetWordCount(articleContents);
        String[] strSplit = articleContents.split("\\s+");
        double sentenceCount = getSentenceCount(article.getArticle());
        double syllableCount = 0;
        //Arrays.stream(strSplit).forEach(s -> syllableCount += SyllableCount(s));
        for (String s : strSplit) {
            syllableCount += SyllableCount(s);
        }

        // Returns the double. The higher the number the easiest it is to read.
        return START_CONST - (WORD_MODIFIER * (wordCount / sentenceCount)) - (SYLLABLE_MODIFIER * (syllableCount / wordCount));
    }


    /**
     * Return the amount of syllables in a word
     * @param word the word to extract from
     * @return the syllable count
     */
    private int SyllableCount(String word) {
        word = word.toLowerCase();
        word.replaceAll("[^\\w]", "");
        int res = 0;

        if (word.length() == 0) {
            return 0;
        }

        if (word.charAt(word.length() - 1) == 'e') {
            Matcher m = Pattern.compile("[aeiouy]").matcher(word);
            if (m.find()) {
                word = word.substring(0, word.length() - 1);
            } else {
                return 1;
            }
        }

        Matcher matcher = Pattern.compile("[^aeiouy]*[aeiouy]+").matcher(word);
        while (matcher.find()) {
            res ++;
        }

        return res;
    }


    /**
     * Return the amount of sentences in a string
     * @param article the article to remove all the strings from
     * @return The amount of sentences
     */
    private int getSentenceCount(String article) {
        return  article.split ("[\\.\\?!]").length;
    }
}
