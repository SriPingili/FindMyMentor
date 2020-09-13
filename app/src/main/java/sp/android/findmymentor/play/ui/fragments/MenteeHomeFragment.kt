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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ramotion.foldingcell.FoldingCell
import kotlinx.android.synthetic.main.fragment_mentee_home.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.MainActivity
import sp.android.findmymentor.play.adapters.MenteeHomeAdapter
import sp.android.findmymentor.play.firebase.FirebaseSource
import sp.android.findmymentor.play.models.Mentee
import sp.android.findmymentor.play.repository.MainRepository
import sp.android.findmymentor.play.ui.viewmodels.LoginViewModel
import sp.android.findmymentor.play.ui.viewmodels.MenteeViewModel
import sp.android.findmymentor.play.ui.viewmodels.factories.MenteeViewModelFactory
import sp.android.findmymentor.play.util.Constants

class MenteeHomeFragment : Fragment(R.layout.fragment_mentee_home) {
    private lateinit var mentee: Mentee
    private lateinit var menteeHomeAdapter: MenteeHomeAdapter
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var viewModel: MenteeViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        initViewModel()
        setUpRecyclerView()
        setListeners()
        addObservers()
    }

    private fun initViewModel() {
        loginViewModel = (activity as MainActivity).viewModel
        mentee = loginViewModel.loggedInMentee!!

        val mainRepository = MainRepository(FirebaseSource())
        loginViewModel.loggedInMentee?.let { mentee ->
            viewModel = ViewModelProvider(this, MenteeViewModelFactory(mainRepository, mentee)).get(MenteeViewModel::class.java)
        }
    }

    private fun setUpRecyclerView() {
        menteeHomeAdapter = MenteeHomeAdapter()
        menteeHomeAdapter.setLoggedInUserEmail(loginViewModel.getLoggedInEmailAddress().toString())
        mentorsRecyclerView.adapter = menteeHomeAdapter
        mentorsRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun setListeners() {
        menteeHomeAdapter.setOnItemClickListener { view, position ->
            // toggle clicked cell state
            (view as FoldingCell).toggle(false)
            menteeHomeAdapter.registerToggle(position)
        }

        menteeHomeAdapter.setOnRequestMentorClickListener {
            viewModel.requestSelectedMentor(it)
        }

        menteeHomeAdapter.setCommonGroupsClickListener {
            val commonInterestsMessage = viewModel.getCommonInterests(it)

            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                    .setTitle(String.format(getString(R.string.dear_user), loginViewModel.getLoggedInUserName()))
                    .setMessage(commonInterestsMessage)
                    .setPositiveButton(android.R.string.ok, null)
                    .create()
                    .show()
        }

        messagesFab.setOnClickListener {
            val bundle = Bundle().apply {
                putString(Constants.TITLE_ARG_KEY, getString(R.string.inbox))
            }

            findNavController().navigate(R.id.action_global_to_messagesListFragment, bundle)
        }
    }

    private fun addObservers() {
        viewModel.mentorsLiveData.observe(viewLifecycleOwner, Observer {
            menteeHomeAdapter.chatKeys = viewModel.chatsKey
            menteeHomeAdapter.submitList(it)
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
            viewModel.firbaseMentorResponse.clear()
            viewModel.chatsKey.clear()
            loginViewModel.logout()

            /*https://stackoverflow.com/a/54536427*/
            val controller = findNavController()
            controller.popBackStack(R.id.menteeHomeFragment, true)
            controller.navigate(R.id.action_global_to_loginFragment)
            return true
        }

        return false
    }
}