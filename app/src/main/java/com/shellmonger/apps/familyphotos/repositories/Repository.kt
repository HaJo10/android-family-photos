package com.shellmonger.apps.familyphotos.repositories

interface Repository<T> {
    /**
     * Get a record of the repository based on its ID
     *
     * @param id the ID to get
     * @returns the record or null
     */
    fun get(id: String): T

    /**
     * Get a record of the repository based on its index
     *
     * @param index the index of the record to get
     * @returns the record or null
     */
    fun get(index: Int): T

    /**
     * Returns the current size of the repository
     */
    fun size(): Int

    /**
     * Add a record to the repository
     *
     * @param record the record to add
     * @returns true if successfully added
     */
    fun add(record: T): Boolean

    /**
     * Delete a record from the repository
     *
     * @param record the record to remove
     * @returns true if successfully removed
     */
    fun remove(record: T): Boolean

    /**
     * Remove a record from the repository based on its ID
     *
     * @param id the ID of the record to remove
     * @returns true if successfully removed
     */
    fun remove(id: String): Boolean

    /**
     * Update a record in the repository
     *
     * @param record the record to save
     * @returns true if successfully saved
     */
    fun update(record: T): Boolean
}