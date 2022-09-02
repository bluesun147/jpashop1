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
}
