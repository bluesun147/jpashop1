package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional // jpa의 모든 데이터 변경이나 로직들은 가급적이면 transaction안에서 다 실행되야 함.
public class MemberService {

    @Autowired // 리포지토리 인젝션
    private final MemberRepository memberRepository; // 변경할 일 없기 때문에 final

    // 생성자 인젝션
    // @Autowired // 생략 가능
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    } // @RequiredArgsConstructor가 이거 다 만들어 줌.

    // 회원 가입
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll(); // spring data jpa에 다 만들어져 있음. repository. 찍으면 다 나옴.
    }

    public Member findOne(Long memberId) {
        // return memberRepository.findOne(memberId);
        return memberRepository.findById(memberId).get(); // spring data jpa. typeorm처럼 find 같은거 이미 다 만들어져 있음
    }

    @Transactional // 데이터 변할 일 있으면
    public void update(Long id, String name) {
//        Member member = memberRepository.findOne(id);
        Member member = memberRepository.findById(id).get();
        member.setName(name); // 변경 감지에 의해서
    }
}
