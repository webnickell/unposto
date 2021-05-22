package com.example.unposto.view.posts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.unposto.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPostsFragment : Fragment() {
    private val TAG = "MyPostsFragment"
    private val signInViewModel: MyPostsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_list, container, false)

        if(savedInstanceState == null) {
            val fabAddPost = view.findViewById<FloatingActionButton>(R.id.button_add_new_post)
            fabAddPost.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.action_myPostsFragment_to_newPostFragment)
            }

            val list = view.findViewById<RecyclerView>(R.id.post_list)
            val layoutManager = LinearLayoutManager(context)
            list.layoutManager = layoutManager
            val adapter = MyPostsRecyclerViewAdapter()
            list.adapter = adapter
            signInViewModel.posts.observe(viewLifecycleOwner) {
                Log.i(TAG, "${it}")
                adapter.setData(it)
            }
        }

        signInViewModel.loadPosts(true)

        return view
    }


}