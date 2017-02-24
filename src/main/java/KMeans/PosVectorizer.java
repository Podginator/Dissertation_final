package KMeans;


import CorpusConverter.ArticleConverters.ArticleUtilities;
import CorpusConverter.FileRepository.Article;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Create Normalized vectors from Parts-Of-Speech
 *
 * These will form the basis of our K-Means Cluster.
 */
public class PosVectorizer {

    public static final int TOTAL_POS_TAGS = 23;

    // Ctor. Stuff. Iunno.
    public PosVectorizer() {

    }

    /**
     * Creates an empty map
     *
     * @return
     */
    private Map<String, Integer> getEmptyMap() {
        Map<String, Integer> instances = new HashMap<>();
        instances.put("CC", 0);
        instances.put("CD", 0);
        instances.put("DT", 0);
        instances.put("EX", 0);
        instances.put("FW", 0);
        instances.put("IN", 0);
        instances.put("ADJ", 0);
        instances.put("LS", 0);
        instances.put("MD", 0);
        instances.put("NOU", 0);
        instances.put("PDT", 0);
        instances.put("PR", 0);
        instances.put("POS", 0);
        instances.put("ADV", 0);
        instances.put("RP", 0);
        instances.put("SYM", 0);
        instances.put("TO", 0);
        instances.put("UH", 0);
        instances.put("VER", 0);
        instances.put("WDT", 0);
        instances.put("WP", 0);
        instances.put("WP$", 0);
        instances.put("WRB", 0);

        return instances;
    }

    /**
     * Returns a vector of the POS-Information from an article.
     */
    public Vector CreatePosVector(Article article) {
        // Remove punctuation from file.
        String fixed = article.getArticle().replaceAll("[^a-zA-Z ]", "");
        List<String> posTags = new ArrayList<>();
        Map<String, Integer> instances = getEmptyMap();

        // Iterate through and lemmatize all terms
        Annotation document = new Annotation(fixed);
        ArticleUtilities.NLP_POS.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                posTags.add(token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
            }
        }

        for (String posTag : posTags) {
            if (posTag.startsWith("NN")) {
                instances.put("NOU", instances.get("NOU") + 1);
            } else if (posTag.startsWith("JJ")) {
                instances.put("ADJ", instances.get("ADJ") + 1);
            } else if (posTag.startsWith("RB")) {
                instances.put("ADV", instances.get("ADV") + 1);
            } else if (posTag.startsWith("PR")) {
                instances.put("PR", instances.get("PR") + 1);
            } else if (instances.containsKey(posTag)) {
                instances.put(posTag, instances.get(posTag) + 1);
            }
        }

        Vector res = new BasicVector(instances.size());
        List<Integer> valueCol = instances.values().stream().collect(Collectors.toList());
        for (int i = 0; i < valueCol.size(); i++) {
            res.set(i, valueCol.get(i).doubleValue());
        }

        // This should return the normal vector?
        return res.divide(res.norm());
    }

}
