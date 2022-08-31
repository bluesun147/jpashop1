package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em; // 스프링이 em 만들어서 주입(인젝션)해줌.

    public void save(Member member) {
        em.persist(member); // 이거 하면 jpa가 얘를 저장함.
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id); // jpa가 제공하는 메서드 (타입, pk)
    }

    public List<Member> findAll() {
        List<Member> result = em.createQuery("select m from Member m", Member.class).getResultList();// jpql, 반환 타입

        return result;
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class).setParameter("name", name) // 파라미터 바인딩
                .getResultList();
    }
}
