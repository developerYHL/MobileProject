package com.example.mobileproject.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobileproject.R;

// 각각의 아이템의 레퍼런스를 저장할 뷰 홀더 클래스
// 반드시 RecyclerView.ViewHolder를 상속해야 함
public class HomeItemHolder extends RecyclerView.ViewHolder {

    public TextView nickname;
    public TextView contents;
    public TextView contentsUserNickname;

    public ImageView imageView;

    public ImageButton commentPost;
    public Button commentOpenButton;

    public RecyclerView commentRecyclerView;

    public ImageButton imageButton;

    public ImageView userProfile;
    //public Button more;

    public HomeItemHolder(View itemView) {
        super(itemView);
        nickname = (TextView) itemView.findViewById(R.id.title_text);
        contents = (TextView) itemView.findViewById(R.id.contents_text);
        contentsUserNickname = itemView.findViewById(R.id.contents_user_text);

        imageView = itemView.findViewById(R.id.imageView);
        userProfile = itemView.findViewById(R.id.miniprofil_imageview);

        commentPost = itemView.findViewById(R.id.comment_post_button);

        commentOpenButton = itemView.findViewById(R.id.comment_open_button);

        commentRecyclerView = itemView.findViewById(R.id.comment_recycler_view);

        imageButton = itemView.findViewById(R.id.imageButton);
    }
}
