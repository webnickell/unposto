package com.example.unposto.view.signin

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.unposto.R

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint


//client id
//566053955880-ld87jghndmehn7s60idnlsrp4njuioka.apps.googleusercontent.com
//client secret
//gW5ZUsFWXbg7JMbXNvLpp74Q

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private val TAG = "SIGN IN GOOGLE"

    private val signInViewModel: SignInViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            signInViewModel.signInWithCredentials(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(savedInstanceState == null) {
            val serverClientId = getString(R.string.default_web_client_id)
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(serverClientId)
                    .requestEmail()
                    .build()
            googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

            signInViewModel.authResult.observe(viewLifecycleOwner) {
                val navController = findNavController()
                navController.navigate(R.id.action_signInFragment_to_myPostsFragment)

                val user = it.user!!
                Toast.makeText(
                        context,
                        "User info uid ${user.uid} email ${user.email}",
                        Toast.LENGTH_LONG,
                ).show()

            }
        }


        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val usernameEditText = view.findViewById<EditText>(R.id.username)
        val passwordEditText = view.findViewById<EditText>(R.id.password)
        val loginButton = view.findViewById<Button>(R.id.login)
        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loading)*/
        val signInWithGoogleButton = view.findViewById<SignInButton>(R.id.button_google_sign_in)


        signInWithGoogleButton.setOnClickListener {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }


    }


    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }
}