package it.polimi.ingsw.common.config;

/**
 * Immutable value object for card info.
 *
 * @param name card display name
 * @param type card category
 * @param era era number
 * @param foodCost food cost
 * @param prestigePoints prestige points
 * @param foodDiscount food discount value
 * @param bonusPrestige bonus prestige
 * @param hasIcon whether the card has an inventor icon
 * @param stars star count
 * @param sustenanceDiscount sustenance discount value
 * @param inventorIcon inventor icon
 * @param val1 first card-specific numeric value
 * @param val2 second card-specific numeric value
 * @param val3 third card-specific numeric value
 */
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
    /**
     * Returns the food discount.
     *
     * @return food discount, or zero when absent
     */
    public Integer foodDiscount() { return foodDiscount != null ? foodDiscount : 0; }
    /**
     * Returns the bonus prestige.
     *
     * @return bonus prestige, or zero when absent
     */
    public Integer bonusPrestige() { return bonusPrestige != null ? bonusPrestige : 0; }
    /**
     * Returns whether the card has an inventor icon.
     *
     * @return true if the icon is present; false otherwise
     */
    public Boolean hasIcon() { return hasIcon != null ? hasIcon : false; }
    /**
     * Returns the stars.
     *
     * @return star count, or zero when absent
     */
    public Integer stars() { return stars != null ? stars : 0; }
    /**
     * Returns the sustenance discount.
     *
     * @return sustenance discount, or zero when absent
     */
    public Integer sustenanceDiscount() { return sustenanceDiscount != null ? sustenanceDiscount : 0; }
    /**
     * Returns the val1.
     *
     * @return first card-specific numeric value, or zero when absent
     */
    public Integer val1() { return val1 != null ? val1 : 0; }
    /**
     * Returns the val2.
     *
     * @return second card-specific numeric value, or zero when absent
     */
    public Integer val2() { return val2 != null ? val2 : 0; }
    /**
     * Returns the val3.
     *
     * @return third card-specific numeric value, or zero when absent
     */
    public Integer val3() { return val3 != null ? val3 : 0; }
}
