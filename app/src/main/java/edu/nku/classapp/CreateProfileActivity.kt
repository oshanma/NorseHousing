package edu.nku.classapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle

import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage



class CreateProfileActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var nameField: EditText
    private lateinit var yearField: EditText
    private lateinit var dormField: EditText
    private lateinit var majorField: EditText
    private lateinit var saveBtn: Button

    private val selectedImageUris = mutableListOf<Uri>()

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        imageView = findViewById(R.id.imageProfile)
        nameField = findViewById(R.id.editName)
        yearField = findViewById(R.id.editClassYear)
        dormField = findViewById(R.id.editDorm)
        majorField = findViewById(R.id.editMajor)
        saveBtn = findViewById(R.id.btnSaveProfile)

        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent, "Select Pictures"), 123)
        }

        saveBtn.setOnClickListener {
            saveProfile()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUris.clear()

            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val uri = data.clipData!!.getItemAt(i).uri
                    selectedImageUris.add(uri)
                }
            } else if (data.data != null) {
                selectedImageUris.add(data.data!!)
            }

            if (selectedImageUris.isNotEmpty()) {
                imageView.setImageURI(selectedImageUris[0]) // Preview first image
            }
        }
    }

    private fun saveProfile() {
        val uid = auth.currentUser?.uid ?: return

        val name = nameField.text.toString().trim()
        val year = yearField.text.toString().trim()
        val dorm = dormField.text.toString().trim()
        val major = majorField.text.toString().trim()

        if (name.isEmpty() || year.isEmpty() || dorm.isEmpty() || major.isEmpty() ){
            Toast.makeText(this, "Fill all fields and select images", Toast.LENGTH_SHORT).show()
            return
        }
// If no images selected, skip upload
        if (selectedImageUris.isEmpty()) {
            val userData = hashMapOf(
                "Name" to name,
                "classYear" to year.toInt(),
                "Dorms" to dorm,
                "Major" to major
            )

            firestore.collection("users").document(uid).set(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile saved without image!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, home::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show()
                }
            return
        }

        // If images selected, proceed to upload

        val downloadUrls = mutableListOf<String>()
        val totalImages = selectedImageUris.size
        var uploadedCount = 0

        for ((index, uri) in selectedImageUris.withIndex()) {
            val ref = storage.reference.child("profiles/${uid}_$index.jpg")
            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                        downloadUrls.add(downloadUrl.toString())
                        uploadedCount++

                        if (uploadedCount == totalImages) {
                            val userData = hashMapOf(
                                "Name" to name,
                                "classYear" to year.toInt(),
                                "Dorms" to dorm,
                                "Major" to major,
                                "imageURL" to downloadUrls
                            )

                            firestore.collection("users").document(uid).set(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, home::class.java))
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Upload failed for image $index", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
