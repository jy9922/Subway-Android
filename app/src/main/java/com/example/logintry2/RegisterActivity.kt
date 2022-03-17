package com.example.logintry2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        reg_nextbtn.setOnClickListener {
            performRegister()
        }
    }

    // 이메일 로그인
    private fun performRegister() {
        val email=reg_etemail.text.toString()
        val password=reg_etpwd.text.toString()

        if(email.isEmpty()||password.isEmpty()){
            Toast.makeText(this,"이메일/비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity","Email is : "+email)
        Log.d("RegisterActivity","Password : $password")

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                // else if successful
                Log.d("RegisterActivity","Successfully created user with uid ${it.result?.user?.uid}")
                finish()
            }
            .addOnFailureListener {
                Log.d("RegisterActivity","Failed to create user : ${it.message}")
                if (it.message=="The email address is badly formatted.") {
                    Toast.makeText(this,"이메일의 형식이 잘못되었습니다.",Toast.LENGTH_SHORT).show()
                }
                if(it.message=="The given password is invalid. [ Password should be at least 6 characters ]"){
                    Toast.makeText(this,"비밀번호는 최소 6글자 이상이어야 합니다.",Toast.LENGTH_SHORT).show()
                }
            }
    }

}
