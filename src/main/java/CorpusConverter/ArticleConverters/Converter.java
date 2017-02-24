package CorpusConverter.ArticleConverters;

import CorpusConverter.FileRepository.Article;

/**
 * Converts a set of articles to a double array.
 *
 * @author Tom Rogers
 */
public interface Converter<E> {


    /**
     * Convert an Article into a generic parameter E.
     * @param article the article to convert
     * @return E.
     */
    E convert(Article article);

    /**
     * Convert result of E to String.
     * @return
     */
    String convertToString(Article article);

}
