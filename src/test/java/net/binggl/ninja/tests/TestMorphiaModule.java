package net.binggl.ninja.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import models.TestModel;
import net.binggl.ninja.mongodb.MongoDB;
import ninja.NinjaTest;

public class TestMorphiaModule extends NinjaTest {
    
	private static MongoDB mongoDB;
	private static final MongodStarter starter = MongodStarter.getDefaultInstance();
	private static MongodExecutable mongodExe;
	private static MongodProcess mongod;
	
    @BeforeClass
    public static void init() throws UnknownHostException, IOException {
    	
    	IMongodConfig mongodConfig = new MongodConfigBuilder()
    	        .version(Version.Main.PRODUCTION)
    	        .net(new Net(29019, Network.localhostIsIPv6()))
    	        .build();
    	
        mongodExe = starter.prepare(mongodConfig);
        mongod = mongodExe.start();
    }
    
    @AfterClass
    public static void shutdown() {
    	mongod.stop();
        mongodExe.stop();
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