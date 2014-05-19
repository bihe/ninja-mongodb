Morphia module for Ninja framework.
=====================
Morphia is a lightweight type-safe library for mapping Java objects to/from MongoDB.

This is an easly plugable module for the Ninja web framework to work with morphia and mongodb by providing a convinent service.

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