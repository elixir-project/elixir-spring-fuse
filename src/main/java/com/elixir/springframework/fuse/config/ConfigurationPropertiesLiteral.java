package com.elixir.springframework.fuse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.enterprise.util.AnnotationLiteral;


/**
 * Created by elixir on 3/5/16.
 */
public class ConfigurationPropertiesLiteral extends AnnotationLiteral<ConfigurationProperties> implements ConfigurationProperties {

    private String value;

    private String prefix;

    private boolean ignoreInvalidFields;

    private boolean ignoreNestedProperties;

    private boolean ignoreUnknownFields = true;

    private boolean exceptionIfInvalid = true;

    private String[] locations;

    private boolean merge;

    @Override
    public String value() {
        return value;
    }

    @Override
    public String prefix() {
        return prefix;
    }

    @Override
    public boolean ignoreInvalidFields() {
        return ignoreInvalidFields;
    }

    @Override
    public boolean ignoreNestedProperties() {
        return ignoreNestedProperties;
    }

    @Override
    public boolean ignoreUnknownFields() {
        return ignoreUnknownFields;
    }

    @Override
    public boolean exceptionIfInvalid() {
        return exceptionIfInvalid;
    }

    @Override
    public String[] locations() {
        return locations;
    }

    @Override
    public boolean merge() {
        return merge;
    }
    //


    public void setValue(String value) {
        this.value = value;
    }

    public void setMerge(boolean merge) {
        this.merge = merge;
    }

    public void setLocations(String[] locations) {
        this.locations = locations;
    }

    public void setExceptionIfInvalid(boolean exceptionIfInvalid) {
        this.exceptionIfInvalid = exceptionIfInvalid;
    }

    public void setIgnoreUnknownFields(boolean ignoreUnknownFields) {
        this.ignoreUnknownFields = ignoreUnknownFields;
    }

    public void setIgnoreNestedProperties(boolean ignoreNestedProperties) {
        this.ignoreNestedProperties = ignoreNestedProperties;
    }

    public void setIgnoreInvalidFields(boolean ignoreInvalidFields) {
        this.ignoreInvalidFields = ignoreInvalidFields;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
