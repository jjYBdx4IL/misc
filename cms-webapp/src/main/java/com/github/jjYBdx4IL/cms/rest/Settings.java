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
package com.github.jjYBdx4IL.cms.rest;

import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.form;
import static j2html.TagCreator.input;
import static j2html.TagCreator.span;
import static j2html.TagCreator.textarea;

import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigKey;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigValue;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import com.github.jjYBdx4IL.cms.rest.app.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

//CHECKSTYLE:OFF
@Path("settings")
@RolesAllowed(Role.ADMIN)
@Transactional
public class Settings {

    private static final Logger LOG = LoggerFactory.getLogger(Settings.class);

    @Inject
    HtmlBuilder htmlBuilder;
    @Inject
    QueryFactory qf;
    @Context
    UriInfo uriInfo;
    @PersistenceContext
    EntityManager em;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get() throws SQLException {
        LOG.trace("get()");

        Map<ConfigKey, String> values = qf.getAllConfigValuesAsMap();
        List<ConfigKey> keys = Arrays.asList(ConfigKey.values());
        Collections.sort(keys, new Comparator<ConfigKey>() {

            @Override
            public int compare(ConfigKey o1, ConfigKey o2) {
                return o1.name().compareToIgnoreCase(o2.name());
            }
        });

        htmlBuilder.mainAdd(
            div(
                each(keys, key -> div(
                    form().withMethod("post").attr("accept-charset", "utf-8").with(
                        span(key.name() + ": ").withClass("col-12"),
                        input().withType("hidden").withName("key").withValue(key.name()),
                        textarea().withName("value").isRequired()
                            .withText(values.get(key) != null ? values.get(key) : "").withClass("col-9"),
                        input().withType("submit").withName("submitButton").withValue("save").withClass("col-2")
                    ).withClass("editForm")
                ).withClass("row")
                )
            ).withClass("container settings")
        );

        return Response.ok(htmlBuilder.toString()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response post(@FormParam("key") String key, @FormParam("value") String value) {
        LOG.trace("post()");

        if (key == null || key.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("empty key").build();
        }

        ConfigKey configKey;
        try {
            configKey = ConfigKey.valueOf(key);
        } catch (IllegalArgumentException ex) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("invalid key").build();
        }

        if (value == null) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("null value").build();
        }
        
        ConfigValue configValue = em.find(ConfigValue.class, configKey);
        if (configValue == null) {
            configValue = new ConfigValue(configKey, value);
        } else {
            configValue.setValue(value);
        }
        em.persist(configValue);
        
        return Response.temporaryRedirect(uriInfo.getAbsolutePathBuilder().build())
            .status(HttpServletResponse.SC_FOUND)
            .build();
    }

}
