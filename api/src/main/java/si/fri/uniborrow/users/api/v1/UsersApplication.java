package si.fri.uniborrow.users.api.v1;

import com.kumuluz.ee.discovery.annotations.RegisterService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@RegisterService(value = "uniborrow-users-service", environment = "dev", version = "1.0.0")
@ApplicationPath("/v1")
public class UsersApplication extends Application {
}
