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

package org.openhubframework.openhub.test.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObjectBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MvcResult;


/**
 * {@code TestRestUtils} is a collection of REST-based utility
 * methods for use in unit and integration testing scenarios.
 *
 * @author Petr Juza
 * @since 2.0
 */
public final class TestRestUtils {

    public static final String UTF8 = "UTF-8";

    final static Boolean ONE_BASED_PAGINATION = Boolean.TRUE;

    final static int MAX_PAGE_SIZE = 100;

    final static String PAGE_PARAMETER_NAME = "p";

    final static String SIZE_PARAMETER_NAME = "s";

    // to avoid instantiate
    private TestRestUtils() {
    }

    /**
     * Formats the {@code GET} query based upon configuration.
     * <p/>
     * <pre>{@code
     *  createPageQuery(1,20) &rarr; p=1&s=20
     * }</pre>
     *
     * @param page the page number
     * @param size the size of page
     * @return the query
     */
    public static String createPageQuery(int page, int size) {
        return PAGE_PARAMETER_NAME + "=" + page + "&" + SIZE_PARAMETER_NAME + "=" + size;
    }

    /**
     * Creates the {@code GET} pageable query based upon configuration.
     * <p/>
     * <pre>{@code
     *  createPagePair(1,20) &rarr; p=1&s=20
     * }</pre>
     *
     * @param page the page number
     * @param size the size of page
     * @return the list of {@link NameValuePair}
     */
    public static List<NameValuePair> createPagePair(int page, int size) {
        List<NameValuePair> pageableQuery = new LinkedList<>();
        pageableQuery.add(new BasicNameValuePair(PAGE_PARAMETER_NAME, "" + page));
        pageableQuery.add(new BasicNameValuePair(SIZE_PARAMETER_NAME, "" + size));
        return pageableQuery;
    }

    /**
     * Creates the {@code GET} sortable query based upon configuration. With this method it is necessary to use
     * URLDecoder#decode(String, String) to decode URL or use {@link #toUrl(URIBuilder)}.
     *
     * <p/>
     * <pre>{@code
     *  createSortPair(new Sort(new Sort.Order(Sort.Direction.DESC, "dateFrom"))) &rarr; sort=name&name,dir=desc
     * }</pre>
     *
     * @param sort the sort instrument
     * @return the list of {@link NameValuePair}
     * @see URLDecoder#decode(String, String)
     * @see #toUrl(URIBuilder)
     */
    public static List<NameValuePair> createSortPair(Sort sort) {
        List<NameValuePair> sortableQuery = new LinkedList<>();
        for (Sort.Order order : sort) {
            sortableQuery.add(new BasicNameValuePair("sort", order.getProperty().concat(",")
                .concat(order.getDirection().toString().toLowerCase())));
        }
        return sortableQuery;
    }

    /**
     * Creates the {@code GET} query for local date based upon configuration.
     * <p/>
     * <pre>{@code
     *  createDateQuery("dateFrom", LocalDate.now()) &rarr; dateFrom=2015-03-09
     * }</pre>
     *
     * @param paramName the name of attribute/parameter
     * @param date      the local date value
     * @return the list of {@link NameValuePair} that represents query parameter for date represented
     *  by {@code paramName} attribute
     */
    public static List<NameValuePair> createDateQuery(String paramName, LocalDate date) {
        List<NameValuePair> localDateQuery = new LinkedList<>();
        localDateQuery.add(new BasicNameValuePair(paramName, date.toString()));
        return localDateQuery;
    }

    /**
     * Creates {@link Pageable} object based upon configuration.
     *
     * @param page the number of page
     * @param size the size of page
     * @return the {@link Pageable}
     */
    public static Pageable createPage(int page, int size) {
        return new PageRequest(ONE_BASED_PAGINATION ? page - 1 : page, size);
    }

    /**
     * Creates {@link JsonObjectBuilder} to easy creation {@code JSON} objects and serialization into string.
     * <pre>{@code
     * final String content = createJson()
     *                          .add("p", 1)
     *                          .add("size", 20)
     *                          .add("fulltext", "value")
     *                              .build().toString();
     * }</pre>
     * @return the {@link JsonObjectBuilder}
     */
    public static JsonObjectBuilder createJson() {
        return Json.createObjectBuilder();
    }

    /**
     * Construct an instance from the REST path which must be a valid URI.
     * <p/>
     * <pre>{@code
     * final String context = createGetUrl("/rpc/orders")
     *      .addParameters(createPagePair(1, 20))
     *      .addParameter("fulltext", "value")
     *          .build().toString();}</pre>
     * </pre>
     * Result: /rpc/orders?p=1&s=20&fulltext=value&createdFrom=2015-06-20
     *
     * @param path a valid REST URI path in string form
     */
    public static URIBuilder createGetUrl(String path) {
        URIBuilder b;
        try {
            b = new URIBuilder("");
            b.setPath(path);
            return b;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates URL string based upon {@link URIBuilder} which is URL decoded in {@link #UTF8} character encoding.
     *
     * @param builder the URI builder
     * @return the URL decoded URI as plain string
     * @throws Exception occurs if URI is wrong
     * @see URLDecoder#decode(String, String)
     * @see #UTF8
     */
    public static String toUrl(URIBuilder builder) throws Exception {
        return URLDecoder.decode(builder.build().toString(), UTF8);
    }

    /**
     * Method to deserialize JSON content from given JSON content String.
     *
     * @param mockResult as result of mock that contains response as JSON string
     * @param valueType  as type of object that represents JSON string
     * @param <T>        JSON object type
     * @return the object that represents JSON content
     * @see #jsonObject(String, Class)
     */
    public static <T> T jsonObject(MvcResult mockResult, Class<T> valueType) {
        try {

            return jsonObject(mockResult.getResponse().getContentAsString(), valueType);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to deserialize JSON content from given JSON content String.
     *
     * @param content   as JSON string
     * @param valueType as type of object that represents JSON string
     * @param <T>       JSON object type
     * @return the object that represents JSON content
     * @see #jsonObject(MvcResult, Class)
     */
    public static <T> T jsonObject(String content, Class<T> valueType) {
        try {

            return new ObjectMapper().readValue(content, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
