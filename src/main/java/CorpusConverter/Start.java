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
     * The Main method
     * @param args args- filepath to the corpus
     */
    public static void main(String[] args) {
        //List<Article>  longArticles = DataSetCreator.createLongArticleDataset();
        //List<Article> techBus = DataSetCreator.createEasyTechHardBusinessDataset();

        //DataSetExporter export = new DataSetExporter("easyTechHardBusinnessTwo", techBus);
        //export.export(System.getProperty("user.dir") + "/dataset");

        MultiLayerPerceptron_ArticleClassifier classifier = new MultiLayerPerceptron_ArticleClassifier("easyTechHardBusinnessTwo");
        classifier.BeginCalculations();
    }

}
