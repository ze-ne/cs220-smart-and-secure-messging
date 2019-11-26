package com.cs220.ssmessaging.frontend.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.ImageHandler
import com.cs220.ssmessaging.clientBackend.ImageMessage
import com.cs220.ssmessaging.clientBackend.TextMessage
import com.cs220.ssmessaging.clientBackend.UnencryptedMessage
import java.lang.Integer.min
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val MY_MESSAGE = 1
private const val OTHER_MESSAGE = 2

class MessagesAdapter(val context: Context, val messages: ArrayList<UnencryptedMessage>, val convoId: String) :
    RecyclerView.Adapter<MessageViewHolder>() {
    val currentUser = MyApplication.currentUser!!

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
        if (holder is MyMessageViewHolder) {
            holder.itemView.setOnClickListener {
                holder.deleteMessage.visibility = View.VISIBLE
            }
        }
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
        private var confirmDeleteMessage: TextView = view.findViewById(R.id.message_deletion_confirm)
        private var cancelDeleteMessage: TextView = view.findViewById(R.id.message_deletion_cancel)
        var deleteMessage: RelativeLayout = view.findViewById(R.id.message_deletion)

        private var box1 = view.findViewById<View>(R.id.box1)
        private var box2 = view.findViewById<View>(R.id.box2)
        private var box3 = view.findViewById<View>(R.id.box3)
        private var box4 = view.findViewById<View>(R.id.box4)
        private var box5 = view.findViewById<View>(R.id.box5)
        private var box6 = view.findViewById<View>(R.id.box6)
        private var box7 = view.findViewById<View>(R.id.box7)
        private var box8 = view.findViewById<View>(R.id.box8)
        private var box9 = view.findViewById<View>(R.id.box9)
        private var box10 = view.findViewById<View>(R.id.box10)
        private var box11 = view.findViewById<View>(R.id.box11)
        private var box12 = view.findViewById<View>(R.id.box12)
        private var box13 = view.findViewById<View>(R.id.box13)
        private var box14 = view.findViewById<View>(R.id.box14)
        private var box15 = view.findViewById<View>(R.id.box15)
        private var box16 = view.findViewById<View>(R.id.box16)
        private var box17 = view.findViewById<View>(R.id.box17)
        private var box18 = view.findViewById<View>(R.id.box18)
        private var box19 = view.findViewById<View>(R.id.box19)
        private var box20 = view.findViewById<View>(R.id.box20)
        private var box21 = view.findViewById<View>(R.id.box21)
        private var box22 = view.findViewById<View>(R.id.box22)
        private var box23 = view.findViewById<View>(R.id.box23)
        private var box24 = view.findViewById<View>(R.id.box24)
        private var box25 = view.findViewById<View>(R.id.box25)
        private var overlayBoxes = arrayListOf<View>(
            box1,
            box2,
            box3,
            box4,
            box5,
            box6,
            box7,
            box8,
            box9,
            box10,
            box11,
            box12,
            box13,
            box14,
            box15,
            box16,
            box17,
            box18,
            box19,
            box20,
            box21,
            box22,
            box23,
            box24,
            box25
        )

        override fun bind(message: UnencryptedMessage) {
            if (message is TextMessage) {
                messageText.text = message.message
                messageText.visibility = View.VISIBLE
                messageImage.visibility = View.GONE

                for (box in overlayBoxes) {
                    box.visibility = View.GONE
                }
            } else {
                messageImage.setImageBitmap(
                    ImageHandler.convertImageMessageToImage(message as ImageMessage)
                )
                messageText.visibility = View.GONE
                messageImage.visibility = View.VISIBLE



                if (!message.isVisible) {
                    for (box in overlayBoxes) {
                        box.visibility = View.VISIBLE

                        val imageHeight = messageImage.drawable.intrinsicHeight
                        val imageWidth = messageImage.drawable.intrinsicWidth
                        val params = box.layoutParams

                        if (imageHeight > imageWidth) {
                            params.height =
                                min(
                                    messageImage.drawable.intrinsicHeight,
                                    messageImage.maxHeight
                                ) / 5
                            params.width = (params.height * imageWidth) / imageHeight
                        } else {
                            params.width =
                                min(messageImage.drawable.intrinsicWidth, messageImage.maxWidth) / 5
                            params.height = (imageHeight * params.width) / imageWidth
                        }

                        box.layoutParams = params
                        box.setOnClickListener {
                            box.visibility = View.INVISIBLE
                            box.postDelayed({ box.visibility = View.VISIBLE }, 4000)
                        }
                    }
                } else {
                    for (box in overlayBoxes) {
                        box.visibility = View.GONE
                    }
                }
            }
            timeText.text = getDate(message.timestamp, "MMM dd, hh:mma")
            deleteMessage.visibility = View.GONE

            cancelDeleteMessage.setOnClickListener {
                deleteMessage.visibility = View.GONE
            }

            // TODO: fix
            confirmDeleteMessage.setOnClickListener {
                println("CLICK")
                currentUser.deleteSentMessageFromDb(convoId, message)
                //currentUser.deleteSentMessage(message)
                //deleteMessage.visibility = View.GONE
            }
        }
    }

    inner class OtherMessageViewHolder(view: View) : MessageViewHolder(view) {
        private var messageText: TextView = view.findViewById(R.id.other_message_text)
        private var messageImage: ImageView = view.findViewById(R.id.other_message_image)
        private var timeText: TextView = view.findViewById(R.id.other_message_time)
        private var box1 = view.findViewById<View>(R.id.box1)
        private var box2 = view.findViewById<View>(R.id.box2)
        private var box3 = view.findViewById<View>(R.id.box3)
        private var box4 = view.findViewById<View>(R.id.box4)
        private var box5 = view.findViewById<View>(R.id.box5)
        private var box6 = view.findViewById<View>(R.id.box6)
        private var box7 = view.findViewById<View>(R.id.box7)
        private var box8 = view.findViewById<View>(R.id.box8)
        private var box9 = view.findViewById<View>(R.id.box9)
        private var box10 = view.findViewById<View>(R.id.box10)
        private var box11 = view.findViewById<View>(R.id.box11)
        private var box12 = view.findViewById<View>(R.id.box12)
        private var box13 = view.findViewById<View>(R.id.box13)
        private var box14 = view.findViewById<View>(R.id.box14)
        private var box15 = view.findViewById<View>(R.id.box15)
        private var box16 = view.findViewById<View>(R.id.box16)
        private var box17 = view.findViewById<View>(R.id.box17)
        private var box18 = view.findViewById<View>(R.id.box18)
        private var box19 = view.findViewById<View>(R.id.box19)
        private var box20 = view.findViewById<View>(R.id.box20)
        private var box21 = view.findViewById<View>(R.id.box21)
        private var box22 = view.findViewById<View>(R.id.box22)
        private var box23 = view.findViewById<View>(R.id.box23)
        private var box24 = view.findViewById<View>(R.id.box24)
        private var box25 = view.findViewById<View>(R.id.box25)
        private var overlayBoxes = arrayListOf<View>(
            box1,
            box2,
            box3,
            box4,
            box5,
            box6,
            box7,
            box8,
            box9,
            box10,
            box11,
            box12,
            box13,
            box14,
            box15,
            box16,
            box17,
            box18,
            box19,
            box20,
            box21,
            box22,
            box23,
            box24,
            box25
        )

        override fun bind(message: UnencryptedMessage) {
            if (message is TextMessage) {
                messageText.text = message.message
                messageText.visibility = View.VISIBLE
                messageImage.visibility = View.GONE

                for (box in overlayBoxes) {
                    box.visibility = View.GONE
                }
            } else {
                messageImage.setImageBitmap(
                    ImageHandler.convertImageMessageToImage(message as ImageMessage)
                )
                messageText.visibility = View.GONE
                messageImage.visibility = View.VISIBLE

                if (!message.isVisible) {
                    for (box in overlayBoxes) {
                        box.visibility = View.VISIBLE

                        val imageHeight = messageImage.drawable.intrinsicHeight
                        val imageWidth = messageImage.drawable.intrinsicWidth
                        val params = box.layoutParams

                        if (imageHeight > imageWidth) {
                            params.height =
                                min(
                                    messageImage.drawable.intrinsicHeight,
                                    messageImage.maxHeight
                                ) / 5
                            params.width = (params.height * imageWidth) / imageHeight
                        } else {
                            params.width =
                                min(messageImage.drawable.intrinsicWidth, messageImage.maxWidth) / 5
                            params.height = (imageHeight * params.width) / imageWidth
                        }

                        box.layoutParams = params
                        box.setOnClickListener {
                            box.visibility = View.INVISIBLE
                            box.postDelayed({ box.visibility = View.VISIBLE }, 4000)
                        }
                    }
                } else {
                    for (box in overlayBoxes) {
                        box.visibility = View.GONE
                    }
                }
            }
            timeText.text = getDate(message.timestamp, "MMM dd, hh:mma")
        }
    }
}

open class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: UnencryptedMessage) {}
}