package sp.android.findmymentor.play.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseSource {
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firebaseUsersReference: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference.child("Users") }
    private val firebaseChatsReference: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference.child("Chats") }
    private val firebaseMessagesReference: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference.child("Messages") }

    fun getFirebaseAuthInstance() = firebaseAuth
    fun getFirebaseUsersDBReferenceInstance() = firebaseUsersReference
    fun getFirebaseChatsDBReferenceInstance() = firebaseChatsReference
    fun getFirebaseMessagesDBReferenceInstance() = firebaseMessagesReference

    fun login(email: String, password: String): Task<AuthResult> = firebaseAuth.signInWithEmailAndPassword(email, password)
    fun register(email: String, password: String) = firebaseAuth.createUserWithEmailAndPassword(email, password)
    fun logout() = firebaseAuth.signOut()
    fun currentUser() = firebaseAuth.currentUser
    fun getChildUserReference() = currentUser()?.uid?.let { firebaseUsersReference?.child(it) }
}