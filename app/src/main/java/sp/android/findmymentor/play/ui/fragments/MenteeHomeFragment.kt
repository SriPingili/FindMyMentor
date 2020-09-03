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
import com.ramotion.foldingcell.FoldingCell
import kotlinx.android.synthetic.main.fragment_mentee_home.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.MainActivity
import sp.android.findmymentor.play.adapter.MentorsAdapter
import sp.android.findmymentor.play.models.Mentee
import sp.android.findmymentor.play.models.Mentor
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

        /*todo move this to a better place*/
        viewModel.getUsersFromFirebase()

        mentorsAdapter = MentorsAdapter()
        mainListView.adapter = mentorsAdapter
        mainListView.layoutManager = LinearLayoutManager(activity)

        mentorsAdapter.setOnItemClickListener { view, position ->
            // toggle clicked cell state
            (view as FoldingCell).toggle(false)
            mentorsAdapter.registerToggle(position)
        }

        addObservers()
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
            }
            findNavController().navigate(
                    R.id.action_menteeHomeFragment_to_userProfileFormFragment,
                    bundle
            )

            return true
        }

        return super.onOptionsItemSelected(item)
    }
}