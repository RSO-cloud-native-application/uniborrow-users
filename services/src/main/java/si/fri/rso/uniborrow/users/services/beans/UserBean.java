package si.fri.rso.uniborrow.users.services.beans;

import si.fri.rso.uniborrow.users.lib.User;
import si.fri.rso.uniborrow.users.models.converters.UserConverter;
import si.fri.rso.uniborrow.users.models.entities.UserEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
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

    public User getUser(Integer id) {
        UserEntity userEntity = em.find(UserEntity.class, id);
        if (userEntity == null) {
            throw new NotFoundException();
        }
        return UserConverter.toDto(userEntity);
    }

    public User createUser(User user) {
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
