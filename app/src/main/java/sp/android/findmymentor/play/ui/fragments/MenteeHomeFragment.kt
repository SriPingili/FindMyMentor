package sp.android.findmymentor.play.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_mentee_home.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.models.Mentee

class MenteeHomeFragment : Fragment(R.layout.fragment_mentee_home) {

    val args: MenteeHomeFragmentArgs by navArgs()
    lateinit var mentee: Mentee

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        mentee = args.menteeArg

        textView.setText("hello ${mentee.full_name}")
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