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
package com.github.jjYBdx4IL.utils.solr.beans;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//CHECKSTYLE:OFF
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldConfig {

    boolean unique() default false;

    /**
     * required for retrieving.
     * 
     * @return true if the document should be stored for later retrieval
     */
    boolean stored() default true;

    boolean required() default false;

    /**
     * required for searching.
     * 
     * @return true if search should be supported for this field
     */
    boolean indexed() default false;

    boolean multiValued() default false;

    FieldType type() default FieldType.string;
}