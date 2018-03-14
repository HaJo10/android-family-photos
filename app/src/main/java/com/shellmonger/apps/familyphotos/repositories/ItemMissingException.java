package com.shellmonger.apps.familyphotos.repositories;

/**
 * Exception thrown when a requested item is missing from the repository.
 */
public class ItemMissingException extends RepositoryException {
    public ItemMissingException(String message) {
        super(message);
    }
}
