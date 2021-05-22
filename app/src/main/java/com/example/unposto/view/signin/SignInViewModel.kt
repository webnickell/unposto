package com.example.unposto.view.signin

import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SignInViewModel  @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val credential = MutableLiveData<AuthCredential>()
    val authResult = credential.switchMap { credential ->
        liveData {
            val result = auth.signInWithCredential(credential).await()
            emit(result)

        }
    }

    fun signInWithCredentials(idToken: String) {

        val creds = GoogleAuthProvider.getCredential(idToken, null)
        credential.postValue(creds)
    }


}