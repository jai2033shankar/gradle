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

package org.gradle.plugin.devel.tasks

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.integtests.fixtures.LocalBuildCacheFixture
import org.gradle.test.fixtures.file.TestFile

class CachedCustomPluginIntegrationTest extends AbstractIntegrationSpec implements LocalBuildCacheFixture {

    def setup() {
        file("buildSrc/settings.gradle") << localCacheConfiguration()
    }

    def "custom task is cached when java-gradle-plugin is used in buildSrc"() {
        file("buildSrc/src/main/groovy/CustomTask.groovy") << customGroovyTask()
        file("buildSrc/src/main/groovy/CustomPlugin.groovy") << """
            import org.gradle.api.*

            class CustomPlugin implements Plugin<Project> {
                @Override
                void apply(Project project) {
                    project.tasks.create("customTask", CustomTask) {
                        inputFile = project.file("input.txt")
                        outputFile = project.file("build/output.txt")
                    }
                }
            }
        """
        file("buildSrc/src/main/resources/META-INF/gradle-plugins/org.example.plugin.properties") << "implementation-class=CustomPlugin"
        file("buildSrc/build.gradle") << """
            apply plugin: "java-gradle-plugin"
        """
        file("input.txt") << "input"
        buildFile << """
            apply plugin: "org.example.plugin"
        """
        when:
        withBuildCache().succeeds "customTask"
        then:
        skippedTasks.empty

        when:
        file("buildSrc/build").deleteDir()
        file("buildSrc/.gradle").deleteDir()
        cleanBuildDir()

        withBuildCache().succeeds "customTask"
        then:
        skippedTasks.contains ":customTask"
    }

    private TestFile cleanBuildDir() {
        file("build").assertIsDir().deleteDir()
    }

    private static String customGroovyTask(String suffix = "") {
        """
            import org.gradle.api.*
            import org.gradle.api.tasks.*

            @CacheableTask
            class CustomTask extends DefaultTask {
                @InputFile File inputFile
                @OutputFile File outputFile
                @TaskAction void doSomething() {
                    outputFile.text = inputFile.text + "$suffix"
                }
            }
        """
    }
}
