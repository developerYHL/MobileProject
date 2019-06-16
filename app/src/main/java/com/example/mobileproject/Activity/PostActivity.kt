package com.example.mobileproject.Activity

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.example.mobileproject.R
import com.example.mobileproject.fragments.NavigationFragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class PostActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var mUser: FirebaseUser? = null

    private var mContentsArea: TextView? = null
    private var mPreviewImageView: ImageView? = null

    private var mProgressBar: ProgressBar? = null
    private val addPlacementButton: Button? = null

    private var mplace: Place? = null

    private var mapLayout: LinearLayout? = null

    private var placesClient: PlacesClient? = null

    private var listItems: Array<String>? = null

    //place


    private var tsLong: Long? = null
    private var ts: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)


        mContentsArea = findViewById(R.id.contents_area)
        mProgressBar = findViewById(R.id.progressBar)
        mPreviewImageView = findViewById(R.id.camera)

        mapLayout = findViewById(R.id.map_layout)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
            placesClient = Places.createClient(this)
        }

        if (FirebaseAuth.getInstance().currentUser == null) {
            // 로그인 안 되었음
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            mUser = FirebaseAuth.getInstance().currentUser
        }

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        //카메라 실행
        findViewById<View>(R.id.camera).setOnClickListener { v ->
            // Firebase에 추가
            listItems = arrayOf("사진 촬영", "엘범에서 선택")
            val mBuilder = AlertDialog.Builder(this@PostActivity)
            mBuilder.setTitle("촬영")
            mBuilder.setIcon(R.drawable.icon)
            mBuilder.setSingleChoiceItems(listItems, -1) { dialog, which ->
                if (which == 0) {
                    dispatchTakePictureIntent()
                } else {
                    val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(i, REQUEST_IMAGE_GALLAY)
                }
                dialog.dismiss()
            }
            mBuilder.setNeutralButton("Cancel") { dialog, which -> }
            val mDialog = mBuilder.create()
            mDialog.show()
        }

        findViewById<View>(R.id.upload_button).setOnClickListener { v ->
            mProgressBar!!.visibility = View.VISIBLE
            uploadPicture()
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<View>(R.id.addlocation_button).setOnClickListener { v -> GetPlacement() }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun DrawMarkOnMap(place: Place) {
        //clear map
        mMap!!.clear()

        // Define a Place ID.
        val placeId = place.id

        // Specify the fields to return.
        val placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)


        // Construct a request object, passing the place ID and fields array.
        val request = FetchPlaceRequest.builder(placeId!!, placeFields)
                .build()
        try {


            placesClient?.fetchPlace(request)?.addOnSuccessListener { response ->
                mplace = response.place
                Log.i(TAG, "Place found: " + mplace!!.name!!)
                Log.i("!!!", mplace!!.latLng!!.toString() + "")

                val markerOptions = MarkerOptions()
                markerOptions.position(mplace!!.latLng!!)
                //markerOptions.title("서울");
                //markerOptions.snippet("한국의 수도");
                mMap!!.addMarker(markerOptions)
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(mplace!!.latLng, 16f))

            }?.addOnFailureListener { exception ->
                if (exception is ApiException) {
                    val statusCode = exception.statusCode
                    // Handle error with given status code.
                    Log.e(TAG, "Place not found: " + exception.message)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun uploadPicture() {
        val storageRef = storage.reference
                .child("images/" + System.currentTimeMillis() + ".jpg")

        mPreviewImageView!!.isDrawingCacheEnabled = true
        mPreviewImageView!!.buildDrawingCache()
        var bitmap = (mPreviewImageView!!.drawable as BitmapDrawable).bitmap

        // 이미지 줄이기
        bitmap = resizeBitmapImage(bitmap, 300)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener { exception ->
            mProgressBar!!.visibility = View.GONE
            // 실패
            Log.d(TAG, "uploadPicture: " + exception.localizedMessage)
            Toast.makeText(this, "업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            // 성공
            storageRef.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Log.d(TAG, "uploadPicture: " + downloadUri!!)

                    writeDb(downloadUri)
                } else {
                    // Handle failures
                    // ...
                }
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun resizeBitmapImage(source: Bitmap, maxResolution: Int): Bitmap {
        val width = source.width
        val height = source.height
        var newWidth = width
        var newHeight = height
        var rate: Float

        if (width > height) {
            if (maxResolution < width) {
                rate = maxResolution / width.toFloat()
                newHeight = (height * rate).toInt()
                newWidth = maxResolution
            }
        } else {
            if (maxResolution < height) {
                rate = maxResolution / height.toFloat()
                newWidth = (width * rate).toInt()
                newHeight = maxResolution
            }
        }

        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true)
    }

    private fun CheckPermission() {

        // Use fields to define the data types to return.
        val placeFields = Arrays.asList(Place.Field.NAME)

        // Use the builder to create a FindCurrentPlaceRequest.
        val request = FindCurrentPlaceRequest.builder(placeFields).build()


        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            placesClient?.findCurrentPlace(request)?.addOnSuccessListener { response ->
                for (placeLikelihood in response.placeLikelihoods) {
                    Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                            placeLikelihood.place.name,
                            placeLikelihood.likelihood))

                }
            }?.addOnFailureListener { exception ->
                if (exception is ApiException) {
                    Log.e(TAG, "Place not found: " + exception.statusCode)
                }
            }
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            //getLocationPermission();
        }
    }

    private fun GetPlacement() {
        CheckPermission()

        val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_IMAGE_GALLAY && data != null) {
            val image = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, image)

                mPreviewImageView!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        //IMAGE_CAPTURE
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data!!.extras
            val imageBitmap = extras!!.get("data") as Bitmap

            mPreviewImageView!!.setImageBitmap(imageBitmap)
        }

        //AUTOCOMPLETE
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                DrawMarkOnMap(place)
                Log.i(TAG, "Place: " + place.name + ", " + place.id)
                mapLayout!!.visibility = View.VISIBLE

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.i(TAG, status.statusMessage)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private fun addPost(post: Map<String, Any>) {

        db.collection("post").document(ts)
                .set(post)
                .addOnSuccessListener { doc ->
                    mProgressBar!!.visibility = View.GONE
                    // 성공
                    Toast.makeText(this, "성공", Toast.LENGTH_SHORT).show()

                    NavigationFragment()
                    // 맨 위로
                    //mRecyclerView.smoothScrollToPosition(0);
                }
                .addOnFailureListener { e ->
                    mProgressBar!!.visibility = View.GONE
                    // 실패
                    Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show()
                }
    }

    private fun writeDb(downloadUri: Uri?) {
        tsLong = System.currentTimeMillis() / 1000
        ts = tsLong!!.toString()
        val docRef = db.collection("User").document(mUser!!.uid)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val theBody = mContentsArea!!.text.toString()
            //int age = Integer.parseInt(mAgeEditText.getText().toString());

            val post = HashMap<String, Any>()
            post["contents"] = theBody
            //post.put("age", age);
            post["downloadUrl"] = downloadUri!!.toString()
            post["uid"] = mUser!!.uid
            post["geopoint"] = GeoPoint(mplace!!.latLng!!.latitude, mplace!!.latLng!!.longitude)
            post["nickname"] = documentSnapshot.getString("nickname")!!
            post["timestamp"] = ts
            addPost(post)
            Log.e("!#@", "AA" + documentSnapshot.getString("nickname")!!)
        }


    }

    companion object {


        internal const val REQUEST_IMAGE_CAPTURE = 1
        internal const val AUTOCOMPLETE_REQUEST_CODE = 2
        internal const val REQUEST_IMAGE_GALLAY = 3

        val TAG = MainActivity::class.java.simpleName
    }
}
