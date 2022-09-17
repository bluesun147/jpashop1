package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    // 주문 저장
    public void save(Order order) {
        em.persist(order);
    }

    // 단건 조회
    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // 검색
//     public List<Order> findAll(OrderSearch orderSearch) {
//        em.createQuery("select o from Order o join o.member m" + // 오더를 조회하고 멤버와 조인. jpql
//                        " where o.status = :status " +
//                        "and m.name like :name", Order.class)
//                        .setParameter("status", orderSearch.getOrderStatus()) // 파라미터 바인딩
//                        .setParameter("name", orderSearch.getMemberName()) // 파라미터 바인딩
//                        .setMaxResults(1000) // 최대 1000건
//                        .getResultList();
//     }

    // jpa가 제공하는 동적 퀴리 빌드해주는
    // jpql 자바 코드로 작성할 때 jpa에서 표준으로 제공
    // jap criteria. 별로 권장x.. 실무에서 안씀.

    public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        // 한번 쿼리로 오더, 멤버, 딜리버리 조인한 다음에 select 절에 다 넣고 한번에 다 땡겨옴.
        return em.createQuery(
                "select o from Order o" + // jpql
                " join fetch o.member m" +  // 지금 order 가져올 때 member 까지 객체 그래프로 한번에 가져옴 (join)
                " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    // 실무에선 대신 Querydsl로 처리. 훨씬 간단

}
