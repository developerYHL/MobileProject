package com.example.mobileproject.DB;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mobileproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Comment {
    private static final Comment instance = new Comment();

    private Comment() {}

    public static Comment getInstance() {

        return instance;
    }


    public void CommentPost(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("post")
                .whereEqualTo("uid", mUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                        }
                    } else {
                    }
                });
    }

    public static void setReadMore(final TextView view, final String text, final int maxLine) {
        final Context context = view.getContext();
        final String expanedText = " ... 더보기";

        if (view.getTag() != null && view.getTag().equals(text)) { //Tag로 전값 의 text를 비교하여똑같으면 실행하지 않음.
            return;
        }




        view.setTag(text); //Tag에 text 저장
        view.setText(text); // setText를 미리 하셔야  getLineCount()를 호출가능
        //getLineCount()는 UI 백그라운드에서만 가져올수 있음
        view.post(() -> {
            if (view.getLineCount() > maxLine) { //Line Count가 설정한 MaxLine의 값보다 크다면 처리시작

                int lineEndIndex = view.getLayout().getLineVisibleEnd(maxLine - 1); //Max Line 까지의 text length

                List<String> lines = new ArrayList<>();
                int count = view.getLineCount();
                for (int line = 0; line < count; line++) {
                    int start = view.getLayout().getLineStart(line);
                    int end = view.getLayout().getLineEnd(line);
                    CharSequence substring = view.getText().subSequence(start, end);
                    lines.add(substring.toString());
                    Log.i("linesdd",lines.size() + "");
                    Log.i("linesdd","asd");

                }

                String[] split = text.split("\n"); //text를 자름

                int splitLength = 0;

                String lessText = "";
                for (String item : lines) {
                    splitLength += item.length() + 1;
                    if (splitLength >= lineEndIndex) { //마지막 줄일때!
                        if (item.length() >= expanedText.length()) {
                            lessText += item.substring(0, item.length() - (expanedText.length())-5) + expanedText;
                        } else {
                            lessText += item + expanedText;
                        }
                        break; //종료
                    }
                    //lessText += item + "\n";
                }
                SpannableString spannableString = new SpannableString(lessText);
                spannableString.setSpan(new ClickableSpan() {//클릭이벤트
                    @Override
                    public void onClick(View v) {
                        view.setText(text);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) { //컬러 처리
                        ds.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    }
                }, spannableString.length() - expanedText.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                view.setText(spannableString);
                view.setMovementMethod(LinkMovementMethod.getInstance());
            }
        });
    }



}
