package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;

    // 원래 엔티티 직접 노출하면 안됨!! 예제용
    @GetMapping("api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            //
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    // 그래도 쿼리 너무 많이 나감, 성능 bad. 최적화 필요
    @GetMapping("api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        // orders를 OrderDto로 변환해야 함.
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o)) // 루프 돌리면서 dto로 변환
                .collect(Collectors.toList());
        return collect;
    }

    // fetch join 활용한 최적화
    @GetMapping("api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o)) // 루프 돌리면서 dto로 변환
                .collect(Collectors.toList());
        return collect;
    }

    @GetMapping("api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
    {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o)) // 루프 돌리면서 dto로 변환
                .collect(Collectors.toList());
        return collect;
    }

    @Getter
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        // private List<OrderItem> orderItems; // 얘를
        private List<OrderItemDto> orderItems; // dto로 바꿈

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
//            order.getOrderItems().stream().forEach(o -> o.getItem().getName()); // 프록시 초기화. 이거 안하면 null 나옴. 엔티티라서
//            // but 래핑도 안됨. 엔티티니까
//            // 엔티티에 대한 의존을 완전히 끊어야 함.
//            // orderItems 조차도 dto로 바꿔야 함.
//
//            orderItems = order.getOrderItems();

            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }

        @Getter
        static class OrderItemDto {

            private String itemName;
            private int orderPrice;
            private int count;

            public OrderItemDto(OrderItem orderItem) {
                itemName = orderItem.getItem().getName();
                orderPrice = orderItem.getOrderPrice();
                count = orderItem.getCount();
            }
        }
    }
}