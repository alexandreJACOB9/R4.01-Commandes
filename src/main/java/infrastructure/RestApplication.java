package infrastructure;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configure le point d'entree principal de l'API REST.
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
}