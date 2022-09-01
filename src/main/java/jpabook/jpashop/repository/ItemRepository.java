package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) { // id가 없으면 저장
            em.persist(item);
        } else { // 있으면
            em.merge(item); // 업데이트랑 비슷..
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        // 하나 찾을 때는 find 쓰지만 여러개 찾을 때는 jpql 쿼리 작성해야 함.
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }
}
