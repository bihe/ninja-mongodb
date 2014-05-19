Morphia module for Ninja framework.
=====================
Morphia is a lightweight type-safe library for mapping Java objects to/from MongoDB.

This is an easly plugable module for the Ninja web framework to work with morphia and mongodb by providing a convinent service, providing you with all dependencies and some testing resources.

Getting started
---------------

Setup
-----

1) Add the ninja-morphia dependency to your pom.xml:

    <dependency>
        <groupId>org.ninjaframework</groupId>
        <artifactId>ninja-morphia</artifactId>
        <version>0.0.1</version>
    </dependency>

2) Install the module in your conf.Module:

    @Override
    protected void configure() {
        install(new NinjaMorphiaModule());
    }

3) Inject the ninja-morphia service where required

	@Inject
	private NinjaMorphia ninjaMorphia

4) Configure the mongodb connection in your ninja application.conf (values are the default values used by this module)

	ninjamorhia.mongodb.host=127.0.0.1
	ninjamorhia.mongodb.port=27017
	ninjamorhia.mongodb.name=ninjamorphia
	ninjamorhia.models.package=models

Please note that you still have to annotate your model classes with morphia annotations!

Optional
-----

ninja-morphia provides a superclass with a ObjectId member variable and an appropreaite getter. You may extend your model classes, if you want to.

	@Entity
	public class MyEntity extends NinjaMorphiaModel