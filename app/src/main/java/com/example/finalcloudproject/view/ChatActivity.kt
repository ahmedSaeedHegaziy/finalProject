package com.example.finalcloudproject.view

import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.finalcloudproject.adapter.Chat
import com.example.finalcloudproject.databinding.ActivityChatBinding
import com.example.finalcloudproject.model.ChatMessage
import com.example.finalcloudproject.model.User
import com.example.finalcloudproject.utils.BaseActivity
import com.example.finalcloudproject.utils.Constants
import com.example.finalcloudproject.utils.GlideLoader
import com.example.finalcloudproject.utils.PreferanceManeger
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Objects

class ChatActivity : BaseActivity() {
    private var binding: ActivityChatBinding? = null
    private lateinit  var receiverUser: User
    private var chatMessages: MutableList<ChatMessage>? = null
    private var chat: Chat? = null
    private var preferanceManeger: PreferanceManeger? = null
    private var database: FirebaseFirestore? = null
    private var conversationId: String? = null
    private var isReceiverAvailable = false
    private lateinit var mUserDetails: User
    private lateinit var mUserDetails2: User
    private val mFireStore = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        loadReceiverDetails()
        setListeners()
        init()
        listenMessages()
        getUserDetails()

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            //Get the user dwtails from intent as a ParcelableExtra
            mUserDetails2 = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

    }
    private fun init() {
        preferanceManeger = PreferanceManeger(applicationContext)
        chatMessages = ArrayList()

        chat = Chat(
            this,
            chatMessages as ArrayList<ChatMessage>,

            preferanceManeger!!.getString(Constants.KEY_USER_ID)!!
        )
        binding!!.chatRecyclerView.adapter = chat
        database = FirebaseFirestore.getInstance()
    }

    private fun listenAvailabilityOfReceiver() {
        database!!.collection(Constants.KEY_COLLECTION_USERS).document(
            receiverUser.id
        )
            .addSnapshotListener(this@ChatActivity) { value: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    if (value.getLong(Constants.KEY_AVAILABILITY) != null) {
                        val availability = Objects.requireNonNull(
                            value.getLong(Constants.KEY_AVAILABILITY)
                        )!!.toInt()
                        isReceiverAvailable = availability == 1
                    }
//                    receiverUser!!.token = value.getString(Constants.KEY_FCM_TOKEN)
                }
                if (isReceiverAvailable) {
                    binding!!.textAvailability.visibility = View.VISIBLE
                } else {
                    binding!!.textAvailability.visibility = View.GONE
                }
            }
    }

    private fun listenMessages() {
        val sharedPreferences =
            this.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)

        database!!.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(
                Constants.KEY_SENDER_ID,
                sharedPreferences.getString(Constants.KEY_USER_ID, null).toString()

//                preferanceManeger!!.getString(Constants.KEY_USER_ID)
            )
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
            .addSnapshotListener(eventListener)

        database!!.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
            .whereEqualTo(
                Constants.KEY_RECEIVER_ID,
                preferanceManeger!!.getString(Constants.KEY_USER_ID)
            ).addSnapshotListener(eventListener)
    }

    private val eventListener =
        EventListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
            if (error != null) {
                return@EventListener
            }
            if (value != null) {
                val count = chatMessages!!.size
                for (documentChange in value.documentChanges) {
                    if (documentChange.type == DocumentChange.Type.ADDED) {
                        val chatMessage = ChatMessage()
                        chatMessage.senderId =
                            documentChange.document.getString(Constants.KEY_SENDER_ID)
                        chatMessage.receiverId =
                            documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        chatMessage.message =
                            documentChange.document.getString(Constants.KEY_MESSAGE)
                        chatMessage.dateTime =
                            getReadableDateTime(documentChange.document.getDate(Constants.KEY_TIMESTAMP))
                        chatMessage.dateObject =
                            documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                        chatMessages!!.add(chatMessage)
                    }
                }
                Collections.sort(chatMessages) { obj1: ChatMessage, obj2: ChatMessage ->
                    obj1.dateObject!!.compareTo(
                        obj2.dateObject
                    )
                }
                if (count == 0) {
                    chat!!.notifyDataSetChanged()
                } else {
                    chat!!.notifyItemRangeInserted(chatMessages!!.size, chatMessages!!.size)
                    binding!!.chatRecyclerView.smoothScrollToPosition(chatMessages!!.size - 1)
                }
                binding!!.chatRecyclerView.visibility = View.VISIBLE
            }
            binding!!.progressBar.visibility = View.GONE
            if (conversationId == null) {
                checkForConversion()
            }
        }

    private fun sendMessage() {
        val message = HashMap<String, Any?>()
        message[Constants.KEY_RECEIVER_ID] = receiverUser!!.id
        message[Constants.KEY_TIMESTAMP] = Date()
        message[Constants.KEY_SENDER_ID] =
            preferanceManeger!!.getString(Constants.KEY_USER_ID)
        message[Constants.KEY_MESSAGE] = binding!!.inputMessage.text.toString()
        database!!.collection(Constants.KEY_COLLECTION_CHAT).add(message)
        if (conversationId != null) {
            updateConversion(binding!!.inputMessage.text.toString())
        } else {
            val conversion = HashMap<String, Any?>()
            conversion[Constants.KEY_SENDER_ID] =
                preferanceManeger!!.getString(Constants.KEY_USER_ID)
            conversion[Constants.KEY_SENDER_IMAGE] =
                preferanceManeger!!.getString(Constants.KEY_IMAGE)
            conversion[Constants.KEY_RECEIVER_ID] = receiverUser!!.id
            conversion[Constants.KEY_RECEIVER_IMAGE] = receiverUser!!.image
            conversion[Constants.KEY_LAST_MESSAGE] =
                binding!!.inputMessage.text.toString()
            conversion[Constants.KEY_SENDER_NAME] =
                preferanceManeger!!.getString(Constants.KEY_NAME)
            conversion[Constants.KEY_RECEIVER_NAME] = receiverUser!!.fullName
            conversion[Constants.KEY_TIMESTAMP] = Date()
            addConversion(conversion)
        }
        binding!!.inputMessage.text = null
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
                    getImage(user)
                }
            }
    }

    private  fun  getImage(user:User){
    mUserDetails = user
    GlideLoader(this).loadUserPicture(
        user.image,
        binding!!.imageInfo
    )
    }

    private fun loadReceiverDetails() {


        receiverUser = intent.getParcelableExtra(Constants.KEY_USER)!!
        binding!!.textName.text = receiverUser.fullName
    }

    private fun setListeners() {
        binding!!.imageBack.setOnClickListener { v: View? -> onBackPressed() }
        binding!!.layoutSend.setOnClickListener { v: View? -> sendMessage() }
    }

    private fun getReadableDateTime(date: Date?): String {
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }

    private fun addConversion(conversion: HashMap<String, Any?>) {
        database!!.collection(Constants.KEY_COLLECTION_CONVERSATIONS).add(conversion)
            .addOnSuccessListener { documentReference: DocumentReference ->
                conversationId = documentReference.id
            }
    }

    private fun updateConversion(message: String) {
        val documentReference =
            database!!.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(
                conversationId!!
            )
        documentReference.update(
            Constants.KEY_LAST_MESSAGE, message,
            Constants.KEY_TIMESTAMP, Date()
        )
    }

    private fun checkForConversion() {
        if (chatMessages!!.size != 0) {
            checkForConversionRemotely(
                preferanceManeger!!.getString(Constants.KEY_USER_ID),
                receiverUser!!.id
            )
            checkForConversionRemotely(
                receiverUser!!.id,
                preferanceManeger!!.getString(Constants.KEY_USER_ID)
            )
        }
    }

    private fun checkForConversionRemotely(senderId: String?, receiverId: String?) {
        database!!.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
            .get()
            .addOnCompleteListener(conversionOnCompleteListener)
    }

    private val conversionOnCompleteListener = OnCompleteListener { task: Task<QuerySnapshot?> ->
        if (task.isSuccessful && task.result != null && task.result!!
                .documents.size > 0
        ) {
            val documentSnapshot = task.result!!.documents[0]
            conversationId = documentSnapshot.id
        }
    }

    override fun onResume() {
        super.onResume()
        listenAvailabilityOfReceiver()
    }
}