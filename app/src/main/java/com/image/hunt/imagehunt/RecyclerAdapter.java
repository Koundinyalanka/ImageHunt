package com.image.hunt.imagehunt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageHolder> {
    @NonNull
    ArrayList<Photo> al;
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    Context context;
    public RecyclerAdapter(Context context,ArrayList<Photo> al)
    {
        this.context=context;
        this.al=al;
    }
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        ImageHolder imageHolder=null;
        context=parent.getContext();

        boolean shouldAttachToParent=false;
        context=parent.getContext();
        switch(viewType)
        {
            case ITEM:
                int layoutid=R.layout.number_image;
                View view=layoutInflater.inflate(layoutid,parent,shouldAttachToParent);
                imageHolder=new ImageHolder(view);
                break;
            case LOADING:
                int layoutid1=R.layout.number_progress;
                View view1=layoutInflater.inflate(layoutid1,parent,shouldAttachToParent);
                imageHolder=new ImageHolder(view1);
                break;

        }

        int layoutid=R.layout.number_image;
        View view=layoutInflater.inflate(layoutid,parent,shouldAttachToParent);
        return imageHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageHolder holder, final int position) {
        holder.bind(position);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                Photo photo=al.get(position);
                final Intent i;
                i=new Intent(context,TransitionActivity.class);
                ActivityOptionsCompat optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,(View)holder.imageView,"details1");
                i.putExtra("details", (Parcelable) photo);
                view.getContext().startActivity(i,optionsCompat.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return al.size();
    }
    public void addItems(ArrayList<Photo> al)
    {
        this.al.addAll(al);
        notifyDataSetChanged();
    }
    public class ImageHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        public ImageHolder(View itemView) {
            super(itemView);
            imageView=(ImageView) itemView.findViewById(R.id.poster_view);
        }
        public void bind(final int index)
        {
            Photo photo=al.get(index);
            Picasso.with(context).load(NetworkUtils.getImageUrl(photo.getId(),photo.getSecret(),photo.getServer(),photo.getFarm())).into(imageView);
        }
    }


}
