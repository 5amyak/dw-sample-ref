package org.example;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

public class DwRefApplication extends Application<DwRefConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DwRefApplication().run(args);
    }

    @Override
    public String getName() {
        return "DwSampleRef";
    }

    @Override
    public void initialize(final Bootstrap<DwRefConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final DwRefConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
