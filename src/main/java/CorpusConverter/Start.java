package CorpusConverter;

import CorpusConverter.DatasetCreators.DataSetCreator;
import CorpusConverter.DatasetCreators.DataSetExporter;
import CorpusConverter.FileRepository.Article;
import MLP.MultiLayerPerceptron_ArticleClassifier;

import java.util.List;


/**
 * The starting point for the Program.
 * Includes the Main Program loop
 */
public class Start {

    /**
     * The Main method . Intended to just create all the necessary data sets
     * To test against.
     * @param args args- filepath to the corpus
     */
    public static void main(String[] args) {

        // Length Article DataSet
        List<Article> articles = DataSetCreator.createLongArticleDataset();
        DataSetExporter export = new DataSetExporter("longArticle", articles);
        export.export(System.getProperty("user.dir") + "/dataset");

//        articles = DataSetCreator.createShortArticleDataset();
//        export = new DataSetExporter("shortArticle", articles);
//        export.export(System.getProperty("user.dir") + "/dataset");
//
//        //Sentiment Dataset
//        articles = DataSetCreator.createPositiveArticleDataset();
//        export = new DataSetExporter("posArticles", articles);
//        export.export(System.getProperty("user.dir") + "/dataset");
//
//        articles = DataSetCreator.createNegativeArticleData();
//        export = new DataSetExporter("negArticles", articles);
//        export.export(System.getProperty("user.dir") + "/dataset");
//
//        //mix
//        articles = DataSetCreator.createShorterPositiveArticleDataset();
//        export = new DataSetExporter("shorterPos", articles);
//        export.export(System.getProperty("user.dir") + "/dataset");
//
//        // Mix (Tech, negative)
//        articles = DataSetCreator.createNegativeTech();
//        export = new DataSetExporter("negTech", articles);
//        export.export(System.getProperty("user.dir") + "/dataset");
//
//        // Mix (Tech, Sentiment, Business, Sentiment)
//        articles = DataSetCreator.createEasyTechHardBusinessDataset();
//        export = new DataSetExporter("easyTechHardBus", articles);
//        export.export(System.getProperty("user.dir") + "/dataset");
//
//        // Mix (Tech, Sentiment, Business, Sentiment)
//        articles = DataSetCreator.createEasyTechSportHardBusinessPolitics();
//        export = new DataSetExporter("easyTechSportHardBusPol", articles);
//        export.export(System.getProperty("user.dir") + "/dataset");
    }

}
