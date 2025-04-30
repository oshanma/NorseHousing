package com.example.roommatematch.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.nku.classapp.R
import edu.nku.classapp.home

class Register : AppCompatActivity() {

    private var currentStep = 0
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var name: String
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        showStep(currentStep)
    }

    private fun showStep(step: Int) {
        when (step) {
            0 -> {
                setContentView(R.layout.step_register_email)
                highlightProgress(step)

                val btnNext = findViewById<Button>(R.id.btnNextEmail)
                val emailInput = findViewById<EditText>(R.id.editEmail)
                val btnBack = findViewById<ImageButton>(R.id.btnBack)

                btnBack?.setOnClickListener { finish() }

                btnNext.setOnClickListener {
                    val enteredEmail = emailInput.text.toString().trim()
                    if (enteredEmail.isEmpty()) {
                        Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                    } else {
                        email = enteredEmail
                        currentStep = 1
                        showStep(currentStep)
                    }
                }
            }

            1 -> {
                setContentView(R.layout.step_register_password)
                highlightProgress(step)

                val btnNext = findViewById<Button>(R.id.btnSubmit)
                val passInput = findViewById<EditText>(R.id.editPassword)
                val confirmInput = findViewById<EditText>(R.id.editConfirmPassword)
                val btnBack = findViewById<ImageButton>(R.id.btnBack)

                btnBack?.setOnClickListener {
                    currentStep = 0
                    showStep(currentStep)
                }

                btnNext.setOnClickListener {
                    val pass = passInput.text.toString().trim()
                    val confirm = confirmInput.text.toString().trim()

                    if (pass.length < 6) {
                        Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    } else if (pass != confirm) {
                        Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                    } else {
                        password = pass
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                                    currentStep = 2
                                    showStep(currentStep)
                                } else {
                                    Toast.makeText(this, "Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                }
            }

            2 -> {
                setContentView(R.layout.step_register_name)
                highlightProgress(step)

                val editName = findViewById<EditText>(R.id.editName)
                val btnNext = findViewById<Button>(R.id.btnNextName)
                val btnBack = findViewById<ImageButton>(R.id.btnBack)

                btnBack?.setOnClickListener {
                    currentStep = 1
                    showStep(currentStep)
                }

                btnNext.setOnClickListener {
                    val enteredName = editName.text.toString().trim()
                    if (enteredName.isEmpty()) {
                        Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show()
                    } else {
                        name = enteredName
                        currentStep = 3
                        showStep(currentStep)
                    }
                }
            }

            3 -> {
                setContentView(R.layout.step_register_student_info)
                highlightProgress(step)

                val editDorm = findViewById<EditText>(R.id.editDorm)
                val editMajor = findViewById<EditText>(R.id.editMajor)
                val editClassYear = findViewById<EditText>(R.id.editClassYear)
                val btnFinish = findViewById<Button>(R.id.btnFinish)
                val btnBack = findViewById<ImageButton>(R.id.btnBack)

                btnBack?.setOnClickListener {
                    currentStep = 2
                    showStep(currentStep)
                }

                btnFinish.setOnClickListener {
                    val dorm = editDorm.text.toString().trim()
                    val major = editMajor.text.toString().trim()
                    val classYear = editClassYear.text.toString().trim()

                    if (dorm.isEmpty() || major.isEmpty() || classYear.isEmpty()) {
                        Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
                    } else {
                        val user = auth.currentUser
                        user?.let {
                            val userData = hashMapOf(
                                "Name" to name,
                                "Dorms" to dorm,
                                "Major" to major,
                                "classYear" to classYear.toIntOrNull(),
                                "email" to email
                            )

                            firestore.collection("users").document(user.uid).set(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Profile completed!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, home::class.java))
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
            }
        }
    }

    private fun highlightProgress(step: Int) {
        val filledColor = "#4A90E2"
        val pendingColor = "#CCCCCC"

        val root = findViewById<LinearLayout>(R.id.progressContainer)
        if (root != null) {
            for (i in 0 until root.childCount) {
                val segment = root.getChildAt(i)
                segment.setBackgroundColor(android.graphics.Color.parseColor(if (i <= step) filledColor else pendingColor))
            }
        }
    }
}
