package it.polimi.ingsw.common.config;

/**
 * Rappresenta i dati statici di una carta caricati dal JSON.
 */
public record CardInfo(
        String name,
        String type,
        int era,
        int foodCost,
        int prestigePoints,
        Integer foodDiscount,        // Specifica per i Builder
        Integer bonusPrestige,       // Punti extra a fine partita (Builder/Edifici)
        Boolean hasIcon,             // Specifica per gli Hunter
        Integer stars,               // Specifica per gli Shaman
        Integer sustenanceDiscount,  // Specifica per i Collector
        String inventorIcon,         // Specifica per gli Inventor
        Integer val1,                // Parametri generici per gli Eventi
        Integer val2,
        Integer val3
) {
    // Metodi di utilità per evitare NullPointerException nel modello
    public Integer foodDiscount() { return foodDiscount != null ? foodDiscount : 0; }
    public Integer bonusPrestige() { return bonusPrestige != null ? bonusPrestige : 0; }
    public Boolean hasIcon() { return hasIcon != null ? hasIcon : false; }
    public Integer stars() { return stars != null ? stars : 0; }
    public Integer sustenanceDiscount() { return sustenanceDiscount != null ? sustenanceDiscount : 0; }
    public Integer val1() { return val1 != null ? val1 : 0; }
    public Integer val2() { return val2 != null ? val2 : 0; }
    public Integer val3() { return val3 != null ? val3 : 0; }
}