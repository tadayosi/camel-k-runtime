/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.k.loader.xml

import org.apache.camel.k.loader.xml.support.TestRuntime
import org.apache.camel.model.ToDefinition
import spock.lang.AutoCleanup
import spock.lang.Specification

class XmlSourceLoaderTest extends Specification {
    @AutoCleanup
    def runtime = new TestRuntime()

    def 'load routes'() {
        when:
            runtime.loadRoutes('classpath:routes.xml')
        then:
            with(runtime.context.routeDefinitions) {
                it[0].input.endpointUri ==~ /timer:.*tick/
                it[0].outputs[0] instanceof ToDefinition
            }

    }

    def 'load rests'() {
        when:
            runtime.loadRoutes('classpath:rests.xml')
        then:
            with(runtime.context.restDefinitions) {
                size() == 1
            }

    }
}
