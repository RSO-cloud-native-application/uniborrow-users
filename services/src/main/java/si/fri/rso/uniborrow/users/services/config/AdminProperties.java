package si.fri.rso.uniborrow.users.services.config;


import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ConfigBundle("admin-properties")
@ApplicationScoped
public class AdminProperties {

    @ConfigValue(watch = true)
    private Boolean restrictUsers;

    @ConfigValue(watch = true)
    private Boolean disableUsers;

    @ConfigValue(watch = true)
    private Boolean broken;

    public Boolean getRestrictUsers() {
        return restrictUsers;
    }

    public void setRestrictUsers(Boolean restrictUsers) {
        this.restrictUsers = restrictUsers;
    }

    public Boolean getDisableUsers() {
        return disableUsers;
    }

    public void setDisableUsers(Boolean disableUsers) {
        this.disableUsers = disableUsers;
    }

    public Boolean getBroken() {
        return broken;
    }

    public void setBroken(Boolean broken) {
        this.broken = broken;
    }
}
