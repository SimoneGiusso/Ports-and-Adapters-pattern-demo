package org.simonegiusso.configurator;

import static java.util.stream.Collectors.toSet;
import static org.simonegiusso.utils.Technologies.CASSANDRA;
import static org.simonegiusso.utils.Technologies.POSTGRES;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;

@SpringBootApplication(scanBasePackages = {"org.simonegiusso.adapters", "org.simonegiusso.app"})
public class SpringStartUp implements EnvironmentPostProcessor, ApplicationListener<ApplicationEvent> {

    private static final DeferredLog log = new DeferredLog();

    private static final List<TechnologyAutoConfigurationMapping> AUTO_CONFIG_CLASSES = List.of(
        new TechnologyAutoConfigurationMapping(
            List.of(CASSANDRA),
            List.of(CassandraAutoConfiguration.class, CassandraReactiveRepositoriesAutoConfiguration.class,
                CassandraRepositoriesAutoConfiguration.class)),
        new TechnologyAutoConfigurationMapping(
            List.of(POSTGRES),
            List.of(DataSourceAutoConfiguration.class))
    );

    public static void main(String[] args) {
        SpringApplication.run(SpringStartUp.class, args);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        disableNotNeededAutoConfigurations(environment);
    }

    private static void disableNotNeededAutoConfigurations(ConfigurableEnvironment env) {
        var enabledTechnologies = getEnabledTechnologies(env);
        log.info("Enabled technologies: " + enabledTechnologies);

        var autoConfigurationsToExclude = computeAutoConfigurationsToExclude(enabledTechnologies);
        log.info("The following auto configurations will be excluded: " + autoConfigurationsToExclude);

        excludeAutoConfigurations(env, autoConfigurationsToExclude);
    }

    private static Set<String> getEnabledTechnologies(ConfigurableEnvironment env) {
        log.debug("Getting enabled technologies from application*.properties in: 'ports.<*>.<*>.source=<technology>'");
        return getAllProperties(env)
            .filter(propertyName -> propertyName.startsWith("ports.") && propertyName.endsWith(".source"))
            .map(env::getProperty)
            .collect(toSet());
    }

    private static Stream<String> getAllProperties(ConfigurableEnvironment env) {
        return env.getPropertySources().stream()
            .filter(MapPropertySource.class::isInstance)
            .map(MapPropertySource.class::cast)
            .map(MapPropertySource::getSource)
            .flatMap(properties -> properties.entrySet().stream())
            .map(Entry::getKey);
    }

    private static Set<String> computeAutoConfigurationsToExclude(Collection<String> enabledTechnologies) {
        return AUTO_CONFIG_CLASSES.stream()
            .filter(techAutoConfig -> techAutoConfig.hasNoneTechnologies(enabledTechnologies))
            .map(TechnologyAutoConfigurationMapping::autoConfigurations)
            .flatMap(List::stream)
            .map(Class::getName)
            .collect(toSet());
    }

    private static void excludeAutoConfigurations(ConfigurableEnvironment environment, Iterable<String> autoConfigsToExclude) {
        Properties props = new Properties();
        props.setProperty("spring.autoconfigure.exclude", String.join(",", autoConfigsToExclude));
        environment.getPropertySources().addFirst(new PropertiesPropertySource("autoConfigurationsToExclude", props));
    }

    // Logging system initialized only after the spring context is ready. Logs in postProcessEnvironment must be deferred until spring context is ready.
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.replayTo(ApplicationEvent.class);
    }

    private record TechnologyAutoConfigurationMapping(List<String> technologies, List<Class<?>> autoConfigurations) {

        boolean hasNoneTechnologies(Collection<String> technologies) {
            return this.technologies.stream().noneMatch(technologies::contains);
        }

    }
}
