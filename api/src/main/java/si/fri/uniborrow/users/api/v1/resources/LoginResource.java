package si.fri.uniborrow.users.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
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
    @Operation(description = "Perform login for user.", summary = "Login user")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Sucessfully logged in",
                    content = @Content(schema = @Schema(implementation = User.class, type = SchemaType.OBJECT))
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Bad request"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "User not found."
            )
    })
    public Response loginUser(
            @RequestBody(
                    description = "DTO for user login.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserLogin.class))
            ) UserLogin userLogin) {
        if (userLogin.getUsername() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        User loggedInUser = userBean.getUser(userLogin.getUsername());
        return loggedInUser != null
                ? Response.status(Response.Status.OK).entity(loggedInUser).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }
}
