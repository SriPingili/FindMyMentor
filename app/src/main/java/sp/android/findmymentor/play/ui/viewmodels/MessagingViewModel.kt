package sp.android.findmymentor.play.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import sp.android.findmymentor.play.models.Message
import sp.android.findmymentor.play.repository.MainRepository
import sp.android.findmymentor.play.util.Constants

/*
* This ViewModel is responsible for actual messaging with sender which includes
* retrieving messages from firebase and sending message
* */
class MessagingViewModel(private val repository: MainRepository, chatKeyValue: String) : ViewModel() {
    val messagesLiveData: MutableLiveData<MutableList<Message>> = MutableLiveData()
    val messagesResponse = mutableListOf<Message>()

    init {
        getMessagesForTheUserChat(chatKeyValue)
    }

    private fun getMessagesForTheUserChat(chatKeyValue: String) {
        repository.getFirebaseMessagesDBReference().child(chatKeyValue).child(Constants.MESSAGES_KEY).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.value?.let { it ->
                    val message = snapshot.getValue(Message::class.java)
                    message?.let {
                        messagesResponse.add(it)
                        messagesLiveData.postValue(messagesResponse)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }

    fun sendMessage(message: Message, chatKeyValue: String) {
        repository.getFirebaseMessagesDBReference().child(chatKeyValue).child(Constants.MESSAGES_KEY).push().setValue(message)
    }
}