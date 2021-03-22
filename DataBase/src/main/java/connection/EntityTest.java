package connection;


import model.Model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

//TODO - delete this class.

@Entity
@Table(name = "Test")
public class EntityTest extends Model implements Serializable {

    private String name;
    private int age;

    public EntityTest() {

    }

    public EntityTest(String jonson, int i) {
        super();
        this.age = i;
        this.name = jonson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "EntityTest{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
