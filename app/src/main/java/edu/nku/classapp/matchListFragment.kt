package edu.nku.classapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import edu.nku.classapp.adapter.MatchesAdapter
import edu.nku.classapp.model.Match

class matchListFragment : Fragment() {

    private lateinit var matchesRecycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorMessage: TextView

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val matchList = mutableListOf<Match>()
    private lateinit var matchesAdapter: MatchesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_match_list, container, false)

        matchesRecycler = view.findViewById(R.id.matchesRecycler)
        progressBar = view.findViewById(R.id.progress_bar)
        errorMessage = view.findViewById(R.id.error_message)

        matchesRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        matchesAdapter = MatchesAdapter(matchList) {} // empty onClick
        matchesRecycler.adapter = matchesAdapter

        loadMatches()
        return view
    }

    private fun loadMatches() {
        progressBar.visibility = View.VISIBLE
        errorMessage.visibility = View.GONE

        val currentUid = auth.currentUser?.uid ?: return

        firestore.collection("users").document(currentUid).get()
            .addOnSuccessListener { document ->
                val matches = document.get("liked") as? List<String> ?: emptyList()
                if (matches.isEmpty()) {
                    progressBar.visibility = View.GONE
                    return@addOnSuccessListener
                }

                firestore.collection("users")
                    .whereIn(FieldPath.documentId(), matches)
                    .get()
                    .addOnSuccessListener { result ->
                        matchList.clear()
                        for (doc in result) {
                            val name = doc.getString("Name") ?: "Unknown"
                            val imageURL = (doc.get("imageURL") as? List<*>)?.firstOrNull() as? String ?: ""
                            val uid = doc.id
                            matchList.add(Match(uid, name, imageURL))
                        }
                        matchesAdapter.notifyDataSetChanged()
                        progressBar.visibility = View.GONE
                    }
                    .addOnFailureListener {
                        progressBar.visibility = View.GONE
                        errorMessage.visibility = View.VISIBLE
                        errorMessage.text = "Failed to load matches: ${it.message}"
                    }
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                errorMessage.visibility = View.VISIBLE
                errorMessage.text = "Failed to load user data: ${it.message}"
            }
    }
}
