package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    // 엔티티 아니라 dto 반환
    // but 성능 문제. 쿼리 너무 많이 나감. N+1 -> 1 + 회원 N + 배송 N
    // 쿼리 5번 나옴. 로그 확인
    // lazy 로딩 갯수만큼 반복해야 함.
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() { // 원래는 list로 반환하면 안됨. result 껍데기 객체로 감싸야 함.
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    // fetch join 사용 (findAllWithMemberDelivery 확인)
    // fetch join 완벽히 알아두기!! 실무에서 매우 자주 쓰임
    // 쿼리 길게 하나 나옴, 로그 확인
    @GetMapping("api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // lazy 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // lazy 초기화
        }
    }
}