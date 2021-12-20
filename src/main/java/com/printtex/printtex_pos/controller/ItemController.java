package com.printtex.printtex_pos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.printtex.printtex_pos.model.*;
import com.printtex.printtex_pos.repository.ItemRepository;
import com.printtex.printtex_pos.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ItemController {

    private final ItemService itemService;
    private final CategoryService categoryService;
    private final ModelMessageService modelMessageService;
    private final SaleService saleService;
    private final BranchService branchService;
    private final LoginController loginController;
    private final EventLogService eventLogService;
    private final ItemRepository itemRepository;

    public ItemController(ItemService itemService, CategoryService categoryService, ModelMessageService modelMessageService, SaleService saleService, BranchService branchService, LoginController loginController, EventLogService eventLogService, ItemRepository itemRepository) {
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.modelMessageService = modelMessageService;
        this.saleService = saleService;
        this.branchService = branchService;
        this.loginController = loginController;
        this.eventLogService = eventLogService;
        this.itemRepository = itemRepository;
    }

    @GetMapping({"/admin/stock/view", "/branch/stock/view"})
    public String stockView(Model model) {
        return "stockview";
    }

    @GetMapping(value = "/admin/additem")
    public String createItem(Model model) {
        modelMessageService.setEmptyResultMessage(model);
        return showItem(model);
    }

    @GetMapping(value = {"/admin/stock/by_branch_category", "/branch/stock/by_branch"})
    public String searchItemByBranchAndCategory(Model model) {
        modelMessageService.setEmptyResultMessage(model);
//        modelMessageService.setModelMessage(model, "item", new Item());
        modelMessageService.setModelMessage(model, "item", new Item());
        modelMessageService.setModelMessage(model, "categories", getAllCategoryList());
        if (loginController.isBranch(model)) {
            User user = loginController.getAuthenticatedUser();
            Long branch_id = user.getBranch().getId();
            modelMessageService.setModelMessage(model, "branches", branchService.findBranchById(branch_id));
            return "searchstockbybranchandcategory";
        }
        modelMessageService.setModelMessage(model, "branches", branchService.getAllBranch());
        return "searchstockbybranchandcategory";
    }

    @PostMapping(value = {"/admin/stock/by_branch_category", "/branch/stock/by_branch"})
    public String getItemByBranchAndCategory(Model model, @ModelAttribute Item item) {
        modelMessageService.setModelMessage(model, "branch", new Branch());
        if (item.getBranchId() == 0 || item.getCategory().getCategoryId() == 0) {
            modelMessageService.setModelMessage(model, "result", "Please select Branch first");
            return searchItemByBranchAndCategory(model);
        }
        Category category = categoryService.findCategoryByCategoryId(item.getCategory().getCategoryId());
        List<Item> itemListByBranch = itemRepository.findAllByStatusAndBranchIdAndCategoryOrderByItemNameAsc(1, item.getBranchId(), category);
        if (itemListByBranch == null || itemListByBranch.size() == 0) {
            modelMessageService.setModelMessage(model, "result", "No Branch Exist with this category");
            return searchItemByBranchAndCategory(model);
        }

        setStocks(model, itemListByBranch);
        return "stockbybranchandcategory";
    }


    @GetMapping(value = {"/admin/stock/by_branch", "/branch/stock/by_branch"})
    public String searchItemByBranch(Model model) {
        modelMessageService.setModelMessage(model, "branch", new Branch());
        modelMessageService.setEmptyResultMessage(model);
//        modelMessageService.setModelMessage(model, "item", new Item());

        if (loginController.isBranch(model)) {
            User user = loginController.getAuthenticatedUser();
            Long branch_id = user.getBranch().getId();
            modelMessageService.setModelMessage(model, "branches", branchService.findBranchById(branch_id));
            return "searchstockbybranch";
        }
        modelMessageService.setModelMessage(model, "branches", branchService.getAllBranch());
        return "searchstockbybranch";
    }

    @PostMapping(value = {"/admin/stock/by_branch", "/branch/stock/by_branch"})
    public String getItemByBranch(Model model, @ModelAttribute Branch branch) {
        modelMessageService.setModelMessage(model, "branch", new Branch());
        if (branch.getId() == 0) {
            modelMessageService.setModelMessage(model, "result", "Please select Branch first");
            return searchItemByBranch(model);
        }
        List<Item> itemListByBranch = itemService.getAllItemByBranchId(branch.getId());
        if (itemListByBranch == null || itemListByBranch.size() == 0) {
            modelMessageService.setModelMessage(model, "result", "No Branch Exist with this category");
            return searchItemByBranch(model);
        }
        branch = branchService.findBranchById(branch.getId());
        modelMessageService.setModelMessage(model, "categoryName", branch.getName());
        setStocks(model, itemListByBranch);
        return "stockbybranch";
    }

    @GetMapping(value = {"/admin/stock/by_category", "/branch/stock/by_category"})
    public String searchItemByCategory(Model model) {
        modelMessageService.setEmptyResultMessage(model);
        modelMessageService.setModelMessage(model, "item", new Item());
        modelMessageService.setModelMessage(model, "categories", getAllCategoryList());
        return "searchstockbycategory";
    }

    @PostMapping(value = {"/admin/stock/by_category", "/branch/stock/by_category"})
    public String getItemByCategory(Model model, @ModelAttribute Item item) {
        if (item.getCategory().getCategoryId() == 0) {
            modelMessageService.setModelMessage(model, "result", "Please select category first");
            return searchItemByCategory(model);
        }
        User user = loginController.getAuthenticatedUser();
        Category category = categoryService.findCategoryByCategoryId(item.getCategory().getCategoryId());
        List<Item> itemListByCategory = itemRepository.findAllByStatusAndBranchIdAndCategoryOrderByItemNameAsc(1, user.getBranch().getId(), category);
        if (itemListByCategory == null || itemListByCategory.size() == 0) {
            modelMessageService.setModelMessage(model, "result", "No Item Exist with this category");
            return searchItemByCategory(model);
        }
        modelMessageService.setModelMessage(model, "categoryName", itemListByCategory.get(0).getCategory().getCategoryName());
        setStocks(model, itemListByCategory);
        return "stockbycategory";
    }

    @PostMapping(value = "/admin/additem")
    public String addItem(@Valid @ModelAttribute Item item, BindingResult bindingResult, Model model, HttpServletRequest request) {
        return addOrUpdateItem(item, bindingResult, model, "additem", request);
    }

    @GetMapping(value = {"/admin/allitem", "/branch/allitem"})
    public String seeAllItems(Model model) {
        showAllItems(model);
        return "listitem";
    }

    @GetMapping(value = {"/admin/allitemadmin"})
    public String seeAllItemsAdmin(Model model) {
        modelMessageService.setEmptyResultMessage(model);
        modelMessageService.setModelMessage(model, "branchList", branchService.getAllBranch());
        return "listitemadmin";
    }

    @GetMapping(value = {"/admin/allcustomeradmin"})
    public String seeAllCustomerAdmin(Model model) {
        modelMessageService.setEmptyResultMessage(model);
        modelMessageService.setModelMessage(model, "branchList", branchService.getAllBranch());
        return "listcustomeradmin";
    }

    @GetMapping(value = {"/admin/allsalespersonadmin"})
    public String seeAllSalespersonAdmin(Model model) {
        modelMessageService.setEmptyResultMessage(model);
        modelMessageService.setModelMessage(model, "branchList", branchService.getAllBranch());
        return "listsalespersonadmin";
    }

    @GetMapping(value = {"/admin/allitembybranch/{branchId}"})
    @ResponseBody
    public List<Item> seeAllItemsAdmin(@PathVariable Long branchId) {
        return itemService.getAllItemByBranchId(branchId);
    }

    @GetMapping(value = {"/admin/item/stock", "/branch/item/stock"})
    public String stockItems(Model model) {
        List<Item> itemList = itemService.getAllItem();
        setStocks(model, itemList);
        return "stockitems";
    }

    @GetMapping(value = {"/admin/item/stock/branch", "/branch/item/stock/branch"})
    public String stockItemsOfBranch(Model model) {
//        if(loginController.isBranch(model)){
        User user = loginController.getAuthenticatedUser();
        Long branch_id = user.getBranch().getId();
        List<ItemDto> itemList = itemService.getAllItemByBranch(branch_id);
        if (!itemList.isEmpty()) {
            modelMessageService.setEmptyResultMessage(model);
            modelMessageService.setModelMessage(model, "stockItems", itemList);
        } else {
            modelMessageService.setModelMessage(model, "result", "No Item yet added");
        }
        return "stockitemsbranch";
        /*}
       return null;*/
    }

    private void setStocks(Model model, List<Item> itemList) {
        try {
            List<StockItem> stockItems = new ArrayList<>();
            final int[] totalSaleItemQuantity = {0};
            itemList.forEach(item -> {
                List<Sale> saleList = saleService.getAllSaleByItemId(item.getItemId());
                saleList.forEach(sale -> {
                    totalSaleItemQuantity[0] += sale.getSaleAmount();
                });
                Category category = categoryService.findCategoryByCategoryId(item.getCategory().getCategoryId());
                int remainingItem = item.getItemAmount();
                StockItem stockItem = new StockItem();
                stockItem.setStockItemId(item.getItemId());
                stockItem.setStockItemName(item.getItemName());
                stockItem.setStockCategoryName(category.getCategoryName());
                stockItem.setStockRemainingItem(remainingItem);
                stockItem.setStockSaleItem(totalSaleItemQuantity[0]);
                stockItem.setTotalItem(totalSaleItemQuantity[0] + remainingItem);
                stockItems.add(stockItem);
                totalSaleItemQuantity[0] = 0;
            });
            final int[] allTotalQuantity = {0};
            final int[] allRemainingQuantity = {0};
            final int[] allSoldQuantity = {0};
            stockItems.forEach(stockItem -> {
                allTotalQuantity[0] += stockItem.getTotalItem();
                allRemainingQuantity[0] += stockItem.getStockRemainingItem();
                allSoldQuantity[0] += stockItem.getStockSaleItem();
            });
            if (!stockItems.isEmpty()) {
                modelMessageService.setEmptyResultMessage(model);
                modelMessageService.setModelMessage(model, "stockItems", stockItems);
                modelMessageService.setModelMessage(model, "allTotalQuantity", allTotalQuantity[0]);
                modelMessageService.setModelMessage(model, "allRemainingQuantity", allRemainingQuantity[0]);
                modelMessageService.setModelMessage(model, "allSoldQuantity", allSoldQuantity[0]);
            } else {
                modelMessageService.setModelMessage(model, "result", "No Item yet added");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/admin/item/{action}/id/{itemId}")
    public String deleteOrEditItem(@PathVariable("itemId") Integer itemId, @PathVariable("action") String action, Model model, HttpServletRequest request) {
        List<Item> items = itemService.getAllItem();
        showAllItems(model);
        if (items.isEmpty()) {
            return "listitem";
        }
        if (action.equals("delete")) {
            Item item = itemService.findItemByItemId(itemId);
            ObjectMapper mapper = new ObjectMapper();
            String oldJson = null;
            try {
                oldJson = mapper.writeValueAsString(item);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if (item != null) {
                item.setStatus(0);
                itemService.addItem(item, request);
                eventLogService.saveDeleteLog(oldJson, Item.class, String.valueOf(item.getItemId()), request);
            }
            return "redirect:/admin/allitem";
        }
        if (action.equals("edit")) {
            setListCategories(model);
            modelMessageService.setModelMessage(model, "result", "Update Item");
            modelMessageService.setModelMessage(model, "item", itemService.findItemByItemId(itemId));
            return "updateitem";
        }
        modelMessageService.setModelMessage(model, "result", "No Item selected to Update or delete");
        return "listitem";
    }

    @GetMapping("/admin/item/update")
    public String allItems() {
        return "redirect:/admin/allitem";
    }

    @PostMapping("/admin/item/update")
    public String updateCategory(@Valid @ModelAttribute Item item, BindingResult bindingResult, Model model, HttpServletRequest request) {
        return addOrUpdateItem(item, bindingResult, model, "listitem", request);

    }

    private String addOrUpdateItem(Item item, BindingResult bindingResult, Model model, String returnString, HttpServletRequest request) {
        item.setStatus(1);
        if (bindingResult.hasErrors() || item.getCategory().getCategoryId() == 0 || item.getItemAmount() == 0 || item.getItemUnitPrice() == 0 || item.getBranchId().equals(Long.valueOf("0"))) {
            modelMessageService.setModelMessage(model, "result", "Item can't be saved with wrong input");
            return showItem(model);
        }
        ObjectMapper mapper = new ObjectMapper();
        String oldJson = null;
        try {
            oldJson = mapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        int countExistingItem = itemRepository.countByItemNameAndBranchIdAndStatus(item.getItemName(), item.getBranchId(), 1);
        if ((returnString.equals("additem") && countExistingItem > 0) || (returnString.equals("listitem") && countExistingItem > 1)) {
            model.addAttribute("result", "This Item name is already used ");
            setItemView(model);
            return returnString;
        }
        Category category = categoryService.findCategoryByCategoryId(item.getCategory().getCategoryId());
        item.setCategory(category);
        itemService.addItem(item, request);
        try {
            String newJson = mapper.writeValueAsString(item);
            eventLogService.saveUpdateLog(newJson, oldJson, Item.class, String.valueOf(item.getItemId()), request);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (returnString.equals("listitem")) {
            modelMessageService.setModelMessage(model, "result", "Item updated successfully");
            showAllItems(model);
        }
        if (returnString.equals("additem")) {
            modelMessageService.setModelMessage(model, "result", "Item added successfully");
            setItemView(model);
        }
        return returnString;
    }

    private void setListCategories(Model model) {
        modelMessageService.setModelMessage(model, "categories", getAllCategoryList());
        modelMessageService.setModelMessage(model, "branchList", branchService.getAllBranch());
    }

    private String showItem(Model model) {
        setItemView(model);
        return "additem";
    }

    private void setItemView(Model model) {
        setListCategories(model);
        modelMessageService.setModelMessage(model, "item", new Item());
    }

    private List<Category> getAllCategoryList() {
        return categoryService.getAllCategory();
    }

    private void showAllItems(Model model) {
        List<Item> itemList = itemService.getAllItem();
        if (!itemList.isEmpty()) {
            modelMessageService.setEmptyResultMessage(model);
            modelMessageService.setModelMessage(model, "itemList", itemList);
        } else {
            modelMessageService.setModelMessage(model, "result", "No Item Yet added");
        }
    }

}
