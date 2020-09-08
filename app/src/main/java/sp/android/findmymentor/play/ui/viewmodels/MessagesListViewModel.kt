package sp.android.findmymentor.play.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import sp.android.findmymentor.play.models.Message
import sp.android.findmymentor.play.repository.MainRepository
import sp.android.findmymentor.play.util.Constants.Companion.MESSAGES_KEY
import sp.android.findmymentor.play.util.Constants.Companion.PARTICIPANTS_KEY

class MessagesListViewModel(private val repository: MainRepository, email: String, userName: String) : ViewModel() {

    val messageSendersResponse = mutableListOf<Message>()
    val messageSendersLiveData: MutableLiveData<MutableList<Message>> = MutableLiveData()

    init {
        getMessagesFromDifferentSenders(email, userName)
    }

    private fun getMessagesFromDifferentSenders(loggedInemailAddress: String, loggedInUserName: String) {
        //todo improve this, may be refactor data model for message in firebase
        repository.getFirebaseChatsDBReference().addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                snapshot.key?.let { key ->
                    snapshot.value?.let { value ->
                        if (key.contains(loggedInemailAddress.replace("""[$#.\[\]]""".toRegex(), ""))) {
                            val valueEventListener = object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (dataSnapshot in snapshot.children) {
                                        val messageTemp = dataSnapshot.getValue(Message::class.java)
                                        messageTemp?.let {
                                            val message = Message()
                                            message.chatKey = key
                                            message.chatKeyValue = value.toString()
                                            message.text = it.text
                                            message.dateInMillis = it.dateInMillis

                                            repository.getFirebaseMessagesDBReference()
                                                    .child(value.toString())
                                                    .child(PARTICIPANTS_KEY)
                                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                                        override fun onCancelled(error: DatabaseError) {
                                                        }

                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            for (dataSnapshot in snapshot.children) {
                                                                val participantName = dataSnapshot.value.toString()
                                                                if (!participantName.equals(loggedInUserName)) { //todo this is bug when user updates their user name, it's not updated under participants
                                                                    message.sender_name = participantName
                                                                    messageSendersResponse.apply {
                                                                        val message = filter {
                                                                            it.sender_name == message.sender_name
                                                                        }
                                                                        this.removeAll(message)
                                                                    }
                                                                    message.dateInMillis = System.currentTimeMillis()
                                                                    messageSendersResponse.add(message)
                                                                    messageSendersLiveData.postValue(messageSendersResponse)
                                                                }
                                                            }
                                                        }
                                                    })


                                        }
                                    }
                                }
                            }

                            repository.getFirebaseMessagesDBReference()
                                    .child(value.toString())
                                    .child(MESSAGES_KEY)
                                    .orderByKey()
                                    .limitToLast(1)
                                    .addValueEventListener(valueEventListener)
                        }


                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }
}