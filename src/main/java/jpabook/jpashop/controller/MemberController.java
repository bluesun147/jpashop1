package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new") // 폼 화면 열기
    public String createForm(Model model) {

        model.addAttribute("memberForm", new MemberForm()); // 컨트롤러에서 뷰로 넘어갈 때 이 데이터를 실어서 넘김
        return "members/createMemberForm";
    }

    @PostMapping("/members/new") // 폼 화면 등록\
    // @Valid: NotEmpty등 validate 해줌
    // form에 오류있으면 원래 튕겨버림(코드 실행x), 근데 valid 다음에 bindingResult 있으면 오류가 result에 담겨서 코드 실행됨.
    public String create(@Valid MemberForm form, BindingResult result) { // '회원 등록' 강의 - 왜 Member 엔티티 그대로 아니라 MemberForm로 받는지 -> 화면에 맞추는게 좋다

        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode()); // 폼에서 값 꺼냄

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member); // 멤버 저장
        return "redirect:/"; // 첫페이지로 리다이렉트
    }

    /*
    강조> 폼 객체를 써야 하냐, 엔티티를 직접 써야 하냐?
    요구사항 매우 단순하면 엔티티 바로 써도 됨.
    but 실무에서는 일대일 매칭되는 단순한 경우 거의 없음.
    엔티티를 폼으로 써버리면 엔티티가 지저분해짐. -> 유지보수 어려워 짐.
    엔티티 최대한 순수하게 유지해야 함. 엔티티는 핵심 비지니스 로직만 갖고 있어야 함. 화면 로직은 x
    화면에 맞는 api는 폼 객체나 dto 사용해야 함.
     */

    @GetMapping("/members")
    public String list(Model model) { // model 객체 통해서 화면에 데이터 전달

        /* 원래는 여기서도 Member 엔티티 아니라 dto로 변환해서 화면에 꼭 필요한 데이터만 출력하는것 권장
        서버 안에서는 큰 상관 없지만, (템플릿 엔진 사용)
        api 만들때는 이유불문 절대 엔티티 넘기면 안됨. 엔티티 외부로 반환 x */
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members); // 화면에 넘김
        return "members/memberList";
    }
}
