package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.jni.Address;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String uname;

    @Embedded // 내장 타입이다
    private Address address;

    // mappedBy 있으면 매핑 하는애가 아니라 저거에 의해 매핑된 거울이라는 뜻
    @OneToMany(mappedBy = "member") // Order 테이블에 있는 member 필드에 의해 매핑 됨.
    private List<Order> orders = new ArrayList<>(); // 연관관계 거울, 주인 아님

}
