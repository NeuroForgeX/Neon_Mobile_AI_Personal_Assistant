package com.forge.bright.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.forge.bright.R
import com.forge.bright.ai.Assistant
import com.forge.bright.ai.ChatAssistant
import com.forge.bright.databinding.FragmentFirstBinding
import com.forge.bright.utils.PreferencesManager
import com.forge.bright.utils.getAbsolutePath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.atomics.AtomicReference
import androidx.core.net.toUri

data class ChatMessage(val message: String, val timestamp: String, val isUser: Boolean = true)

class ChatAdapter(private var messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageText = itemView.findViewById<TextView>(R.id.message_text)
        private val timestampText = itemView.findViewById<TextView>(R.id.timestamp_text)

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

class ChatScreen : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    private val chatAssistant = ChatAssistant()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var loadingDialog: AlertDialog? = null

    private lateinit var preferencesManager: PreferencesManager
    private var isLoadingModel = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize PreferencesManager
        preferencesManager = PreferencesManager(requireContext())

        setupRecyclerView()
        setupSwipeRefresh()
        setupInputControls()

        // Load model from preferences
        loadModelFromPreferences()
    }

    override fun onResume() {
        super.onResume()
        loadModelFromPreferences()

    }

    private fun loadModelFromPreferences() {
        // Don't start new loading if already loading
        if (isLoadingModel) {
            return
        }
        
        val modelUri = preferencesManager.modelUri
        modelUri?.let { filepath ->
            isLoadingModel = true
            showLoadingDialog()
            scope.launch {
                try {
                    val path = filepath
                    
                    withContext(Dispatchers.Main) {
                        addMessage("Loading model @ $path", isUser = false)
                    }
                    
                    // Run model loading on IO thread
                    withContext(Dispatchers.IO) {
                        path.let { chatAssistant.loadChatModel(requireContext(), it) }
                    }
                    
                    withContext(Dispatchers.Main) {
                        hideLoadingDialog()
                        addMessage("AI Assistant ready! How can I help you today?", isUser = false)
                        isLoadingModel = false
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        hideLoadingDialog()
                        addMessage("Failed to load model: ${e.message}", isUser = false)
                        Log.e("ChatScreen", "Model loading failed", e)
                        isLoadingModel = false
                    }
                }
            }
        }

    }

    private fun showLoadingDialog() {
        hideLoadingDialog() // Hide any existing dialog
        loadingDialog = AlertDialog.Builder(requireContext())
            .setTitle("Loading Model")
            .setMessage("Please wait while we load the AI model...")
            .setCancelable(false)
            .create()
        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
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

                // Get AI response
                getAiResponse(message)
            }
        }

        // Send message on Enter key
        binding.messageInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                binding.sendButton.performClick()
                true
            } else {
                false
            }
        }
    }

    private fun getAiResponse(userMessage: String) {
        scope.launch {
            try {
                if (!chatAssistant.isLoaded()) {
                    // Show typing indicator on main thread
                    withContext(Dispatchers.Main) {
                        addMessage("AI is typing...", isUser = false)
                    }

                    // Get response from AI on IO thread
                    val response = withContext(Dispatchers.IO) {
                        chatAssistant.chat(userMessage)
                    }

                    // Update UI on main thread
                    withContext(Dispatchers.Main) {
                        // Remove typing indicator and add actual response
                        if (messages.isNotEmpty()) {
                            messages.removeAt(messages.size - 1)
                            chatAdapter.updateMessages(messages.toList())
                        }
                        addMessage(response, isUser = false)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        addMessage("AI Assistant not available", isUser = false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Remove typing indicator
                    if (messages.isNotEmpty() && messages.last().message == "AI is typing...") {
                        messages.removeAt(messages.size - 1)
                        chatAdapter.updateMessages(messages.toList())
                    }
                    addMessage("Error getting AI response: ${e.message}", isUser = false)
                }
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
        hideLoadingDialog()
        scope.cancel()
        _binding = null
    }
}
