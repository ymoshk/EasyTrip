package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

//@Entity
public abstract class Model implements Serializable {
    private final static long serialVersionUID = 1L;

//    //TODO - לבדוק איך ננהל את ה id - אולי לא צריך את ה GENERATED
//    @Id
//    @GeneratedValue(strategy= GenerationType.AUTO)
//    private Long id;

}
