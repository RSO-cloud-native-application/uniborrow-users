package si.fri.uniborrow.users.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.metrics.ConcurrentGauge;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
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

@Log
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

    @Inject
    @Metric(name = "users_counter")
    private ConcurrentGauge userCounter;

    @GET
    @Metered(name = "users_requests")
    @Operation(description = "Get all users.", summary = "Get all users")
    @APIResponses(
            @APIResponse(
                    responseCode = "200",
                    description = "List of users",
                    content = @Content(schema = @Schema(implementation = User.class, type = SchemaType.ARRAY))
            )
    )
    public Response getUsers() {
        List<User> users = userBean.getUsers();
        return Response.status(200).entity(users).build();
    }

    @GET
    @Path("/{userId}")
    @Metered(name = "user_requests")
    @Operation(description = "Get data for a user.", summary = "Get data for user")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "User data",
                    content = @Content(schema = @Schema(implementation = User.class, type = SchemaType.OBJECT))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    public Response getUser(
            @Parameter(description = "User ID.", required = true) @PathParam("userId") Integer userId) {
        User user = userBean.getUser(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(user).build();
    }

    @POST
    @Operation(description = "Add a user.", summary = "Add user")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "User successfully added.",
                    content = @Content(schema = @Schema(implementation = User.class, type = SchemaType.OBJECT))
            ),
            @APIResponse(
                    responseCode = "405",
                    description = "Method not allowed."
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Bad request."
            )
    })
    public Response createUser(
            @RequestBody(
                    description = "DTO with user data.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = User.class))
            ) User user) {
        userCounter.inc();
        if (adminProperties.getRestrictUsers()) {
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
        }

        if (user == null || user.getEmail() == null || user.getFirstName() == null
                || user.getLastName() == null || user.getUsername() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            User createdUser = userBean.createUser(user);
            if (createdUser == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            return Response.status(Response.Status.CREATED).entity(createdUser).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{ \"message\" : \"User already exists!\"}")
                    .build();
        }
    }

    @PUT
    @Path("{userId}")
    @Operation(description = "Update data for user.", summary = "Update user data")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "User data successfully updated."
            ),
            @APIResponse(
                    responseCode = "405",
                    description = "Method not allowed"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    public Response updateUser(
            @RequestBody(
                    description = "DTO object with user data.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = User.class))
            ) User user,
            @Parameter(description = "User ID.", required = true) @PathParam("userId") Integer userId) {
        if (adminProperties.getRestrictUsers()) {
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
        }

        user = userBean.putUser(user, userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @PATCH
    @Path("{userId}")
    @Operation(description = "Partially update user data.", summary = "Partially update user data")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "User data sucessfully updated."
            ),
            @APIResponse(
                    responseCode = "405",
                    description = "Method not allowed"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "User not found."
            )
    })
    public Response patchUser(
            @RequestBody(
                    description = "DTO with user data.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = User.class))
            ) User user,
            @Parameter(description = "User ID", required = true) @PathParam("userId") Integer userId) {
        if (adminProperties.getRestrictUsers()) {
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
        }
        user = userBean.patchUser(user, userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("{userId}")
    @Operation(description = "Delete a user.", summary = "Delete user")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Successfully delete a user."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "User not found."
            )
    })
    public Response deleteUser(
            @Parameter(description = "User ID.", required = true) @PathParam("userId") Integer userId) {
        userCounter.dec();
        if (adminProperties.getRestrictUsers()) {
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
        }

        boolean isSuccessful = userBean.deleteUser(userId);
        if (isSuccessful) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
