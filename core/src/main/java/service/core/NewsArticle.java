package service.core;

public class NewsArticle {
    private String id;
    private String title;
    private String description;
    private String url;
    private String author;
    private String image;
    private String language;
    private String published;

    public NewsArticle() {
    }

    public NewsArticle(String id, String title, String description, String url, String author, String image, String language, String published) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url = url;
        this.author = author;
        this.image = image;
        this.language = language;
        this.published = published;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Title:\n");
        sb.append(title);
        sb.append("\nURL:\n");
        sb.append(url);
        sb.append("\n-------------------------------------------------------------------------------------------\n");


        return sb.toString();
    }
}

