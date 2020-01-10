package es.upv.gnd.letslock;

import android.os.Bundle;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

import es.upv.gnd.letslock.adapters.AdaptadorVideos;
import es.upv.gnd.letslock.bbdd.Videos;

public class VideosActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Vector<Videos> videos = new Vector<Videos>();
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        toolbar = findViewById(R.id.toolbar);

        recyclerView = findViewById(R.id.recyclerViewVideos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        videos.add( new Videos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/yYibaCr7D8E\" frameborder=\"0\" allowfullscreen></iframe>") );
        videos.add( new Videos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/c-Q3-lgyzGw\" frameborder=\"0\" allowfullscreen></iframe>") );
        videos.add( new Videos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/veqZbM2OYP8\" frameborder=\"0\" allowfullscreen></iframe>") );
        videos.add( new Videos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/tN_XPuIhbF8\" frameborder=\"0\" allowfullscreen></iframe>") );

        AdaptadorVideos adaptadorVideos = new AdaptadorVideos(videos);

        recyclerView.setAdapter(adaptadorVideos);

    }


}
