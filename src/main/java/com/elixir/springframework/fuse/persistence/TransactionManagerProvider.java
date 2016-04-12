package com.elixir.springframework.fuse.persistence;

import org.springframework.transaction.PlatformTransactionManager;

/**
 * Created by elixir on 3/11/16.
 */
public interface TransactionManagerProvider {
    public PlatformTransactionManager transactionManager();
}
