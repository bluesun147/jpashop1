package jpabook.jpashop.service;

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

    // 조회
    // 얘는 @Transactional 없으니까 readOnly 적용.
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
