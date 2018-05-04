package popularmovies.udacity.com.models;

public class Video {
    private String name;
    private String key;

    public Video(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getThumbnailUrl() {
        return "https://img.youtube.com/vi/" + this.key + "/hqdefault.jpg";
    }
}
