package com.github.jonasrutishauser.cdi.test.jta;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.function.ThrowingSupplier;

import jakarta.enterprise.context.Dependent;
import jakarta.transaction.Transactional;

@Dependent
public class ExecutionService {

    @Transactional(Transactional.TxType.NEVER)
    public void executeTxNever(Executable runnable) throws Throwable {
        runnable.execute();
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public <T> T executeRequiresNew(ThrowingSupplier<T> supplier) throws Throwable {
        return supplier.get();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void executeRequired(Executable runnable) throws Throwable {
        runnable.execute();
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public void executeNotSupported(Executable runnable) throws Throwable {
        runnable.execute();
    }

}
