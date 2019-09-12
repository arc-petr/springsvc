package pvi.samplespring.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingManager {

	private Logger libraryLogger = LogManager.getLogger(LoggingManager.class);

	@Around("execution(* pvi.samplespring.*.*(..))")
	public void coverMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		String longName = proceedingJoinPoint.getSignature().toLongString();

		libraryLogger.info(String.format("Method started: %s  ", longName));
		try {

			Object result = proceedingJoinPoint.proceed();
			libraryLogger.info(String.format("Method %s returned %s  ", longName, result));
		} catch (Throwable t) {
			libraryLogger.info(String.format("Method %s throwed %s ", longName, t.getLocalizedMessage()));
			throw t;
		} finally {
			libraryLogger.info(String.format("Method %s finished", longName));
		}

	}
	
	
	
}
