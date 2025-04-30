import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import edu.nku.classapp.R

class StudentCharacterDetailFragment : Fragment() {

    private var studentName: String? = null
    private var studentEmail: String? = null
    private lateinit var profileImageView: ImageView

    private var imageUrls: List<String> = emptyList()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            studentName = it.getString(ARG_STUDENT_NAME)
            studentEmail = it.getString(ARG_STUDENT_EMAIL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_list_detail, container, false)

        val emailTextView: TextView = view.findViewById(R.id.student_email_detail)
        val nameTextView: TextView = view.findViewById(R.id.student_name_detail)
        profileImageView = view.findViewById(R.id.profileImage)

        emailTextView.text = studentEmail ?: "Email not provided"
        nameTextView.text = studentName ?: "Name not provided"

        fetchImageUrlsFromFirestore()

        return view
    }

    private fun fetchImageUrlsFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("Name", studentName) 
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    val images = doc.get("imageURL") as? List<*>
                    imageUrls = images?.filterIsInstance<String>() ?: emptyList()
                    if (imageUrls.isNotEmpty()) {
                        loadImage(imageUrls[currentIndex])
                        setupTapListener()
                    }
                }
            }
    }

    private fun setupTapListener() {
        profileImageView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val width = profileImageView.width
                val touchX = event.x

                if (touchX < width / 2) {
                    currentIndex = if (currentIndex > 0) currentIndex - 1 else imageUrls.size - 1
                } else {
                    currentIndex = (currentIndex + 1) % imageUrls.size
                }

                loadImage(imageUrls[currentIndex])
                true
            } else {
                false
            }
        }
    }

    private fun loadImage(url: String) {
        Glide.with(requireContext())
            .load(url)
            .placeholder(R.drawable.ic_launcher_foreground) 
            .into(profileImageView)
    }

    companion object {
        private const val ARG_STUDENT_NAME = "student_name"
        private const val ARG_STUDENT_EMAIL = "student_email"

        @JvmStatic
        fun newInstance(studentName: String, studentEmail: String) =
            StudentCharacterDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_STUDENT_NAME, studentName)
                    putString(ARG_STUDENT_EMAIL, studentEmail)
                }
            }
    }
}
