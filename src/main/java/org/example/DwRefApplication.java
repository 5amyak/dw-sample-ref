package org.example;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.forms.MultiPartBundle;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.example.cli.StartRmqTask;
import org.example.cli.StopRmqTask;
import org.example.resources.AsyncMsgResource;
import org.example.resources.HelloWorldResource;
import org.example.setup.configs.DwRefConfiguration;
import org.example.setup.managed.RmqManager;

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
        RmqManager rmqManager = new RmqManager(configuration.getRmqConfig());
        environment.lifecycle().manage(rmqManager);

        HelloWorldResource helloWorldResource = new HelloWorldResource();
        AsyncMsgResource asyncMsgResource = new AsyncMsgResource(rmqManager);

        environment.jersey().register(helloWorldResource);
        environment.jersey().register(asyncMsgResource);

        environment.admin().addTask(new StopRmqTask(rmqManager));
        environment.admin().addTask(new StartRmqTask(rmqManager));
    }

}
