/*
 * Copyright © 2019 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.remoterobot.dto;

import java.io.Serializable;

public class ShutdownRequest extends Request implements Serializable {

    private static final long serialVersionUID = 1L;

    public int keycode;

    public ShutdownRequest() {
    }

    /**
     * Create an instance.
     * 
     * @param seq
     *            the request sequence id
     * @return the instance
     */
    public static ShutdownRequest create(long seq) {
        ShutdownRequest request = new ShutdownRequest();
        request.seq = seq;
        return request;
    }

}
