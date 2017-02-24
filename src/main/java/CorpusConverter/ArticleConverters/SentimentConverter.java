package CorpusConverter.ArticleConverters;

import CorpusConverter.FileRepository.Article;
import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentSentiment;

import java.util.HashMap;
import java.util.Map;

/**
 * Uses IBM Watson Sentiment Analysis tool.
 * I have a 6 month trial to it.
 * After that, this code will be functionally useless. Fun!
 *
 */
public class SentimentConverter extends DoubleConverter {


    /**
     * Returns a sentiment analysis score
     * @param article the article to evaluatex
     * @return sentiment analysis pre-normalized between 0-1
     */
    public Double convert(Article article) {
        AlchemyLanguage service = new AlchemyLanguage();
        service.setApiKey("31d0b8662a6d25453dcaaf45ff9296e5bf3ec173");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AlchemyLanguage.TEXT, article.getArticle());
        DocumentSentiment sentiment = service.getSentiment(params).execute();

        double score = sentiment.getSentiment().getScore() != null ? sentiment.getSentiment().getScore() : 0;

        System.out.println(article.getTitle() + " : " + score + sentiment.toString());
        System.out.println(article.getArticle());
        return score;
    }
}
