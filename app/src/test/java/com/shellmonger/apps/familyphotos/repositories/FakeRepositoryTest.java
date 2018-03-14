package com.shellmonger.apps.familyphotos.repositories;

import com.shellmonger.apps.familyphotos.models.BaseModel;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

class TestModel extends BaseModel {
    private String test;

    public String getTest() { return test; }
    public void setTest(String test) { this.test = test; }
}

public class FakeRepositoryTest {
    @Test
    public void newRepositoryShouldBeEmpty() throws RepositoryException {
        FakeRepository<TestModel> repository = new FakeRepository<>(null);
        assertEquals("Repository is not empty", 0, repository.getLength());
    }

    @Test (expected = NullPointerException.class)
    public void saveNullItemShouldThrow() throws RepositoryException {
        FakeRepository<TestModel> repository = new FakeRepository<>(null);
        repository.saveItem(null);
        fail("Null item was apparently saved");
    }

    @Test
    public void saveItemShouldBeStored() throws RepositoryException {
        FakeRepository<TestModel> repository = new FakeRepository<>(null);
        TestModel r = new TestModel();
        TestModel t = repository.saveItem(r);
        long c = System.currentTimeMillis();
        assertEquals("Record was not inserted", 1, repository.getLength());
        assertTrue("Updated was not updated", c - t.getUpdated() < 50);
    }

    @Test
    public void saveSameItemShouldUpdate() throws RepositoryException {
        FakeRepository<TestModel> repository = new FakeRepository<>(null);
        TestModel r = new TestModel();
        TestModel t = repository.saveItem(r);
        t.setTest("Update");
        TestModel u = repository.saveItem(t);
        long c = System.currentTimeMillis();
        assertEquals("Record was not inserted", 1, repository.getLength());
        assertTrue("Updated was not updated", c - t.getUpdated() < 50);
    }

    @Test
    public void saveDifferentItemsShouldAdd() throws RepositoryException {
        FakeRepository<TestModel> repository = new FakeRepository<>(null);
        TestModel r = new TestModel();
        TestModel t = repository.saveItem(r);
        TestModel u = new TestModel();
        TestModel u2 = repository.saveItem(u);
        assertEquals("Record was not inserted", 2, repository.getLength());
    }

    @Test
    public void removeItemWorks() throws RepositoryException {
        FakeRepository<TestModel> repository = new FakeRepository<>(null);
        TestModel r = new TestModel();
        TestModel t = repository.saveItem(r);
        assertEquals("Record was not inserted", 1, repository.getLength());
        repository.removeItem(t);
        assertEquals("Record was not removed", 0, repository.getLength());
    }

    @Test (expected=NullPointerException.class)
    public void removeNullItemThrowsException() throws RepositoryException {
        FakeRepository<TestModel> repository = new FakeRepository<>(null);
        repository.removeItem(null);
        fail("Null item was apparently removed");
    }

    @Test (expected=ItemMissingException.class)
    public void removeMissingItemThrowsException() throws RepositoryException {
        FakeRepository<TestModel> repository = new FakeRepository<>(null);
        TestModel r = new TestModel();
        repository.removeItem(r);
        fail("Removing a non-existent item apparently works");
    }

    @Test (expected=ItemMissingException.class)
    public void getMissingItemThrowsException() throws RepositoryException {
        FakeRepository<TestModel> repository = new FakeRepository<>(null);
        TestModel r = new TestModel();
        repository.getItem(r.getId());
        fail("Removing a non-existent item apparently works");
    }

    @Test
    public void getItemWorks() throws RepositoryException {
        FakeRepository<TestModel> repository = new FakeRepository<>(null);
        TestModel r = new TestModel();
        r.setTest("foo");
        repository.saveItem(r);
        TestModel t = repository.getItem(r.getId());
        assertEquals("Data not retrieved", "foo", t.getTest());
    }

    @Test
    public void getItemWithLargeRepositoryWorks() throws RepositoryException {
        FakeRepository<TestModel> repository = new FakeRepository<>(null);
        TestModel o1 = new TestModel();
        repository.saveItem(o1);
        TestModel o2 = new TestModel();
        o2.setTest("o2");
        repository.saveItem(o2);
        TestModel o3 = new TestModel();
        repository.saveItem(o3);
        TestModel o4 = new TestModel();
        repository.saveItem(o4);
        TestModel o5 = new TestModel();
        repository.saveItem(o5);
        TestModel t = repository.getItem(o2.getId());
        assertEquals("Data not retrieved", "o2", t.getTest());
    }

    @Test
    public void getItemsReturnsAllItems() throws RepositoryException {
        FakeRepository<TestModel> repository = new FakeRepository<>(null);
        TestModel o1 = new TestModel();
        repository.saveItem(o1);
        TestModel o2 = new TestModel();
        repository.saveItem(o2);
        TestModel o3 = new TestModel();
        repository.saveItem(o3);
        List<TestModel> r = repository.getItems();
        assertEquals("list is wrong size", 3, r.size());
        assertTrue("object missing", r.contains(o1));
        assertTrue("object missing", r.contains(o2));
        assertTrue("object missing", r.contains(o3));
    }

    @Test
    public void getItemsPagingWorks() throws RepositoryException {
        FakeRepository<TestModel> repository = new FakeRepository<>(null);
        TestModel o1 = new TestModel();
        repository.saveItem(o1);
        TestModel o2 = new TestModel();
        repository.saveItem(o2);
        TestModel o3 = new TestModel();
        repository.saveItem(o3);
        TestModel o4 = new TestModel();
        repository.saveItem(o4);
        TestModel o5 = new TestModel();
        repository.saveItem(o5);
        List<TestModel> r = repository.getItems(2, 2);
        assertEquals("list is wrong size", 2, r.size());
        assertTrue("object missing", r.contains(o3));
        assertTrue("object missing", r.contains(o4));
    }

    @Test
    public void getItemsPagingToEndWorks() throws RepositoryException {
        FakeRepository<TestModel> repository = new FakeRepository<>(null);
        TestModel o1 = new TestModel();
        repository.saveItem(o1);
        TestModel o2 = new TestModel();
        repository.saveItem(o2);
        TestModel o3 = new TestModel();
        repository.saveItem(o3);
        TestModel o4 = new TestModel();
        repository.saveItem(o4);
        TestModel o5 = new TestModel();
        repository.saveItem(o5);
        List<TestModel> r = repository.getItems(4, 2);
        assertEquals("list is wrong size", 1, r.size());
    }

    @Test
    public void getItemsPagingBeyondEndWorks() throws RepositoryException {
        FakeRepository<TestModel> repository = new FakeRepository<>(null);
        TestModel o1 = new TestModel();
        repository.saveItem(o1);
        TestModel o2 = new TestModel();
        repository.saveItem(o2);
        TestModel o3 = new TestModel();
        repository.saveItem(o3);
        TestModel o4 = new TestModel();
        repository.saveItem(o4);
        TestModel o5 = new TestModel();
        repository.saveItem(o5);
        List<TestModel> r = repository.getItems(6, 2);
        assertEquals("list is wrong size", 0, r.size());
    }
}