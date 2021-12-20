package com.printtex.printtex_pos.model;

import lombok.Data;

@Data
public class ItemDto {
    private String itemName;
    private String categoryName;
    private Integer itemAmount;
    private Integer itemRemainingAmount;
    private Integer itemSoldAmount;
}
