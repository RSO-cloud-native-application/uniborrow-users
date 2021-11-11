package si.fri.uniborrow.users.api.v1.resources;

import si.fri.rso.uniborrow.users.lib.User;
import si.fri.rso.uniborrow.users.services.beans.UserBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsersResource {

    private final Logger log = Logger.getLogger(UsersResource.class.getName());

    @Inject
    private UserBean userBean;

    @GET
    public Response getUsers() {
        log.info("Got a get request!");
        List<User> users = userBean.getUsers();
        return Response.status(200).entity(users).build();
    }
}
