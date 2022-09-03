package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B") // 저장 시 구분. 안쓰면 Book 들어감
@Getter @Setter
public class Book extends Item {

    private String author;
    private String isbn;
}
