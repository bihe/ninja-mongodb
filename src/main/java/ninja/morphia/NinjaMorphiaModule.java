package ninja.morphia;

import com.google.inject.AbstractModule;

public class NinjaMorphiaModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(NinjaMorphia.class);
    }
}