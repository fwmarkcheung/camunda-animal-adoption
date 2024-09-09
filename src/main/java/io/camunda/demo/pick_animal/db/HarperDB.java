
package io.camunda.demo.pick_animal.db;

import io.harperdb.core.Server;
import io.harperdb.core.ServerBuilder;
import io.harperdb.core.Template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HarperDB {

    private final static Logger LOG = LoggerFactory.getLogger(HarperDB.class);

    @Value("${harperDB.uri:http://localhost:9925}")
    private String serverUri;

    @Value("${harperDB.password:password}")
    private String password;

    @Value("${harperDB.username:root}")
    private String username;

    @Bean
    public Template template() {

        LOG.debug("serverUri: {}", serverUri);
        LOG.debug("username: {}", username);
        LOG.debug("password: {}", password);

        final Server server = ServerBuilder.of(serverUri)
                .withCredentials(username, password);
        server.createDatabase("animals_app");
        server.createTable("userchoice").id("id").database("animals_app");
        return server.template("animals_app");
    }
}
