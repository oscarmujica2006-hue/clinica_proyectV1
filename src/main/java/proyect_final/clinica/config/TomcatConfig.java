package proyect_final.clinica.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            factory.addConnectorCustomizers(connector -> {
                // Estas son las configuraciones que realmente funcionan
                connector.setProperty("maxParameterCount", "-1");
                connector.setProperty("maxPostSize", "-1");
                connector.setProperty("maxSwallowSize", "-1");
                connector.setProperty("maxHttpHeaderSize", "32768");
            });
        };
    }
}

// descomentar control k + u