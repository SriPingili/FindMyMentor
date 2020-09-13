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

/*
Adapter class for populating the chatting recycler view with list of messages from firebase
* */
class MessagingAdapter : ListAdapter<Message, MessagingAdapter.ViewHolder>(MessageDiffCallback()) {

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

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.dateInMillis == newItem.dateInMillis && oldItem.sender_name.equals(newItem.sender_name)
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}