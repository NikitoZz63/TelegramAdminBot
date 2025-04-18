package DAO;

import entity.UserEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class UserDAO {

    private static final Session session = new Configuration().configure().buildSessionFactory().openSession();

    public static void saveUser(UserEntity user) {
        Transaction transaction = session.beginTransaction();
        session.persist(user);
        transaction.commit();
    }

    public static UserEntity getUserByTelegramUserId(Long userId) {
        return session.createQuery("FROM UserEntity WHERE userId = :userId", UserEntity.class)
                .setParameter("userId", userId)
                .uniqueResult();
    }

    public static List<UserEntity> getAllUsers() {
        return session.createQuery("FROM UserEntity", UserEntity.class).list();
    }

    public static Integer getViolationCounter(Long userId) {
        return session.createQuery(
                        "SELECT violationCounter FROM UserEntity WHERE userId = :userId", Integer.class)
                .setParameter("userId", userId)
                .uniqueResult();
    }


}
