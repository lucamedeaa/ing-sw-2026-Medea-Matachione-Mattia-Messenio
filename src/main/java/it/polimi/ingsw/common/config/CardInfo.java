package it.polimi.ingsw.common.config;

public record CardInfo(
        String name,
        String type,
        int era,
        int foodCost,
        int prestigePoints,
        Integer foodDiscount,
        Integer bonusPrestige,
        Boolean hasIcon,
        Integer stars,
        Integer sustenanceDiscount,
        String inventorIcon,
        Integer val1,
        Integer val2,
        Integer val3
) {
    public Integer foodDiscount() { return foodDiscount != null ? foodDiscount : 0; }
    public Integer bonusPrestige() { return bonusPrestige != null ? bonusPrestige : 0; }
    public Boolean hasIcon() { return hasIcon != null ? hasIcon : false; }
    public Integer stars() { return stars != null ? stars : 0; }
    public Integer sustenanceDiscount() { return sustenanceDiscount != null ? sustenanceDiscount : 0; }
    public Integer val1() { return val1 != null ? val1 : 0; }
    public Integer val2() { return val2 != null ? val2 : 0; }
    public Integer val3() { return val3 != null ? val3 : 0; }
}