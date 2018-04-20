package com.image.hunt.imagehunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class TransitionActivity extends AppCompatActivity {
    ImageView imageView;
    Photo photo;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        imageView=(ImageView)findViewById(R.id.poster_view);
        tv=(TextView)findViewById(R.id.text_view);
        Intent i=getIntent();
        if(i.hasExtra("details"))
        {
            photo=i.getParcelableExtra("details");
            tv.setText(photo.getTitle());
            Picasso.with(this).load(NetworkUtils.getImageUrl(photo.getId(),photo.getSecret(),photo.getServer(),photo.getFarm())).into(imageView);

        }

    }
}
