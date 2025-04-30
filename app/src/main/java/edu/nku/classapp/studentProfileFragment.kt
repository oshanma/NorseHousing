package edu.nku.classapp

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.roommatematch.activities.Login

class studentProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvDorm: TextView
    private lateinit var tvMajor: TextView
    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_profile, container, false)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Get references
        tvName = view.findViewById(R.id.tvName)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvDorm = view.findViewById(R.id.tvDorm)
        tvMajor = view.findViewById(R.id.tvMajor)
        btnLogout = view.findViewById(R.id.btnLogout)

        val user = auth.currentUser
        if (user != null) {
            tvEmail.text = "Email: ${user.email}"

            // Fetch profile data using UID
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        tvName.text = "Name: ${document.getString("Name")}"
                        tvDorm.text = "Dorm: ${document.getString("Dorms")}"
                        tvMajor.text = "Major: ${document.getString("Major")}"
                    }
                }
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), Login::class.java))
            requireActivity().finish()
        }

        return view
    }
}
