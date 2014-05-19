package ninja.morphia;

import com.google.inject.AbstractModule;

/**
 * Google juice module loader for injecting in
 * ninja framework applications
 * 
 * @author skubiak
 *
 */
public class NinjaMorphiaModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(NinjaMorphia.class);
    }
}