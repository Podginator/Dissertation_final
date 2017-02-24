package CorpusConverter.ArticleConverters;

import CorpusConverter.FileRepository.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of Article Converters, returns a double array
 *
 */
public class MultiArticleConverter {

    public List<Converter<?>> mCalculators;

    /**
     * Constructor
     * @param converters VarArgs of Converters
     */
    public MultiArticleConverter(Converter<?>... converters) {
        mCalculators = new ArrayList<Converter<?>>();

        // Add all the calculators to the list
        for (Converter converter : converters) {
            if (converter != null) {
                mCalculators.add(converter);
            }
        }

    }

    /**
     * Add an individual calculator
     * @param converter the converter to add
     */
    public void addCalculator(DoubleConverter converter) {
        mCalculators.add(converter);
    }

    /**
     * Calculate The Articles.
     * @param article The Article To calculate the results for.
     * @return the results of each calculation
     */
    public String calculateArticle(Article article) {
        String res = "" ;

        for (int i = 0; i < mCalculators.size(); i++) {
            res += mCalculators.get(i).convertToString(article) + ",";
        }

        return res;
    }
}
