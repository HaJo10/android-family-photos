package com.shellmonger.apps.familyphotos.repositories;

import android.content.Context;

import com.shellmonger.apps.familyphotos.models.Album;
import com.shellmonger.apps.familyphotos.models.Photo;

public class RepositoryFactory {
    private static Repository<Photo> _photosRepository = null;
    private static Repository<Album> _albumsRepository = null;

    public static synchronized void initialize(Context context) {
        if (_photosRepository == null) {
            _photosRepository = new FakeRepository<Photo>(context);
        }
        if (_albumsRepository == null) {
            _albumsRepository = new FakeRepository<Album>(context);
        }
    }

    /**
     * Used during testing to clear away the old repository.
     */
    static synchronized void cleanup() {
        _photosRepository = null;
        _albumsRepository = null;
    }

    public static Repository<Photo> getPhotosRepository() throws RepositoryException {
        if (_photosRepository == null) {
            throw new RepositoryException("Repository is not initialized");
        }
        return _photosRepository;
    }

    public static Repository<Album> getAlbumsRepository() throws RepositoryException {
        if (_albumsRepository == null) {
            throw new RepositoryException("Repository is not initialized");
        }
        return _albumsRepository;
    }
}
