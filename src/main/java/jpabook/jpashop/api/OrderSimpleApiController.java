package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
xToOne(ManyToOne, OneToOne)의 관계 어떻게 최적화 할 지
Order
Order -> Member
Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders") // 엔티티 그대로 반환 -> bad!
    public List<Order> ordersV1() {
        // Order에서 Member로, Member에서 Order로.. 무한 루프
        // Order에서 Member있고, Member에서 Order있음. 양방향 연관 관계
        // -> 한쪽은 @JsonIgnore 해줘야 함.
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all) {
            // 강제로 lazy loading. 레이지 로딩은 order.getMember() 까지는 프록시 객체. db에 쿼리 아직 안날라감.
            order.getMember().getName(); // getName까지 하면 실제 이름 끌고 옴. lazy 강제 초기화. member에 쿼리 날려서 jpa가 데이터 다 끌고 옴.
            order.getDelivery().getAddress(); // lazy 강제 초기화
        }

        return all;
    }

}
