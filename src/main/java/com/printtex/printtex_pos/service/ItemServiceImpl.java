package com.printtex.printtex_pos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.printtex.printtex_pos.controller.LoginController;
import com.printtex.printtex_pos.model.*;
import com.printtex.printtex_pos.repository.CategoryRepository;
import com.printtex.printtex_pos.repository.ItemRepository;
import com.printtex.printtex_pos.repository.ItemSaleBranchRepository;
import com.printtex.printtex_pos.repository.SaleRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final EventLogService eventLogService;
    private final LoginController loginController;
    private final ItemSaleBranchRepository itemSaleBranchRepository;
    private final SaleRepository saleRepository;
    private final CategoryRepository categoryRepository;

    public ItemServiceImpl(ItemRepository itemRepository, EventLogService eventLogService, LoginController loginController, ItemSaleBranchRepository itemSaleBranchRepository, SaleRepository saleRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.eventLogService = eventLogService;
        this.loginController = loginController;
        this.itemSaleBranchRepository = itemSaleBranchRepository;
        this.saleRepository = saleRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void addItem(Item item, HttpServletRequest request) {
//        if(item.getBranchId() == null){
//            User user = loginController.getAuthenticatedUser();
//            item.setBranchId(user.getBranch().getId());
//        }
        itemRepository.save(item);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String userNewJson = mapper.writeValueAsString(item);
            eventLogService.saveInsertLog(userNewJson, Item.class, String.valueOf(item.getItemId()), request);
        } catch (JsonProcessingException e) {

        }
    }

    @Override
    public List<Item> getAllItem() {
        User user = loginController.getAuthenticatedUser();
        return itemRepository.findAllByStatusAndBranchIdOrderByItemNameAsc(1, user.getBranch().getId());
    }

    @Override
    public List<Item> getAllItemAdmin() {
        return itemRepository.findAllByStatusOrderByItemNameAsc(1);
    }

    @Override
    public Item findItemByItemId(int id) {
        return itemRepository.findItemByItemId(id);
    }

    @Override
    public void deleteItem(int itemId, HttpServletRequest request) {
        Item item = itemRepository.findItemByItemId(itemId);
        ObjectMapper mapper = new ObjectMapper();
        String oldJson = null;
        try {
            oldJson = mapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (item != null) {
            item.setStatus(0);
            itemRepository.save(item);
        }
        eventLogService.saveDeleteLog(oldJson, Item.class, String.valueOf(item.getItemId()), request);

    }

    @Override
    public List<Item> getAllCategoryExceptUpdateItem(int itemId) {
        return itemRepository.findAllByWithoutUpdateItem(itemId);
    }

    @Override
    public List<Item> getAllItemByCategoryId(int categoryId) {
        User user = loginController.getAuthenticatedUser();
        if (!user.getRole().equals("1")) {
            Category category = categoryRepository.findCategoryByCategoryId(categoryId);
            return itemRepository.findAllByStatusAndBranchIdAndCategory(1, user.getBranch().getId(), category);
        }
        return itemRepository.getAllItemByCategoryId(categoryId);
    }

    @Override
    public List<Item> getAllItemByBranchId(Long branchId) {
        return itemRepository.getAllItemByBranchId(branchId);
    }

    @Override
    public List<ItemDto> getAllItemByBranch(Long branchId) {
        List<ItemDto> itemList = new ArrayList<>();
        List<ItemSaleBranch> itemSaleBranches = itemSaleBranchRepository.findAllByBranchIdOrderByItemIdAsc(branchId);
        Set<Integer> itemIds = new HashSet<>();
        itemSaleBranches.forEach(itemSaleBranch -> {
            itemIds.add(itemSaleBranch.getItemId());
        });
        itemIds.forEach(itemId -> {
            List<ItemSaleBranch> itemSaleBranchList = itemSaleBranchRepository.findAllByBranchIdAndItemId(branchId, itemId);
            final Integer[] salesAmount = {0};
            itemSaleBranchList.forEach(itemSaleBranch -> {
                salesAmount[0] += itemSaleBranch.getSalesAmount();
            });
            final int[] totalSaleItemQuantity = {0};
            List<Sale> saleList = saleRepository.getAllSaleByItemId(itemId);
            saleList.forEach(sale -> {
                totalSaleItemQuantity[0] += sale.getSaleAmount();
            });
            Item item = itemRepository.findItemByItemId(itemId);
            ItemDto itemDto = new ItemDto();
            itemDto.setCategoryName(item.getCategory().getCategoryName());
            itemDto.setItemRemainingAmount(item.getItemAmount());
            itemDto.setItemSoldAmount(salesAmount[0]);
            itemDto.setItemAmount(item.getItemAmount() + totalSaleItemQuantity[0]);
            itemList.add(itemDto);
        });
        return itemList;
    }

}
