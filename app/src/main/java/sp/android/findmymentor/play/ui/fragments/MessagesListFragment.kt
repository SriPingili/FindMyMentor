package sp.android.findmymentor.play.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_messages_list.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.MainActivity
import sp.android.findmymentor.play.adapters.MessagesListAdapter
import sp.android.findmymentor.play.firebase.FirebaseSource
import sp.android.findmymentor.play.repository.MainRepository
import sp.android.findmymentor.play.ui.viewmodels.LoginViewModel
import sp.android.findmymentor.play.ui.viewmodels.MessagesListViewModel
import sp.android.findmymentor.play.ui.viewmodels.factories.MessagesListViewModelFactory
import sp.android.findmymentor.play.util.Constants

/*
* This Fragment is responsible for displaying the list of messages from different senders
* in a single page.
* */
class MessagesListFragment : Fragment(R.layout.fragment_messages_list) {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var viewModel: MessagesListViewModel
    private lateinit var messagesListAdapter: MessagesListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        setUpRecyclerView()
        setHasOptionsMenu(true)
        setListeners()
        setObservers()

        if (loginViewModel.loggedInMentor == null) {
            (activity as MainActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
            (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initViewModel() {
        loginViewModel = (activity as MainActivity).viewModel

        val mainRepository = MainRepository(FirebaseSource())
        loginViewModel.getLoggedInEmailAddress()?.let { email ->
            loginViewModel.getLoggedInUserName()?.let { userName ->
                viewModel = ViewModelProvider(this, MessagesListViewModelFactory(mainRepository, email, userName)).get(MessagesListViewModel::class.java)
            }
        }
    }

    private fun setUpRecyclerView() {
        messagesListAdapter = MessagesListAdapter()
        messagesListRecyclerView.adapter = messagesListAdapter
        messagesListRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun setListeners() {
        messagesListAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable(Constants.MESSAGE_ARG__KEY, it)
                putString(Constants.TITLE_ARG_KEY, it.sender_name)
            }
            findNavController().navigate(
                    R.id.action_messagesListFragment_to_messagingFragment,
                    bundle
            )
        }
    }

    private fun setObservers() {
        viewModel.messageSendersLiveData.observe(viewLifecycleOwner, Observer {

            if (it.size > 0) {
                messagesListRecyclerView.visibility = View.VISIBLE
                noMessagesTextView.visibility = View.GONE
            } else {
                messagesListRecyclerView.visibility = View.GONE
                noMessagesTextView.visibility = View.VISIBLE
            }
            messagesListAdapter.submitList(it.toList()) //https://stackoverflow.com/questions/56881149/diffutil-is-not-updating-the-recyclerview
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.edit_profile) {
            val bundle = Bundle().apply {
                putString(Constants.TITLE_ARG_KEY, getString(R.string.your_profile))
            }
            findNavController().navigate(
                    R.id.action_global_to_userProfileFormFragment,
                    bundle
            )
            return true
        }

        if (item.itemId == R.id.logout) {
            viewModel.messageSendersResponse.clear()
            loginViewModel.logout()

            findNavController().navigate(R.id.action_global_to_loginFragment)
            return true
        }

        return false
    }
}
