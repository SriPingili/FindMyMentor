package sp.android.findmymentor.play.models

import java.io.Serializable
import java.util.Date

data class Message(val sender_id: String? = "",
                   var sender_name: String = "null",
                   var text: String = "",
                   var dateInMillis: Long = 0L) : Serializable {
    var chatKey: String = ""
    var chatKeyValue: String? = ""
}