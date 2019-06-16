package com.example.mobileproject.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import com.example.mobileproject.R

// 각각의 아이템의 레퍼런스를 저장할 뷰 홀더 클래스
// 반드시 RecyclerView.ViewHolder를 상속해야 함
class HomeItemHolder
//public Button more;

(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var nickname: TextView = itemView.findViewById<View>(R.id.title_text) as TextView
    var contents: TextView = itemView.findViewById<View>(R.id.contents_text) as TextView

    var imageView: ImageView = itemView.findViewById(R.id.imageView)

    var commentPost: ImageButton = itemView.findViewById(R.id.comment_post_button)

}
