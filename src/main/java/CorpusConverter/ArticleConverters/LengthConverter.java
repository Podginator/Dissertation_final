package CorpusConverter.ArticleConverters;

import CorpusConverter.FileRepository.Article;

/**
 * Convert a Document to its word length.
 */
public class LengthConverter extends DoubleConverter{

    /**
     * Returns the length of the article.
     * @param article the article to convert
     * @return The Length of the article
     */
    public Double convert(Article article) {
        return (double) ArticleUtilities.GetWordCount(article.getArticle());
    }
}
