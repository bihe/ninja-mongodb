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

@Singleton
public class NinjaMorphia {
    private Datastore datastore;
    private final NinjaProperties ninjaProperties;
    private final Logger logger;
    private final String MORPHIA_PACKAGE_NAME = "morphia.models.package";
    private final String MORPHIA_DB_NAME = "morphia.db.name";
    
    @Inject
    private NinjaMorphia(Logger logger, NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
        this.logger = logger;
        
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient();
        } catch (UnknownHostException e) {
            this.logger.error("Failed to connect to mongo db", e);
        }
        
        if (mongoClient != null) {
            final String morphiaPackage = this.ninjaProperties.getWithDefault(MORPHIA_PACKAGE_NAME, "models");
            final String morphiaDbName = this.ninjaProperties.getWithDefault(MORPHIA_DB_NAME, "ninjamorphia");
            
            this.datastore = new Morphia().mapPackage(morphiaPackage).createDatastore(mongoClient, morphiaDbName);
            this.logger.info("Created DataStore for MongoDB: " + morphiaDbName);
        } else {
            this.logger.error("Failed to created morphia instance");
        }
    }
    
    public Datastore getDatastore() {
        return this.datastore;
    }
    
    public void setMongoClient(MongoClient mongoClient) {
        this.datastore = new Morphia().mapPackage(MORPHIA_PACKAGE_NAME).createDatastore(mongoClient, MORPHIA_DB_NAME);
    }
    
    public <T extends Object> T findByObjectId(Class<T> clazz, Object id) {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkNotNull(id);

        String objectId = null;
        if (!(id instanceof ObjectId)) {
            objectId = String.valueOf(id);
        }
        
        return this.datastore.get(clazz, new ObjectId(objectId));  
    }
    
    public <T extends Object> List<T> findAll(Class<T> clazz) {
        Preconditions.checkNotNull(clazz);
        
        return this.datastore.find(clazz).asList();
    }
    
    public <T extends Object> long countAll(Class<T> clazz) {
        Preconditions.checkNotNull(clazz);
        
        return this.datastore.find(clazz).countAll();
    }
    
    public void save(Object object) {
        Preconditions.checkNotNull(object);
        
        this.datastore.save(object);
    }
    
    public <T extends Object> void findBy(String query, Class<T> clazz) {
        Preconditions.checkNotNull(query);
        Preconditions.checkNotNull(clazz);
    }
    
    public void delete(Object object) {
        Preconditions.checkNotNull(object);
        
        this.datastore.delete(object);
    }
    
    public <T extends Object> void deleteAll(Class<T> clazz) {
        this.datastore.delete(this.datastore.createQuery(clazz));
    }
    
    public void dropdatabase() {
        this.datastore.getDB().dropDatabase();
    }
}