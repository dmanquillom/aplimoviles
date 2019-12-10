package co.unal.myexperience.Model;

public class Post {

    private long time;
    private String author, description, id, image;

    public Post(){
    }

    public Post(long time, String author, String description, String id, String image) {
        this.time = time;
        this.author = author;
        this.description = description;
        this.id = id;
        this.image = image;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
