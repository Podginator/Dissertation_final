package CorpusConverter.DatasetCreators;

import CorpusConverter.ArticleConverters.*;
import CorpusConverter.FileRepository.Article;
import CorpusConverter.FileRepository.FileArticleRepository;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class in charge of creating datasets based on predicates
 */
public class DataSetCreator {

    /** The Repository to get from */
    private static FileArticleRepository mRepository = new FileArticleRepository(
                                                            new File(System.getProperty("user.dir") + "/bbc"));


    // Person who likes Longer Articles.
    public static List<Article> createLongArticleDataset()
    {
        DoubleConverter lengthConvert = new LengthConverter();
        return mRepository.List(e -> { return lengthConvert.convert(e) > 500; });
    }

    /**
     * Return the negative dataset based on the data passed in.
      * @param dataset
     * @return
     */
    public static List<Article> retrieveInverseDataset(List<Article> dataset) {
        return mRepository.List().stream()
                .filter(e -> { return !dataset.contains(e); })
                .collect(Collectors.toList());
    }

    public static List<Article> getAllArticles(){
        return mRepository.List();
    }

    // Person who likes Shorter Articles.
    public static List<Article> createShortArticleDataset()
    {
        DoubleConverter lengthConvert = new LengthConverter();
        return mRepository.List(e -> { return lengthConvert.convert(e) < 400; });
    }


    // Person who likes positive articles
    public static List<Article> createPositiveArticleDataset()
    {
        DoubleConverter sentimentConverter = new PreCalculatedSentimentConverter();
        return mRepository.List(e -> { return sentimentConverter.convert(e) > 0.2; });
    }


    public static List<Article> createNegativeArticleData() {
        DoubleConverter sentimentConverter = new PreCalculatedSentimentConverter();
        return mRepository.List(e -> { return sentimentConverter.convert(e) < -0.2; });
    }

    // Person Who likes Positive Article && Longer Articles
    public static List<Article> createShorterPositiveArticleDataset()
    {
        DoubleConverter lengthConvert = new LengthConverter();
        DoubleConverter sentimentConverter = new PreCalculatedSentimentConverter();
        return mRepository.List(e -> { return lengthConvert.convert(e) < 600 && sentimentConverter.convert(e) > 0.2; });
    }

    public static List<Article> createNegativeTech()
    {
        DoubleConverter sentimentConverter = new PreCalculatedSentimentConverter();
        return mRepository.List(e -> { return e.getTopic().equals("tech") && sentimentConverter.convert(e) > 0.2; });
    }

    // Person who likes difficult articles
    // 55 or lower is around "12th grade" or 18-college level.
    public static List<Article> createDifficultDataset()
    {
        DoubleConverter fleschConverter = new FleschConverter();
        return mRepository.List(e -> { return fleschConverter.convert(e) < 55.0; });
    }

    // Person who likes easy articles.
    // The higher the Flesch Reading Ease score correlates with the ease of the article.
    // 60 or above should correlate with "easy for a 13-15 year old to read"
    public static List<Article> createEasyDataset()
    {
        DoubleConverter fleschConverter = new FleschConverter();
        return mRepository.List(e -> { return fleschConverter.convert(e) > 60.0; });
    }

    public static List<Article> createEasyTechHardBusinessDataset() {
        DoubleConverter fleschConverter = new FleschConverter();

        return mRepository.List(e -> {
            return (fleschConverter.convert(e) > 60.0 && e.getTopic().equals("tech")) ||
                    (fleschConverter.convert(e) < 55.0 && e.getTopic().equals("business"));

        });


    }

    //TODO - CREATE DATASETS AND CALCULATOR.
    // Person who likes similar articles (Qualitive)
    public static List<Article> createSimilarArticleDataset()
    {
        DoubleConverter lengthConvert = new LengthConverter();
        return mRepository.List(e -> { return lengthConvert.convert(e) > 700; });
    }


    // Person who likes articles about Technology.
    public static List<Article> createTechnologyArticleDataset()
    {
        return mRepository.List(e -> { return e.getTopic().equals("tech"); });
    }

}
