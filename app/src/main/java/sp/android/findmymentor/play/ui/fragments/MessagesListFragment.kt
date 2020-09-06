package sp.android.findmymentor.play.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_messages_list.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.MainActivity
import sp.android.findmymentor.play.adapters.MessagesListAdapter
import sp.android.findmymentor.play.models.Mentor
import sp.android.findmymentor.play.ui.viewmodels.MainViewModel

class MessagesListFragment : Fragment(R.layout.fragment_messages_list) {

    val args: MessagesListFragmentArgs by navArgs()
    lateinit var viewModel: MainViewModel
    lateinit var messagesListAdapter: MessagesListAdapter
    var mentor: Mentor? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mentor = args.mentorArg
        viewModel = (activity as MainActivity).viewModel
        setUpRecyclerView()

        messagesListAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("messageArg", it)
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
                messagesListAdapter.differ.submitList(it)
                messagesListAdapter.notifyDataSetChanged()


        })

    }

    private fun setUpRecyclerView() {
        messagesListAdapter = MessagesListAdapter()
        messagesListRecyclerView.adapter = messagesListAdapter
        messagesListRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

}
