package com.example.mobileproject.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileproject.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.support.v4.app.Fragment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PostActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int AUTOCOMPLETE_REQUEST_CODE = 2;

    public static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseUser mUser;

    private TextView mContentsArea;
    private ImageView mPreviewImageView;

    private ProgressBar mProgressBar;
    private Button addPlacementButton;

    //place

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mContentsArea = findViewById(R.id.contents_area);
        mProgressBar = findViewById(R.id.progressBar);
        mPreviewImageView = findViewById(R.id.camera);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // 로그인 안 되었음
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //카메라 실행
        findViewById(R.id.camera).setOnClickListener(v -> {
            // Firebase에 추가
            dispatchTakePictureIntent();
        });

        findViewById(R.id.upload_button).setOnClickListener(v -> {
            mProgressBar.setVisibility(View.VISIBLE);
            uploadPicture();
        });

        findViewById(R.id.addlocation_button).setOnClickListener(v -> {
            GetPlacement();
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng SEOUL = new LatLng(37.56, 126.97);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
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

    private void CheckPermission(){
        PlacesClient placesClient = Places.createClient(this);


        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();


        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            placesClient.findCurrentPlace(request).addOnSuccessListener(((response) -> {
                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));

                }
            })).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            //getLocationPermission();
        }
    }

    private void GetPlacement(){



        CheckPermission();



        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //IMAGE_CAPTURE
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            mPreviewImageView.setImageBitmap(imageBitmap);
        }

        //AUTOCOMPLETE
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
