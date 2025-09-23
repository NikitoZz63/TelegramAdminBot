package spring.admintgbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.admintgbot.entity.ViolatorEntity;

import java.util.Optional;

public interface ViolatorRepository extends JpaRepository<ViolatorEntity, Long> {

    Optional<ViolatorEntity> findByUserId(Long userId);

    @Query(value = """
            WITH upsert AS (
                INSERT INTO tg_admin_bot.violators (telegram_user_id, username, first_name, violation_counter)
                VALUES (:userId, :userName, :firstName, 1)
                ON CONFLICT (telegram_user_id) DO UPDATE
                    SET violation_counter = tg_admin_bot.violators.violation_counter + 1
                RETURNING violation_counter
            )
            SELECT violation_counter FROM upsert
            """, nativeQuery = true)
    Integer upsertAndReturnCounter(@Param("userId") long userId,
                                   @Param("userName") String userName,
                                   @Param("firstName") String firstName);

}
