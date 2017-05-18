/*
 *  Copyright 2017 the original author or authors.
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

package org.openhubframework.openhub.admin.web.console.rpc;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import org.openhubframework.openhub.common.OpenHubPropertyConstants;


/**
 * Console configuration holder.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
@Configuration
// actually this object is autodetected via AOP and holds configuration, so we have to serializable only our properties
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        setterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
@ConfigurationProperties(ConsoleConfigRpc.PREFIX)
public class ConsoleConfigRpc {

    /**
     * Prefix of properties names.
     */
    public static final String PREFIX = OpenHubPropertyConstants.PREFIX + "admin.console";
    
    private Config config;

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * Configuration of console.
     */
    public static class Config {

        private Menu menu;

        public Menu getMenu() {
            return menu;
        }

        public void setMenu(Menu menu) {
            this.menu = menu;
        }

        /**
         * Configuration of menu.
         */
        public static class Menu {

            private Analytics analytics;
            private Infrastructure infrastructure;
            private Cluster cluster;
            private Configuration configuration;
            private ExternalLinksRpc externalLinks;
            private MenuItemRpc changes;

            public Analytics getAnalytics() {
                return analytics;
            }

            public void setAnalytics(Analytics analytics) {
                this.analytics = analytics;
            }

            public Infrastructure getInfrastructure() {
                return infrastructure;
            }

            public void setInfrastructure(Infrastructure infrastructure) {
                this.infrastructure = infrastructure;
            }

            public Cluster getCluster() {
                return cluster;
            }

            public void setCluster(Cluster cluster) {
                this.cluster = cluster;
            }

            public Configuration getConfiguration() {
                return configuration;
            }

            public void setConfiguration(Configuration configuration) {
                this.configuration = configuration;
            }

            public ExternalLinksRpc getExternalLinks() {
                return externalLinks;
            }

            public void setExternalLinks(ExternalLinksRpc externalLinks) {
                this.externalLinks = externalLinks;
            }

            public MenuItemRpc getChanges() {
                return changes;
            }

            public void setChanges(MenuItemRpc changes) {
                this.changes = changes;
            }

            /**
             * Analytics menu configuration.
             */
            public static class Analytics extends MenuNodeRpc {
                private MenuItemRpc messages;

                public MenuItemRpc getMessages() {
                    return messages;
                }

                public void setMessages(MenuItemRpc messages) {
                    this.messages = messages;
                }
            }

            /**
             * Infrastructure menu configuration.
             */
            public static class Infrastructure extends MenuNodeRpc {
                private Services services;

                public Services getServices() {
                    return services;
                }

                public void setServices(Services services) {
                    this.services = services;
                }

                /**
                 * Services menu configuration.
                 */
                public static class Services extends MenuNodeRpc {
                    private MenuItemRpc wsdl;

                    public MenuItemRpc getWsdl() {
                        return wsdl;
                    }

                    public void setWsdl(MenuItemRpc wsdl) {
                        this.wsdl = wsdl;
                    }
                }
            }

            /**
             * Cluster menu configuration.
             */
            public static class Cluster extends MenuNodeRpc {
                private MenuItemRpc nodes;

                public MenuItemRpc getNodes() {
                    return nodes;
                }

                public void setNodes(MenuItemRpc nodes) {
                    this.nodes = nodes;
                }
            }

            /**
             * Configuration section of menu.
             */
            public static class Configuration extends MenuNodeRpc {
                
                private MenuItemRpc systemParams;
                private MenuItemRpc logging;
                private MenuItemRpc environment;
                private MenuItemRpc errorCodeCatalog;

                public MenuItemRpc getSystemParams() {
                    return systemParams;
                }

                public void setSystemParams(MenuItemRpc systemParams) {
                    this.systemParams = systemParams;
                }

                public MenuItemRpc getLogging() {
                    return logging;
                }

                public void setLogging(MenuItemRpc logging) {
                    this.logging = logging;
                }

                public MenuItemRpc getEnvironment() {
                    return environment;
                }

                public void setEnvironment(MenuItemRpc environment) {
                    this.environment = environment;
                }

                public MenuItemRpc getErrorCodeCatalog() {
                    return errorCodeCatalog;
                }

                public void setErrorCodeCatalog(MenuItemRpc errorCodeCatalog) {
                    this.errorCodeCatalog = errorCodeCatalog;
                }
            }
        }

    }
}
