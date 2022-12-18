package com.example.mvvmrecyclerview.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmrecyclerview.Navigator
import com.example.mvvmrecyclerview.UsersActionListener
import com.example.mvvmrecyclerview.UsersAdapter
import com.example.mvvmrecyclerview.databinding.FragmentUsersListBinding
import com.example.mvvmrecyclerview.model.User
import com.example.mvvmrecyclerview.tasks.EmptyResult
import com.example.mvvmrecyclerview.tasks.ErrorResult
import com.example.mvvmrecyclerview.tasks.PendingResult
import com.example.mvvmrecyclerview.tasks.SuccessResult

class UsersListFragment: Fragment() {

    private lateinit var binding: FragmentUsersListBinding
    private lateinit var adapter: UsersAdapter

    //доступ к viewmodel
    private val viewModel: UsersListViewModel by viewModels{factory()}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersListBinding.inflate(inflater, container, false)
        adapter = UsersAdapter(viewModel)

        //подписка на данные viewmodel
        viewModel.users.observe(viewLifecycleOwner, Observer {
            hideAll()
            when(it){
                is SuccessResult ->{
                    binding.recyclerView.visibility = View.VISIBLE
                    adapter.users = it.data
                }
                is ErrorResult ->{
                    binding.tryAgainContainer.visibility = View.VISIBLE
                }
                is PendingResult ->{
                    binding.progressBar.visibility = View.VISIBLE
                }
                //если данных нет
                is EmptyResult ->{
                    binding.noUsersTextView.visibility = View.VISIBLE
                }
            }
        })

        viewModel.actionShowDetails.observe(viewLifecycleOwner, Observer {
            it.getValue()?.let { user -> navigator().showDetails(user) }
        })
        viewModel.actionShowToast.observe(viewLifecycleOwner, Observer {
            it.getValue()?.let { messageRes -> navigator().toast(messageRes) }
        })

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        return binding.root
    }

    private fun hideAll(){
        binding.recyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.tryAgainContainer.visibility = View.GONE
        binding.noUsersTextView.visibility = View.GONE
    }
}