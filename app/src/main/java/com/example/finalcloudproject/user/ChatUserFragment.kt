package com.example.finalcloudproject.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.finalcloudproject.view.ChatActivity
import com.example.finalcloudproject.view.UsersActivity
import com.example.finalcloudproject.adapter.ResentConversation
import com.example.finalcloudproject.databinding.FragmentChatUserBinding
import com.example.finalcloudproject.listeners.ConversationListeners
import com.example.finalcloudproject.model.ChatMessage
import com.example.finalcloudproject.model.User
import com.example.finalcloudproject.utils.Constants
import com.example.finalcloudproject.utils.GlideLoader
import com.example.finalcloudproject.utils.PreferanceManeger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class ChatUserFragment : Fragment() , ConversationListeners {

    private var _binding: FragmentChatUserBinding? = null
    private val binding get() = _binding!!
    private var preferanceManeger: PreferanceManeger? = null
    private var conversations: MutableList<ChatMessage>? = null
    private var conversationsAdapter: ResentConversation? = null
    private var database: FirebaseFirestore? = null
    private lateinit var mUserDetails: User
    private val mFireStore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatUserBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferanceManeger = PreferanceManeger(requireContext())
        init()
        getUserDetails()
        setListeners()
        listenConversations()

    }

    private fun init() {
        conversations = ArrayList()
        conversationsAdapter =
            ResentConversation(
                requireContext(),
                conversations as ArrayList<ChatMessage>,
                this
            )
        binding!!.conversationsRecyclerView.adapter = conversationsAdapter
        database = FirebaseFirestore.getInstance()
    }

    private fun setListeners() {
        binding!!.fabNewChat.setOnClickListener { v: View? ->
            startActivity(
                Intent(
                    requireContext(), UsersActivity::class.java
                )
            )
        }
    }

    private fun listenConversations() {
        database!!.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(
                Constants.KEY_SENDER_ID,
                preferanceManeger!!.getString(Constants.KEY_USER_ID)
            ).addSnapshotListener(eventListener)
        database!!.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(
                Constants.KEY_RECEIVER_ID,
                preferanceManeger!!.getString(Constants.KEY_USER_ID)
            ).addSnapshotListener(eventListener)
    }
    fun getCurrentUserID(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
    private fun getUserDetails() {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)!!
                    userDetailsSuccess(user)
                }
            }
    }

    fun userDetailsSuccess(user: User) {
        mUserDetails = user
        GlideLoader(requireActivity()).loadUserPicture(
            user.image,
            binding.imageProfile
        )

    }

    private val eventListener =
        EventListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
            if (error != null) {
                return@EventListener
            }
            if (value != null) {
                for (documentChange in value.documentChanges) {
                    if (documentChange.type == DocumentChange.Type.ADDED) {
                        val senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                        val receiverId =
                            documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        val chatMessage = ChatMessage()
                        chatMessage.senderId = senderId
                        chatMessage.receiverId = receiverId
                        if (preferanceManeger!!.getString(Constants.KEY_USER_ID) == senderId) {
                            chatMessage.conversationImage =
                                documentChange.document.getString(Constants.KEY_RECEIVER_IMAGE)
                            chatMessage.conversationName =
                                documentChange.document.getString(Constants.KEY_RECEIVER_NAME)
                            chatMessage.conversationId =
                                documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        } else {
                            chatMessage.conversationImage =
                                documentChange.document.getString(Constants.KEY_SENDER_IMAGE)
                            chatMessage.conversationName =
                                documentChange.document.getString(Constants.KEY_SENDER_NAME)
                            chatMessage.conversationId =
                                documentChange.document.getString(Constants.KEY_SENDER_ID)
                        }
                        chatMessage.message =
                            documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                        chatMessage.dateObject =
                            documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                        conversations!!.add(chatMessage)
                    } else if (documentChange.type == DocumentChange.Type.MODIFIED) {
                        var i = 0
                        while (i < conversations!!.size) {
                            val senderId =
                                documentChange.document.getString(Constants.KEY_SENDER_ID)
                            val receiverId =
                                documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                            if (conversations!![i].senderId == senderId && conversations!![i].receiverId == receiverId) {
                                conversations!![i].message =
                                    documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                                conversations!![i].dateObject =
                                    documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                                break
                            }
                            i++
                        }
                    }
                }
                conversations!!.sortWith(Comparator { obj1: ChatMessage, obj2: ChatMessage ->
                    obj2.dateObject!!.compareTo(
                        obj1.dateObject
                    )
                })
                conversationsAdapter!!.notifyDataSetChanged()
                binding!!.conversationsRecyclerView.smoothScrollToPosition(0)
                binding!!.conversationsRecyclerView.visibility = View.VISIBLE
                binding!!.progressBar.visibility = View.GONE
            }
        }
    override fun onConversionClicked(user: User?) {
        val intent = Intent(requireContext(), ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
    }

}