package net.binggl.ninja.mongodb;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

/**
 * Superclass for mapping mongodb morphia models
 * @author bihe (original author: skubiak)
 *
 */
public class MorphiaModel implements Serializable {
    private static final long serialVersionUID = -3141621127850129919L;

    @Id
    protected ObjectId objectId;

    public ObjectId getId() {
        return this.objectId;
    }
}