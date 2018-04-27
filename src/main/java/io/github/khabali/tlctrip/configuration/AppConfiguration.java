package io.github.khabali.tlctrip.configuration;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.core.api.config.Configuration;

@Configuration(prefix = "tlc.trip.api")
public interface AppConfiguration {

    @ConfigProperty(name = "config")
    String config();

}
