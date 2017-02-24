package CorpusConverter.FileRepository;

import java.util.List;
import java.util.function.Predicate;

/**
 * Interface that stands as a intermediate abstraction to retrieve files.
 * Implemented so that we can later use DBMS.
 */
public interface Repository<T> {

    /**
     * Retrieve a T by it's ID
     *
     * @param id
     * @return The T that corresponds with it's ID
     */
    T GetById(int id);

    /**
     * Return a List of all the T items
     * @return A List of all the T items
     */
    List<T> List();

    /**
     * Return a List based on the predicate
     * @param predicate
     * @return
     */
    List<T> List(Predicate<T> predicate);

}
