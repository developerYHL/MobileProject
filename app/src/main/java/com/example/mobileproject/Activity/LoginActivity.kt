package com.example.mobileproject.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import com.example.mobileproject.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*

class LoginActivity : AppCompatActivity() {
    private val mAuth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore
    private var mUser: FirebaseUser? = null

    private var context: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        context = this


        val providers = Arrays.asList(
                AuthUI.IdpConfig.EmailBuilder().build())

        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.email_login).setOnClickListener {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        db = FirebaseFirestore.getInstance()
        mUser = FirebaseAuth.getInstance().currentUser

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                CheckSetNickName()

                // ...
                //startActivity(new Intent(this, MainActivity.class));
                //finish();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    private fun CheckSetNickName() {
        val TAG = "CheckSetNickName"

        val docRef = db.collection("User").document(mUser!!.uid)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    if (document.getString("nickname") != null) {
                        startActivity(Intent(context, MainActivity::class.java))
                        finish()
                    } else {
                    }
                } else {
                    AlertDialog()


                    Log.d(TAG, "No such document")
                }
            } else {
                Log.d(TAG, "get failed with ", task.exception)
            }
        }
    }

    private fun AlertDialog() {
        val TAG = "AlertDialog"
        val alert = AlertDialog.Builder(this)

        alert.setTitle("이름을 입력하세요")
        //alert.setMessage("Pz, input yourname");


        val name = EditText(this)
        alert.setView(name)

        alert.setPositiveButton("저장") { dialog, whichButton ->
            val username = name.text.toString()

            //add 닉네임 db
            val user = HashMap<String, Any>()
            user["nickname"] = username

            db.collection("User").document(mUser!!.uid)
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener {
                        startActivity(Intent(context, MainActivity::class.java))
                        finish()
                        Log.d(TAG, "DocumentSnapshot successfully written!")
                    }
                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }


        //        alert.setNegativeButton("no",new DialogInterface.OnClickListener() {
        //            public void onClick(DialogInterface dialog, int whichButton) {
        //
        //            }
        //        });

        alert.show()
    }

    companion object {

        private const val RC_SIGN_IN = 1000
    }
}
