package com.example.cameratutorial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 20;

    private Uri pictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button capture = findViewById(R.id.button);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File picture = getTempFile(getPictureName(), ".jpg");
                    pictureUri = FileProvider.getUriForFile(MainActivity.this,
                            getApplicationContext().getPackageName() + ".provider", picture);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(pictureUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    ImageView image = findViewById(R.id.imageView);
                    image.setImageBitmap(bitmap);

                    Log.d(TAG, "onActivityResult: " + pictureUri.getScheme());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                //ImageView image = findViewById(R.id.imageView);
                //image.setImageBitmap(thumbnail);
            } else {
                Toast.makeText(this, "Failed to take picture", Toast.LENGTH_LONG);
            }
        }
    }

    private File getTempFile(String fileName, String fileExt) throws IOException {
        File picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (!picturesDir.exists()) {
            picturesDir.mkdirs();
        }

        return File.createTempFile(fileName, fileExt, picturesDir);
    }

    private String getPictureName() {
        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        return String.format("pic %s-%s-%s-", day, month, year);
    }

    private void writeFile(File file, String content) {
        try {
            OutputStream outputStream = //openFileOutput("config.txt", MODE_PRIVATE);
                    new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(content);
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFile(File file) {
        try {
            InputStream inputStream = //openFileInput("config.txt");
                    new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            StringBuilder builder = new StringBuilder();
            int data;
            while ((data = inputStreamReader.read()) != -1) {
                builder.append((char) data);
            }
            inputStreamReader.close();

            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
