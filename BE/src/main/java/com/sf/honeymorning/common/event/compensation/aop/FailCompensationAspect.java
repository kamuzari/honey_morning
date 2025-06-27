package com.sf.honeymorning.common.event.compensation.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.sf.honeymorning.common.event.compensation.compensator.CompensationFactory;
import com.sf.honeymorning.common.event.compensation.model.FailEventTyper;

import jakarta.validation.ValidationException;

@Order
@Aspect
@Component
public class FailCompensationAspect {

	private static final Logger logger = LoggerFactory.getLogger(FailCompensationAspect.class);
	private final CompensationFactory compensationFactory;

	public FailCompensationAspect(CompensationFactory compensationFactory) {
		this.compensationFactory = compensationFactory;
	}

	@Around("@annotation(failCompensation)")
	public Object handleFailCompensation(ProceedingJoinPoint joinPoint, FailCompensation failCompensation) throws
		Throwable {
		try {
			return joinPoint.proceed();
		} catch (ValidationException ex) {
			throw ex;
		} catch (Exception ex) {
			logger.error("Exception caught in FailCompensation aspect: {}", ex.getMessage(), joinPoint.getArgs()[0]);
			FailEventTyper failEventTyper = (FailEventTyper)joinPoint.getArgs()[0];
			compensationFactory.compensate(failEventTyper);
			throw ex;
		}
	}
}
