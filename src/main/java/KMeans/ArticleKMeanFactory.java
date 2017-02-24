package KMeans;

import CorpusConverter.FileRepository.Article;
import org.la4j.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * From a list of articles create a KMeans Instance.
 */
public class ArticleKMeanFactory {


    /**
     * Return a Kmeans instance of Part Of Speech Tags
     * @param k The amount of K in the K-Cluster
     * @param iterations The amount of iterations to resolve the Clusters
     * @param articles the Articles to check
     * @return The K Means Instance.
     */
    public static KMeans CreateKMeans(int k, int iterations, List<Article> articles) {
        List<Vector> partOfSpeechVectors = new ArrayList<>();
        PosVectorizer vectorizer = new PosVectorizer();

        for (Article article : articles) {
            partOfSpeechVectors.add(vectorizer.CreatePosVector(article));
        }

        KMeans res = new KMeans(k, iterations, PosVectorizer.TOTAL_POS_TAGS, partOfSpeechVectors);
        return res;
    }

}
