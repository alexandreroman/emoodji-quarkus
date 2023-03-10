/*
 * Copyright (c) 2023 VMware, Inc. or its affiliates
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

package com.vmware.tanzu.demos.emoodji.quarkus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/v1/emoodji")
public class EmoodjiRestResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmoodjiRestResource.class);
    private final EmoodjiService emoodjiService;

    public EmoodjiRestResource(EmoodjiService emoodjiService) {
        this.emoodjiService = emoodjiService;
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String current() {
        final String emoodji = emoodjiService.getCurrent();
        LOGGER.info("Get current emoodji: {}", emoodji);
        return emoodji;
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String switchToNext() {
        final String emoodji = emoodjiService.switchToNext();
        LOGGER.info("Set new emoodji: {}", emoodji);
        return emoodji;
    }
}
