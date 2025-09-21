package dev.tpcoder.coupon;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationYamlTest {

    private static List<EnumerablePropertySource<?>> sources;

    @BeforeAll
    static void loadYaml() throws IOException {
        sources = new ArrayList<>();
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        boolean loadedAny = false;
        for (String name : new String[] { "application.yml", "application.yaml" }) {
            Resource resource = new ClassPathResource(name);
            if (resource.exists()) {
                List<PropertySource<?>> loaded = loader.load(name, resource);
                for (PropertySource<?> ps : loaded) {
                    if (ps instanceof EnumerablePropertySource) {
                        sources.add((EnumerablePropertySource<?>) ps);
                    }
                }
                loadedAny = true;
            }
        }
        if (!loadedAny) {
            fail("application.yml or application.yaml was not found on the test classpath.");
        }
    }

    private Object get(String key) {
        for (EnumerablePropertySource<?> s : sources) {
            Object v = s.getProperty(key);
            if (v != null) return v;
        }
        return null;
    }

    private boolean asBoolean(Object v) {
        if (v instanceof Boolean) return (Boolean) v;
        if (v instanceof CharSequence) return Boolean.parseBoolean(v.toString());
        return false;
    }

    @Test
    @DisplayName("server.shutdown is graceful")
    void serverShutdownIsGraceful() {
        assertEquals("graceful", get("server.shutdown"));
    }

    @Test
    @DisplayName("spring.application.name is coupon")
    void applicationNameIsCoupon() {
        assertEquals("coupon", get("spring.application.name"));
    }

    @Test
    @DisplayName("Datasource properties are set (url, username, password)")
    void datasourcePropertiesPresent() {
        assertEquals("jdbc:postgresql://localhost:5432/coupon", get("spring.datasource.url"));
        assertEquals("myuser", get("spring.datasource.username"));
        assertEquals("secret", get("spring.datasource.password"));
    }

    @Test
    @DisplayName("Docker Compose integration enabled")
    void dockerComposeEnabled() {
        assertTrue(asBoolean(get("spring.docker.compose.enabled")));
    }

    @Test
    @DisplayName("Virtual threads enabled")
    void virtualThreadsEnabled() {
        assertTrue(asBoolean(get("spring.threads.virtual.enabled")));
    }

    @Test
    @DisplayName("SQL init mode is always (dev only)")
    void sqlInitModeAlways() {
        assertEquals("always", get("spring.sql.init.mode"));
    }

    @Test
    @DisplayName("MVC Problem Details enabled")
    void mvcProblemDetailsEnabled() {
        assertTrue(asBoolean(get("spring.mvc.problemdetails.enabled")));
    }

    @Test
    @DisplayName("Management endpoints exposure includes health, metrics, prometheus")
    void managementEndpointsExposureInclude() {
        assertEquals("health,metrics,prometheus", get("management.endpoints.web.exposure.include"));
    }

    @Test
    @DisplayName("Logging level for dev.tpcoder.coupon is debug")
    void loggingLevelForPackage() {
        assertEquals("debug", get("logging.level.dev.tpcoder.coupon"));
    }

    @Test
    @DisplayName("A non-existent property is absent")
    void nonexistentPropertyIsNull() {
        assertNull(get("this.property.does.not.exist"));
    }
}