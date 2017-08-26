/*
 * Copyright © 2017 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.utils.vmmgmt;

//CHECKSTYLE:OFF
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 *
 * @author jjYBdx4IL
 */
public class VMInstanceProviderRule implements TestRule {

    private VMInstanceProvider provider = null;

    public VMInstanceProviderRule() {
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try (VMInstanceProvider instanceProvider = new VMInstanceProvider()) {
                    setProvider(instanceProvider);
                    base.evaluate();
                }
            }
        };
    }

    /**
     * @return the provider
     */
    public VMInstanceProvider getProvider() {
        return provider;
    }

    /**
     * @param provider the provider to set
     */
    private void setProvider(VMInstanceProvider provider) {
        this.provider = provider;
    }

}
