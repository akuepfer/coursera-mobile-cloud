package org.aku.sm.smclient.service;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Strategy for exclusion of entity attributes from JSON serialization.
 */
public class AttributeExclusionStrategy implements ExclusionStrategy {


    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getName().equals("symptomCheckinStatus")
                || f.getName().equals("checkingQuenstions")
                || f.getName().equals("symptomPhotoPath");
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}