package CorpusConverter.FileRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * File Article Repository, Created using Files
 */
public class FileArticleRepository implements Repository<Article> {

    /** The Article List */
    private List<Article> mArticleList;

    /**
     * Constructor, takes a file path and builds a
     * repository out of it.
     * @param path The Path to the files.
     */
    public FileArticleRepository(File path)
    {
        mArticleList = new ArrayList<Article>();
        populateArticleFromFile(path);
    }

    /**
     * Returns an Article by it's ID
     * @param id The Id of the Article
     * @return An ARticle
     */
    public Article GetById(int id) {
        return null;
    }

    /**
     * Returns a List of Articles
     * @return List of All the articles
     */
    public List<Article> List() {
        return mArticleList;
    }

    /**
     * Returns a list of that match the predicate
     * @param predicate The Predicate to check against
     * @return a List of articles matching that description
     */
    public List<Article> List(Predicate<Article> predicate) {
        List<Article> res = new ArrayList<>();
        for (Article article : mArticleList) {
            if (predicate.test(article)) {
                res.add(article);
            }
        }

        return res;
    }

    /**
     * The corpus currently follows a Root -> Topic -> Article File Format.
     * @param path Path to the file that contains the corpus
     */
    private void populateArticleFromFile(File path) {
        File[] directoryListing = path.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.isDirectory()){
                    for (File grandchild : child.listFiles()) {
                        if (!grandchild.isHidden()) {
                            mArticleList.add(
                                    ArticleFactory.createArticleFromFile(child.getName(),
                                            grandchild));
                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("No Files in Path");
        }
    }
}
