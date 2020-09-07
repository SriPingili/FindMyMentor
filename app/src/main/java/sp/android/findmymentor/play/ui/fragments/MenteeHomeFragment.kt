package sp.android.findmymentor.play.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ramotion.foldingcell.FoldingCell
import kotlinx.android.synthetic.main.fragment_mentee_home.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.MainActivity
import sp.android.findmymentor.play.adapters.MentorsAdapter
import sp.android.findmymentor.play.models.Mentee
import sp.android.findmymentor.play.ui.viewmodels.MainViewModel

class MenteeHomeFragment : Fragment(R.layout.fragment_mentee_home) {
    val args: MenteeHomeFragmentArgs by navArgs()
    lateinit var mentee: Mentee
    lateinit var mentorsAdapter: MentorsAdapter
    lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        mentee = args.menteeArg
        viewModel = (activity as MainActivity).viewModel

        (activity as MainActivity).supportActionBar?.title = "Welcome ${viewModel.loggedInMentee?.full_name}"

        /*todo move this to a better place, after login success*/
        viewModel.getUsersFromFirebase()

        setUpRecyclerView()

        mentorsAdapter.setOnItemClickListener { view, position ->
            // toggle clicked cell state
            (view as FoldingCell).toggle(false)
            mentorsAdapter.registerToggle(position)
        }

        mentorsAdapter.setOnRequestMentorClickListener {
            viewModel.requestSelectedMentor(it)
        }

        mentorsAdapter.setCommonGroupsClickListener {
            val commonInterestsMessage = viewModel.getCommonInterests(it)


            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                    .setTitle(String.format(getString(R.string.dear_user), viewModel.getLoggedInUserName()))
                    .setMessage(commonInterestsMessage)
                    .setPositiveButton(android.R.string.ok, null)
                    .create()
                    .show()
        }

        messagesFab.setOnClickListener {
            viewModel.getMessagesFromDifferentSenders()//todo make this call once
            findNavController().navigate(R.id.action_menteeHomeFragment_to_messagesListFragment)
        }

        addObservers()
    }

    private fun setUpRecyclerView() {
        mentorsAdapter = MentorsAdapter()
        mentorsAdapter.setKeysAndLoggedInUserEmail(viewModel.chatsKey, viewModel.getLoggedInEmailAddress().toString())
        mentorsRecyclerView.adapter = mentorsAdapter
        mentorsRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun addObservers() {
        viewModel.mentorsLiveData.observe(viewLifecycleOwner, Observer {
            mentorsAdapter.submitList(it)
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.edit_profile) {
            val bundle = Bundle().apply {
                putBoolean("isMentor", false)
                putString("title", "Your Profile")
            }
            findNavController().navigate(
                    R.id.action_menteeHomeFragment_to_userProfileFormFragment,
                    bundle
            )

            return true
        }

        if (item.itemId == R.id.logout) {
            viewModel.isLoggedInUserMentor = false
            viewModel.loggedInMentee = null
            viewModel.loggedInMentor = null
            viewModel.firbaseMentorResponse.clear()
            viewModel.firbaseMenteeResponse.clear()
            viewModel.messageSendersResponse.clear()
            viewModel.chatsKey.clear()

            findNavController().navigate(
                    R.id.action_menteeHomeFragment_to_loginFragment
            )
            return true
        }

        return false
    }
}