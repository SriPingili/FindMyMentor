package sp.android.findmymentor.play.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import sp.android.findmymentor.R
import sp.android.findmymentor.play.models.Mentee
import sp.android.findmymentor.play.models.Mentor
import sp.android.findmymentor.play.models.Message
import sp.android.findmymentor.play.repository.MainRepository
import sp.android.findmymentor.play.util.Constants
import sp.android.findmymentor.play.util.Constants.Companion.ADMIN_ID
import sp.android.findmymentor.play.util.Constants.Companion.ADMIN_NAME
import sp.android.findmymentor.play.util.Constants.Companion.CHATS
import sp.android.findmymentor.play.util.Constants.Companion.MESSAGES_KEY
import sp.android.findmymentor.play.util.Constants.Companion.PARTICIPANTS_KEY
import sp.android.findmymentor.play.util.Constants.Companion.SENDER_ID
import sp.android.findmymentor.play.util.Constants.Companion.SENDER_NAME
import sp.android.findmymentor.play.util.Constants.Companion.TEXT
import sp.android.findmymentor.play.util.Event
import java.util.Arrays
import java.util.Objects
import java.util.UUID

class MainViewModel(private val repository: MainRepository) : ViewModel() {
    var isLoggedInUserMentor = false
    var loggedInMentor: Mentor? = null
    var loggedInMentee: Mentee? = null
    val updateProfileStatus: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val registerResult: MutableLiveData<Event<Task<AuthResult>>> = MutableLiveData()
    val loggedInUserIsMentor: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val loginResult: MutableLiveData<Event<Task<AuthResult>>> = MutableLiveData()
    val mentorsLiveData: MutableLiveData<MutableList<Mentor>> = MutableLiveData()
    val menteeLiveData: MutableLiveData<MutableList<Mentee>> = MutableLiveData()
    val messageSendersLiveData: MutableLiveData<MutableList<Message>> = MutableLiveData()
    val messagesLiveData: MutableLiveData<MutableList<Message>> = MutableLiveData()
    val firbaseMentorResponse: HashMap<String, Mentor> = hashMapOf()
    val firbaseMenteeResponse: HashMap<String, Mentee> = hashMapOf()
    val messageSendersResponse = mutableListOf<Message>()
    val messagesResponse = mutableListOf<Message>()

    var hasCalledGetMessages = false

    fun login(email: String, password: String) = viewModelScope.launch {
        repository.login(email, password).addOnCompleteListener {
            loginResult.postValue(Event(it))
        }
    }

    fun routeToUsersTimeline() {
        repository.getChildUserReference()?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                /*do nothing*/
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val map = snapshot.value as HashMap<String, String>?
                if (map?.containsKey("mentor")!!) {
                    loggedInMentor = snapshot.getValue(Mentor::class.java)
                    isLoggedInUserMentor = true
                    loggedInUserIsMentor.postValue(Event(true))
                } else {
                    loggedInMentee = snapshot.getValue(Mentee::class.java)
                    isLoggedInUserMentor = false
                    loggedInUserIsMentor.postValue(Event(false))
                }
            }
        })
    }

    fun registerUser(email: String, password: String) = viewModelScope.launch {
        repository.register(email, password).addOnCompleteListener {
            registerResult.postValue(Event(it))
        }
    }

    fun createUser(mentee: Mentee? = null, mentor: Mentor? = null) {
        if (mentee != null) {
            isLoggedInUserMentor = false
            loggedInMentee = mentee
            repository.getChildUserReference()?.setValue(mentee)
        } else if (mentor != null) {
            isLoggedInUserMentor = true
            loggedInMentor = mentor
            repository.getChildUserReference()?.setValue(mentor)
        }
    }


    fun updateProfile(map: HashMap<String, Any>, mentee: Mentee?, mentor: Mentor? = null) {
        repository.getChildUserReference()?.updateChildren(map) { error, _ ->
            if (error == null) {
                if (mentee != null) loggedInMentee = mentee
                if (mentor != null) loggedInMentor = mentor
                updateProfileStatus.postValue(Event(true))
            } else {
                updateProfileStatus.postValue(Event(false))
            }
        }
    }


    val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val usersTableHashMap = snapshot.value as HashMap<String, Object>
            val keySet = usersTableHashMap.keys

            if (keySet.contains("mentor")) {
                val mentor = snapshot.getValue(Mentor::class.java)
                mentor?.let {
                    snapshot.key?.let { key -> firbaseMentorResponse.put(key, it) }
                    val list = ArrayList<Mentor>(firbaseMentorResponse.values)
                    mentorsLiveData.postValue(list)
                }
            } else {
                val mentee = snapshot.getValue(Mentee::class.java)
                mentee?.let {
                    //todo think about array list for this case, below line possess high time complexity, looping through collection for every time
                    snapshot.key?.let { key -> firbaseMenteeResponse.put(key, it) }
                    menteeLiveData.postValue(ArrayList(firbaseMenteeResponse.values))
                }
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val usersTableHashMap = snapshot.value as HashMap<String, Object>
            val keySet = usersTableHashMap.keys

            if (keySet.contains("mentor")) {
                val mentor = snapshot.getValue(Mentor::class.java)
                mentor?.let {

                    snapshot.key?.let { key -> firbaseMentorResponse.put(key, it) }
                    mentorsLiveData.postValue(ArrayList(firbaseMentorResponse.values))
                }
            } else {
                val mentee = snapshot.getValue(Mentee::class.java)
                mentee?.let {
                    snapshot.key?.let { key -> firbaseMenteeResponse.put(key, it) }
                    menteeLiveData.postValue(ArrayList(firbaseMenteeResponse.values))
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
        }
    }

    fun getUsersFromFirebase() = repository.getFirebaseUsersDBReference().addChildEventListener(listener)


    fun requestSelectedMentor(mentor: Mentor) {
        loggedInMentee?.let {
            val uuid = UUID.randomUUID().toString()
            val menteeEmail = it.email_address.replace("""[$#.\[\]]""".toRegex(), "")
            val mentorEmail = mentor.email_address.replace("""[$#.\[\]]""".toRegex(), "")

            val chatKey = "$menteeEmail:$mentorEmail"

            repository.getFirebaseChatsDBReference().child(chatKey).setValue(uuid)

            val messagesMap = HashMap<String, Any>()
            messagesMap.put(PARTICIPANTS_KEY, mutableListOf(it.full_name, mentor.full_name))
            messagesMap[MESSAGES_KEY] = mutableMapOf<String, String>()

            repository.getFirebaseMessagesDBReference().child(uuid).setValue(messagesMap)

            val adminMessage = HashMap<String, Any>()
            adminMessage[TEXT] = "Welcome to your chat! Use this to find a time to talk, plan a meeting, or get to know each other and the goals you want to set in these meetings."
            adminMessage[SENDER_NAME] = ADMIN_NAME
            adminMessage[SENDER_ID] = ADMIN_ID
            adminMessage["dateInMillis"] = System.currentTimeMillis()

            repository.getFirebaseMessagesDBReference().child(uuid).child(MESSAGES_KEY).push().setValue(adminMessage)
        }
    }

    fun getMessagesFromDifferentSenders() {

        //chats
        repository.getFirebaseChatsDBReference().addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                snapshot?.let { dataSnapshot ->
                    dataSnapshot.key?.let { key ->
                        dataSnapshot.value?.let { value ->
                            loggedInMentor?.let { mentor ->
                                if (key.contains(mentor.email_address.replace("""[$#.\[\]]""".toRegex(), ""))) {
                                    val message = Message()
                                    message.chatKey = key
                                    message.chatKeyValue = value.toString()

                                    val valueEventListener = object : ValueEventListener {
                                        override fun onCancelled(error: DatabaseError) {
                                        }

                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val count = snapshot.childrenCount
                                            Log.v("zzzzzzzzz", "count = $count")

                                            for (dataSnapshot in snapshot.children) {
                                                val messageTemp = dataSnapshot.getValue(Message::class.java)
                                                messageTemp?.let {
                                                    message.text = it.text
                                                    message.dateInMillis = it.dateInMillis
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


                                    val singleValueEventListener = object : ValueEventListener {
                                        override fun onCancelled(error: DatabaseError) {
                                        }

                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for (dataSnapshot in snapshot.children) {
                                                val participantName = dataSnapshot.value.toString()
                                                if (!participantName.equals(mentor.full_name)) {
                                                    message.sender_name = participantName
                                                    messageSendersResponse.add(message)
                                                    messageSendersLiveData.postValue(messageSendersResponse)
                                                }
                                            }
                                        }
                                    }

                                    repository.getFirebaseMessagesDBReference()
                                            .child(value.toString())
                                            .child(PARTICIPANTS_KEY)
                                            .addListenerForSingleValueEvent(singleValueEventListener)
                                }
                            }
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


    fun logout() = repository.logout()

    fun currentUser() = repository.currentUser()
}