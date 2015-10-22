package nalsonlabs.dailyselfie.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nalsonlabs.dailyselfie.R;
import nalsonlabs.dailyselfie.activities.ViewSelfieActivity;

/**
 * Adapter for the selfies taken with this application.
 *
 * @author Lucas Damiani
 */
public class SelfieAdapter extends BaseAdapter {
    protected final Context context;
    protected final File storageDir;
    protected final List<Selfie> selfies;
    protected LayoutInflater layoutInflater;

    public SelfieAdapter(Context context) {
        this.context = context;
        this.storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        this.selfies = new ArrayList<>();
    }

    public Selfie[] getSelfies() {
        return selfies.toArray(new Selfie[selfies.size()]);
    }

    /**
     * Adds a new {@link Selfie} to the list of selfies taken.
     *
     * @param selfie New {@link Selfie} taken.
     */
    public void add(Selfie selfie) {
        selfies.add(selfie);
        notifyDataSetChanged();
    }

    /**
     * Loads existing selfies taken by the application.
     */
    public void loadSelfies() {
        File[] files = storageDir.listFiles();
        for (File file : files) {
            selfies.add(new Selfie(file));
        }

        notifyDataSetChanged();
    }

    /**
     * Delete selfies taken by the application.
     */
    public void deleteSelfies() {
        selfies.clear();
        notifyDataSetChanged();

        File[] files = storageDir.listFiles();
        for (File file : files) {
            file.delete();
        }
    }

    /**
     * Creates a file entry for the new {@link Selfie}.
     *
     * @return Reference to the new {@link Selfie} file.
     */
    public File createImageFile() {
        String imageFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File image = new File(storageDir, imageFileName + ".jpg");

        return image;
    }

    @Override
    public int getCount() {
        return selfies.size();
    }

    @Override
    public Object getItem(int position) {
        return selfies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = getItemView(convertView, parent);
        final Selfie selfie = selfies.get(position);

        TextView textView = (TextView) view.findViewById(R.id.file_name_text_view);
        textView.setText(selfie.getImageFileName());

        ImageView imageView = (ImageView) view.findViewById(R.id.thumbnail_image_view);
        imageView.setImageBitmap(selfie.getThumbnail(imageView.getMaxWidth(), imageView.getMaxHeight()));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewSelfieActivity.class);
                intent.putExtra(ViewSelfieActivity.SELECTED_SELFIE, selfie);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return view;
    }

    private View getItemView(View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = getLayoutInflater().inflate(R.layout.daily_selfie_list_item, parent, false);
        }

        return view;
    }

    private LayoutInflater getLayoutInflater() {
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        return layoutInflater;
    }
}
