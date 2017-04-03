/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.web.common;

import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;


/**
 * Jackson custom configuration.
 *
 * @author <a href="mailto:tomas.hanus@openwise.cz">Tomas Hanus</a>
 * @since 2.0
 */
@Configuration
public class JacksonConfiguration {

    /**
     * Exposes specific {@link HttpMessageConverter} for {@literal Jackson} as bean.
     */
    @Bean
    public HttpMessageConverter jackson() {
        // add jackson with our object mapper
        MappingJackson2HttpMessageConverter jackson = new MappingJackson2HttpMessageConverter();
        jackson.setObjectMapper(objectMapper().getObject());

        // Jackson used default UTC timezone. We need to use server one.
        // note: we use default zone, same as in org.openhubframework.openhub.api.common.jaxb.JaxbDateAdapter
        jackson.getObjectMapper().setTimeZone(TimeZone.getDefault());

        return jackson;
    }

    /**
     * Exposes Jackson2ObjectMapperFactoryBean as bean.
     */
    @Bean
    public Jackson2ObjectMapperFactoryBean objectMapper() {
        // this could be configured also via spring.jackson properties / JacksonProperties, but this is more flexible
        Jackson2ObjectMapperFactoryBean res = new Jackson2ObjectMapperFactoryBean();
        res.afterPropertiesSet();

        res.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // unknown ENUM values are rejected by exception
        res.setFeaturesToDisable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

        // to force API consumer to use ISO date time
        //  note: must correspond with org.openhubframework.openhub.api.common.jaxb.JaxbDateAdapter
        res.setFeaturesToDisable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        res.setFeaturesToDisable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);

        res.setFeaturesToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return res;
    }
}
