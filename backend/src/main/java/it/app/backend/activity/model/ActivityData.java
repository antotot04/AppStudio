package it.app.backend.activity.model;

// for activity registration
public class ActivityData {
    private String title;
    private String description;
    private int pomoCounter;

    public ActivityData(String title, String description, int pomoCounter) throws IllegalArgumentException {
        if(title == null){
            throw new IllegalArgumentException("title can't be null");
        }
        if(title.length() > 100){
            throw new IllegalArgumentException("title is too long");
        }
        this.title = title;
        this.description = description;
        if(pomoCounter <= 0){
            throw new IllegalArgumentException("pomoCounter can't be zero or negative");
        }
        this.pomoCounter = pomoCounter;
    }

    public String getDescription() {
        return description;
    }

    public int getPomoCounter() {
        return pomoCounter;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPomoCounter(int pomoCounter) throws IllegalArgumentException {
        if(pomoCounter <= 0){
            throw new IllegalArgumentException("pomoCounter can't be zero or negative");
        }
        this.pomoCounter = pomoCounter;
    }

    public void setTitle(String title) throws IllegalArgumentException {
        if(title == null){
            throw new IllegalArgumentException("title can't be null");
        }
        if(title.length() > 100){
            throw new IllegalArgumentException("title is too long");
        }
        this.title = title;
    }
}
