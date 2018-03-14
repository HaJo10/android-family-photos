package com.shellmonger.apps.familyphotos.repositories;

import android.content.Context;

import com.shellmonger.apps.familyphotos.models.Album;
import com.shellmonger.apps.familyphotos.models.Photo;

public class RepositoryFactory {
    private static IRepository<Photo> _photosRepository = null;
    private static IRepository<Album> _albumsRepository = null;

    public static synchronized IRepository<Photo> getPhotosRepository(Context context) {
        if (_photosRepository == null) {
            _photosRepository = new FakeRepository<Photo>(context);
        }
        return _photosRepository;
    }

    public static IRepository<Album> getAlbumsRepository(Context context) {
        if (_albumsRepository == null) {
            _albumsRepository = new FakeRepository<Album>(context);
        }
        return _albumsRepository;
    }
}
