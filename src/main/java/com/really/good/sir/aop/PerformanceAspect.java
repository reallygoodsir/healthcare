package com.really.good.sir.aop;

import com.really.good.sir.dao.AuditDAO;
import com.really.good.sir.entity.AuditEntity;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {

    private final AuditDAO auditDAO;

    public PerformanceAspect(AuditDAO auditDAO) {
        this.auditDAO = auditDAO;
    }

    @Around("@annotation(com.really.good.sir.aop.Audit)")
    public Object measure(ProceedingJoinPoint pjp) throws Throwable {

        long start = System.nanoTime();

        try {
            return pjp.proceed();
        } finally {
            long end = System.nanoTime();
            long totalTimeMs = (end - start) / 1_000_000;

            AuditEntity auditEntity = new AuditEntity();
            auditEntity.setMethodName(pjp.getSignature().getName());
            auditEntity.setTotalTime(totalTimeMs);

            auditDAO.createAudit(auditEntity);

            System.out.println(
                    "[AUDIT] " + pjp.getSignature().getName() + " -> " + totalTimeMs + " ms"
            );
        }
    }
}