package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    // 사용자 수정
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2( // 업데이트 용 응답 dto
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) { // 업데이트 용 request dto

        // 수정시에는 가급적이면 변경 감지 쓰기
        memberService.update(id, request.getName()); // 커맨드와 쿼리를 분리 - 유지보수성 증대. 특별히 트래픽 많지 않음.
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
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

    @Data
    static class UpdateMemberRequest { // 업데이트 용 request dto
        private String name;
    }

    @Data
    @AllArgsConstructor // 클래스에 존재하는 모든 필드에 대해 생성자를 자동 생성
    static class UpdateMemberResponse { // 업데이트 용 응답 dto
        private Long id;
        private String name;
    }

}
