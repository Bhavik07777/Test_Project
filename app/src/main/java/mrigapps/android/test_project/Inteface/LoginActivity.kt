package mrigapps.android.test_project.Inteface

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import mrigapps.android.test_project.R

class LoginActivity : AppCompatActivity() {


    private lateinit var edtemail: EditText
    private lateinit var edtpassword: EditText
    private lateinit var btnsignup: Button
    private lateinit var btnlogin: Button


    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        edtemail = findViewById(R.id.edemail)
        edtpassword = findViewById(R.id.edpassword)
        btnlogin = findViewById(R.id.btnlogin)
        btnsignup = findViewById(R.id.btnSignUp)

        mAuth = FirebaseAuth.getInstance()
        btnsignup.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            finish()
            startActivity(intent)
        }

        btnlogin.setOnClickListener {
            val email = edtemail.text.toString()
            val password = edtpassword.text.toString()

            login(email, password)
        }


    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {

                    Toast.makeText(
                        applicationContext,
                        "Please first enter details ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}