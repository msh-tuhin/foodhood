package myapp.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import site.sht.bd.foodhood.ImageFull;
import site.sht.bd.foodhood.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class PostImagesAdapter extends PagerAdapter {
    public ArrayList<String> imageUris = new ArrayList<>();
    private Context mContext;

    public PostImagesAdapter(Context context){
        mContext = context;
    }

    public PostImagesAdapter(Context context, ArrayList<String> images){
        mContext = context;
        imageUris.addAll(images);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        final ViewGroup layout = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.post_images_iv, container, false);
        layout.findViewById(R.id.parent_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContext instanceof ImageFull) return;
                Log.i("post_image", "clicked");
                Intent intent = new Intent(mContext, ImageFull.class);
                intent.putExtra("position", position);
                intent.putStringArrayListExtra("imageUris", imageUris);
                mContext.startActivity(intent);
            }
        });
        ImageView imageView = layout.findViewById(R.id.imageView);
        //imageView.setImageURI(imageUris.get(position));
        Picasso.get().load(imageUris.get(position))
                .placeholder(R.drawable.ltgray)
                .error(R.drawable.ltgray)
                .into(imageView);
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imageUris.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o ;
    }
}
