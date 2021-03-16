package connection;


import model.Model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "test")
public class EntityTest extends Model implements Serializable {



    @Column(name = "name", nullable = true)
    private String name = "S";

    @Column(name = "age")
    private int age = 3;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }



}
