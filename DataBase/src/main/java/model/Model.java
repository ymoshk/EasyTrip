package model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@MappedSuperclass
public abstract class Model implements Serializable {
    private final static long serialVersionUID = 1L;
    private final static int UPDATE_EXP_PERIOD_IN_DAYS = 14;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static boolean isCollectionUpdated(List<? extends Model> modelCollection) {
        return modelCollection.stream()
                .noneMatch(model -> model.getUpdateTime().isBefore(LocalDateTime.now().minusDays(UPDATE_EXP_PERIOD_IN_DAYS)));
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public long getId() {
        return this.id;
    }
}
