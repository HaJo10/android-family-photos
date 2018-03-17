package com.shellmonger.apps.familyphotos.repositories;

import com.shellmonger.apps.familyphotos.models.BaseModel;

import java.util.List;
import java.util.Observable;

/**
 * Definition of the Repository
 *
 * @param <T> The type of data that the repository holds
 */
public abstract class Repository<T extends BaseModel> extends Observable {
    /**
     * Save an individual record
     * @param record the record to save
     * @return the updated record
     * @throws RepositoryException if the data cannot be saved.
     */
    public T saveItem(T record) throws RepositoryException {
        throw new RepositoryException("saveItem is not implemented");
    }

    /**
     * Removes an individual record
     * @param record the record to remove
     * @throws RepositoryException if the data cannot be removed.
     * @throws ItemMissingException if the data is not present.
     */
    public void removeItem(T record) throws RepositoryException {
        throw new RepositoryException("removeItem is not implemented");
    }

    /**
     * Retrieves an individual record based on the string ID
     * @param id the ID of the item to retrieve
     * @return the retrieved record
     * @throws RepositoryException if the data cannot be retrieved.
     * @throws ItemMissingException is the data is not present.
     */
    public T getItem(String id) throws RepositoryException {
        throw new RepositoryException("getItem(id) is not implemented");
    }

    /**
     * Retrieves an individual record based on the position in the list.
     * The position is ordered by the created date.
     * @param position the position of the item
     * @return the retrieved record
     * @throws RepositoryException if the data cannot be retrieved.
     * @throws ItemMissingException if the data is not present.
     */
    public T getItem(int position) throws RepositoryException {
        throw new RepositoryException("getItem(position) is not implemented");
    }

    /**
     * Obtain a list of items
     * @return all items in the list
     * @throws RepositoryException if the data cannot be retrieved.
     */
    public List<T> getItems() throws RepositoryException {
        throw new RepositoryException("getItems() is not implemented");
    }

    /**
     * Obtain a list of items, with paging support.  If no more data is
     * available (start > length), then the return will be an empty list.
     * @param start the start index of the items to return
     * @param count the number of items to return
     * @return the requested items.
     * @throws RepositoryException if the data cannot be retrieved.
     */
    public List<T> getItems(int start, int count) throws RepositoryException {
        throw new RepositoryException("getItems(start, count) is not implemented");
    }

    /**
     * Returns the number of items in the repository.
     * @return the number of items in the repository.
     * @throws RepositoryException if the data cannot be retrieved.
     */
    public int getLength() throws RepositoryException {
        throw new RepositoryException("getLength() is not implemented");
    }
}
