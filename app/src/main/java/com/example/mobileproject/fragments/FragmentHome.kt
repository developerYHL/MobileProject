package com.example.mobileproject.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.example.mobileproject.Activity.MainActivity
import com.example.mobileproject.Adapter.CommentRecyclerAdapter
import com.example.mobileproject.ItemClickSupport
import com.example.mobileproject.R
import com.example.mobileproject.holder.HomeItemHolder
import com.example.mobileproject.model.CommentItem
import com.example.mobileproject.model.DetailItem
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class FragmentHome : Fragment() {
    private var mAdapter: FirestoreRecyclerAdapter<*, *>? = null
    private var commentAdapter: FirestoreRecyclerAdapter<*, *>? = null

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var recyclerView: RecyclerView? = null
    private var commentView: RecyclerView? = null

    private val mPreviewImageView: ImageView? = null

    private val mProgressBar: ProgressBar? = null

    private var mUser: FirebaseUser? = null

    // Item의 클릭 상태를 저장할 array 객체
    private val selectedItems = SparseBooleanArray()
    // 직전에 클릭됐던 Item의 position
    private var prePosition = -1
    private var commentLayout: RelativeLayout? = null

    private var commentEditText: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        mUser = FirebaseAuth.getInstance().currentUser

        recyclerView = view.findViewById(R.id.recycler_view)
        commentView = view.findViewById(R.id.comment_recycler_view)

        commentLayout = view.findViewById(R.id.comment_layout)

        recyclerView!!.setHasFixedSize(false)
        //        commentView.setHasFixedSize(false);

        // 레이아웃 매니저로 LinearLayoutManager를 설정
        val layoutManager = LinearLayoutManager(activity)
        //        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        //        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView!!.layoutManager = layoutManager
        //        commentView.setLayoutManager(layoutManager);

        ItemClickSupport.addTo(recyclerView!!).setOnItemClickListener { recyclerView, position, v ->
            Log.e("123", "???")



            if (selectedItems.get(position)) {
                commentAdapter!!.stopListening()


                // 펼쳐진 Item을 클릭 시
                Log.e("delete1", "delete")
                selectedItems.delete(position)
            } else {
                commentAdapter!!.stopListening()

                // 직전의 클릭됐던 Item의 클릭상태를 지움
                Log.e("delete2", "delete")
                selectedItems.delete(prePosition)
                // 클릭한 Item의 position을 저장
                selectedItems.put(position, true)
            }
            // 해당 포지션의 변화를 알림
            if (prePosition != -1) mAdapter!!.notifyItemChanged(prePosition)
            mAdapter!!.notifyItemChanged(position)
            // 클릭된 position 저장
            prePosition = position
        }

        ItemClickSupport.addTo(recyclerView!!).setOnItemLongClickListener { recyclerView, position, v ->
            Log.e("321", "!!!")
            true
        }
        queryData()
        return view
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
            commentLayout!!.layoutParams.height = value
            commentLayout!!.requestLayout()
            // imageView가 실제로 사라지게하는 부분
            commentLayout!!.visibility = if (isExpanded) View.VISIBLE else View.GONE
        }
        // Animation start
        va.start()
    }

    override fun onStart() {
        super.onStart()
        if (mAdapter != null)
            mAdapter!!.startListening()

        if (commentAdapter != null)
            commentAdapter!!.startListening()

    }

    override fun onStop() {
        super.onStop()
        mAdapter!!.stopListening()
    }

    private fun commentQueryData(model: DetailItem) {
        val query = FirebaseFirestore.getInstance()
                .collection("post").document(model.timeStamp)
                .collection("comment")

        val options = FirestoreRecyclerOptions.Builder<CommentItem>()
                .setQuery(query, CommentItem::class.java)
                .build()

        commentAdapter = CommentRecyclerAdapter(options)

        commentView!!.adapter = commentAdapter
    }

    private fun queryData() {


        val query = FirebaseFirestore.getInstance()
                .collection("post")

        val options = FirestoreRecyclerOptions.Builder<DetailItem>()
                .setQuery(query, DetailItem::class.java)
                .build()

        mAdapter = object : com.example.mobileproject.Adapter.FirestoreRecyclerAdapter(options) {
            override fun onBindViewHolder(holder: HomeItemHolder, position: Int, model: DetailItem) {
                // Bind the Chat object to the ChatHolder
                // ...


                //model.setPostNum();
                holder.nickname.text = model.nickname
                holder.contents.text = model.contents + ""
                holder.commentPost.setOnClickListener { v ->

                    //시간
                    val tsLong = System.currentTimeMillis() / 1000
                    val ts = tsLong.toString()

                    val docData = HashMap<String, Any>()
                    docData["contents"] = commentEditText!!.text.toString()
                    docData["timestamp"] = Date()
                    docData["uid"] = mUser!!.uid


                    db.collection("post").document(model.timeStamp)
                            .collection("comment").document(ts)
                            .set(docData)
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }


                }
                commentQueryData(model)

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
                commentLayout = view.findViewById(R.id.comment_layout)
                commentEditText = view.findViewById(R.id.comment_edittext)
                commentView = view.findViewById(R.id.comment_recycler_view)

                return HomeItemHolder(view)
            }
        }

        recyclerView!!.adapter = mAdapter
    }

    companion object {

        private val TAG = MainActivity::class.java.simpleName
    }
}// Required empty public constructor
