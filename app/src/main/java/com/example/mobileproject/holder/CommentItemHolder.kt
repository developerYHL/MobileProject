package com.example.mobileproject.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import com.example.mobileproject.R

class CommentItemHolder
//public ImageView imageView;
//public Button share;
//public Button more;

(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var nickname: TextView = itemView.findViewById<View>(R.id.comment_nickname_textview) as TextView
    var contents: TextView = itemView.findViewById<View>(R.id.comment_contents_textview) as TextView

    init {

        //imageView = itemView.findViewById(R.id.imageView);
    }
}
