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

public class MousePressRequest extends Request implements Serializable {

    private static final long serialVersionUID = 1L;

    public int buttons;

    public MousePressRequest() {
    }

    /**
     * Create an instance.
     * 
     * @param seq
     *            the request sequence id
     * @param buttons
     *            the mouse buttons to press
     * @return the instance
     */
    public static MousePressRequest create(long seq, int buttons) {
        MousePressRequest request = new MousePressRequest();
        request.seq = seq;
        request.buttons = buttons;
        return request;
    }

}
