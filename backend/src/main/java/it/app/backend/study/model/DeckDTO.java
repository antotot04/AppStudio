package it.app.backend.study.model;

import java.util.UUID;

public class DeckDTO {
    private UUID id;
    private String title;
    private String layout;

    public DeckDTO() {
    }

    public DeckDTO(UUID id, String title, String layout) throws IllegalArgumentException {
        if(id == null){
            throw new IllegalArgumentException("deck id is null");
        }
        this.id = id;
        if(title == null){
            throw new IllegalArgumentException("deck title is null");
        }
        this.title = title;

        if("quiz".equals(layout) ||
        "double-sided".equals(layout) ||
        "true-false".equals(layout)){
            this.layout = layout;
        }else if("general".equals(layout) || layout == null){
            this.layout = null;
        }else{
            throw new IllegalArgumentException("invalid deck layout");
        }
    }

    public UUID getId() {
        return id;
    }

    public String getLayout() {
        return layout;
    }

    public String getTitle() {
        return title;
    }

    public void setId(UUID id) throws IllegalArgumentException {
        if(id == null){
            throw new IllegalArgumentException("deck id is null");
        }
        this.id = id;
    }

    public void setLayout(String layout) throws IllegalArgumentException {
        if("quiz".equals(layout) ||
        "double-sided".equals(layout) ||
        "true-false".equals(layout)){
            this.layout = layout;
        }else if("general".equals(layout) || layout == null){
            this.layout = null;
        }else{
            throw new IllegalArgumentException("invalid deck layout");
        }
    }

    public void setTitle(String title) throws IllegalArgumentException {
        if(title == null){
            throw new IllegalArgumentException("deck title is null");
        }
        this.title = title;
    }
}
