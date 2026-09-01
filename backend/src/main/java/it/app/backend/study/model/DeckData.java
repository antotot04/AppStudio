package it.app.backend.study.model;

public class DeckData {
    private String deckTitle;
    private String deckLayout; 

    public DeckData() {
    }

    public DeckData(String deckTitle, String deckLayout) throws IllegalArgumentException {
        this.deckTitle = deckTitle;
        if("quiz".equals(deckLayout) ||
        "double-sided".equals(deckLayout) ||
        "true-false".equals(deckLayout)){
            this.deckLayout = deckLayout;
        }else if(deckLayout == null || deckLayout.equals("general")){
            this.deckLayout = null;
        }else{
            throw new IllegalArgumentException("invalid deck layout");
        }
    }

    public String getDeckLayout() {
        return deckLayout;
    }

    public String getDeckTitle() {
        return deckTitle;
    }

    public void setDeckLayout(String deckLayout) throws IllegalArgumentException {
        if("quiz".equals(deckLayout) ||
        "double-sided".equals(deckLayout) ||
        "true-false".equals(deckLayout)){
            this.deckLayout = deckLayout;
        }else if(deckLayout == null || deckLayout.equals("general")){
            this.deckLayout = null;
        }else{
            throw new IllegalArgumentException("invalid deck layout");
        }
    }

    public void setDeckTitle(String deckTitle) throws IllegalArgumentException {
        if(deckTitle == null){
            throw new IllegalArgumentException("deck title is null");
        }
        this.deckTitle = deckTitle;
    }
}
