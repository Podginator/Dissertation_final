package CorpusConverter.ArticleConverters;

import CorpusConverter.FileRepository.Article;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Article Utilities class.
 */
public class ArticleUtilities {

    static Properties props = new Properties();
    public static StanfordCoreNLP NLP_POS;
    static {
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        NLP_POS = new StanfordCoreNLP(props);
    }

    /**
     * Returns the word count of an article.
     * @param article the article to retrieve the word count from
     * @return the word count.
     */
    public static int GetWordCount(String article) {
        String[] wordArray = article.split("\\s+");
        return wordArray.length;
    }

    /**
     * Returns a list of terms
     *
     * @param article The Article to get terms from.
     * @return A List of terms in the article, excluding stop terms.
     */
    public static Collection<String> GetListOfTerms(String article) {
        List<String> res =  new ArrayList<>();
        // Remove punctuation from file.
        String fixed = article.replaceAll("[^a-zA-Z ]", "");


        // Iterate through and lemmatize all terms
        Annotation document = new Annotation(fixed);
        NLP_POS.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                res.add(token.get(CoreAnnotations.LemmaAnnotation.class));
            }
        }


        return res;
    }

    /**
     * Remove the stop words from our article
     *
     * This is not particularly efficient. It does not need to be.
     * @param article  The article to remove stop words from
     * @return The article devoid of stop words
     */
    public static String RemoveStopWords(Article article) {
        List<String> stopWords = Arrays.asList(StopWords.stopwords);
        String stopString = String.join("|", stopWords);
        stopString = stopString.substring(1, stopString.length()-1);
        Pattern p = Pattern.compile("\\b(?i)(" + stopString + ")\\b\\s?");
        String fixed = article.getArticle().replaceAll("[^a-zA-Z- ]", "");
        Matcher m = p.matcher(fixed);
        String s = m.replaceAll("").replaceAll("( )+", " ").toLowerCase();

        return s;

    }

    /**
     * Return a List of Terms without stop words
     * @param article The article to extract a list of terms from
     * @return A list of terms.
     */
    public static Collection<String> GetListOfTermsSansStopWords(Article article) {
        return (GetListOfTerms(RemoveStopWords(article)));
    }
}
