package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // protected Order() {}
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 모든 연관관계 lazy로 할것, eager로 하면 모두 다 불러옴
    @JoinColumn(name = "member_id") // 매핑을 뭘로 할건지. fk이름이 member_id가 됨.
    private Member member; // 얘를 연관관계 주인. fk쪽을 주인으로.

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // order만 persist 하면 밑에것들도(컬렉션에 들어와있는 orderItem) 다 한꺼번에.
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // order가 persist 될 때 delivery 엔티티도 persist 해줌.
    @JoinColumn(name = "delivery_id") // 연관관계 주인
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL]

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

    // 생성 메서드
    public static Order createOrder(Member member, Delivery delivery,OrderItem... orderItems) { // ... 은 가변인자(varargs)
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    // == 비지니스 로직 ==
    // 주문 취소
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) { // 배송이 이미 완료되었다면 취소 불가능
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다.");
        }
        // 통과하면 (배송 완료 안된상태)
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) { // 강화 for문
            orderItem.cancel();
        }
    }

    // == 조회 로직 ==
    // 전체 주문 가격 조회
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;

        // 같은 결과. 자바8 스트림..
        // return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }
}
