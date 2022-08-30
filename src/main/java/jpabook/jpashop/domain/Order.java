package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 모든 연관관계 lazy로 할것, eager로 하면 모두 다 불러옴
    @JoinColumn(name = "member_id") // 매핑을 뭘로 할건지. fk이름이 member_id가 됨.
    private Member member; // 얘를 연관관계 주인. fk쪽을 주인으로.

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // order만 persist 하면 밑에것들도 다 한꺼번에.
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id") // 연관관계 주인
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OderStatus status; // 주문 상태 [ORDER, CANCEL]

    // 연관관계 편의 메서드
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this); // 멤버 세팅 시 양방향 연관관계 알아서 해줌
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}
