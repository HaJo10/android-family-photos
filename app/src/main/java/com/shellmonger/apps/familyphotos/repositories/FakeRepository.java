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
public class FakeRepository<T extends BaseModel> extends Repository<T> {
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
        record.setUpdated(System.currentTimeMillis());
        int index = items.indexOf(record);
        if (index == -1) {
            items.add(record);
            index = items.indexOf(record);
            notifyObservers(new RepositoryChange(RepositoryOperation.ADD, record.getId(), index));
        } else {
            items.set(index, record);
            notifyObservers(new RepositoryChange(RepositoryOperation.CHANGE, record.getId(), index));
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
        int index = items.indexOf(record);
        if (index != -1) {
            items.remove(record);
            notifyObservers(new RepositoryChange(RepositoryOperation.REMOVE, record.getId(), index));
        } else {
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
     * Retrieves an individual record based on the position in the list.
     * The position is ordered by the created date.
     * @param position the position of the item
     * @return the retrieved record
     * @throws RepositoryException if the data cannot be retrieved.
     * @throws ItemMissingException if the data is not present.
     */
    @Override
    public T getItem(int position) throws RepositoryException {
        if (position < 0) {
            throw new RepositoryException("Invalid position");
        } else if (position > items.size()) {
            throw new ItemMissingException(String.format("No data at position %d", position));
        } else {
            return items.get(position);
        }
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
