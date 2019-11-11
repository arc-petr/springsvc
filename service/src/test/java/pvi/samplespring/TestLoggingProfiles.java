package pvi.samplespring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DelegatingSmartContextLoader;

import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@PropertySource("classpath:application.properties")
@ContextConfiguration(classes = {TestProfileLoggingContextConfiguration.class}, loader = LogbackApplLogLoader.class)
@ActiveProfiles(profiles = {"aws", "dev"})
public class TestLoggingProfiles {
    private static final Logger logger = LoggerFactory.getLogger(TestLoggingProfiles.class);


    @Autowired
    Environment env;

    @Test
    public void testTwoProfiles() {
        String appName = env.getProperty("spring.application.name");
        System.out.println(appName);
        Stream.of(env.getActiveProfiles()).forEach(p -> System.out.println(p));

        assertTrue(logger.isErrorEnabled());
        assertTrue(logger.isWarnEnabled());
        assertTrue(logger.isInfoEnabled());
        assertTrue(logger.isDebugEnabled());
        logger.error("This is error message");
        logger.info("This is INFO message");
        logger.debug("This is DEBUG message");
        logger.warn("This is WARN message");

    }
}
