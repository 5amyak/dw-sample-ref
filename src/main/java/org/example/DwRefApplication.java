package org.example;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.forms.MultiPartBundle;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.example.resources.HelloWorldResource;

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
        bootstrap.addBundle(getSwaggerBundle());
        bootstrap.addBundle(new MultiPartBundle());
    }

    private static SwaggerBundle<DwRefConfiguration> getSwaggerBundle() {
        return new SwaggerBundle<>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(DwRefConfiguration config) {
                return config.getSwaggerBundleConfiguration();
            }
        };
    }

    @Override
    public void run(final DwRefConfiguration configuration,
                    final Environment environment) {
        HelloWorldResource helloWorldResource = new HelloWorldResource();
        environment.jersey().register(helloWorldResource);
    }

}
