package com.example.mobileproject.DB;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mobileproject.R;
import com.example.mobileproject.model.DetailItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Comment {
    private static final Comment instance = new Comment();

    private Comment() {}

    public static Comment getInstance() {

        return instance;
    }


    public void CommentPost(EditText editText, DetailItem model, FirebaseFirestore db){
        String TAG = "CommentPost";
        //시간
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        Map<String, Object> docData = new HashMap<>();
        docData.put("contents", editText.getText().toString());
        docData.put("timestamp", new Date());
        docData.put("nickname", model.getNickname());

        db.collection("post").document(model.getTimeStamp())
                .collection("comment").document(ts)
                .set(docData)
                .addOnSuccessListener(aVoid -> {
                    editText.setText("");
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error writing document", e));
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


    public int CommentOpenButton(SparseBooleanArray selectedItems, int position, int prePosition, FirestoreRecyclerAdapter adapter){
        if (selectedItems.get(position)) {
            // 펼쳐진 Item을 클릭 시
            Log.e("delete1","delete");
            selectedItems.delete(position);
        } else {
            // 직전의 클릭됐던 Item의 클릭상태를 지움
            Log.e("delete2","delete");
            selectedItems.delete(prePosition);
            // 클릭한 Item의 position을 저장
            selectedItems.put(position, true);
        }
        // 해당 포지션의 변화를 알림
        if (prePosition != -1) adapter.notifyItemChanged(prePosition);
        adapter.notifyItemChanged(position);
        // 클릭된 position 저장
        prePosition = position;

        return prePosition;
    }

}
