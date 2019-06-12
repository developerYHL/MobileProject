package com.example.mobileproject.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobileproject.R;

public class CommentItemHolder  extends RecyclerView.ViewHolder {
    public TextView nickname;
    public TextView contents;

    public ImageView imageView;

    public Button share;
    public Button more;

    public CommentItemHolder(View itemView) {
        super(itemView);
        nickname = (TextView) itemView.findViewById(R.id.title_text);
        contents = (TextView) itemView.findViewById(R.id.contents_text);

        imageView = itemView.findViewById(R.id.imageView);
    }
}
