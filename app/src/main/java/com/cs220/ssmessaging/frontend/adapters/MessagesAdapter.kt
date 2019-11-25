package com.cs220.ssmessaging.frontend.adapters

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.ImageHandler
import com.cs220.ssmessaging.clientBackend.ImageMessage
import com.cs220.ssmessaging.clientBackend.TextMessage
import com.cs220.ssmessaging.clientBackend.UnencryptedMessage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val MY_MESSAGE = 1
private const val OTHER_MESSAGE = 2

class MessagesAdapter(val context: Context, val messages: ArrayList<UnencryptedMessage>) :
    RecyclerView.Adapter<MessageViewHolder>() {

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]

        return if (MyApplication.currentUser?.userId == message.senderId) {
            MY_MESSAGE
        } else {
            OTHER_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == MY_MESSAGE) {
            MyMessageViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_my_message,
                    parent,
                    false
                )
            )
        } else {
            OtherMessageViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_other_message,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    fun getDate(milliSeconds: Long, dateFormat: String): String {
        val formatter = SimpleDateFormat(dateFormat)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    inner class MyMessageViewHolder(view: View) : MessageViewHolder(view) {
        private var messageText: TextView = view.findViewById(R.id.my_message_text)
        private var messageImage: ImageView = view.findViewById(R.id.my_message_image)
        private var timeText: TextView = view.findViewById(R.id.my_message_time)

        override fun bind(message: UnencryptedMessage) {
            if (message is TextMessage)
                messageText.text = message.message
            else {
                messageImage.setImageBitmap(
                    ImageHandler.convertImageMessageToImage(message as ImageMessage)
                )
            }
            timeText.text = getDate(message.timestamp, "MMM dd, hh:mma")
        }
    }

    inner class OtherMessageViewHolder(view: View) : MessageViewHolder(view) {
        private var messageText: TextView = view.findViewById(R.id.other_message_text)
        private var messageImage: ImageView = view.findViewById(R.id.other_message_image)
        private var timeText: TextView = view.findViewById(R.id.other_message_time)

        override fun bind(message: UnencryptedMessage) {
            if (message is TextMessage)
                messageText.text = message.message
            else {
                messageImage.setImageBitmap(
                    ImageHandler.convertImageMessageToImage(message as ImageMessage)
                )
            }
            timeText.text = getDate(message.timestamp, "MMM dd, hh:mma")
        }
    }
}

open class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: UnencryptedMessage){}
}