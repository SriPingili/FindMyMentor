package sp.android.findmymentor.play.util

import android.app.Activity
import android.content.Context
import android.view.View
import sp.android.findmymentor.play.extensions.showSnackBar

class Constants {

    companion object {
        const val IS_MENTOR: String = "isMentor"
        const val USERS = "Users"
        const val CHATS = "Chats"
        const val MESSAGES = "Messages"
        const val PARTICIPANTS_KEY = "participants"
        const val MESSAGES_KEY = "messages"
        const val TEXT = "text"
        const val SENDER_NAME = "sender_name"
        const val SENDER_ID = "sender_id"
        const val ADMIN_NAME = "Admin"
        const val ADMIN_ID = "admin"
        const val DATE_IN_MILLIS = "dateInMillis"

        var loggedInUserName = ""

        //UserProfile
        const val TITLE_ARG_KEY = "title"
        const val MESSAGE_ARG__KEY = "messageArg"
        const val FULL_NAME_KEY = "full_name"
        const val EMAIL_ADDRESS_KEY = "email_address"
        const val AVAILABILITY_KEY = "availability"
        const val TOTAL_SPOTS_KEY = "totalSpots"
        const val ABOUT_YOURSELF_KEY = "aboutYourself"
        const val ORGANIZATION_KEY = "organization"
        const val ROLE_KEY = "role"
        const val INTERESTS__KEY = "interests"
        const val LOCATION__KEY = "location"
        const val MENTOR__KEY = "mentor"

        //regex
        const val ONLY_LETTERS = "[a-zA-Z ]+"
        const val ONLY_NUMBERS = "[0-9]+"
        const val ONLY_LETTERS_NUMBERS_NEW_LINES = "[a-zA-Z0-9\n ]+"
        const val ONLY_LETTERS_NUMBERS = "[a-zA-Z0-9]+"
        const val EMAIL_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"

        fun getKey(menteeEmail: String, mentorEmail: String): String {
            val menteeEmail = menteeEmail.replace("""[$#.\[\]]""".toRegex(), "")
            val mentorEmail = mentorEmail.replace("""[$#.\[\]]""".toRegex(), "")

            return "$menteeEmail:$mentorEmail"
        }

        fun showSnackBar(context: Context, message: String, duration: Int) {
            val parentLayout: View? = (context as Activity).findViewById(android.R.id.content)

            parentLayout?.let {
                it.showSnackBar(message, duration)
            }
        }
    }
}