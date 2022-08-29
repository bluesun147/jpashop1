package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "delivery_id") // 연관관계 주인
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // ORDINAL은 인덱스, but 중간에 다른 거 들어가면 다 바뀌기 때문에 절대 쓰지 말기
    private DeliveryStatus status; // READY, COMP
}
