package si.fri.uniborrow.users.api.v1.resources;

import si.fri.rso.uniborrow.users.lib.User;
import si.fri.rso.uniborrow.users.services.beans.UserBean;
import si.fri.rso.uniborrow.users.services.config.AdminProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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

    @Inject
    private AdminProperties adminProperties;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getUsers() {
        List<User> users = userBean.getUsers();
        return Response.status(200).entity(users).build();
    }

    @GET
    @Path("/{userId}")
    public Response getUser(@PathParam("userId") Integer userId) {
        User user = userBean.getUser(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(user).build();
    }

    @POST
    public Response createUser(User user) {
        if (adminProperties.getRestrictUsers()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (user == null || user.getEmail() == null || user.getFirstName() == null || user.getLastName() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        User createdUser = userBean.createUser(user);
        if (createdUser == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.CREATED).entity(createdUser).build();
    }

    @PUT
    @Path("{userId}")
    public Response updateUser(User user, @PathParam("userId") Integer userId) {
        if (adminProperties.getRestrictUsers()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        user = userBean.putUser(user, userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.NOT_MODIFIED).build();
    }

    @DELETE
    @Path("{userId}")
    public Response deleteUser(@PathParam("userId") Integer userId) {
        if (adminProperties.getRestrictUsers()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        boolean isSuccessful = userBean.deleteUser(userId);
        if (isSuccessful) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
