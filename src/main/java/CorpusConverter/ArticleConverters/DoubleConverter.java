package CorpusConverter.ArticleConverters;

import CorpusConverter.FileRepository.Article;

/**
 * Created by podginator on 27/01/2017.
 */
public abstract class DoubleConverter implements Converter<Double> {

    /**
     * In the double case we simply need to return the Double as a string
     * @param article
     * @return
     */
    public String convertToString(Article article) {
        return convert(article).toString();
    }
}
