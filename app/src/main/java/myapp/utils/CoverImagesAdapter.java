package myapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import site.sht.bd.foodhood.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class CoverImagesAdapter extends PagerAdapter {
    // for experimenting
    private ArrayList<Integer> images;
    // actual
    // private ArrayList<String> images;
    private Context mContext;

    public CoverImagesAdapter(Context context, ArrayList<Integer> images){
        this.mContext = context;
        this.images = images;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        final ViewGroup layout = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.slider_image, container, false);
        ImageView imageView = layout.findViewById(R.id.imageView);
        imageView.setImageResource(images.get(position));
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o ;
    }
}
