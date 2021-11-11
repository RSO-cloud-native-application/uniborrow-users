package si.fri.rso.uniborrow.users.services.beans;

import si.fri.rso.uniborrow.users.lib.User;
import si.fri.rso.uniborrow.users.models.converters.UserConverter;
import si.fri.rso.uniborrow.users.models.entities.UserEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class UserBean {

    private final Logger log = Logger.getLogger(UserBean.class.getName());

    @Inject
    private EntityManager em;

    public List<User> getUsers() {
        TypedQuery<UserEntity> query =
                em.createNamedQuery("UserEntity.getAll", UserEntity.class);
        List<UserEntity> resultList = query.getResultList();
        return resultList.stream().map(UserConverter::toDto).collect(Collectors.toList());
    }
}
