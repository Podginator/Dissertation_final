package CorpusConverter.ArticleConverters;

import CorpusConverter.FileRepository.Article;

/**
 * Convert A categorical input into an array of int
 * output expected like following
 *
 * OPTION1, OPTION2, OPTION3
 * OPTION1 = [1, 0, 0]
 * OPTION2 = [0, 1, 0]
 * OPTION3 = [0, 0, 1]
 */
public abstract class CategoricalConverter implements Converter<boolean[]> {


    /**
     * Converts to a string as follows
     * converted = [1, 0, 0]
     * res = 1, 0, 0
     *
     * @param article
     * @return String of array
     */
    @Override
    public String convertToString(Article article) {

        String res = "";
        boolean[] convert = convert(article);

        for (boolean i : convert) {
            res += (i ? "1" : "0") + ",";
        }
        res = res.substring(0, res.length() - 1);

        return res;
    }
}
