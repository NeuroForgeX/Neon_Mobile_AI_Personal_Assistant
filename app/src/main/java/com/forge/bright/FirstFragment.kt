package com.forge.bright

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.forge.bright.databinding.FragmentFirstBinding
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(val message: String, val timestamp: String, val isUser: Boolean = true)

class ChatAdapter(private var messages: List<ChatMessage>) : androidx.recyclerview.widget.RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    
    class ChatViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        private val messageText = itemView.findViewById<android.widget.TextView>(R.id.message_text)
        private val timestampText = itemView.findViewById<android.widget.TextView>(R.id.timestamp_text)
        
        fun bind(message: ChatMessage) {
            messageText.text = message.message
            timestampText.text = message.timestamp
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }
    
    override fun getItemCount() = messages.size
    
    fun updateMessages(newMessages: List<ChatMessage>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSwipeRefresh()
        setupInputControls()
        
        // Add welcome message
        addMessage("Welcome to My Happy Bot! How can I help you today?", isUser = false)
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messages)
        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            // Navigate to SecondFragment on swipe
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupInputControls() {
        binding.sendButton.setOnClickListener {
            val message = binding.messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                addMessage(message, isUser = true)
                binding.messageInput.text.clear()
                
                // Simulate bot response
                addMessage("I received your message: \"$message\"", isUser = false)
            }
        }
        
        // Send message on Enter key
        binding.messageInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND ||
                (event?.keyCode == android.view.KeyEvent.KEYCODE_ENTER && event.action == android.view.KeyEvent.ACTION_DOWN)) {
                binding.sendButton.performClick()
                true
            } else {
                false
            }
        }
    }

    private fun addMessage(message: String, isUser: Boolean = true) {
        val timestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val chatMessage = ChatMessage(message, timestamp, isUser)
        messages.add(chatMessage)
        chatAdapter.updateMessages(messages.toList())
        
        // Scroll to bottom
        binding.chatRecyclerView.scrollToPosition(messages.size - 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
