package org.openhubframework.openhub.core.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openhubframework.openhub.common.OpenHubPropertyConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for JPA.
 *
 * @author Karel Kovarik
 * @since 2.1
 */
@Component
@ConfigurationProperties(prefix = OpenHubPropertyConstants.PREFIX + "jpa")
public class JpaConfigurationProperties {

    /**
     * Transaction manager for JPA enabled.
     *
     * Note: used in ConditionalOnProperty annotation directly, not via this object.
     */
    static final String TRANSACTION_MANAGER_ENABLED = OpenHubPropertyConstants.PREFIX + "jpa.transaction-manager.enabled";

    /**
     * Additional packages to be scanned for JPA entities.
     */
    private List<String> additionalPackages = new ArrayList<>();

    public List<String> getAdditionalPackages() {
        return additionalPackages;
    }

    public void setAdditionalPackages(final List<String> additionalPackages) {
        this.additionalPackages = additionalPackages;
    }

    @Override public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("additionalPackages", additionalPackages)
                .toString()
                ;
    }
}
