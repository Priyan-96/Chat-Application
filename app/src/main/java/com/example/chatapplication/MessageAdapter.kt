package com.example.chatapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context,val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_SEND = 1
    val ITEM_RECEIVE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == 1) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.send_msg,parent,false)
            return sendViewHolder(view)
        }
        else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive_msg,parent,false)
            return receiveViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var currentMessage = messageList[position]

        if (holder.javaClass == sendViewHolder::class.java) {
            val viewHolder = holder as sendViewHolder
            viewHolder.send_msg.text = currentMessage.message
        }
        else {
            val viewHolder = holder as receiveViewHolder
            viewHolder.receive_msg.text = currentMessage.message
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {

        if (FirebaseAuth.getInstance().currentUser?.uid == messageList[position].senderId) {
            return ITEM_SEND
        }
        else {
            return ITEM_RECEIVE
        }

    }

    class sendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var send_msg = itemView.findViewById<TextView>(R.id.msg_send_txt)
    }

    class receiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var receive_msg = itemView.findViewById<TextView>(R.id.msg_receive_txt)
    }

}