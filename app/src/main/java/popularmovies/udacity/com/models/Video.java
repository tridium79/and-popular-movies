package popularmovies.udacity.com.models;

import android.net.Uri;

public class Video {
    private String name;
    private String id;

    public Video(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnailUrl() {
        return "https://img.youtube.com/vi/" + this.id + "/hqdefault.jpg";
    }

    public Uri getAppUri() {
        return Uri.parse("vnd.youtube:" + this.id);
    }

    public Uri getWebUri() {
        return Uri.parse("https://www.youtube.com/watch?v=" + this.id);
    }
}
