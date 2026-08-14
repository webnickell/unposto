package com.example.unposto.view.newpost

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.unposto.data.firebase.FirebaseFileRepository
import com.example.unposto.data.firebase.FirebasePostRepository
import com.example.unposto.data.firebase.domain.FirebasePost
import com.example.unposto.util.IntentViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


sealed class NewPostIntent {
    data class SetDescription(val text: String) : NewPostIntent()
    data class SetGeolocation(val geolocation: GeoPoint) : NewPostIntent()
    data class SetImageUri(val imageUri: Uri) : NewPostIntent()
    object CreateNewPost : NewPostIntent()
}

sealed class NewPostState {
    data class Data(
        val description: String = "",
        val geolocation: GeoPoint? = null,
        val imageUri: Uri?  = null,
        val loading: Boolean = false,
    ) : NewPostState()

    object Loading: NewPostState()
    data class Created(val errors: List<Int> = listOf()): NewPostState()
}

@HiltViewModel
class NewPostViewModel @Inject constructor(
        private val auth: FirebaseAuth,
        private val postRepository: FirebasePostRepository,
        private val fileRepository: FirebaseFileRepository
) : IntentViewModel<NewPostIntent, NewPostState>(NewPostState.Data()) {


    override fun mapIntentOnState(
        intent: NewPostIntent,
        currentState: NewPostState,
    ): Flow<NewPostState> = flow {
        when (currentState) {
            is NewPostState.Data -> when (intent) {
                is NewPostIntent.SetDescription -> emit(
                    currentState.copy(description = intent.text)
                )
                is NewPostIntent.SetGeolocation -> emit(
                    currentState.copy(geolocation = intent.geolocation)
                )
                is NewPostIntent.SetImageUri -> emit(
                    currentState.copy(imageUri = intent.imageUri)
                )
                is NewPostIntent.CreateNewPost -> {
                    try {
                        emit(NewPostState.Loading)
                        val userId = auth.currentUser?.uid!!
                        val imageUrl = fileRepository.saveImage(currentState.imageUri!!)

                        val post = FirebasePost(
                            "",
                            userId,
                            currentState.description,
                            listOf(imageUrl.toString()),
                            Timestamp.now(),
                            currentState.geolocation!!,
                        )
                        val result = postRepository.createPost(post)
                        emit(NewPostState.Created())
                    } catch (e: Exception) {
                        throw e;
                        //TODO: set resource error text
                        emit(NewPostState.Created(listOf(0)))
                    }

                }
            }
        }

    }
}