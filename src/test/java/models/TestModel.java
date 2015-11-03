package models;

import net.binggl.ninja.mongodb.MorphiaModel;

public class TestModel extends MorphiaModel {
    private static final long serialVersionUID = -3974611906988154231L;
    
    private String name;
    
    public TestModel(){
    }
    
    public TestModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}