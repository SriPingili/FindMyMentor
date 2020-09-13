package sp.android.findmymentor.play.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import sp.android.findmymentor.R
import sp.android.findmymentor.play.application.CustomApplication
import sp.android.findmymentor.play.models.Mentee
import sp.android.findmymentor.play.models.Mentor
import sp.android.findmymentor.play.repository.MainRepository
import sp.android.findmymentor.play.util.Constants
import java.util.UUID

/*
* This ViewModel is responsible for handling Mentee operations which includes pulling mentor data
* from firebase, displaying common interests and pulling chat keys from firebase
* */
class MenteeViewModel(private val repository: MainRepository, private val loggedInMentee: Mentee) : ViewModel() {
    val firbaseMentorResponse: HashMap<String, Mentor> = hashMapOf()
    val mentorsLiveData: MutableLiveData<MutableList<Mentor>> = MutableLiveData()
    var chatsKey: MutableSet<String> = mutableSetOf()

    init {
        getChatKeysFromFirebase()
        getUsersFromFirebase()
    }

    private fun getUsersFromFirebase() {
        repository.getFirebaseUsersDBReference().addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val usersTableHashMap = snapshot.value as HashMap<String, Object>
                val keySet = usersTableHashMap.keys

                if (keySet.contains(Constants.MENTOR__KEY)) {
                    val mentor = snapshot.getValue(Mentor::class.java)
                    mentor?.let {
                        snapshot.key?.let { key -> firbaseMentorResponse.put(key, it) }
                        val list = ArrayList<Mentor>(firbaseMentorResponse.values)
                        mentorsLiveData.postValue(list)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val usersTableHashMap = snapshot.value as HashMap<String, Object>
                val keySet = usersTableHashMap.keys

                if (keySet.contains(Constants.MENTOR__KEY)) {
                    val mentor = snapshot.getValue(Mentor::class.java)
                    mentor?.let {
                        snapshot.key?.let { key -> firbaseMentorResponse.put(key, it) }
                        mentorsLiveData.postValue(ArrayList(firbaseMentorResponse.values))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }

    fun requestSelectedMentor(mentor: Mentor) {
        val uuid = UUID.randomUUID().toString()
        val chatKey = Constants.getKey(loggedInMentee.email_address, mentor.email_address)
        repository.getFirebaseChatsDBReference().child(chatKey).setValue(uuid)

        val messagesMap = HashMap<String, Any>()
        messagesMap.put(Constants.PARTICIPANTS_KEY, mutableListOf(loggedInMentee.full_name, mentor.full_name))
        messagesMap[Constants.MESSAGES_KEY] = mutableMapOf<String, String>()
        repository.getFirebaseMessagesDBReference().child(uuid).setValue(messagesMap)

        val adminMessage = HashMap<String, Any>()
        adminMessage[Constants.TEXT] = CustomApplication.context?.getString(R.string.welcome_to_chat).toString()
        adminMessage[Constants.SENDER_NAME] = Constants.ADMIN_NAME
        adminMessage[Constants.SENDER_ID] = Constants.ADMIN_ID
        adminMessage[Constants.DATE_IN_MILLIS] = System.currentTimeMillis()
        repository.getFirebaseMessagesDBReference().child(uuid).child(Constants.MESSAGES_KEY).push().setValue(adminMessage)
    }

    fun getCommonInterests(mentor: Mentor): String {
        val menteeInterests = loggedInMentee.interests?.toMutableList() //creates a copy
        val mentorInterests = mentor.interests
        var message = CustomApplication.context?.getString(R.string.initial_common_interests_message)

        menteeInterests?.let {
            it.retainAll(mentorInterests)
            if (it.isNotEmpty()) {
                val interests = it.toString().replace("[", "").replace("]", "")
                message = CustomApplication.context?.let {
                    String.format(it.getString(R.string.common_interests_message), interests)
                }
            }
        }

        return message.toString()
    }

    private fun getChatKeysFromFirebase() {
        repository.getFirebaseChatsDBReference().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.value?.let {
                    val hashMap = it as HashMap<String, String>
                    chatsKey = hashMap.keys
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}