package CorpusConverter.ArticleConverters;

import CorpusConverter.FileRepository.Article;
import KMeans.KMeans;
import KMeans.PosVectorizer;
import org.la4j.Vector;

/**
 * The KMeans Cluster that returns a category between 0-K [0, 0, 1 ... K]
 * where 1 indicates which cluster.
 */
public class KMeansClusterConverter extends CategoricalConverter {

    /** The instance of a Kmeans Clusterer */
    private KMeans mMeans;

    /** How many Ks */
    private final int mK;

    /** The Vectorizer */
    private PosVectorizer mVectorizer;

    /**
     * Pass in the means instance
     * @param means the instance of the Kmeans.
     */
    public KMeansClusterConverter(KMeans means) {
        mMeans = means;
        mK = means.getK();
        mVectorizer = new PosVectorizer();
    }

    /**
     * Calculate an article to a sector.
     * @param article the article to convert
     * @return
     */
    @Override
    public boolean[] convert(Article article) {
        // Should produce [0, 0, 0]
        boolean[] res = new boolean[mK];
        // Convert to vector
        Vector posVector = mVectorizer.CreatePosVector(article);
        int cluster = mMeans.getCluster(posVector);
        res[cluster] = true;

        return res;
    }
}
