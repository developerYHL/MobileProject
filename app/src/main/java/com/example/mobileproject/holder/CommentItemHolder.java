package com.example.mobileproject.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.mobileproject.R;

public class CommentItemHolder  extends RecyclerView.ViewHolder {
    public TextView nickname;
    public TextView contents;


    //public ImageView imageView;
    //public Button share;
    //public Button more;

    public CommentItemHolder(View itemView) {
        super(itemView);
        nickname = (TextView) itemView.findViewById(R.id.comment_nickname_textview);
        contents = (TextView) itemView.findViewById(R.id.comment_contents_textview);

        //imageView = itemView.findViewById(R.id.imageView);
    }
}
