package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController // Controller와 ResponseBody 합친것. ResponseBody는 템플릿 아니고 데이터 자체(json등) 바로 리턴하는.
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    // 회원 조회
    // 가장 단순, 안좋은 버전 (엔티티 직접 노출하지 말자!)
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() { // 리스트로 바로 넘기면 확장 안좋음. json 형태로 넘기는게 좋다.
        return memberService.findMembers();
    }


    @GetMapping("/api/v2/members")
    public Result membersV2() { // 응답값 껍데기 클래스 Result
        List<Member> findMembers = memberService.findMembers(); // 가져와서 memberDto로 변환

        // 멤버 엔티티에서 이름 꺼내와서 dto로 넣고 변환
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());// 리스트로 변환

        // -> list Member를 list MemberDto로 변환

        return new Result(collect);
        // return new Result(collect.size(), collect); // count 넣는 경우
    }

    @Data
    @AllArgsConstructor
    // 오브젝트 타입으로 반환하기 때문에 Result라는 껍데기 씌워 줌.
    // 이런식으로 한번 감싸줘야 한다. 리스트를 바로 내면 json 배열 타입으로 나가버리기 때문에 유연성 떨어짐.
    static class Result<T> { // 제네릭
        // private int count; // 이런식으로 바로 넣으면 됨.
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto { // 이름만 반환하는 api 만든다고 했을 때
        private String name;
    }

    // 회원 등록
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


