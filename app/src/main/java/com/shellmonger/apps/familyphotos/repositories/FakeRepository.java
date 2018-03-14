package com.shellmonger.apps.familyphotos.repositories;

import android.content.Context;

import com.shellmonger.apps.familyphotos.models.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * An in-memory repository.  The data goes away when the app is restarted
 *
 * @param <T> the type that the repository holds.
 */
public class FakeRepository<T extends BaseModel> implements IRepository<T> {
    private List<T> items = new ArrayList<>();

    public FakeRepository(Context context) {
        /* Do nothing */
    }

    /**
     * Save an individual record
     * @param record the record to save
     * @return the updated record
     * @throws RepositoryException if the data cannot be saved.
     */
    @Override
    public T saveItem(T record) throws RepositoryException {
        /* Update the record so that the updated value is set */
        record.setUpdated(System.currentTimeMillis());

        int index = items.indexOf(record);
        if (index == -1) {
            items.add(record);
        } else {
            items.set(index, record);
        }
        return record;
    }

    /**
     * Removes an individual record
     * @param record the record to remove
     * @throws RepositoryException if the data cannot be removed.
     * @throws ItemMissingException if the data is not present.
     */
    @Override
    public void removeItem(T record) throws RepositoryException {
        if (!items.remove(record)) {
            throw new ItemMissingException(String.format("Item %s does not exist", record.getId()));
        }
    }

    /**
     * Retrieves an individual record based on the string ID
     * @param id the ID of the item to retrieve
     * @return the retrieved record
     * @throws RepositoryException if the data cannot be retrieved.
     * @throws ItemMissingException is the data is not present.
     */
    @Override
    public T getItem(String id) throws RepositoryException {
        for (T item : items) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        throw new ItemMissingException(String.format("Item %s does not exist", id));
    }

    /**
     * Obtain a list of items
     * @return all items in the list
     * @throws RepositoryException if the data cannot be retrieved.
     */
    @Override
    public List<T> getItems() throws RepositoryException {
        return items;
    }

    /**
     * Obtain a list of items, with paging support.  If no more data is
     * available (start > length), then the return will be an empty list.
     * @param start the start index of the items to return
     * @param count the number of items to return
     * @return the requested items.
     * @throws RepositoryException if the data cannot be retrieved.
     */
    @Override
    public List<T> getItems(int start, int count) throws RepositoryException {
        if (start > items.size()) {
            return new ArrayList<>();
        }
        int endIndex = (start + count > items.size()) ? items.size() : start + count;
        return items.subList(start, endIndex);
    }

    /**
     * Returns the number of items in the repository.
     * @return the number of items in the repository.
     * @throws RepositoryException if the data cannot be retrieved.
     */
    @Override
    public int getLength() throws RepositoryException {
        return items.size();
    }
}
