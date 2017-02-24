package CorpusConverter.ArticleConverters;

import CorpusConverter.FileRepository.Article;
import TF_IDF.TF_IDF;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Used to calculate the similarity between two words
 */
public class TFIDFSimilarityConverter extends DoubleConverter {


    /** Word2Vec instance with Google news Documents */
    static Word2Vec WORD_TO_VEC =  WordVectorSerializer.readWord2VecModel(
            System.getProperty("user.dir") + "/GoogleNews-vectors-negative300.bin");


    /** The Positive Article List */
    private Set<Article> mPositiveList;

    /** The TFIDF Calculator */
    private TF_IDF mTF_IDFCalc;


    private LinkedHashMap<String, LinkedHashMap<String, Double>> mEachScore;

    static String PATH_TO_FILE = System.getProperty("user.dir") + "/tfidf.txt";



    /**
     * Constructor, takes the positive list to check against
     * @param posCollection the collection listed as "Positive"
     */
    public TFIDFSimilarityConverter(Collection<Article> posCollection)  {
        mPositiveList = posCollection.stream().collect(Collectors.toSet());
        mTF_IDFCalc = new TF_IDF();

    }

    /**
     * Iterates through the TFIDF list, and compares the Word2Vec Cosine Average of the first three.
     *
     * @param article the article to convert
     * @return The Cosine similarity between the first three.
     */
    @Override
    public Double convert(Article article) {
        double res = 0;
        LinkedHashMap<String, Double> scores = mTF_IDFCalc.CalculateArticlesTFIDF(article, mPositiveList);
        Collection<String[]> posArticleWordList = extractTermsFromArticle(mPositiveList);

        int counter = 0;
        ListIterator<String> it = new ArrayList<String>(scores.keySet()).listIterator(scores.size());

        String wordToCompare = null;

        while (it.hasPrevious()) {
            if (WORD_TO_VEC.hasWord(it.previous())) {
                wordToCompare = it.previous();
                break;
            }
        }

        for (String[] strings : posArticleWordList) {
            int index = 0;
            while (!WORD_TO_VEC.hasWord(strings[index])) index++;
            res += WORD_TO_VEC.similarity(wordToCompare, strings[index]);

        }

        return (res / posArticleWordList.size());
    }

    /**
     * Return a set of strings from an article.
     * @return The article
     */
    private Collection<String[]> extractTermsFromArticle(Collection<Article> col) {

        File tfidfFile = new File(PATH_TO_FILE);
        Set<String[]> res = new HashSet<>();

        // Assume the sentiment file contains the title
        // and in the format Title : Score
        // It should also be in order.
        if (tfidfFile.exists()) {
            for (Article a : col) {
                Scanner scanner;
                try {
                    scanner = new Scanner(tfidfFile);
                } catch (Exception e) {
                    throw new IllegalArgumentException("I checked this. How did you get here. ");
                }
                String currentLine;

                while(scanner.hasNextLine())
                {
                    currentLine = scanner.nextLine();
                    if(currentLine.contains(a.getTitle()))
                    {
                        currentLine = currentLine.substring(a.getTitle().length() + 2);
                        res.add(currentLine.replaceAll("\\s","").split(","));
                    }
                }
            }
        }

        return res;
    }
}
