package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController // Controller와 ResponseBody 합친것. ResponseBody는 템플릿 아니고 데이터 자체(json등) 바로 리턴하는.
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    // 인자로 엔티티 받지말자
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { // 회원 엔티티 받음
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) { // 별도의 dto 사용

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @Data // getter, setter 등등 다 합친것. lombok
    static class CreateMemberResponse { // 회원 생성 시 반환값
        private Long id;

        public CreateMemberResponse(Long id) { // 생성자
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest { // dto. api 만들때는 dto 쓰는게 정석
        private String name;
    }
}
