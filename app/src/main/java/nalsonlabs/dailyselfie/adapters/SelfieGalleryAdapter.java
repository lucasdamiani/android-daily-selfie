package nalsonlabs.dailyselfie.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import nalsonlabs.dailyselfie.activities.ViewSelfieActivity;

/**
 * Created by lucasdamiani on 11/06/15.
 */
public class SelfieGalleryAdapter extends SelfieAdapter {

    public SelfieGalleryAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 180));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView) convertView;
        }

        final Selfie selfie = selfies.get(position);
        imageView.setImageBitmap(selfie.getThumbnail());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewSelfieActivity.class);
                intent.putExtra(ViewSelfieActivity.SELECTED_SELFIE, selfie);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return imageView;
    }
}
