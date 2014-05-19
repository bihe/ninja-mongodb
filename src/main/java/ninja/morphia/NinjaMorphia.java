package ninja.morphia;

import java.net.UnknownHostException;
import java.util.List;

import ninja.utils.NinjaProperties;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;

/**
 * Convinent service class for interacting with mongodb over morphia
 * @author skubiak
 *
 */
@Singleton
public class NinjaMorphia {
    private Datastore datastore;
    private static final String MONGODB_HOST = "ninjamorphia.mongodb.host";
    private static final String MONGODB_PORT = "ninjamorphia.mongodb.port";
    private static final String MONGODB_NAME = "ninjamorphia.mongodb.name";
    private static final String MORPHIA_PACKAGE_NAME = "ninjamorphia.models.package";
    private static final String MONGODB_DEFAULT_HOST = "127.0.0.1";
    private static final String MORPHIA_DEFAULT_PACKAGE_NAME = "models";
    private static final String MONGODB_DEFAULT_NAME = "ninjamorphia";
    private static final int MONGODB_DEFAULT_PORT = 27017;
    private final NinjaProperties ninjaProperties;
    private final Logger logger;
    
    @Inject
    private NinjaMorphia(Logger logger, NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
        this.logger = logger;
        
        MongoClient mongoClient = null;
        try {
            final String mongoDbHost = this.ninjaProperties.getWithDefault(MONGODB_HOST, MONGODB_DEFAULT_HOST);
            final int mongoDbPort = this.ninjaProperties.getIntegerWithDefault(MONGODB_PORT, MONGODB_DEFAULT_PORT);
            
            mongoClient = new MongoClient(mongoDbHost, mongoDbPort);
        } catch (UnknownHostException e) {
            this.logger.error("Failed to connect to MongoDB", e);
        }
        
        if (mongoClient != null) {
            final String mongoDbName = this.ninjaProperties.getWithDefault(MONGODB_NAME, MONGODB_DEFAULT_NAME);
            final String morphiaPackage = this.ninjaProperties.getWithDefault(MORPHIA_PACKAGE_NAME, MORPHIA_DEFAULT_PACKAGE_NAME);
            
            this.datastore = new Morphia().mapPackage(morphiaPackage).createDatastore(mongoClient, mongoDbName);
            this.logger.info("Created morphia datastore for MongoDB: " + mongoDbName);
        } else {
            this.logger.error("Failed to created morphia datastore. No mongoclient available.");
        }
    }
    
    /**
     * Returns the created morphia datastore
     * 
     * @return Morphia Datastore object
     */
    public Datastore getDatastore() {
        return this.datastore;
    }
    
    /**
     * Convinent mehod for overwriting the morphia
     * object with a given MongoClient
     * 
     * @param A created MongoClient object
     */
    public void setMongoClient(MongoClient mongoClient) {
        Preconditions.checkNotNull(mongoClient);
        
        this.datastore = new Morphia().mapPackage(MORPHIA_PACKAGE_NAME).createDatastore(mongoClient, MONGODB_NAME);
    }
    
    /**
     * Retrieves a mapped morphia object from mongodb. If the id is not of 
     * type ObjectId, it will we converted to ObjectId
     * 
     * @param id The id of the object
     * @param clazz The mapped morphia class
     * 
     * @return The reqeusted class from mongodb or null if none found
     */
    public <T extends Object> T findById(Object id, Class<T> clazz) {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkNotNull(id);

        String objectId = null;
        if (!(id instanceof ObjectId)) {
            objectId = String.valueOf(id);
        }
        
        return this.datastore.get(clazz, new ObjectId(objectId));  
    }
    
    /**
     * Retrieves all mapped morphia objects from mongodb
     * 
     * @param clazz The mapped morphia class
     * @return A list of mapped morphia objects or an empty list of none found
     */
    public <T extends Object> List<T> findAll(Class<T> clazz) {
        Preconditions.checkNotNull(clazz);
        
        return this.datastore.find(clazz).asList();
    }
    
    /**
     * Counts all objected of a mapped morphia class
     * 
     * @param clazz The mapped morphia class
     * @return The number of objects in mongodb
     */
    public <T extends Object> long countAll(Class<T> clazz) {
        Preconditions.checkNotNull(clazz);
        
        return this.datastore.find(clazz).countAll();
    }
    
    /**
     * Saves a mapped morphia object to mongodb
     * 
     * @param object The object to save
     */
    public void save(Object object) {
        Preconditions.checkNotNull(object);
        
        this.datastore.save(object);
    }
    
    /**
     * Deletes a mapped morphia object in mongodb
     * 
     * @param object The object to delete
     */
    public void delete(Object object) {
        Preconditions.checkNotNull(object);
        
        this.datastore.delete(object);
    }
    
    /**
     * Deletes all mapped morphia objects of a given class
     * 
     * @param clazz The mapped morphia class
     */
    public <T extends Object> void deleteAll(Class<T> clazz) {
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