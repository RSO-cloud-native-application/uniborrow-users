package si.fri.rso.uniborrow.users.services.beans;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import si.fri.rso.uniborrow.users.lib.User;
import si.fri.rso.uniborrow.users.models.converters.UserConverter;
import si.fri.rso.uniborrow.users.models.entities.UserEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class UserBean {

    private final Logger log = Logger.getLogger(UserBean.class.getName());

    @Inject
    private EntityManager em;

    @CircuitBreaker
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "getUsersFallback")
    public List<User> getUsers() {
        TypedQuery<UserEntity> query =
                em.createNamedQuery("UserEntity.getAll", UserEntity.class);
        List<UserEntity> resultList = query.getResultList();
        return resultList.stream().map(UserConverter::toDto).collect(Collectors.toList());
    }

    public List<User> getUsersFallback() {
        return new ArrayList<>();
    }

    @CircuitBreaker
    @Fallback(fallbackMethod = "getUserFallback")
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    public User getUser(Integer id) {
        UserEntity userEntity = em.find(UserEntity.class, id);
        if (userEntity == null) {
            throw new NotFoundException();
        }
        return UserConverter.toDto(userEntity);
    }

    public User getUserFallback(Integer id) {
        return null;
    }

    public User getUser(String username) {
        TypedQuery<UserEntity> query =
                em.createNamedQuery("UserEntity.getByUsername", UserEntity.class)
                        .setParameter("username", username);
        List<UserEntity> userEntities = query.getResultList();
        return userEntities.isEmpty()
                ? null
                : UserConverter.toDto(userEntities.get(0));
    }

    public User createUser(User user) throws Exception {
        if (getUser(user.getUsername()) != null) {
            throw new Exception("User exists");
        }
        UserEntity userEntity = UserConverter.toEntity(user);
        try {
            beginTransaction();
            em.persist(userEntity);
            commitTransaction();
        } catch (Exception e) {
            log.warning(e.getMessage());
            rollbackTransaction();
        }

        if (userEntity.getId() == null) {
            log.warning("Failed to create a user!");
            return null;
        }
        return UserConverter.toDto(userEntity);
    }

    public User putUser(User user, Integer id) {
        UserEntity userEntity = em.find(UserEntity.class, id);
        if (userEntity == null) {
            return null;
        }

        UserEntity updatedUserEntity = UserConverter.toEntity(user);
        try {
            beginTransaction();
            updatedUserEntity.setId(userEntity.getId());
            updatedUserEntity = em.merge(updatedUserEntity);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            log.warning(e.getMessage());
            return null;
        }
        return UserConverter.toDto(updatedUserEntity);
    }

    public User patchUser(User user, Integer id) {
        UserEntity userEntity = em.find(UserEntity.class, id);
        if (userEntity == null) {
            return null;
        }

        UserEntity updatedUserEntity = UserConverter.toEntity(user);
        try {
            beginTransaction();
            if (updatedUserEntity.getEmail() == null) {
                updatedUserEntity.setEmail(userEntity.getEmail());
            }
            if (updatedUserEntity.getFirstName() == null) {
                updatedUserEntity.setFirstName(userEntity.getFirstName());
            }
            if (updatedUserEntity.getLastName() == null) {
                updatedUserEntity.setLastName(userEntity.getFirstName());
            }
            if (updatedUserEntity.getUsername() == null) {
                updatedUserEntity.setUsername(userEntity.getUsername());
            }
            updatedUserEntity.setId(userEntity.getId());
            updatedUserEntity = em.merge(updatedUserEntity);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            log.warning(e.getMessage());
            return null;
        }
        return UserConverter.toDto(updatedUserEntity);
    }

    public boolean deleteUser(Integer id) {
        UserEntity userEntity = em.find(UserEntity.class, id);
        if (userEntity == null) {
            return false;
        }
        try {
            beginTransaction();
            em.remove(userEntity);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            log.warning(e.getMessage());
            return false;
        }
        return true;
    }

    private void beginTransaction() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTransaction() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private  void rollbackTransaction() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
