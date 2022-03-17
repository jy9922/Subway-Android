package com.example.logintry2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*

class MainActivity : AppCompatActivity() {
    // 파이어베이스 인증
    private lateinit var firebaseAuth: FirebaseAuth
    // 구글 클라이언트
    private lateinit var googleSignInClient:GoogleSignInClient
    // private const val TAG="GoogleActivity"
    private val RC_SIGN_IN=99 // 구글 API 클라이언트
    var loginEmailOrGoogle="Google"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 구글 로그인 버튼
        btn_google.setOnClickListener { _ ->
            signIn()
        }

        // 이메일 회원가입
        register_btn.setOnClickListener { _ ->
            val registerIntent=Intent(this,RegisterActivity::class.java)
            startActivity(registerIntent)
        }

        // 구글 로그인 옵션 구성 requestToken 및 Email 요청
        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient=GoogleSignIn.getClient(this,gso)

        // 파이어베이스 인증 객체
        firebaseAuth=FirebaseAuth.getInstance()

        // 이메일 로그인
        login_btn.setOnClickListener {
            val email=et_id.text.toString()
            val password=et_pwd.text.toString()

            Log.d("MainActivity","Attempt login with email/pwd : $email/***")
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(){
                Log.d("MainActivity","Login Success !")
                val resultIntent=Intent(this,ResultActivity::class.java)
                startActivity(resultIntent)
                Toast.makeText(this,"로그인 성공",Toast.LENGTH_SHORT).show()
                loginEmailOrGoogle="Email" // 로그아웃 때문에 만든 변수
                client_name.text=email // 클라이언트 이름 : 이메일 이름
            }
            .addOnFailureListener(){
                Log.d("MainActivity","Failed to login : ${it.message}")
            }
        }
    }

    // 구글 onStart : 유저가 앱에 이미 구글 로그인을 했는지 확인
    public override fun onStart() {
        super.onStart()
        val account=GoogleSignIn.getLastSignedInAccount(this)
        if(account!==null){ // 이미 로그인 되어있을시 바로 메인 액티비티로 이동
            toResultActivity(firebaseAuth.currentUser)
        }
    } // onStart End

    // 구글 onActivityResult
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
        if(requestCode==RC_SIGN_IN){
            val task=GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account=task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e:ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("MainActivity","Google sign in failed",e)
            }
        }
    } // onActivityResult End

    // 구글 firebaseAuthWithGoogle
    private fun firebaseAuthWithGoogle(acct:GoogleSignInAccount){
        Log.d("MainActivity","firebaseAuthWithGoogle:"+acct.id!!)

        // Google SignIn Account 객체에서 ID 토큰을 가져와서 Firebase Auth로 교환하고 Firebase에 인증
        val credential=GoogleAuthProvider.getCredential(acct.idToken,null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this){task->
                if(task.isSuccessful){
                    Log.w("MainActivity","firebaseAuthWithGoogle 성공",task.exception)
                    toResultActivity(firebaseAuth.currentUser)
                } else {
                    Log.w("MainActivity","firebaseAuthWithGoogle 실패",task.exception)
                    Toast.makeText(this@MainActivity,"로그인에 실패하였습니다.",Toast.LENGTH_SHORT).show()
                }
            }
    } // firebaseAuthWithGoogle END

    // 구글 toResultActivity
    fun toResultActivity(user: FirebaseUser?){
        if(user!=null){ // ResultActivity로 이동
            startActivity(Intent(this,ResultActivity::class.java))
            finish()
        }
    } // toResultActivity End

    // 구글 signIn
    private fun signIn() {
        val signInIntent=googleSignInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    } // signIn End

//    override fun Onclick(p0: View?){ // ???

//    }

//    // 구글 파이어베이스 로그아웃
//    private fun signOut() {
//        // Firebase sign out
//        firebaseAuth.signOut()
//        // Google sign out
//        googleSignInClient.signOut().addOnCompleteListener(this){
//            // updateUI(null)
//        }
//    }

    // 구글 파이어베이스 회원 탈퇴
    private fun revokeAccess() {
        // Firebase sign out
        firebaseAuth.signOut()
        googleSignInClient.revokeAccess().addOnCompleteListener(this){

        }
    }

}
