package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 데이터 변경 못하고 읽기만.
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 주문
    @Transactional // 이거 있어야 데이터 변경 가능
    public Long order(Long memberId, Long itemId, int count) {

        // 엔티티 조회
        Member member = memberRepository.findOne(memberId); // 해당 멤버 찾기
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress()); // 회원 정보에 있는 주소로 배송함

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count); // 생성 메서드 만들어 둔 것 사용
        /*
        // 누군가가 이런식으로 값 하나씩 채워넣는 식으로 개발했다면
        // 여기선 이거 쓰고 저기선 다른거 쓰고.. 유지보수 어려움. 막아야 함.
        // -> OrderItem에 protected 생성자 만들면 됨.
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setOrder();
        */

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        // 하나만 저장해줘도 orderItem, delivery 자동으로 persist 됨. -> Order에 CascadeType.All 했기 때문
        // cascade 범위 고민 -> orderItem과 delivery는 Order에서만 참조됨.
        // 다른데서도 많이 쓰인다면 cascade 하면 안됨.
        // 개념 잘 모르겠으면 아예 안쓰는것이 좋음.
        orderRepository.save(order);

        return order.getId();
    }

    // 주문 취소
    @Transactional // 데이터 변경해야 하기 때문
    public void cancelOrder(Long orderId) { // 취소 버튼 누르면 order 아이디 값만 넘어옴
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel();
    }

    // 검색
  /*  public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAll(orderSearch);
    }*/
}
