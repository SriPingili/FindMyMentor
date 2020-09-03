package sp.android.findmymentor.play.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_list_layout_item.view.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.models.Message

class MessagesListAdapter : RecyclerView.Adapter<MessagesListAdapter.MessagesViewHolder>() {
    private var onItemClickListener: ((Message) -> Unit)? = null

    inner class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.dateInMillis == newItem.dateInMillis
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        return MessagesViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.message_list_layout_item,
                        parent,
                        false
                )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val message = differ.currentList[position]

        holder.itemView.apply {

            roundedTextView.setText(message.sender_name?.toUpperCase()?.subSequence(0, 1))
            senderNameTextView.setText(message.sender_name)
            messageContentTextView.setText(message.text)


            setOnClickListener {
                onItemClickListener?.let {
                    it(message)
                }
            }
        }
    }

    /*
    on click listener for the recycler view row item
    * */
    fun setOnItemClickListener(listener: (Message) -> Unit) {
        onItemClickListener = listener
    }
}