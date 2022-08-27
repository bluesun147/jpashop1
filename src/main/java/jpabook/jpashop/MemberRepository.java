package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext // 엔티티 매니저 사용 위해.
    private EntityManager em;

    // 저장
    public Long save(Member member) {
        em.persist(member); // 저장
        return member.getId();
    }

    // 조회
    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
