package com.example.unposto.view.newpost

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.unposto.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewPostFragment  : Fragment() {
    private val newPostViewModel: NewPostViewModel by viewModels()
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { result ->
        val fileUri = result.first()
        photo.setImageURI(fileUri)
        newPostViewModel.addIntent(NewPostIntent.SetImageUri(fileUri))

    }
    private lateinit var photo: ImageView
    private lateinit var googleMap: GoogleMap
    private val TAG = "NewPostFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_post, container, false)

        if(savedInstanceState == null) {
            photo = view.findViewById<ImageView>(R.id.imageview_photo)

            val addPhotoButton = view.findViewById<Button>(R.id.button_add_photo)
            addPhotoButton.setOnClickListener {
                galleryLauncher.launch("image/*")
            }

            val editDescription = view.findViewById<EditText>(R.id.edittext_new_post_description)

            editDescription.addTextChangedListener(object: TextWatcher {

                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun  onTextChanged(s: CharSequence, start: Int,
                    before: Int, count: Int) {
                    newPostViewModel.addIntent(NewPostIntent.SetDescription(s.toString()))
                }
            })

            val createPostButton = view.findViewById<Button>(R.id.button_create_post)

            createPostButton.setOnClickListener {
                newPostViewModel.addIntent(NewPostIntent.CreateNewPost)
            }
            val textViewLocation = view.findViewById<TextView>(R.id.textview_location)
            val mapviewLocation = childFragmentManager.findFragmentById(R.id.map_container) as SupportMapFragment

            mapviewLocation.getMapAsync { it ->
                googleMap = it
                mapviewLocation.view?.isClickable = true
                val odessa = LatLng(46.4598865,30.5717043)
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            odessa,
                            11.0f,
                    ),
                )
                googleMap.addMarker(MarkerOptions()
                        .position(odessa)
                        .draggable(true)

                        .title("Marker in Odessa"))
                googleMap.setOnMarkerClickListener {
                    Log.i(TAG, "on click marker map")
                    newPostViewModel.addIntent(
                            NewPostIntent.SetGeolocation(GeoPoint(it.position.latitude, it.position.longitude)),
                    )
                    true
                }
                googleMap.setOnMapClickListener {

                    Log.i(TAG, "on click map")
                    newPostViewModel.addIntent(
                    NewPostIntent.SetGeolocation(GeoPoint(it.latitude, it.longitude)),
                ) }
            }


            newPostViewModel.state.observe(viewLifecycleOwner) {
                when (it) {
                    is NewPostState.Data -> {
                        val position = it.geolocation
                        textViewLocation.text =
                            if (position == null) "Select location"
                            else "Selected location (${it.geolocation.latitude}, ${it.geolocation.longitude})"
                    }
                    is NewPostState.Loading -> {}
                    is NewPostState.Created -> {
                        val navController = findNavController()
                        navController.popBackStack()
                    }
                }
            }
        }

        return view
    }




}