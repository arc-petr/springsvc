package pvi.samplespring;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.joran.action.NOPAction;
import ch.qos.logback.core.joran.event.InPlayListener;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.*;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.util.OptionHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.boot.logging.LoggingSystemProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.SmartContextLoader;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.xml.sax.Attributes;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to use logback-spring-{applicationname}.xml for logback initialization.
 * Primary goal for this loader is to allow tests to use spring profiles in the logback configurations for tests.
 */

public class LogbackApplLogLoader implements SmartContextLoader {

    protected static final Log logger = LogFactory.getLog(LogbackApplLogLoader.class);
    private final AnnotationConfigContextLoader annotationConfigContextLoader;
    private ClassLoader classLoader;
    Environment environment;

    public LogbackApplLogLoader() {
        annotationConfigContextLoader = new AnnotationConfigContextLoader();
        this.classLoader = ClassUtils.getDefaultClassLoader();
    }


    public void applicationLogConfig(Environment env) {
        String appName = env.getProperty("spring.application.name");
        String location = findConfig(getAppConfigLocations(appName));
        if (location != null) {
            new LoggingSystemProperties(env).apply(null);
            LoggerContext loggerContext = getLoggerContext();
            stopAndReset(loggerContext);
            try {
                configureByResourceUrl(env, loggerContext, ResourceUtils.getURL(location));
            } catch (Exception ex) {
                throw new IllegalStateException("Could not initialize Logback logging from " + location, ex);
            }
            List<Status> statuses = loggerContext.getStatusManager().getCopyOfStatusList();
            StringBuilder errors = new StringBuilder();
            for (Status status : statuses) {
                if (status.getLevel() == Status.ERROR) {
                    errors.append((errors.length() > 0) ? String.format("%n") : "");
                    errors.append(status.toString());
                }
            }
            if (errors.length() > 0) {
                throw new IllegalStateException(String.format("Logback configuration error detected: %n%s", errors));
            }

        }
    }

    private String[] getSpringConfigLocations() {
        return new String[]{"logback-test-spring.groovy", "logback-test-spring.xml", "logback-spring.groovy", "logback-spring.xml"};
    }

    private String[] getAppConfigLocations(String appName) {
        String[] locations = getSpringConfigLocations();
        String suffix = (appName == null ? "" : "-" + appName) + ".";
        for (int i = 0; i < locations.length; i++) {
            String extension = StringUtils.getFilenameExtension(locations[i]);
            locations[i] = locations[i].substring(0, locations[i].length() - extension.length() - 1) + suffix
                    + extension;
        }
        return locations;
    }

    private String findConfig(String[] locations) {
        for (String location : locations) {
            ClassPathResource resource = new ClassPathResource(location, this.classLoader);
            if (resource.exists()) {
                return "classpath:" + location;
            }
        }
        return null;
    }

    private LoggerContext getLoggerContext() {
        ILoggerFactory factory = StaticLoggerBinder.getSingleton().getLoggerFactory();
        Assert.isInstanceOf(LoggerContext.class, factory,
                String.format(
                        "LoggerFactory is not a Logback LoggerContext but Logback is on "
                                + "the classpath. Either remove Logback or the competing "
                                + "implementation (%s loaded from %s). If you are using "
                                + "WebLogic you will need to add 'org.slf4j' to "
                                + "prefer-application-packages in WEB-INF/weblogic.xml",
                        factory.getClass(), getLocation(factory)));
        return (LoggerContext) factory;
    }

    private Object getLocation(ILoggerFactory factory) {
        try {
            ProtectionDomain protectionDomain = factory.getClass().getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            if (codeSource != null) {
                return codeSource.getLocation();
            }
        } catch (SecurityException ex) {
            // Unable to determine location
        }
        return "unknown location";
    }

    private void stopAndReset(LoggerContext loggerContext) {
        loggerContext.stop();
        loggerContext.reset();

    }

    private void configureByResourceUrl(Environment env, LoggerContext loggerContext,
                                        URL url) throws JoranException {
        if (url.toString().endsWith("xml")) {
            JoranConfigurator configurator = new R4ESpringJoranConfigurator(env);
            configurator.setContext(loggerContext);
            configurator.doConfigure(url);
        } else {
            new ContextInitializer(loggerContext).configureByResource(url);
        }
    }

    public void processContextConfiguration(ContextConfigurationAttributes configAttributes) {
        annotationConfigContextLoader.processContextConfiguration(configAttributes);
    }

    public String[] processLocations(Class<?> clazz, String... locations) {
        return annotationConfigContextLoader.processLocations(clazz, locations);
    }

    public ConfigurableApplicationContext loadContext(MergedContextConfiguration mergedConfig) throws Exception {
        ConfigurableApplicationContext result = annotationConfigContextLoader.loadContext(mergedConfig);
        applicationLogConfig(result.getEnvironment());
        return result;
    }

    public ConfigurableApplicationContext loadContext(String... locations) throws Exception {
        ConfigurableApplicationContext result = annotationConfigContextLoader.loadContext(locations);
        applicationLogConfig(result.getEnvironment());
        return result;
    }

    static class R4ESpringJoranConfigurator extends JoranConfigurator {

        private Environment environment;

        R4ESpringJoranConfigurator(Environment env) {
            this.environment = env;
        }

        @Override
        public void addInstanceRules(RuleStore rs) {
            super.addInstanceRules(rs);
            rs.addRule(new ElementSelector("configuration/springProperty"), new R4ESpringPropertyAction(environment));
            rs.addRule(new ElementSelector("*/springProfile"), new R4ESpringProfileAction(environment));
            rs.addRule(new ElementSelector("*/springProfile/*"), new NOPAction());
        }

    }

    static class R4ESpringPropertyAction extends Action {

        private static final String SOURCE_ATTRIBUTE = "source";

        private static final String DEFAULT_VALUE_ATTRIBUTE = "defaultValue";

        private final Environment environment;

        R4ESpringPropertyAction(Environment environment) {
            this.environment = environment;
        }

        @Override
        public void begin(InterpretationContext context, String elementName, Attributes attributes) throws ActionException {
            String name = attributes.getValue(NAME_ATTRIBUTE);
            String source = attributes.getValue(SOURCE_ATTRIBUTE);
            ActionUtil.Scope scope = ActionUtil.stringToScope(attributes.getValue(SCOPE_ATTRIBUTE));
            String defaultValue = attributes.getValue(DEFAULT_VALUE_ATTRIBUTE);
            if (OptionHelper.isEmpty(name) || OptionHelper.isEmpty(source)) {
                addError("The \"name\" and \"source\" attributes of <springProperty> must be set");
            }
            ActionUtil.setProperty(context, name, getValue(source, defaultValue), scope);
        }

        private String getValue(String source, String defaultValue) {
            if (this.environment == null) {
                addWarn("No Spring Environment available to resolve " + source);
                return defaultValue;
            }
            String value = this.environment.getProperty(source);
            if (value != null) {
                return value;
            }
            int lastDot = source.lastIndexOf('.');
            if (lastDot > 0) {
                String prefix = source.substring(0, lastDot + 1);
                return this.environment.getProperty(prefix + source.substring(lastDot + 1), defaultValue);
            }
            return defaultValue;
        }

        @Override
        public void end(InterpretationContext context, String name) throws ActionException {
        }

    }


    static class R4ESpringProfileAction extends Action implements InPlayListener {

        private final Environment environment;

        private int depth = 0;

        private boolean acceptsProfile;

        private List<SaxEvent> events;

        R4ESpringProfileAction(Environment environment) {
            this.environment = environment;
        }

        @Override
        public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
            this.depth++;
            if (this.depth != 1) {
                return;
            }
            ic.pushObject(this);
            this.acceptsProfile = acceptsProfiles(ic, attributes);
            this.events = new ArrayList<>();
            ic.addInPlayListener(this);
        }

        private boolean acceptsProfiles(InterpretationContext ic, Attributes attributes) {
            if (this.environment == null) {
                return false;
            }
            String[] profileNames = StringUtils
                    .trimArrayElements(StringUtils.commaDelimitedListToStringArray(attributes.getValue(NAME_ATTRIBUTE)));
            if (profileNames.length == 0) {
                return false;
            }
            for (int i = 0; i < profileNames.length; i++) {
                profileNames[i] = OptionHelper.substVars(profileNames[i], ic, this.context);
            }
            return this.environment.acceptsProfiles(Profiles.of(profileNames));
        }

        @Override
        public void end(InterpretationContext ic, String name) throws ActionException {
            this.depth--;
            if (this.depth != 0) {
                return;
            }
            ic.removeInPlayListener(this);
            verifyAndPop(ic);
            if (this.acceptsProfile) {
                addEventsToPlayer(ic);
            }
        }

        private void verifyAndPop(InterpretationContext ic) {
            Object o = ic.peekObject();
            Assert.state(o != null, "Unexpected null object on stack");
            Assert.isInstanceOf(R4ESpringProfileAction.class, o, "logback stack error");
            Assert.state(o == this, "ProfileAction different than current one on stack");
            ic.popObject();
        }

        private void addEventsToPlayer(InterpretationContext ic) {
            Interpreter interpreter = ic.getJoranInterpreter();
            this.events.remove(0);
            this.events.remove(this.events.size() - 1);
            interpreter.getEventPlayer().addEventsDynamically(this.events, 1);
        }

        @Override
        public void inPlay(SaxEvent event) {
            this.events.add(event);
        }

    }

}


