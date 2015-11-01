package org.ninjaframework.morphia;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import ninja.utils.NinjaProperties;


/**
 * Convinent service class for interacting with MongoDb over Morphia
 * @author bihe (original author: skubiak)
 *
 */
@Singleton
public class MongoDB {
    private static final Logger LOG = LoggerFactory.getLogger(MongoDB.class);
    
    private static final int DEFAULT_MONGODB_PORT = 27017;
    private static final String DEFAULT_MORPHIA_PACKAGE = "models";
    private static final String DEFAULT_MONGODB_NAME = "MyMongoDb";
    private static final String DEFAULT_MONGODB_HOST = "localhost";
    private static final String MONGODB_HOST = "ninja.mongodb.host";
    private static final String MONGODB_PORT = "ninja.mongodb.port";
    private static final String MONGODB_USER = "ninja.mongodb.user";
    private static final String MONGODB_PASS = "ninja.mongodb.pass";
    private static final String MONGODB_DBNAME = "ninja.mongodb.dbname";
    private static final String MONGODB_AUTHDB = "ninja.mongodb.authdb";
    private static final String MORPHIA_PACKAGE = "ninja.morphia.package";
    private static final String MORPHIA_INIT = "ninja.morphia.init";
    private Datastore datastore;
    private Morphia Morphia;
    private MongoClient mongoClient;
    private NinjaProperties ninjaProperties;

    
    @Inject
    public MongoDB(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
        
        this.connect();
        if (this.ninjaProperties.getBooleanWithDefault(MORPHIA_INIT, false)) {
            this.morphify();
        }
    }
    
     public Datastore getDatastore() {
        return this.datastore;
    }

    public Morphia getMorphia() {
        return this.Morphia;
    }

    public MongoClient getMongoClient() {
        return this.mongoClient;
    }
	
	private void connect() {
        String string = this.ninjaProperties.getWithDefault(MONGODB_HOST, DEFAULT_MONGODB_HOST);
        int n = this.ninjaProperties.getIntegerWithDefault(MONGODB_PORT, DEFAULT_MONGODB_PORT);
        String username = this.ninjaProperties.getWithDefault(MONGODB_USER, null);
        String password = this.ninjaProperties.getWithDefault(MONGODB_PASS, null);
        String authdb = this.ninjaProperties.get(MONGODB_AUTHDB);
        
		if (StringUtils.isNotBlank((CharSequence)username) && StringUtils.isNotBlank((CharSequence)password) && StringUtils.isNotBlank((CharSequence)authdb)) {
            this.mongoClient = new MongoClient(new ServerAddress(string, n), Arrays.asList(new MongoCredential[]{MongoCredential.createScramSha1Credential((String)username, (String)authdb, (char[])password.toCharArray())}));
            LOG.info("Successfully created MongoClient @ {}:{} with authentication {}/*******", new Object[]{string, n, username});
        } else {
            this.mongoClient = new MongoClient(string, n);
            LOG.info("Successfully created MongoClient @ {}:{}", (Object)string, (Object)n);
        }
    }

    private void morphify() {
        String packageName = this.ninjaProperties.getWithDefault(MORPHIA_PACKAGE, DEFAULT_MORPHIA_PACKAGE);
        String dbName = this.ninjaProperties.getWithDefault(MONGODB_DBNAME, DEFAULT_MONGODB_NAME);
        
        this.Morphia = new Morphia().mapPackage(packageName);
        this.datastore = this.Morphia.createDatastore(this.mongoClient, dbName);
        
        LOG.info("Mapped Morphia models of package '" + packageName + "' and created Morphia Datastore to database '" + dbName + "'");
    }

    public void ensureIndexes(boolean bl) {
        this.datastore.ensureIndexes(bl);
    }

    public void ensureCaps() {
        this.datastore.ensureCaps();
    }

    
    /**
     * Retrieves a mapped Morphia object from MongoDb. If the id is not of 
     * type ObjectId, it will we converted to ObjectId
     * 
     * @param id The id of the object
     * @param clazz The mapped Morphia class
     * 
     * @return The reqeusted class from MongoDb or null if none found
     */
    public <T extends Object> T findById(Object id, Class<T> clazz) {
        Preconditions.checkNotNull(clazz, "Tryed to find an object by id, but given class is null");
        Preconditions.checkNotNull(id, "Tryed to find an object by id, but given id is null");

        return this.datastore.get(clazz, (id instanceof ObjectId) ? id : new ObjectId(String.valueOf(id)));
    }
    
    /**
     * Retrieves a list of mapped Morphia objects from MongoDB
     *
     * @param clazz The mapped Morphia class
     * @return A list of mapped Morphia objects or an empty list if none found
     */
    public <T extends Object> List<T> findAll(Class<T> clazz) {
        Preconditions.checkNotNull(clazz, "Tryed to get all morphia objects of a given object, but given object is null");

        return this.datastore.find(clazz).asList();
    }

    /**
     * Counts all objected of a mapped Morphia class
     *
     * @param clazz The mapped Morphia class
     * @return The number of objects in MongoDB
     */
    public <T extends Object> long countAll(Class<T> clazz) {
        Preconditions.checkNotNull(clazz, "Tryed to count all a morphia objects of a given object, but given object is null");

        return this.datastore.find(clazz).countAll();
    }

    /**
     * Saves a mapped Morphia object to MongoDB
     *
     * @param object The object to save
     */
    public void save(Object object) {
        Preconditions.checkNotNull(object, "Tryed to save a morphia object, but a given object is null");

        this.datastore.save(object);
    }

    /**
     * Deletes a mapped Morphia object in MongoDB
     *
     * @param object The object to delete
     */
    public void delete(Object object) {
        Preconditions.checkNotNull(object, "Tryed to delete a morphia object, but given object is null");

        this.datastore.delete(object);
    }

    /**
     * Deletes all mapped Morphia objects of a given class
     *
     * @param clazz The mapped Morphia class
     */
    public <T extends Object> void deleteAll(Class<T> clazz) {
        Preconditions.checkNotNull(clazz, "Tryed to delete list of mapped morphia objects, but given class is null");

        this.datastore.delete(this.datastore.createQuery(clazz));
    }
    
    /**
     * Drops all data in mognodb on the configured database in 
     * ninja framework application.conf
     */
    public void dropDatabase() {
        this.datastore.getDB().dropDatabase();
    }
}