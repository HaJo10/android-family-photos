package com.shellmonger.apps.familyphotos.repositories

import android.content.Context
import com.shellmonger.apps.familyphotos.lifecycle.Logger
import com.shellmonger.apps.familyphotos.models.Post

class PostRepository: Repository<Post> {
    private val logger = Logger("PostRepository")

    /**
     * Singleton pattern with initialization
     */
    companion object {
        var instance: PostRepository? = null

        @Synchronized
        fun initialize(context: Context) {
            instance = PostRepository().initialize(context)
        }
    }

    /**
     * Initializes the repository
     */
    private fun initialize(context: Context): PostRepository {
        logger.debug("initialize()")
        return this
    }

    /**
     * Get a record of the repository based on its ID
     *
     * @param id the ID to get
     * @returns the record or null
     */
    override fun get(id: String): Post {
        logger.debug("getById($id)")
        TODO("Implement getById")
    }

    /**
     * Get a record of the repository based on its index
     *
     * @param index the index of the record to get
     * @returns the record or null
     */
    override fun get(index: Int): Post {
        logger.debug("getByIndex($index)")
        TODO("Implement getByIndex")
    }

    /**
     * Returns the current size of the repository
     */
    override fun size(): Int {
        logger.debug("size()")
        TODO("Implement size")
    }

    /**
     * Add a record to the repository
     *
     * @param record the record to add
     * @returns true if successfully added
     */
    override fun add(record: Post): Boolean {
        logger.debug("add($record)")
        TODO("Implement add")
    }

    /**
     * Delete a record from the repository
     *
     * @param record the record to remove
     * @returns true if successfully removed
     */
    override fun remove(record: Post): Boolean = remove(record.id)

    /**
     * Remove a record from the repository based on its ID
     *
     * @param id the ID of the record to remove
     * @returns true if successfully removed
     */
    override fun remove(id: String): Boolean {
        logger.debug("remove($id)")
        TODO("Implement removeById")
    }

    /**
     * Update a record in the repository
     *
     * @param record the record to save
     * @returns true if successfully saved
     */
    override fun update(record: Post): Boolean {
        logger.debug("update($record)")
        TODO("Implement update")
    }
}