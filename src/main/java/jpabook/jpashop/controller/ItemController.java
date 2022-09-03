package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // 상품 등록
    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    // 상품 등록
    @PostMapping("/items/new")
    public String create(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    // 상품 목록
    @GetMapping("items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items); // 모델에 담고 쭉 뿌리기
        return "items/itemList";
    }

    // 상품 수정
    @GetMapping("items/{itemId}/edit") // path variable
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit") // path variable
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) {

//        Book book = new Book(); // 준영속 엔티티. jpa가 관리 안함 -> 변경 감지 안함. 값 바꿔도 디비에 업데이트 안됨.
//        book.setIsbn(form.getIsbn());
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//
//        itemService.saveItem(book);

        // 어설프게 엔티티를 파라미터로 안쓰고 내가 필요한 데이터만 딱딱 받기
        // 유지보수 훨씬 좋음. 정확히 매칭.
        // 서비스 계층에 dto 만들어도 됨.
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());

        return "redirect:/items"; // 수정하고 나서 다시 리스트로.
    }
}
