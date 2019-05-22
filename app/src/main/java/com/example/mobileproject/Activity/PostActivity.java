package com.example.mobileproject.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseUser mUser;

    private TextView mContentsArea;
    private ImageView mPreviewImageView;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mContentsArea = findViewById(R.id.contents_area);
        mProgressBar = findViewById(R.id.progressBar);
        mPreviewImageView = findViewById(R.id.camera);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // 로그인 안 되었음
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
        }


        //카메라 실행
        findViewById(R.id.camera).setOnClickListener(v -> {
            // Firebase에 추가
            dispatchTakePictureIntent();
        });

        findViewById(R.id.upload_button).setOnClickListener(v -> {
            mProgressBar.setVisibility(View.VISIBLE);
            uploadPicture();
        });

    }

    private void addPost(Map<String, Object> post) {
        db.collection("post")
                .add(post)
                .addOnSuccessListener(doc -> {
                    mProgressBar.setVisibility(View.GONE);
                    // 성공
                    Toast.makeText(this, "성공", Toast.LENGTH_SHORT).show();
                    // 맨 위로
                    //mRecyclerView.smoothScrollToPosition(0);
                })
                .addOnFailureListener(e -> {
                    mProgressBar.setVisibility(View.GONE);
                    // 실패
                    Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show();
                });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            mPreviewImageView.setImageBitmap(imageBitmap);
        }
    }

    private void uploadPicture() {
        StorageReference storageRef = storage.getReference()
                .child("images/" + System.currentTimeMillis() + ".jpg");

        mPreviewImageView.setDrawingCacheEnabled(true);
        mPreviewImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) mPreviewImageView.getDrawable()).getBitmap();

        // 이미지 줄이기
        bitmap = resizeBitmapImage(bitmap, 300);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            mProgressBar.setVisibility(View.GONE);
            // 실패
            Log.d(TAG, "uploadPicture: " + exception.getLocalizedMessage());
            Toast.makeText(this, "업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            // 성공
            storageRef.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d(TAG, "uploadPicture: " + downloadUri);

                    writeDb(downloadUri);
                } else {
                    // Handle failures
                    // ...
                }
            });
        });
    }

    private void writeDb(Uri downloadUri) {
        String theBody = mContentsArea.getText().toString();
        //int age = Integer.parseInt(mAgeEditText.getText().toString());

        Map<String, Object> post = new HashMap<>();
        post.put("theBody", theBody);
        //post.put("age", age);
        post.put("downloadUrl", downloadUri.toString());
        post.put("uid", mUser.getUid());

        addPost(post);
    }

    public Bitmap resizeBitmapImage(Bitmap source, int maxResolution) {
        int width = source.getWidth();
        int height = source.getHeight();
        int newWidth = width;
        int newHeight = height;
        float rate = 0.0f;

        if (width > height) {
            if (maxResolution < width) {
                rate = maxResolution / (float) width;
                newHeight = (int) (height * rate);
                newWidth = maxResolution;
            }
        } else {
            if (maxResolution < height) {
                rate = maxResolution / (float) height;
                newWidth = (int) (width * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }
}
