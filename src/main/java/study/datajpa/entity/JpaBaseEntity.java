package study.datajpa.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
public class JpaBaseEntity {

    //업데이트 되지 않음.
    @Column(updatable = false)
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @PrePersist  //최초 저장시에 발생하는 이벤트(최초 등록된 데이터)
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createDate = now;
        updateDate = now;
    }

    @PreUpdate // 업데이트시에 발생하는 이벤트
    public void preUpdate(){
        updateDate = LocalDateTime.now();
    }


}
