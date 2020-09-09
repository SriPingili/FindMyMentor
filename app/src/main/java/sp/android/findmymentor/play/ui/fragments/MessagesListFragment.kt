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


class MessagesListFragment : Fragment(R.layout.fragment_messages_list) {
    lateinit var loginViewModel: LoginViewModel
    lateinit var viewModel: MessagesListViewModel
    lateinit var messagesListAdapter: MessagesListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel = (activity as MainActivity).viewModel

        val mainRepository = MainRepository(FirebaseSource())

        loginViewModel.getLoggedInEmailAddress()?.let { email ->
            loginViewModel.getLoggedInUserName()?.let { userName ->
                viewModel = ViewModelProvider(this, MessagesListViewModelFactory(mainRepository, email, userName)).get(MessagesListViewModel::class.java)
            }
        }

        setUpRecyclerView()
        setHasOptionsMenu(true)

        if (loginViewModel.loggedInMentor == null) {
            setHasOptionsMenu(true)
            (activity as MainActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
            (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        messagesListAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("messageArg", it)
                putString("title", it.sender_name)
            }
            findNavController().navigate(
                    R.id.action_messagesListFragment_to_messagingFragment,
                    bundle
            )
        }

        viewModel.messageSendersLiveData.observe(viewLifecycleOwner, Observer {

            if (it.size > 0) {
                messagesListRecyclerView.visibility = View.VISIBLE
                no_messages_text_view.visibility = View.GONE
            } else {
                messagesListRecyclerView.visibility = View.GONE
                no_messages_text_view.visibility = View.VISIBLE
            }
            messagesListAdapter.submitList(it.toList()) //https://stackoverflow.com/questions/56881149/diffutil-is-not-updating-the-recyclerview
        })

    }

    private fun setUpRecyclerView() {
        messagesListAdapter = MessagesListAdapter()
        messagesListRecyclerView.adapter = messagesListAdapter
        messagesListRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.edit_profile) {
            val bundle = Bundle().apply {
                putBoolean("isMentor", true)
                putString("title", "Your Profile")
            }
            findNavController().navigate(
                    R.id.action_messagesListFragment_to_userProfileFormFragment,
                    bundle
            )

            return true
        }

        if (item.itemId == R.id.logout) {
            loginViewModel.isLoggedInUserMentor = false
            loginViewModel.loggedInMentee = null
            loginViewModel.loggedInMentor = null
            viewModel.messageSendersResponse.clear()

            loginViewModel.logout()

            val bundle = Bundle().apply {
                putString("title", getString(R.string.app_name))
            }

            findNavController().navigate(
                    R.id.action_messagesListFragment_to_loginFragment,
                    bundle
            )
            return true
        }

        return false
    }
}
