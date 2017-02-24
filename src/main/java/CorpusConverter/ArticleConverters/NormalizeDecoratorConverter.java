package CorpusConverter.ArticleConverters;

import CorpusConverter.FileRepository.Article;

/**
 * Class to convert the output from a double converted
 * to a normalized value between 0-1
 */
public class NormalizeDecoratorConverter extends DoubleConverter {

    // A double converter to normalize the results from
    DoubleConverter mConverter;
    // The Minimum expected value
    double mMin;
    // The Maximum expected value
    double mMax;


    /**
     * Constructor
     * @param converter To normalize
     * @param min The Minimum expected value
     * @param max The Maximum expected value
     */
    public NormalizeDecoratorConverter(DoubleConverter converter, double min, double max) {
        mConverter = converter;
        mMin = min;
        mMax = max;
    }


    /**
     * Converts an article to a normalized value
     * @param article the article to convert
     * @return The Normalized Value.
     */
    public Double convert(Article article) {
        return Math.min(1, (mConverter.convert(article) - mMin) / (mMax - mMin));
    }
}
