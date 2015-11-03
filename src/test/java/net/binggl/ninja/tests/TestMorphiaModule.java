package net.binggl.ninja.tests;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Inject;

import de.svenkubiak.embeddedmongodb.EmbeddedMongo;
import models.TestModel;
import net.binggl.ninja.mongodb.MongoDB;
import ninja.NinjaTest;

public class TestMorphiaModule extends NinjaTest {
    
	@Inject private static MongoDB mongoDB;

    @BeforeClass
    public static void init() {
        EmbeddedMongo.DB.port(29019).start();
    }
    
    @AfterClass
    public static void shutdown() {
        EmbeddedMongo.DB.stop();
    }
    
    @Before
    public void setup() throws Exception{
    	mongoDB = getInjector().getInstance(MongoDB.class);
    	mongoDB.dropDatabase();
    }

    @Test
    public void testInit() {
        assertNotNull(mongoDB.getMongoClient());
        assertNotNull(mongoDB.getMorphia());
        assertNotNull(mongoDB.getDatastore());
    }
    
    @Test
    public void testInsertAndFindAll() {
        mongoDB.save(new TestModel("foo"));
        assertEquals(1, mongoDB.findAll(TestModel.class).size());

        mongoDB.dropDatabase();
        assertEquals(0, mongoDB.findAll(TestModel.class).size());
    }
    
    @Test
    public void testFindById() {
        mongoDB.save(new TestModel("foo"));

        TestModel testModel = mongoDB.getDatastore().find(TestModel.class).field("name").equal("foo").get();
        assertNotNull(mongoDB.findById(testModel.getId(), TestModel.class));
    }

    @Test
    public void testCountAll() {
        mongoDB.save(new TestModel("foo"));
        mongoDB.save(new TestModel("bar"));
        mongoDB.save(new TestModel("bla"));

        assertEquals(3, mongoDB.countAll(TestModel.class));
    }

    @Test
    public void testDeleteAll() {
        mongoDB.save(new TestModel("foo"));
        mongoDB.save(new TestModel("bar"));
        mongoDB.save(new TestModel("bla"));

        assertEquals(3, mongoDB.countAll(TestModel.class));
        mongoDB.deleteAll(TestModel.class);
        assertEquals(0, mongoDB.countAll(TestModel.class));
    }
}