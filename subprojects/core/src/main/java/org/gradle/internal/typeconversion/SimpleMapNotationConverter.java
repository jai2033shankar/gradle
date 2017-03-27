/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.internal.typeconversion;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.tasks.Optional;
import org.gradle.internal.UncheckedException;
import org.gradle.internal.exceptions.DiagnosticsVisitor;
import org.gradle.internal.reflect.ReflectionCache;
import org.gradle.util.ConfigureUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Convenience base class for type converters that convert from a Map.
 */
public abstract class SimpleMapNotationConverter<T> extends TypedNotationConverter<Map, T> {
    public SimpleMapNotationConverter() {
        super(Map.class);
    }

    @Override
    public void describe(DiagnosticsVisitor visitor) {
        visitor.candidate("Maps");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected final T parseType(Map notation) {
        return parseMap((Map<String, ?>) notation);
    }

    protected abstract T parseMap(Map<String, ?> notation);

    protected String getString(Map<String, ?> args, String key) {
        Object value = args.get(key);
        String str = value == null ? null : value.toString();
        return Strings.isNullOrEmpty(str) ? null : str;
    }

    protected void checkMandatoryKeys(Map<String, ?> args, String... mandatoryKeys) {
        HashSet<String> missing = Sets.newHashSet(mandatoryKeys);
        missing.removeAll(args.keySet());
        if (!missing.isEmpty()) {
            throw new InvalidUserDataException(String.format("Required keys %s are missing from map %s.", missing, args));
        }
    }
}
