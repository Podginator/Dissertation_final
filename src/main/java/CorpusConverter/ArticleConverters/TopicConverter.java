package CorpusConverter.ArticleConverters;

import CorpusConverter.FileRepository.Article;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Creates a category output for the different topics present in the corpus
 * business, entertainment, politics, sport, tech.
 */
public class TopicConverter extends CategoricalConverter {

    private static final List<String> TOPIC_SET;

    /**
     * Instantiate the Topic Set.
     */
    static {
        TOPIC_SET = new ArrayList<>();
        TOPIC_SET.add("business");
        TOPIC_SET.add("entertainment");
        TOPIC_SET.add("politics");
        TOPIC_SET.add("sport");
        TOPIC_SET.add("tech");
    }


    @Override
    public boolean[] convert(Article article) {
        boolean[] categoryTopics = new boolean[5];

        String articleTopic = article.getTopic();
        int index = TOPIC_SET.indexOf(articleTopic);

        if (index > -1) {
            categoryTopics[index] = true;
        }


        return categoryTopics;
    }
}
