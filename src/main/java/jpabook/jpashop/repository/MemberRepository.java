package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 인터페이스
// 스프링 데이터 jpa!!
// typeorm 처럼 save, find 다 있음
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이렇게만 하면 끝!!
    // 스프링 data jpa 가 알아서 다 만들어 줌.
    // select m from member m where m.name = ? (jpql)
    List<Member> findByName(String name);

}
