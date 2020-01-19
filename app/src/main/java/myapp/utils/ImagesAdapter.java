package myapp.utils;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import site.sht.bd.foodhood.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImagesAdapter extends PagerAdapter {

    public ArrayList<Uri> imageUris = new ArrayList<>();
    private Context mContext;

    public ImagesAdapter(Context context){
        mContext = context;
    }

    ImagesAdapter(Context context, ArrayList<Uri> images){
        mContext = context;
        imageUris.addAll(images);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        final ViewGroup layout = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.slider_image, container, false);
        ImageView imageView = layout.findViewById(R.id.imageView);
        // imageView.setImageURI(imageUris.get(position));
        Picasso.get().load(imageUris.get(position))
                .placeholder(R.drawable.ltgray)
                .error((R.drawable.ltgray))
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
