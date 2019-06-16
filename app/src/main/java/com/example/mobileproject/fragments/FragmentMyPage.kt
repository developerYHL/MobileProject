package com.example.mobileproject.fragments

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.example.mobileproject.Activity.MainActivity
import com.example.mobileproject.Adapter.MyPageRecyclerAdapter
import com.example.mobileproject.ItemClickSupport
import com.example.mobileproject.R
import com.example.mobileproject.holder.HomeItemHolder
import com.example.mobileproject.model.DetailItem
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class FragmentMyPage : Fragment() {
    //private FirestoreRecyclerAdapter mAdapter;
    private var mAdapter: MyPageRecyclerAdapter? = null
    private var linearAdapter: FirestoreRecyclerAdapter<*, *>? = null
    private val storage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private var recyclerView: RecyclerView? = null
    private var linearRecyclerView: RecyclerView? = null
    private var commentRecyclerView: RecyclerView? = null

    private var gridItemLayout: LinearLayout? = null
    private var linearItemLayout: LinearLayout? = null
    private var CommentLayout: RelativeLayout? = null

    private var mUser: FirebaseUser? = null

    internal var detailItem: DetailItem? = null

    private var changeGridViewButton: Button? = null
    private var changeLinearViewButton: Button? = null

    private var idTextView: TextView? = null
    private var idImageView: ImageView? = null

    // Item의 클릭 상태를 저장할 array 객체
    private val selectedItems = SparseBooleanArray()
    // 직전에 클릭됐던 Item의 position
    private var prePosition = -1
    //private ImageView imageView2;


    private var listItems: Array<String>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_page, container, false)
        mUser = FirebaseAuth.getInstance().currentUser

        recyclerView = view.findViewById(R.id.mypage_grid_recycler_view)
        linearRecyclerView = view.findViewById(R.id.mypage_linear_recycler_view)
        commentRecyclerView = view.findViewById(R.id.comment_recycler_view)
        CommentLayout = view.findViewById(R.id.comment_layout)

        changeGridViewButton = view.findViewById(R.id.change_grid_button)
        changeLinearViewButton = view.findViewById(R.id.change_Linear_button)

        gridItemLayout = view.findViewById(R.id.grid_item_layout)
        linearItemLayout = view.findViewById(R.id.linear_item_layout)

        idTextView = view.findViewById(R.id.ID)
        idImageView = view.findViewById(R.id.id_imageview)

        recyclerView!!.setHasFixedSize(false)

        val gridLayoutManager = GridLayoutManager(activity, 3)
        recyclerView!!.layoutManager = gridLayoutManager
        recyclerView!!.addItemDecoration(SpacesItemDecoration(1))

        val linearLayoutManager = LinearLayoutManager(activity)
        linearRecyclerView!!.layoutManager = linearLayoutManager
        linearRecyclerView!!.addItemDecoration(SpacesItemDecoration(1))

        val linearParams = linearItemLayout!!.layoutParams as LinearLayout.LayoutParams
        val gridParams = gridItemLayout!!.layoutParams as LinearLayout.LayoutParams

        changeGridViewButton!!.setOnClickListener {
            linearParams.weight = 0f
            linearItemLayout!!.layoutParams = linearParams

            gridParams.weight = 1f
            gridItemLayout!!.layoutParams = gridParams

            gridItemLayout!!.visibility = View.VISIBLE
            linearItemLayout!!.visibility = View.INVISIBLE
        }

        changeLinearViewButton!!.setOnClickListener {
            linearParams.weight = 1f
            linearItemLayout!!.layoutParams = linearParams

            gridParams.weight = 0f
            gridItemLayout!!.layoutParams = gridParams

            gridItemLayout!!.visibility = View.INVISIBLE
            linearItemLayout!!.visibility = View.VISIBLE
        }

        ItemClickSupport.addTo(recyclerView!!).setOnItemClickListener { recyclerView, position, v -> Log.e("123", "???") }

        ItemClickSupport.addTo(recyclerView!!).setOnItemLongClickListener { recyclerView, position, v ->
            Log.e("321", "!!!")
            true
        }

        ItemClickSupport.addTo(linearRecyclerView!!).setOnItemClickListener { recyclerView, position, v ->
            if (selectedItems.get(position)) {
                // 펼쳐진 Item을 클릭 시
                Log.e("delete1", "delete")
                selectedItems.delete(position)
            } else {
                // 직전의 클릭됐던 Item의 클릭상태를 지움
                Log.e("delete2", "delete")
                selectedItems.delete(prePosition)
                // 클릭한 Item의 position을 저장
                selectedItems.put(position, true)
            }
            // 해당 포지션의 변화를 알림
            if (prePosition != -1) linearAdapter!!.notifyItemChanged(prePosition)
            linearAdapter!!.notifyItemChanged(position)
            // 클릭된 position 저장
            prePosition = position
        }

        ItemClickSupport.addTo(linearRecyclerView!!).setOnItemLongClickListener { recyclerView, position, v ->
            Log.e("321", "!!!")
            true
        }

        idImageView!!.setOnClickListener { v ->
            // Firebase에 추가
            listItems = arrayOf("사진 촬영", "엘범에서 선택", "기본 이미지")
            val mBuilder = AlertDialog.Builder(activity!!)
            mBuilder.setTitle("촬영")
            mBuilder.setIcon(R.drawable.icon)
            mBuilder.setSingleChoiceItems(listItems, -1) { dialog, which ->
                if (which == 0) {
                    dispatchTakePictureIntent()
                } else if (which == 1) {
                    val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(i, REQUEST_IMAGE_GALLAY)
                } else {
                    deleteDb()
                    idImageView!!.setImageResource(R.drawable.ic_menu_camera)
                }
                dialog.dismiss()
            }
            mBuilder.setNeutralButton("Cancel") { dialog, which -> }
            val mDialog = mBuilder.create()
            mDialog.show()
        }

        showID()
        queryData()
        LinearQueryData()
        return view
    }

    private fun uploadPicture() {
        val storageRef = storage.reference
                .child("images/" + System.currentTimeMillis() + ".jpg")

        idImageView!!.isDrawingCacheEnabled = true
        idImageView!!.buildDrawingCache()
        var bitmap = (idImageView!!.drawable as BitmapDrawable).bitmap

        // 이미지 줄이기
        bitmap = resizeBitmapImage(bitmap, 300)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener { exception -> Toast.makeText(activity, "업로드에 실패했습니다.", Toast.LENGTH_SHORT).show() }.addOnSuccessListener { taskSnapshot ->
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
        if (takePictureIntent.resolveActivity(activity!!.packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun resizeBitmapImage(source: Bitmap, maxResolution: Int): Bitmap {
        val width = source.width
        val height = source.height
        var newWidth = width
        var newHeight = height
        var rate = 0.0f

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

    private fun writeDb(downloadUri: Uri) {
        val post = HashMap<String, Any>()
        post["downloadUrl"] = downloadUri.toString()

        db.collection("User").document(mUser!!.uid)
                .set(post, SetOptions.merge())
                .addOnSuccessListener { Toast.makeText(activity, "성공", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener { Toast.makeText(activity, "실패", Toast.LENGTH_SHORT).show() }
    }

    private fun deleteDb() {
        val post = HashMap<String, Any>()
        post["downloadUrl"] = FieldValue.delete()

        db.collection("User").document(mUser!!.uid)
                .set(post, SetOptions.merge())
                .addOnSuccessListener { Toast.makeText(activity, "성공", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener { Toast.makeText(activity, "실패", Toast.LENGTH_SHORT).show() }
    }

    private fun changeVisibility(isExpanded: Boolean) {
        // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
        val dpValue = 150
        val d = activity!!.resources.displayMetrics.density
        val height = (dpValue * d).toInt()

        // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
        val va = if (isExpanded) ValueAnimator.ofInt(0, height) else ValueAnimator.ofInt(height, 0)
        // Animation이 실행되는 시간, n/1000초
        va.duration = 600
        va.addUpdateListener { animation ->
            // value는 height 값
            val value = animation.animatedValue as Int
            // imageView의 높이 변경
            CommentLayout!!.layoutParams.height = value
            CommentLayout!!.requestLayout()
            // imageView가 실제로 사라지게하는 부분
            CommentLayout!!.visibility = if (isExpanded) View.VISIBLE else View.GONE
        }
        // Animation start
        va.start()
    }

    override fun onStart() {
        super.onStart()
        if (mAdapter != null)
            linearAdapter!!.startListening()
        mAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        linearAdapter!!.stopListening()
        mAdapter!!.stopListening()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_GALLAY && data != null) {
            val image = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, image)
                idImageView!!.setImageBitmap(bitmap)
                uploadPicture()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        //IMAGE_CAPTURE
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data!!.extras
            val imageBitmap = extras!!.get("data") as Bitmap
            idImageView!!.setImageBitmap(imageBitmap)
            uploadPicture()
        }
    }

    private fun CommentQueryData() {


        val query = FirebaseFirestore.getInstance()
                .collection("post")
                .whereEqualTo("uid", mUser!!.uid)

        val options = FirestoreRecyclerOptions.Builder<DetailItem>()
                .setQuery(query, DetailItem::class.java)
                .build()

        mAdapter = object : com.example.mobileproject.Adapter.MyPageRecyclerAdapter(options) {

        }

        recyclerView!!.adapter = mAdapter
    }

    private fun queryData() {
        val query = FirebaseFirestore.getInstance()
                .collection("post")
                .whereEqualTo("uid", mUser!!.uid)

        val options = FirestoreRecyclerOptions.Builder<DetailItem>()
                .setQuery(query, DetailItem::class.java)
                .build()

        mAdapter = object : com.example.mobileproject.Adapter.MyPageRecyclerAdapter(options) {

        }

        recyclerView!!.adapter = mAdapter
    }

    private fun showID() {
        val db: FirebaseFirestore
        db = FirebaseFirestore.getInstance()
        val docRef = db.collection("User").document(mUser!!.uid)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            idTextView!!.text = documentSnapshot.getString("nickname")
            if (documentSnapshot.getString("downloadUrl") != null) {
                Glide.with(activity!!)
                        .load(documentSnapshot.getString("downloadUrl"))
                        .into(idImageView!!)
            } else {
                idImageView!!.setImageResource(R.drawable.ic_menu_camera)
            }
        }

    }

    private fun LinearQueryData() {
        val query = FirebaseFirestore.getInstance()
                .collection("post")
                .whereEqualTo("uid", mUser!!.uid)

        val options = FirestoreRecyclerOptions.Builder<DetailItem>()
                .setQuery(query, DetailItem::class.java)
                .build()

        linearAdapter = object : com.example.mobileproject.Adapter.FirestoreRecyclerAdapter(options) {
            override fun onBindViewHolder(holder: HomeItemHolder, position: Int, model: DetailItem) {
                // Bind the Chat object to the ChatHolder
                // ...
                holder.nickname.text = model.nickname
                holder.contents.text = model.contents + ""
                //Log.e("log",model.getTimeStamp());

                Glide.with(holder.itemView)
                        .load(model.downloadUrl)
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher)
                        .into(holder.imageView)

                changeVisibility(selectedItems.get(position))
            }

            override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): HomeItemHolder {
                val view = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.item_detail, viewGroup, false)
                CommentLayout = view.findViewById(R.id.comment_layout)
                return HomeItemHolder(view)
            }
        }

        linearRecyclerView!!.adapter = linearAdapter
    }

    companion object {

        private val TAG = MainActivity::class.java.simpleName
        internal val REQUEST_IMAGE_CAPTURE = 1
        internal val REQUEST_IMAGE_GALLAY = 3
    }
}// Required empty public constructor

internal class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = space
        outRect.right = space
        outRect.bottom = space

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space
        } else {
            outRect.top = 0
        }
    }
}