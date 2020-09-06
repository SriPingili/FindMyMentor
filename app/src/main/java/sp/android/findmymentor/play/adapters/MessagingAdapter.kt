package sp.android.findmymentor.play.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_item.view.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.models.Message
import sp.android.findmymentor.play.util.Constants.Companion.loggedInUserName


class MessagingAdapter : ListAdapter<Message, MessagingAdapter.ViewHolder>(TaskDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.message_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(message: Message) {
            itemView.apply {
                if (message.sender_name.equals(loggedInUserName)) {
                    messageTextView.gravity = Gravity.RIGHT
                } else {
                    messageTextView.gravity = Gravity.LEFT
                }
                messageTextView.text = message.text
            }
        }
    }
}


class TaskDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.dateInMillis == newItem.dateInMillis && oldItem.sender_name.equals(newItem.sender_name)
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}


//class MessagingAdapter : RecyclerView.Adapter<MessagingAdapter.MessagesViewHolder>() {
//    private var loggedInUserName = ""
//
//    inner class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
//
//    private val differCallback = object : DiffUtil.ItemCallback<Message>() {
//        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
//            return oldItem.dateInMillis == newItem.dateInMillis
//        }
//
//        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
//            return oldItem.dateInMillis == newItem.dateInMillis && oldItem.sender_name.equals(newItem.sender_name) && oldItem.sender_id.equals(newItem.sender_id) &&oldItem.text.equals(newItem.text)
//        }
//    }
//
//    val differ = AsyncListDiffer(this, differCallback)
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
//        return MessagesViewHolder(
//                LayoutInflater.from(parent.context).inflate(
//                        R.layout.message_item,
//                        parent,
//                        false
//                )
//        )
//    }
//
//    fun setLoggedInUserName(userName: String) {
//        loggedInUserName = userName
//    }
//
//    override fun getItemCount(): Int {
//        return differ.currentList.size
//    }
//
//    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
//
//        val message = differ.currentList[position]
//
//        holder.itemView.apply {
//            if (message.sender_name.equals(loggedInUserName)) {
//                messageTextView.gravity = Gravity.RIGHT
//            } else {
//                messageTextView.gravity = Gravity.LEFT
//            }
//
//            messageTextView.text = message.text
//        }
//    }
//}