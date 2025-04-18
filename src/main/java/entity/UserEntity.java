package entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "username", length = Integer.MAX_VALUE)
    private String userName;

    @Column(name = "first_name", nullable = false, length = Integer.MAX_VALUE)
    private String firstName;

    @ColumnDefault("0")
    @Column(name = "violation_counter")
    private Integer violationCounter;

    public UserEntity(String firstName, Long userId, String userName, Integer violationCounter) {
        this.firstName = firstName;
        this.userId = userId;
        this.userName = userName;
        this.violationCounter = violationCounter;
    }

    public UserEntity() {

    }


    public Long getUserid() {
        return userId;
    }

    public void setUserid(Long user_id) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Integer getViolationCounter() {
        return violationCounter;
    }

    public void setViolationCounter(Integer violationCounter) {
        this.violationCounter = violationCounter;
    }

}