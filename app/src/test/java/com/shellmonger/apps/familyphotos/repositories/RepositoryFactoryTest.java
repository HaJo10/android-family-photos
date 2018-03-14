package com.shellmonger.apps.familyphotos.repositories;

import com.shellmonger.apps.familyphotos.models.Album;
import com.shellmonger.apps.familyphotos.models.Photo;

import org.junit.Test;
import static org.junit.Assert.*;

public class RepositoryFactoryTest {
    @Test
    public void photosRepositoryCanBeRetrieved() throws RepositoryException {
        IRepository<Photo> photosRepository = RepositoryFactory.getPhotosRepository(null);
        assertNotNull(photosRepository);
    }

    @Test
    public void albumsRepositoryCanBeRetrieved() throws RepositoryException {
        IRepository<Album> albumsRepository = RepositoryFactory.getAlbumsRepository(null);
        assertNotNull(albumsRepository);
    }
}
