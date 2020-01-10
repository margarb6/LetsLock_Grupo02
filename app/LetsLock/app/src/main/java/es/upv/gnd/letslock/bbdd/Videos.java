package es.upv.gnd.letslock.bbdd;

public class Videos {
    String videoUrl;

    public Videos() {
    }

    public Videos(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
