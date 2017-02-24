package CorpusConverter.FileRepository;

import CorpusConverter.ArticleConverters.ArticleUtilities;

import java.util.Collection;

/**
 * Model of an Article
 */
public class Article {

    private int mId;
    private String mTitle;
    private String mArticle;
    private Collection<String> mStopWord;
    private String mTopic;

    /**
     * An Article
     * @param id The id of the article
     * @param topic the topic of the article
     * @param title The title of the article
     * @param article The Article contents
     */
    public Article(int id, String topic, String title, String article) {
        mTitle = title;
        mArticle = article;
        mId = id;
        mTopic = topic;
        mStopWord = ArticleUtilities.GetListOfTermsSansStopWords(this);
    }



    /**
     * Returns the id.
     * @return The Id.
     */
    public int getID(){
        return mId;
    }

    /**
     * Return the Topic
     * @return The Topic
     */
    public String getTopic()
    {
        return mTopic;
    }

    /**
     * Return the title
     * @return the title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Return the article
     * @return The Article
     */
    public String getArticle() {
        return mArticle;
    }

    /**
     * Return the article devoid of sub words
     * @return Article sans subwords
     */
    public Collection<String> getArticleTermsSansSubwords() {
        return mStopWord;
    }
}
