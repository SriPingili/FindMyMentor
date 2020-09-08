package sp.android.findmymentor.play.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_list_layout_item.view.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.models.Message

class MessagesListAdapter : ListAdapter<Message, MessagesListAdapter.MessagesViewHolder>(MessageListDiffCallback()) {
    private var onItemClickListener: ((Message) -> Unit)? = null

    inner class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: Message) {
            itemView.apply {
                roundedTextView.text = message.sender_name.toUpperCase().subSequence(0, 1)
                senderNameTextView.text = message.sender_name
                messageContentTextView.text = message.text

                setOnClickListener {
                    onItemClickListener?.let {
                        it(message)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        return MessagesViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.message_list_layout_item,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /*
    on click listener for the recycler view row item
    * */
    fun setOnItemClickListener(listener: (Message) -> Unit) {
        onItemClickListener = listener
    }
}

class MessageListDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.sender_name.equals(newItem.sender_name)
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.dateInMillis == newItem.dateInMillis
    }
}