/*
 * Copyright 2009 the original author or authors.
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
package org.gradle.api.internal.notations;

import org.gradle.api.artifacts.ExternalDependency;
import org.gradle.api.internal.artifacts.dsl.dependencies.ModuleFactoryHelper;
import org.gradle.internal.exceptions.DiagnosticsVisitor;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.typeconversion.SimpleMapNotationConverter;

import java.util.Map;

public class DependencyMapNotationConverter<T extends ExternalDependency> extends SimpleMapNotationConverter<T> {

    private final Instantiator instantiator;
    private final Class<T> resultingType;

    public DependencyMapNotationConverter(Instantiator instantiator, Class<T> resultingType) {
        this.instantiator = instantiator;
        this.resultingType = resultingType;
    }

    @Override
    public void describe(DiagnosticsVisitor visitor) {
        visitor.candidate("Maps").example("[group: 'org.gradle', name: 'gradle-core', version: '1.0']");
    }

    @Override
    protected T parseMap(Map<String, ?> map) {
        T dependency = instantiator.newInstance(resultingType, getString(map, "group"), getString(map, "name"), getString(map, "version"), getString(map, "configuration"));
        ModuleFactoryHelper.addExplicitArtifactsIfDefined(dependency, getString(map, "ext"), getString(map, "classifier"));
        return dependency;
    }

}
