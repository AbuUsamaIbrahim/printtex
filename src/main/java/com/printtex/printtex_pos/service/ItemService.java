package com.printtex.printtex_pos.service;

import com.printtex.printtex_pos.model.Item;
import com.printtex.printtex_pos.model.ItemDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ItemService {
    void addItem(Item item, HttpServletRequest request);

    List<Item> getAllItem();

    List<Item> getAllItemAdmin();

    Item findItemByItemId(int id);

    void deleteItem(int itemId, HttpServletRequest request);

    List<Item> getAllCategoryExceptUpdateItem(int itemId);

    List<Item> getAllItemByCategoryId(int categoryId);

    List<Item> getAllItemByBranchId(Long branchId);

    List<ItemDto> getAllItemByBranch(Long branch_id);
}
