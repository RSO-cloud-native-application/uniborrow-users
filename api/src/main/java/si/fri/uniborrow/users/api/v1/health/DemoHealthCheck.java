package si.fri.uniborrow.users.api.v1.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import si.fri.rso.uniborrow.users.services.config.AdminProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Liveness
@ApplicationScoped
public class DemoHealthCheck implements HealthCheck {

    @Inject
    private AdminProperties adminProperties;

    @Override
    public HealthCheckResponse call() {
        return adminProperties.getBroken() ?
                HealthCheckResponse.down(DemoHealthCheck.class.getSimpleName()) :
                HealthCheckResponse.up(DemoHealthCheck.class.getSimpleName());
    }
}
