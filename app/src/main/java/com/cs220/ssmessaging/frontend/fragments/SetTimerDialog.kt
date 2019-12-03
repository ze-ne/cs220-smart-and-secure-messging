package com.cs220.ssmessaging.frontend.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.Conversation
import com.cs220.ssmessaging.clientBackend.ImageHandler
import com.cs220.ssmessaging.clientBackend.User
import java.text.SimpleDateFormat


class SetTimerDialog(
    val text: String,
    val image: Bitmap?,
    private val isImage: Boolean,
    val conversation: Conversation,
    private val isMsgVisible: Boolean
) : DialogFragment() {
    private lateinit var currentUser: User

    private lateinit var timerSetting: EditText
    private lateinit var sendMessageButton: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        currentUser = MyApplication.currentUser!!

        val timerView =
            inflater.inflate(R.layout.dialog_set_timer, container, false)

        timerSetting = timerView.findViewById(R.id.dialog_set_timer_field)
        sendMessageButton = timerView.findViewById(R.id.dialog_send_timed_message_button)

        sendMessageButton.setOnClickListener {
            var deletionTime = 5.toLong()
            val str = timerSetting.text.toString()
            if (str.isNotEmpty()) {
                val formatter = SimpleDateFormat("HH:mm:ss")
                val date = formatter.parse(str)
                val time = formatter.format(date)
                val timePieces = time.split(":")
                if (timePieces.size == 3) {
                    deletionTime =
                        ((timePieces[0].toLong() * 3600) + (timePieces[1].toLong() * 60) + (timePieces[2].toLong()))
                } else {
                    Toast.makeText(
                        activity,
                        "Please enter a valid time",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            if (isImage) {
                currentUser.sendImageMsg(
                    ImageHandler.getByteArrayFromImage(image),
                    conversation,
                    isVisible = isMsgVisible,
                    deletionTimer = deletionTime
                )
            } else {
                currentUser.sendTextMsg(
                    text,
                    conversation,
                    isVisible = isMsgVisible,
                    deletionTimer = deletionTime
                )
            }

            this.dismiss()
        }
        return timerView
    }
}