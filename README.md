MongoDB module for Ninja framework
=====================
This is an easly plugable module for the [Ninja web framework](http://www.ninjaframework.org/) to work with MongoDB and optional moprhia (a MongoDB ORM) by providing convinent services, all required dependencies and some testing utilities.

[Morphia][1] is a lightweight type-safe library for mapping Java objects to/from [MongoDB][2].

This is a recreation of the module of [Sven Kubiak (svenkubiak)]( https://github.com/svenkubiak). Unfortunately the github repository was deleted.


[![Build Status](https://travis-ci.org/bihe/ninja-mongodb.png)](https://travis-ci.org/bihe/ninja-mongodb)
[![license](http://img.shields.io/badge/license-apache_2.0-red.svg?style=flat)](https://raw.githubusercontent.com/bihe/ninja-mongodb/master/LICENSE)

Setup
-----

1) Add the ninja-mongodb dependency to your pom.xml:

	<dependency>
	    <groupId>net.binggl</groupId>
	    <artifactId>ninja-mongodb-module</artifactId>
	    <version>x.x.x</version>
	</dependency>

2) Configure the mongodb connection in your ninja application.conf (values show are the default values used by this module if no properties are provided)
	
	ninja.mongodb.host=localhost
	ninja.mongodb.port=27017
	ninja.mongodb.dbname=MyMongoDB
	# if mechanism is MONGO-X509: The x.509 certificate derived user name, e.g. "CN=user,OU=OrgUnit,O=myOrg,..."
	ninja.mongodb.user=  
	ninja.mongodb.pass=
	# possible values: SCRAM-SHA-1 (default)|MONGODB-CR|MONGO-X509
	ninja.mongodb.authMechanism=SCRAM-SHA-1
	ninja.mongodb.authdb=MyMongoDB
	ninja.mongodb.connectonstart=true
	ninja.morphia.package=models
	ninja.morphia.init=true
	
ninja.morphia.package, ninja.mongodb.user and ninja.mongodb.pass are optional.

If connectonstart is set to 'false' the connection can be later initiated by invoking the 'connect' method.

    mongoDB.connect();
	
The same approach is possible to initiate the morphia module. This is done automatically, or can be controlled individually.

    mongoDB.initMorphia();

If ninja.mongodb.connectonstart is 'false' and ninja.morphia.init is 'true' no init is done - because a connection is needed!

3) Inject the ninja-morphia service where needed

	@Inject
	private MyDataService(MongoDB mongoDB) {
	    this.mongoDB = mongoDB;
	}

4) The MongoDB instance handles the connection and gives you the MongoClient to interact with MognoDB

	this.mongoDB.getMongoClient()
	
If you want to use Morphia, you can get the Morphia instance or the Morphia Datastore

	this.mongoDB.getMorphia()
	this.mongoDB.getDatastore()
	
The MongoDB instance also has some convenient methods for interacting with Morphia
	
	public abstract <T extends Object> T findById(Object id, Class<T> clazz);
	public abstract <T extends Object> List<T> findAll(Class<T> clazz);
	public abstract <T extends Object> long countAll(Class<T> clazz);
	public abstract void save(Object object);
	public abstract void delete(Object object);
	public abstract <T extends Object> void deleteAll(Class<T> clazz);
	public abstract void dropDatabase();
	
The ninja-morphia-module provides a superclass with a ObjectId member variable and an appropriate getter method. You may extend your model classes if you want to.

	@Entity
	public class MyEntity extends MorphiaModel

Please note that you still have to annotate your model classes with [morphia annotations][3]!


  [1]: https://github.com/mongodb/morphia
  [2]: http://www.mongodb.org/
  [3]: https://github.com/mongodb/morphia/wiki/GettingStarted
