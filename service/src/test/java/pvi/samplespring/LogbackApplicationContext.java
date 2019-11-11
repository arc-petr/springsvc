package pvi.samplespring;

import org.springframework.beans.BeansException;
import org.springframework.context.support.GenericApplicationContext;

public class LogbackApplicationContext extends GenericApplicationContext {

    @Override
    protected void onRefresh() throws BeansException {
        LogbackApplLogLoader appLogConfigLoader = new LogbackApplLogLoader();

        appLogConfigLoader.applicationLogConfig(getEnvironment());
    }
}
