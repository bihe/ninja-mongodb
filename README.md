Morphia module for Ninja framework.
=====================
Morphia is a lightweight type-safe library for mapping Java objects to/from MongoDB.

This is an easly plugable module for the Ninja web framework to work with morphia and mongodb by providing a convinent service, providing you with all required dependencies.

Getting started
---------------

Setup
-----

1) Add the ninja-morphia dependency to your pom.xml:

    <dependency>
        <groupId>org.ninjaframework</groupId>
        <artifactId>ninja-morphia-module</artifactId>
        <version>x.x.x</version>
    </dependency>

2) Configure the mongodb connection in your ninja application.conf (values are the default values used by this module)

	ninjamorhia.mongodb.host=127.0.0.1
	ninjamorhia.mongodb.port=27017
	ninjamorhia.mongodb.name=ninjamorphia
	ninjamorhia.models.package=models

3) Install the module in your ninja framework conf/Module file:

    @Override
    protected void configure() {
        install(new NinjaMorphiaModule());
    }

4) Inject the ninja-morphia service where needed

	@Inject
	private NinjaMorphia ninjaMorphia

Please note that you still have to annotate your model classes with morphia annotations!

Optional
-----

The ninja-morphia-module provides a superclass with a ObjectId member variable and an appropriate getter method. You may extend your model classes if you want to.

	@Entity
	public class MyEntity extends NinjaMorphiaModel