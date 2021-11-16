package si.fri.uniborrow.users.api.v1.filters;

import si.fri.rso.uniborrow.users.services.config.AdminProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;


@Provider
@ApplicationScoped
public class DisableFilter implements ContainerRequestFilter {

    @Inject
    private AdminProperties adminProperties;

    private static final Logger log = Logger.getLogger(DisableFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext ctx) {
        if (adminProperties.getDisableUsers()) {
            ctx.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .entity("{ \"message\": \"Users service is currently disabled\" }")
                    .build());
        }
    }
}
