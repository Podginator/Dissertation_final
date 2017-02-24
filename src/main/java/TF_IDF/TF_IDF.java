package TF_IDF;
import CorpusConverter.ArticleConverters.ArticleUtilities;
import CorpusConverter.FileRepository.Article;
import javafx.util.Pair;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Calculate the Term Frequency Inverse Document Frequency.
 */
public class TF_IDF {

    /**
     * Constructor
     * @param : Etc
     */
    public TF_IDF() {
        // The Term Frequency, and the Inverse Document Frequency.
    }

    /**
     * Calculates the frequency of a term in a set of terms
     * @param terms The terms to check (?)
     * @param term  The Term to check
     * @return The Term frequency in the list
     */
    public double TermFrequency(Collection<String> terms, String term) {
        double count = 0;
        for (String s : terms) {
            count += term.equalsIgnoreCase(s) ? 1 : 0;
        }

        return count / terms.size();
    }


    /**
     * Calculates the inverse document frequency
     * @param articles The List of terms to check against
     * @param term the term of
     * @return The inverse document frequency.
     */
    public double InverseDocumentFrequency(Collection<Article> articles, String term){
        double count = 0;
        for (Article article : articles) {
            for (String s : article.getArticleTermsSansSubwords()) {
                if (term.equalsIgnoreCase(s)) {
                    count++;
                    break;
                }
            }
        }
        return Math.log( ((double) articles.size()) / count);
    }

    /**
     * Calculate the TermFrequency-InverseDocumentFrequency.
     *
     * @param doc The document to evaluate
     * @param docs The Documents to evaluate against
     * @param term The Term to evaluate.
     * @return
     */
    public double Calculate(Article doc, Collection<Article> docs, String term) {
        return TermFrequency(doc.getArticleTermsSansSubwords(), term) * InverseDocumentFrequency(docs, term);
    }

    /**
     * Return a list of String:Score from an article
      * @param doc the doc to check the tfidf scores from
     * @param docs the list of documents to check against
     * @return All the TFIDF Scores from the article.
     */
    public LinkedHashMap<String, Double> CalculateArticlesTFIDF(Article doc, Collection<Article> docs) {
        Collection<String> termList = ArticleUtilities.GetListOfTermsSansStopWords(doc)
                .stream().collect(Collectors.toSet());
        LinkedHashMap<String, Double> articleScores = new LinkedHashMap<>();

        for (String term : termList) {
            articleScores.put(term, Calculate(doc, docs, term));
        }

        LinkedHashMap<String, Double> sv = articleScores.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new
                ));

        return sv;
    }
}
