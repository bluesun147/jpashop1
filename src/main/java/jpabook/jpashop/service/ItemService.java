package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    // 저장
    @Transactional // 이거 없으면 저장 안됨. 위에서 readOnly true로 했기 때문. 오버라이딩 함.
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional // 세터 없이 엔티티안에서 바로 추적할 수 있는 메서드 만들자
    public Item updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId); // id기반으로 실제 db에 있는 영속 상태 엔티티 찾아옴.

        // findItem.change(name, price, stockQuantity); // 이렇게라도 만드는게 낫다. 이 안에서 값 변경하도록. -> change만 보면 어디서 변경하는지 다 알 수 있음
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity); // 이렇게 풀어 놓으면 추적하기 어려움
        return findItem;
    }

//    @Transactional // 이게 더 나은 코드.
//    // 파라미터 너무 많으면 dto 사용
//    public void updateItem(Long itemId, UpdateItemDto itemDto) {
//        ...
//    }

    // 조회
    // 얘는 @Transactional 없으니까 readOnly 적용.
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
