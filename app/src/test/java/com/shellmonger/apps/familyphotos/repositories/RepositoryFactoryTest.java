package com.shellmonger.apps.familyphotos.repositories;

import com.shellmonger.apps.familyphotos.models.Album;
import com.shellmonger.apps.familyphotos.models.Photo;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RepositoryFactoryTest {
    @Before
    public void runBeforeTestMethod() {
        RepositoryFactory.cleanup();
    }

    @Test (expected=RepositoryException.class)
    public void photosRepositoryNeedsInitialization() throws RepositoryException {
        Repository<Photo> p = RepositoryFactory.getPhotosRepository();
        fail("Photos Repository is unexpectedly present");
    }

    @Test
    public void photosRepositoryCanBeRetrieved() throws RepositoryException {
        RepositoryFactory.initialize(null);
        Repository<Photo> photosRepository = RepositoryFactory.getPhotosRepository();
        assertNotNull(photosRepository);
    }

    @Test
    public void albumsRepositoryCanBeRetrieved() throws RepositoryException {
        RepositoryFactory.initialize(null);
        Repository<Album> albumsRepository = RepositoryFactory.getAlbumsRepository();
        assertNotNull(albumsRepository);
    }
}
