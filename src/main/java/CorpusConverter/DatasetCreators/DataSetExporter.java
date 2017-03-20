package CorpusConverter.DatasetCreators;

import CorpusConverter.ArticleConverters.*;
import CorpusConverter.FileRepository.Article;
import KMeans.ArticleKMeanFactory;
import KMeans.KMeans;
import TF_IDF.TFIDF_Exporter;
import Util.IOUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Export data to a csv file.
 *
 * What I want this to do:
 *
 * Create the relevant files (TF_IDF, SENTIMENT), and create a dataset from the relevant
 */
public class DataSetExporter {

    private static String EXPORT_LOCATION = System.getProperty("user.dir") + "/dataset/";

    /** The converters */
    private MultiArticleConverter mConverters;

    /** The name of the dataset */
    private String mNameOfDataset;

    /** The Article List of what we want to export */
    private List<Article> mArticleList;

    /** The inverse list */
    private List<Article> mInverseList;

    /**
     * Constructor
     * @param articleList Pass in the articles we want to create a CSV for
     */
    public DataSetExporter(String name, List<Article> articleList) {
        mArticleList = articleList;
        mInverseList = DataSetCreator.retrieveInverseDataset(mArticleList);
        Converter<?>[] converters = getConverters();

        // Add all the new Converters.
        mConverters = new MultiArticleConverter(converters);
        mNameOfDataset = name;
    }

    /**
     * Returns the Set of Converters
     * @return The Converters
     */
    private Converter<?>[] getConverters() {
        Converter<?>[] resArr = new Converter<?>[6];

        // Articles are 0 at 100, 1.0 at 750
        resArr[0] = new NormalizeDecoratorConverter(new LengthConverter(), 100, 750);
        resArr[1] = new NormalizeDecoratorConverter(new FleschConverter(), 100, 40);
        // Normalize the Sentiment Converter to be between 0 and 1.
        resArr[2] = new NormalizeDecoratorConverter(new PreCalculatedSentimentConverter(), -1, 1);

        resArr[3] = new TFIDFSimilarityConverter(mArticleList);

        // Create a KMeans instance.
        KMeans means = ArticleKMeanFactory.CreateKMeans(3, 1000, mArticleList);
        resArr[4] = new KMeansClusterConverter(means);

        resArr[5] = new TopicConverter();

        return resArr;
    }

    /**
     * Export to a CSV file based on the parameters we have.
     *
     * @param location The location to save out to.
     */
    public void export(String location) {

        String toExtract = "";
        // Go through and
        // With the resulting array comma seperate each value and add a new line.

        // Articles that we like
        for (Article article : mArticleList) {
            toExtract += "1,";
            toExtract += mConverters.calculateArticle(article);
            // Remove the last index with a new line character.
            toExtract = toExtract.substring(0, toExtract.length()-1);
            toExtract += "\n";
        }

        // Articles that we do not like.
        for (Article article : mInverseList) {
            toExtract += "0,";
            toExtract += mConverters.calculateArticle(article);
            // Remove the last index with a new line character.
            toExtract = toExtract.substring(0, toExtract.length()-1);
            toExtract += "\n";
        }

        // Remove trailing end
        toExtract = toExtract.substring(0, toExtract.length() - 1);
        //Randomize
        String[] evalAndTest = randomizeExports(toExtract);

        //Write out to csv file.
        boolean fileCreated = new File(EXPORT_LOCATION + mNameOfDataset).mkdirs();
        IOUtil.CreateAndWriteToFile(EXPORT_LOCATION + "/" + mNameOfDataset + "/train.csv", evalAndTest[0]);
        IOUtil.CreateAndWriteToFile(EXPORT_LOCATION + "/" + mNameOfDataset + "/eval.csv", evalAndTest[1]);
    }


    /**
     * Return the export text
     * @param export the text to randomize
     * @return the Randomized text for eval and testing.
     */
    private String[] randomizeExports(String export) {
        String[] res = new String[2];
        String[] split = export.split("\n");

        for (int i = split.length; i != 0; i--) {
            int j = (int) Math.floor(Math.random() * i);
            String x = split[i - 1];
            split[i - 1] = split[j];
            split[j] = x;
        }

        int splitSize = split.length;
        int testPos = splitSize - (int)(splitSize * 0.2);
        String[] testData = Arrays.copyOfRange(split,  0, testPos);
        String[] evalData = Arrays.copyOfRange(split, testPos, splitSize);
        res[0] = String.join("\n", testData);
        res[1] = String.join("\n", evalData);

        return res;
    }

    // Create the necessary files for parsibng.
    private void createFilesIfNecessary() {
        // Go through and create the output folder and the tfidf file if necessary.
        // Check if exists.

        // Check if the Output folder exists
        boolean fileCreated = new File(EXPORT_LOCATION + mNameOfDataset).mkdirs();

        if (!fileCreated) {
            // Check whether TF-IDF Data Exists.
            File tfidf_export = new File(EXPORT_LOCATION + mNameOfDataset + mNameOfDataset + "/tfidf.txt");
            if (!tfidf_export.exists()) {
                TFIDF_Exporter exporter = new TFIDF_Exporter(mArticleList);
                // Create the dataset.
                exporter.export(EXPORT_LOCATION + mNameOfDataset + mNameOfDataset + "/tfidf.txt");
             }
        }
    }
}
