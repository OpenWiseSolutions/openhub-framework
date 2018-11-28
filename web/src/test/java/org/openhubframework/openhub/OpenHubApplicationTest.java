package org.openhubframework.openhub;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhubframework.openhub.common.Profiles;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Simple test to check spring context is configured properly.
 *
 * @author Karel Kovarik
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = OpenHubApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({Profiles.H2})
public class OpenHubApplicationTest {

    @Test
    public void context_loads() {
        // nothing in here, fails if spring context does not start at all.
    }
}
