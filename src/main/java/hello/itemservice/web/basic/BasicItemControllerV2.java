package hello.itemservice.web.basic;

import hello.itemservice.domain.Item.DeliveryCode;
import hello.itemservice.domain.Item.Item;
import hello.itemservice.domain.Item.ItemRepository;
import hello.itemservice.domain.Item.ItemType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Slf4j
@Controller
@RequestMapping("/form/v2/items")
@RequiredArgsConstructor
public class BasicItemControllerV2 {
    private final ItemRepository itemRepository;

    @ModelAttribute("regions")
    public static Map<String, String> regions(){
        Map<String, String> regions = new LinkedHashMap<>();

        regions.put("SEOUL", "서울");
        regions.put("BUSAN", "부산");
        regions.put("JEJU", "제주");

        return regions;
    }
    @ModelAttribute("itemTypes")
    public ItemType[] itemTypes(){
        return ItemType.values();
    }

    @ModelAttribute("deliveryCodes")
    public List<DeliveryCode> deliveryCodes(){
        List<DeliveryCode> deliveryCodes = new ArrayList<>();
        deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
        deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
        deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));
        return deliveryCodes;
    }

    @GetMapping
    public String items(Model model){
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "form/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model){
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "form/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model){
        model.addAttribute("item", new Item());
        return "form/v2/addForm";
    }
    //@PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                           @RequestParam Integer price,
                           @RequestParam Integer quantity,
                           Model model){
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);
        model.addAttribute("item", item);

        return "form/v2/item";
    }
    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item){
        itemRepository.save(item);
        //model.addAttribute("item", item); 자동추가 생략가능

        return "form/v2/item";
    }
   // @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item){
        itemRepository.save(item);
        //model.addAttribute("item", item); 자동추가 생략가능

        return "form/v2/item";
    }
    //@PostMapping("/add")
    public String addItemV4(Item item){
        itemRepository.save(item);
        //model.addAttribute("item", item); 자동추가 생략가능

        return "form/v2/item";
    }
    //@PostMapping("/add")
    public String addItemV5(Item item){
        itemRepository.save(item);
        //model.addAttribute("item", item); 자동추가 생략가능

        return "redirect:/form/v2/items/"+item.getId();
    }
    @PostMapping("/add")
    public String addItemV6(@ModelAttribute Item item, RedirectAttributes redirectAttributes, Model model){
        //검증 오류 결과를 보관
        Map<String, String> errors = new HashMap<>();

        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            errors.put("itemName", "상품 이름은 필수입니다.");
        }
        if(item.getPrice() == null || item.getPrice()<1000 || item.getPrice() >1000000){
            errors.put("price", "가격은 1,000~1,000,000까지 허용합니다.");
        }
        if(item.getQuantity()==null || item.getQuantity()>=9999){
            errors.put("quantity","수량은 최대 9,999까지 허용합니다.");
        }

        //특정 필드가 아닌 복합룰 검증
        if(item.getPrice() != null && item.getQuantity()!= null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                errors.put("globalError", "가격*수량의 합은 10,000원 이상이어야합니다. 현재 값 = "+resultPrice);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(!errors.isEmpty()){
            log.info("errors = {}", errors);
            model.addAttribute("errors", errors);
            return "form/v2/addform";
        }

        Item savedItem = itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        //model.addAttribute("item", item); 자동추가 생략가능

        return "redirect:/form/v1/items/{itemId}";
    }


    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model){
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "form/v2/editForm";
    }
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item){
        itemRepository.update(itemId,item);
        return "redirect:/form/v2/items/{itemId}";
    }


    /**
     *  테스트용 데이터 추가
     */
    @PostConstruct
    public void init(){
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }
}
