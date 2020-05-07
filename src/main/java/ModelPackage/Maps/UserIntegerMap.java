package ModelPackage.Maps;

import ModelPackage.Users.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Data
@Entity @Table(name = "t_user_int_map")
public class UserIntegerMap {
    @Setter(AccessLevel.NONE)
    @Id @GeneratedValue
    private int id;

    @OneToOne
    @JoinColumn(name = "USER")
    private User user;

    @Column(name = "INTEGER_VALUE")
    int integer;
}
