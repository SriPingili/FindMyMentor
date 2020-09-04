package sp.android.findmymentor.play.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_messaging.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.MainActivity
import sp.android.findmymentor.play.adapters.MessagingAdapter
import sp.android.findmymentor.play.firebase.FirebaseSource
import sp.android.findmymentor.play.models.Message
import sp.android.findmymentor.play.repository.MainRepository
import sp.android.findmymentor.play.ui.viewmodels.MainViewModel
import sp.android.findmymentor.play.ui.viewmodels.MessagingViewModel
import sp.android.findmymentor.play.ui.viewmodels.MessagingViewModelFactory
import sp.android.findmymentor.play.util.Constants.Companion.loggedInUserName

class MessagingFragment : Fragment(R.layout.fragment_messaging) {
    val args: MessagingFragmentArgs by navArgs()
    lateinit var viewModel: MessagingViewModel
    lateinit var mainViewModel: MainViewModel
    lateinit var messagingAdapter: MessagingAdapter
    var message: Message = Message()
    var previousRespone = mutableListOf<Message>()
    lateinit var observer: Observer<MutableList<Message>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = (activity as MainActivity).viewModel
        val mainRepository = MainRepository(FirebaseSource())
        message = args.messageArg

        viewModel = ViewModelProvider(this, MessagingViewModelFactory(mainRepository, message.chatKeyValue.toString())).get(MessagingViewModel::class.java)


        //todo seprateview model for this fragment? how about passing keyvalue 1)constructor 2)by property
//        message.chatKeyValue?.let {
//            viewModel.getMessagesForTheUserChat(it)
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecylcerView()

        observer = Observer {
            messagingAdapter.submitList(it)
//            messagingAdapter.notifyDataSetChanged()
//
//            messagingAdapter.notifyItemInserted(messagingAdapter.differ.currentList.size - 1)
            messagesRecyclerView.scrollToPosition(messagingAdapter.currentList.size - 1)

        }


        viewModel.messagesLiveData.observe(viewLifecycleOwner, observer)

        typeMessageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString().trim().length > 0) {
                    sendMessageImageView.setEnabled(true)
                } else {
                    sendMessageImageView.setEnabled(false)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })


        sendMessageImageView.setOnClickListener {

            val message = Message(mainViewModel.loggedInMentor?.email_address, mainViewModel.loggedInMentor?.full_name!!, typeMessageEditText.text.toString(), System.currentTimeMillis())

            this.message.chatKeyValue?.let { chatKeyValue -> viewModel.sendMessage(message, chatKeyValue) }



            typeMessageEditText.setText("")
        }
    }

    private fun setUpRecylcerView() {
        messagingAdapter = MessagingAdapter()

        if (mainViewModel.isLoggedInUserMentor) {
            mainViewModel.loggedInMentor?.let {
                loggedInUserName = it.full_name
            }
        } else {
            mainViewModel.loggedInMentee?.let {
                loggedInUserName = it.full_name
            }
        }

        messagesRecyclerView.adapter = messagingAdapter

        val layoutManager = LinearLayoutManager(activity)
        messagesRecyclerView.layoutManager = layoutManager

        messagesRecyclerView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                messagesRecyclerView.scrollToPosition(messagingAdapter.currentList.size - 1)
            }

        })

        //https://stackoverflow.com/questions/31964027/android-recyclerview-not-resizing-when-softinput-keyboard-appears
//        (messagesRecyclerView.layoutManager as LinearLayoutManager?)!!.stackFromEnd = true
    }

    override fun onStop() {
        super.onStop()
        viewModel.messagesResponse.clear()

    

}