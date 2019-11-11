package pvi.samplespring;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

public class LogbackApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        Environment env = applicationContext.getEnvironment();

        LogbackApplLogLoader appLogConfigLoader = new LogbackApplLogLoader();

        appLogConfigLoader.applicationLogConfig(env);
    }
}