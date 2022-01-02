package si.fri.uniborrow.users.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.metrics.annotation.Metered;
import si.fri.rso.uniborrow.users.lib.User;
import si.fri.rso.uniborrow.users.lib.UserLogin;
import si.fri.rso.uniborrow.users.services.beans.UserBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Log
@ApplicationScoped
@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {

    private final Logger log = Logger.getLogger(LoginResource.class.getSimpleName());

    @Inject
    private UserBean userBean;

    @POST
    @Metered(name = "num_user_logins")
    public Response loginUser(UserLogin userLogin) {
        if (userLogin.getUsername() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        User loggedInUser = userBean.getUser(userLogin.getUsername());
        return loggedInUser != null
                ? Response.status(Response.Status.OK).entity(loggedInUser).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }
}
