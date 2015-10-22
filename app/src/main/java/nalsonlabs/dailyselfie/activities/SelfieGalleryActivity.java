package nalsonlabs.dailyselfie.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.io.File;
import java.util.Random;

import nalsonlabs.dailyselfie.R;
import nalsonlabs.dailyselfie.adapters.Selfie;
import nalsonlabs.dailyselfie.adapters.SelfieAdapter;
import nalsonlabs.dailyselfie.adapters.SelfieGalleryAdapter;
import nalsonlabs.dailyselfie.receivers.SelfieAlarmReceiver;

public class SelfieGalleryActivity extends Activity {

    private static final int REQUEST_IMAGE_CAPTURE = 48927759;
    private static final long SELFIE_INTERVAL = 120000;
    private static final long JITTER = new Random().nextInt((int) SELFIE_INTERVAL * 2);
    private static final int CANCEL_SELFIE_ALARM = 342578;
    private static final int SET_SELFIE_ALARM = 2354234;

    private SelfieAdapter adapter;
    private File currentImage;
    private PendingIntent selfieIntent;
    private boolean isAlarmSet;
    private GridView galleryView;
    private LinearLayout noSelfiesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie_gallery);

        galleryView = (GridView) findViewById(R.id.gallery_view);
        setEmptyGalleryMessage();
        configureSelfieAdapter();
        createSelfieIntent();


        noSelfiesView = (LinearLayout) findViewById(R.id.no_selfies_taken_view);
        noSelfiesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeSelfie();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_daily_selfie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_take_selfie:
                takeSelfie();
                break;
            case R.id.action_delete_all:
                deleteAllSelfies();
                break;
            case R.id.action_view_selfies_on_map:
                viewSelfiesOnMap();
                break;
            case CANCEL_SELFIE_ALARM:
                cancelSelfieAlarm();
                break;
            case SET_SELFIE_ALARM:
                setSelfieAlarm();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void viewSelfiesOnMap() {
        Intent intent = new Intent(SelfieGalleryActivity.this, SelfieLocationActivity.class);
        intent.putExtra(SelfieLocationActivity.SELFIES, adapter.getSelfies());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean result = super.onPrepareOptionsMenu(menu);

        // Clean up the menu to ensure that we have the correct menu item set.
        menu.removeItem(CANCEL_SELFIE_ALARM);
        menu.removeItem(SET_SELFIE_ALARM);

        if (isAlarmSet) {
            menu.add(0, CANCEL_SELFIE_ALARM, 1, R.string.action_cancel_alarm);
        } else {
            menu.add(0, SET_SELFIE_ALARM, 1, R.string.action_set_alarm);
        }

        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            adapter.add(new Selfie(currentImage));
            currentImage = null;
        }
    }

    private void setEmptyGalleryMessage() {
        galleryView.setEmptyView(findViewById(R.id.no_selfies_taken_view));
    }

    private void takeSelfie() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File image = adapter.createImageFile();

            if (image != null) {
                currentImage = image;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImage));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void deleteAllSelfies() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SelfieGalleryActivity.this);
        builder.setMessage(R.string.delete_all_selfies_confirmation_message);
        builder.setNegativeButton(R.string.delete_all_selfies_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(R.string.delete_all_selfies_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.deleteSelfies();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void configureSelfieAdapter() {
        adapter = new SelfieGalleryAdapter(getApplicationContext());
        adapter.loadSelfies();
        galleryView.setAdapter(adapter);
    }

    private void createSelfieIntent() {
        Intent intent = new Intent(this, SelfieAlarmReceiver.class);
        selfieIntent = PendingIntent.getBroadcast(this, SelfieAlarmReceiver.SELFIE_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void cancelSelfieAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(selfieIntent);
        isAlarmSet = false;
    }

    private void setSelfieAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + JITTER, SELFIE_INTERVAL, selfieIntent);
        isAlarmSet = true;
    }


}
