package com.elixir.springframework.fuse.persistence;

import java.lang.annotation.*;

/**
 * Created by elixir on 3/7/16.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Persistence {

    PersistenceUnit[] persistenceUnit() default {} ;

}
