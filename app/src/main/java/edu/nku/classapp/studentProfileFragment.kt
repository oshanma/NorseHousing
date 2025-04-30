package edu.nku.classapp

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.roommatematch.activities.Login
import edu.nku.classapp.adapter.LikedStudentAdapter

class studentProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvDorm: TextView
    private lateinit var tvMajor: TextView
    private lateinit var btnLogout: Button
    private lateinit var imageProfile: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_profile, container, false)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Bind UI elements
        tvName = view.findViewById(R.id.tvName)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvDorm = view.findViewById(R.id.tvDorm)
        tvMajor = view.findViewById(R.id.tvMajor)
        btnLogout = view.findViewById(R.id.btnLogout)
        imageProfile = view.findViewById(R.id.imageProfile)

        val user = auth.currentUser
        if (user != null) {
            tvEmail.text = "Email: ${user.email}"

            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        tvName.text = "Name: ${document.getString("Name")}"
                        tvDorm.text = "Dorm: ${document.getString("Dorms")}"
                        tvMajor.text = "Major: ${document.getString("Major")}"

                        // Get first image from imageURL array
                        val imageUrls = document.get("imageURL") as? List<*>
                        val firstImageUrl = imageUrls?.firstOrNull() as? String

                        if (!firstImageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(firstImageUrl)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .into(imageProfile)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to load profile data.", Toast.LENGTH_SHORT).show()
                }
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), Login::class.java))
            requireActivity().finish()
        }
        // RecyclerView for liked users
        val recyclerLikedUsers = view.findViewById<RecyclerView>(R.id.recyclerLikedUsers)
        recyclerLikedUsers.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val likedList = mutableListOf<Pair<String, String>>()

        if (user != null) {
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    val likedUids = document.get("liked") as? List<String> ?: emptyList()

                    for (uid in likedUids) {
                        firestore.collection("users").document(uid).get()
                            .addOnSuccessListener { likedDoc ->
                                val name = likedDoc.getString("Name") ?: "Unknown"
                                val imageUrls = likedDoc.get("imageURL") as? List<*>
                                val image = imageUrls?.firstOrNull() as? String ?: ""

                                likedList.add(Pair(image, name))
                                recyclerLikedUsers.adapter = LikedStudentAdapter(requireContext(), likedList)
                            }
                    }
                }
        }


        return view
    }
}
