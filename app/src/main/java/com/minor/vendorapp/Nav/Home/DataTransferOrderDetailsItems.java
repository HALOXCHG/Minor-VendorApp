package com.minor.vendorapp.Nav.Home;

class DataTransferOrderDetailsItems {

    String itemImage, itemName, offeredUnits, orderedUnits, itemSellingPrice, itemTotalCost;

    public DataTransferOrderDetailsItems(String itemImage, String itemName, String offeredUnits, String orderedUnits, String itemSellingPrice, String itemTotalCost) {
        this.itemImage = itemImage;
        this.itemName = itemName;
        this.offeredUnits = offeredUnits;
        this.orderedUnits = orderedUnits;
        this.itemSellingPrice = itemSellingPrice;
        this.itemTotalCost = itemTotalCost;
    }

    public String getItemImage() {
        return itemImage;
    }

    public String getItemName() {
        return itemName;
    }

    public String getOfferedUnits() {
        return offeredUnits;
    }

    public String getOrderedUnits() {
        return orderedUnits;
    }

    public String getItemSellingPrice() {
        return itemSellingPrice;
    }

    public String getItemTotalCost() {
        return itemTotalCost;
    }
}

