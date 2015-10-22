package nalsonlabs.dailyselfie.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import nalsonlabs.dailyselfie.R;
import nalsonlabs.dailyselfie.adapters.Selfie;

/**
 * Activity to preview a given selfie. This activity doesn't image scale and orientation.
 *
 * @author Lucas Damiani
 */
public class ViewSelfieActivity extends Activity {

    public static final String SELECTED_SELFIE = "SELECTED_SELFIE";
    private ImageView selfieImageView;
    private TextView fileNameTextView;
    private Button showSelfieLocationButton;
    private Selfie selfie;
    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_selfie);

        selfie = getSelfie();

        selfieImageView = (ImageView) findViewById(R.id.view_selfie_image_view);
        fileNameTextView = (TextView) findViewById(R.id.file_name_text_view);
        showSelfieLocationButton = (Button) findViewById(R.id.show_selfie_location_button);
        showSelfieLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO We consider that location is always available for the photo
                Intent intent = new Intent(ViewSelfieActivity.this, SelfieLocationActivity.class);
                intent.putExtra(SelfieLocationActivity.SELFIES, new Selfie[] { selfie });
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        showSelfie(selfie);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_view_selfie, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        shareActionProvider = (ShareActionProvider) item.getActionProvider();
        setShareIntent(shareActionProvider, createShareSelfieIntent(selfie));

        // Return true to display menu
        return true;
    }

    private Intent createShareSelfieIntent(Selfie selfie) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, selfie.getImageFile().toURI());
        shareIntent.setType("image/jpeg");

        return shareIntent;
    }

    // Call to update the share intent
    private void setShareIntent(ShareActionProvider shareActionProvider, Intent shareIntent) {
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Selfie getSelfie() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        
        return (Selfie) bundle.get(SELECTED_SELFIE);
    }

    private void showSelfie(Selfie selfie) {
        selfieImageView.setImageBitmap(BitmapFactory.decodeFile(selfie.getImageFile().getAbsolutePath()));
        fileNameTextView.setText(selfie.getImageFileName());
    }
}
