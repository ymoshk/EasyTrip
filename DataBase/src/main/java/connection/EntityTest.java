package connection;


import model.Model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "test")
public class EntityTest extends Model implements Serializable {

    @Column(name = "name", nullable = true)
    private String name = "S";

    @Column(name = "age", nullable = true)
    private int age = 3;

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = 10L;



    public EntityTest(String jonson, int i) {
        super();
        this.age = i;
        this.name = jonson;
    }

    public EntityTest() {

    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

}
