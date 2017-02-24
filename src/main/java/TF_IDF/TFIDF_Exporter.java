package TF_IDF;

import CorpusConverter.ArticleConverters.ArticleUtilities;
import CorpusConverter.FileRepository.Article;
import Util.IOUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Export the TFIDF terms to a file
 */
public class TFIDF_Exporter {

    /** The Article List */
    private List<Article> mArticleList;

    /** How many terms should we write out */
    private int mTermNumbers;

    /** TF IDF Calculator */
    private TF_IDF mTFIDFCalc;


    /**
     * Constructor that exports all terms in order of uniqueness and relevance to article.
     *
     * @param articleList The Article List
     */
    public TFIDF_Exporter(List<Article> articleList) {
        mArticleList = articleList;
        mTermNumbers = -1;
        mTFIDFCalc = new TF_IDF();
    }


    /**
     * Constructor for a finite set of numbers
     *
     * @param articleList The Article List
     * @param numTerms The Number of terms we wish to export
     */
    public TFIDF_Exporter(List<Article> articleList, int numTerms) {
        mArticleList = articleList;
        mTermNumbers = numTerms;
        mTFIDFCalc = new TF_IDF();
    }

    /**
     * Export to the file.
     * @param filePath
     */
    public void export(String filePath) {

        // Create the file
        IOUtil.CreateFile(filePath);

        // Iterate through each article and create a
        for (Article article : mArticleList) {
            Collection<String> termList = ArticleUtilities.GetListOfTermsSansStopWords(article)
                                                .stream().collect(Collectors.toSet());
            LinkedHashMap<String, Double> articleScores = new LinkedHashMap<>();

            for (String term : termList) {
                articleScores.put(term, mTFIDFCalc.Calculate(article, mArticleList, term));
            }

            LinkedHashMap<String, Double> sv = articleScores.entrySet().stream().sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new
                    ));

            // write out the file
            IOUtil.WriteToFile(filePath, article.getTitle() + " : ");
            int counter = 0;
            ListIterator<String> it = new ArrayList<String>(sv.keySet()).listIterator(sv.size());

            while (it.hasPrevious() && (mTermNumbers == -1 || counter++ < mTermNumbers)) {
                IOUtil.WriteToFile(filePath, it.previous() + ", ");
            }

            IOUtil.WriteToFile(filePath, "\n");
        }
    }
}