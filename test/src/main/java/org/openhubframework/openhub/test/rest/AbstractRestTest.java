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

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.List;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import org.openhubframework.openhub.test.AbstractDbTest;
import org.openhubframework.openhub.test.TestConfig;


/**
 * Parent class for REST web layer tests.
 * It is used to test REST behavior with fully backend services with database and Camel.
 * <p>
 * If you want to test REST services on mock backend services (without database) then define your own unit test
 * with using {@link TestRestConfig} that defines basic MVC configuration.
 * Example:
 * <pre class="code">
 *    RunWith(SpringRunner.class)
 *    SpringBootTest(classes = TestRestConfig.class)
 *    WebAppConfiguration
 *    public class BookmarkRestControllerTest {
 * </pre>
 * See blog post <a href="https://spring.io/guides/tutorials/bookmarks/">
 *     Building REST services with Spring</a> for more details.
 *
 * @author Petr Juza
 * @since 2.0
 */
@SpringBootTest(classes = {TestConfig.class, TestRestConfig.class})
public abstract class AbstractRestTest extends AbstractDbTest {

    public static final String TEST_USERNAME = "testUser";

    public static final String TEST_PASSWORD = "testPass";

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    /**
     * Sets authenticated user with username '{@value #TEST_USERNAME}' and password '{@value #TEST_PASSWORD}'.
     */
    @Before
    public void prepareAuthentication() {
        TestingAuthenticationToken auth = mockAuthentication("USER", "PRODUCTS");

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * Creates authentication token with required profiles (granted authorities).
     *
     * @param profiles which represents modules and granted authorities
     * @return new instance of {@link TestingAuthenticationToken}
     */
    public static TestingAuthenticationToken mockAuthentication(String... profiles) {
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(profiles);

        UserDetails testUser = new User(TEST_USERNAME, TEST_PASSWORD, authorityList);

        return new TestingAuthenticationToken(testUser, TEST_PASSWORD, authorityList);
    }
}
