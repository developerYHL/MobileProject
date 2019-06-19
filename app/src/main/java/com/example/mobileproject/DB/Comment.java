package com.example.mobileproject.DB;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mobileproject.Adapter.CommentRecyclerAdapter;
import com.example.mobileproject.R;
import com.example.mobileproject.holder.HomeItemHolder;
import com.example.mobileproject.model.CommentItem;
import com.example.mobileproject.model.DetailItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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


    public FirestoreRecyclerOptions<CommentItem> CommentQuery(DetailItem model){
        Query query = FirebaseFirestore.getInstance()
                .collection("post").document(model.getTimeStamp())
                .collection("comment")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<CommentItem> options = new FirestoreRecyclerOptions.Builder<CommentItem>()
                .setQuery(query, CommentItem.class)
                .build();
        return options;
    }

    public void CommentPost(EditText editText, DetailItem model, FirebaseFirestore db, CommentRecyclerAdapter adapter){
        String TAG = "CommentPost";
        //시간
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        FirebaseUser asd = FirebaseAuth.getInstance().getCurrentUser();


        DocumentReference docRef = db.collection("User").document(asd.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> docData = new HashMap<>();
                docData.put("contents", editText.getText().toString());
                docData.put("timestamp", new Date());
                docData.put("nickname", documentSnapshot.getString("nickname"));

                db.collection("post").document(model.getTimeStamp())
                        .collection("comment").document(ts)
                        .set(docData)
                        .addOnSuccessListener(aVoid -> {
                            editText.setText("");
//                    adapter.notifyDataSetChanged();
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        })
                        .addOnFailureListener(e ->
                                Log.w(TAG, "Error writing document", e));
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


    public int CommentOpenButton(SparseBooleanArray selectedItems, int position, int prePosition, FirestoreRecyclerAdapter adapter, EditText editText, FragmentActivity context){

        if (selectedItems.get(position)) {
            // 펼쳐진 Item을 클릭 시

            InputMethodManager immhide = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

            immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


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

    public void LinearLayoutAdapteronBindViewHolder(HomeItemHolder holder, int position, DetailItem model, FragmentActivity context, CommentRecyclerAdapter commentAdapter){
        holder.nickname.setText(model.getNickname());

        holder.contentsUserNickname.setText(model.getUid());

        Glide.with(holder.itemView)
                .load(model.getUserProfile())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.userProfile);

        Glide.with(holder.itemView)
                .load(model.getDownloadUrl())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.imageView);


        //holder.contents.setText(model.getContents() + "");
        Comment.getInstance().setReadMore(holder.contents, model.getContents() + "", 1);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        holder.commentRecyclerView.setLayoutManager(layoutManager);

        holder.commentRecyclerView.setHasFixedSize(true);

        commentAdapter = new CommentRecyclerAdapter(Comment.getInstance().CommentQuery(model));

        holder.commentRecyclerView.setAdapter(commentAdapter);

        if(commentAdapter != null){
            commentAdapter.startListening();
        }


    }

    public void Delete(FragmentActivity context, FirebaseFirestore db, DetailItem model){
        String TAG = "Delete";

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // 제목셋팅
        alertDialogBuilder.setTitle("게시물을 삭제");

        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage("게시물을 삭제")
                .setCancelable(false);

        alertDialogBuilder
                .setMessage("게시물을 삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("삭제",
                        (dialog, id) ->
                                db.collection("post").document(model.getTimeStamp())
                                .delete()
                                .addOnSuccessListener(aVoid ->{
                                    Toast.makeText(context, "삭제했습니다.", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        })
                                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e)))
                .setNegativeButton("취소",
                        (dialog, id) -> {
                            // 다이얼로그를 취소한다
                            dialog.cancel();
                        });

        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();

        // 다이얼로그 보여주기
        alertDialog.show();
    }

    public void PermissionDenied(FragmentActivity context){
        String TAG = "Delete";

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // 제목셋팅
        alertDialogBuilder.setTitle("게시물을 삭제");

        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage("게시물을 삭제")
                .setCancelable(false);

        alertDialogBuilder
                .setMessage("권한이 없습니다.")
                .setCancelable(false)

                .setNegativeButton("확인.",
                        (dialog, id) -> {
                            // 다이얼로그를 취소한다
                            dialog.cancel();
                        });


        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();

        // 다이얼로그 보여주기
        alertDialog.show();
    }

}
