package sp.android.findmymentor.play.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_mentor_home.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.models.Mentor

class MentorHomeFragment : Fragment(R.layout.fragment_mentor_home) {

    val args: MentorHomeFragmentArgs by navArgs()
    lateinit var mentor: Mentor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        mentor = args.mentorArg

        textView2.setText("hello ${mentor.full_name}")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.edit_profile) {
            val bundle = Bundle().apply {
                putBoolean("isMentor", true)
            }

            findNavController().navigate(
                    R.id.action_mentorHomeFragment_to_userProfileFormFragment,
                    bundle
            )

            return true
        }

        return super.onOptionsItemSelected(item)
    }
}