package com.coventry.hkqipao.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.coventry.hkqipao.R
import com.coventry.hkqipao.databinding.FragmentProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment(), AdapterView.OnItemClickListener {

    companion object {
        private const val TAG = "ProfileFragment"
        private const val RC_SIGN_IN = 9001
        private val settingsTitles = arrayOf("Reservation Record", "Saved Inspiration")
    }

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebase_web_client_id))
            .requestEmail()
            .build()

        val listviewAccountSetting: ListView = binding.listviewAccountSetting
        val adapter = ArrayAdapter(requireContext(), R.layout.item_account_setting, R.id.settingTitle, settingsTitles)
        listviewAccountSetting.adapter = adapter
        listviewAccountSetting.onItemClickListener = this

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        // Initialize Firebase Auth
        auth = Firebase.auth

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // Handle item click event here
        val selectedItem = settingsTitles[position]

        // Start the new activity
        val intent = Intent(requireContext(), ReservationRecordActivity::class.java)
        intent.putExtra("selectedItem", selectedItem)
        startActivity(intent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun getUserProfile() {
        val user = Firebase.auth.currentUser
        val imageUserProfile: ImageView = binding.imageUserProfile
        val textUsername: TextView = binding.textUsername
        val buttonSignIn: SignInButton = binding.buttonGoogleSignin
        val buttonSignOut: Button = binding.buttonSignout
        user?.let {
            // Name, email address, and profile photo Url
            val name = it.displayName
            textUsername.text = name

            val email = it.email

            val photoUrl = it.photoUrl
            Glide.with(requireContext()).load(photoUrl).into(imageUserProfile)

            // Check if user's email is verified
            val emailVerified = it.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            val uid = it.uid

            buttonSignIn.visibility = View.GONE
            buttonSignOut.visibility = View.VISIBLE
            buttonSignOut.setOnClickListener {
                signOut()
            }

        } ?: run {
            textUsername.text = getString(R.string.profile_message_guest)
            Glide.with(requireContext()).load(R.drawable.ic_person_black_24dp).into(imageUserProfile)
            buttonSignOut.visibility = View.GONE
            buttonSignIn.visibility = View.VISIBLE
            buttonSignIn.setOnClickListener {
                signIn()
            }
        }
    }

    private fun signOut() {
        // [START auth_sign_out]
        Firebase.auth.signOut()
        updateUI(null)
        // [END auth_sign_out]
    }

    private fun updateUI(user: FirebaseUser?) {
        getUserProfile()
    }
}