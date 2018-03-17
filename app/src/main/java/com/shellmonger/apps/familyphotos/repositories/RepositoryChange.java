package com.shellmonger.apps.familyphotos.repositories;

/**
 * Model for the repository changes that happen.
 */
public class RepositoryChange {
    private RepositoryOperation operation;
    private String id;
    private int position;

    /**
     * Create a new repository change operation.
     * @param operation the operation type
     * @param id the ID of the record
     * @param position the position of the record in the list
     */
    public RepositoryChange(RepositoryOperation operation, String id, int position){
        this.operation = operation;
        this.id = id;
        this.position = position;
    }

    /**
     * @return the operation type
     */
    public RepositoryOperation getOperation() {
        return operation;
    }

    /**
     * @return the ID of the record
     */
    public String getId() {
        return id;
    }

    /**
     * @return the position in the list
     */
    public int getPosition() {
        return position;
    }
}
