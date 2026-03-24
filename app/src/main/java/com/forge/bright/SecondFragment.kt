package com.forge.bright

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.forge.bright.databinding.FragmentSecondBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

data class AIModel(val name: String, val url: String? = null, val isLocal: Boolean = false)

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    
    private val models = listOf(
        AIModel("Phi3K - 2-GB", "https://huggingface.co/microsoft/Phi-3-mini-4k-instruct-gguf/resolve/main/Phi-3-mini-4k-instruct-q4.gguf", false),
        AIModel("Select Local Model", null, true)
    )
    
    private var selectedModel: AIModel? = null
    private var selectedLocalModelPath: String? = null
    
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleFileSelection(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        createModelDirectory()
        setupSpinner()
        setupActionButton()
        setupNavigationButton()
    }

    private fun createModelDirectory() {
        val modelsDir = File(requireContext().getExternalFilesDir(null), "HappyBot/Models")
        if (!modelsDir.exists()) {
            modelsDir.mkdirs()
        }
    }

    private fun setupSpinner() {
        val modelNames = models.map { it.name }.toTypedArray()
        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            modelNames
        )
        binding.modelSpinner.adapter = adapter
        
        binding.modelSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedModel = models[position]
                updateUIForModelSelection()
            }
            
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                selectedModel = null
                updateUIForModelSelection()
            }
        }
        
        // Select first model by default
        if (models.isNotEmpty()) {
            binding.modelSpinner.setSelection(0)
            selectedModel = models[0]
            updateUIForModelSelection()
        }
    }

    private fun updateUIForModelSelection() {
        selectedModel?.let { model ->
            if (model.isLocal) {
                binding.downloadButton.text = "Open Folder"
                binding.statusText.text = "Click 'Open Folder' to select a local GGUF model"
            } else {
                binding.downloadButton.text = "Download Model"
                binding.statusText.text = "Selected: ${model.name}"
            }
        } ?: run {
            binding.downloadButton.text = "Download Model"
            binding.statusText.text = "Select a model and click download to begin"
        }
    }

    private fun setupActionButton() {
        binding.downloadButton.setOnClickListener {
            selectedModel?.let { model ->
                if (model.isLocal) {
                    openFilePicker()
                } else {
                    model.url?.let { url ->
                        DownloadTask().execute(url, model.name)
                    }
                }
            }
        }
    }

    private fun setupNavigationButton() {
        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/octet-stream"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/octet-stream", "application/gguf"))
        }
        filePickerLauncher.launch(Intent.createChooser(intent, "Select GGUF Model File"))
    }

    private fun handleFileSelection(uri: Uri) {
        try {
            val fileName = getFileName(uri)
            val modelsDir = File(requireContext().getExternalFilesDir(null), "HappyBot/Models")
            val destinationFile = File(modelsDir, fileName)
            
            // Copy the selected file to the models directory
            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            
            selectedLocalModelPath = destinationFile.absolutePath
            updateStatus("Local model selected: $fileName")
            
        } catch (e: Exception) {
            e.printStackTrace()
            updateStatus("Failed to copy model file: ${e.message}")
        }
    }

    private fun getFileName(uri: Uri): String {
        var fileName = "selected_model.gguf"
        
        // Try to get the actual filename from the URI
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                fileName = it.getString(displayName) ?: "selected_model.gguf"
            }
        }
        
        return fileName
    }

    private fun updateStatus(message: String) {
        binding.statusText.text = message
    }

    private inner class DownloadTask : AsyncTask<String, Int, Boolean>() {
        
        private var fileName = ""
        
        override fun onPreExecute() {
            super.onPreExecute()
            binding.downloadButton.isEnabled = false
            binding.downloadProgress.visibility = View.VISIBLE
            binding.statusText.text = "Starting download..."
        }

        override fun doInBackground(vararg params: String): Boolean {
            val url = params[0]
            fileName = params[1]
            
            return try {
                downloadFile(url, fileName)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        override fun onProgressUpdate(vararg progress: Int?) {
            super.onProgressUpdate(*progress)
            progress[0]?.let { percentage ->
                binding.statusText.text = "Downloading: $percentage%"
            }
        }

        override fun onPostExecute(success: Boolean) {
            super.onPostExecute(success)
            binding.downloadButton.isEnabled = true
            binding.downloadProgress.visibility = View.GONE
            
            if (success) {
                updateStatus("Download completed: $fileName")
            } else {
                updateStatus("Download failed. Please try again.")
            }
        }

        private fun downloadFile(urlString: String, fileName: String) {
            val url = URL(urlString)
            val connection = url.openConnection()
            connection.connect()
            
            val fileLength = connection.contentLength
            val input = url.openStream()
            
            // Create models directory if it doesn't exist
            val modelsDir = File(requireContext().getExternalFilesDir(null), "HappyBot/Models")
            if (!modelsDir.exists()) {
                modelsDir.mkdirs()
            }
            
            val outputFile = File(modelsDir, fileName)
            val output = FileOutputStream(outputFile)
            
            val data = ByteArray(1024)
            var total: Long = 0
            var count: Int
            
            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()
                output.write(data, 0, count)
                
                // Publish progress
                if (fileLength > 0) {
                    val progress = ((total * 100) / fileLength).toInt()
                    publishProgress(progress)
                }
            }
            
            output.flush()
            output.close()
            input.close()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
