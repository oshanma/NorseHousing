package edu.nku.classapp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.nku.classapp.R

class matchListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_match_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = FirebaseFirestore.getInstance()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: return

        val likedYouContainer = view.findViewById<LinearLayout>(R.id.likedYouContainer)
        val matchesContainer = view.findViewById<LinearLayout>(R.id.matchesContainer)

        db.collection("users").get().addOnSuccessListener { result ->
            val allUsers = result.documents

            val currentUserDoc = allUsers.find { it.getString("Email") == currentUserEmail }
            val currentUserName = currentUserDoc?.getString("Name")?.trim() ?: return@addOnSuccessListener
            val currentLiked = (currentUserDoc.get("liked") as? List<*>)?.map { it.toString().trim() } ?: emptyList()

            var matchFound = false

            for (doc in allUsers) {
                val name = doc.getString("Name")?.trim() ?: continue
                val imageList = doc.get("imageURL") as? List<*>
                val imageUrl = imageList?.firstOrNull() as? String ?: continue
                val theirLiked = (doc.get("liked") as? List<*>)?.map { it.toString().trim() } ?: emptyList()

                if (name == currentUserName) continue

                // "You Liked" section - current user liked this person
                if (currentLiked.contains(name)) {
                    likedYouContainer.addView(createProfileView(requireContext(), name, imageUrl))
                }

                // Match section - both liked each other
                if (currentLiked.contains(name) && theirLiked.contains(currentUserName)) {
                    matchesContainer.addView(createProfileView(requireContext(), name, imageUrl))
                    matchFound = true
                }
            }

            if (!matchFound) {
                val noMatchView = TextView(requireContext()).apply {
                    text = "No matches found yet."
                    textSize = 14f
                    setTextColor(Color.GRAY)
                    setPadding(24, 24, 24, 24)
                }
                matchesContainer.addView(noMatchView)
            }
        }
    }

    private fun createProfileView(context: Context, name: String, imageUrl: String): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_profile_picture, null)
        val img = view.findViewById<ImageView>(R.id.profileImageView)
        val nameText = view.findViewById<TextView>(R.id.profileName)

        Glide.with(context)
            .load(imageUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(img)

        nameText.text = name

        view.setOnClickListener {
            Toast.makeText(context, "Clicked: $name", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
