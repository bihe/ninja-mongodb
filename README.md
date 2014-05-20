Morphia module for Ninja framework
=====================
[Morphia][1] is a lightweight type-safe library for mapping Java objects to/from [MongoDB][2].

This is an easily plugable module for the Ninja web framework to work with morphia and mongodb by providing a convinient service and providing you with all required dependencies.

Setup
-----

1) Add the ninja-morphia dependency to your pom.xml:

    <dependency>
        <groupId>de.svenkubiak.ninjaframework</groupId>
        <artifactId>ninja-morphia-module</artifactId>
        <version>x.x.x</version>
    </dependency>

2) Configure the mongodb connection in your ninja application.conf (values show are the default values used by this module if no properties are provided)
	
	ninjamorphia.mongodb.host=127.0.0.1
	ninjamorphia.mongodb.port=27017
	ninjamorphia.mongodb.name=ninjamorphia
	ninjamorphia.models.package=models

3) Inject the ninja-morphia service where needed

	@Inject
	private NinjaMorphia ninjaMorphia

Please note that you still have to annotate your model classes with [morphia annotations][3]!

Optional
-----

The ninja-morphia-module provides a superclass with a ObjectId member variable and an appropriate getter method. You may extend your model classes if you want to.

	@Entity
	public class MyEntity extends NinjaMorphiaModel


  [1]: https://github.com/mongodb/morphia
  [2]: http://www.mongodb.org/
  [3]: https://github.com/mongodb/morphia/wiki/GettingStarted