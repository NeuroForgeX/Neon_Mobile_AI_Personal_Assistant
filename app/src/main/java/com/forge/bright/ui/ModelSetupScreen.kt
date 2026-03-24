package com.forge.bright.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.forge.bright.R
import com.forge.bright.databinding.FragmentSecondBinding
import com.forge.bright.utils.FileTransferHelper
import com.forge.bright.utils.hasStoragePermissions
import com.forge.bright.utils.requestAllPermissions
import com.forge.bright.utils.PreferencesManager
import java.net.URL
import android.util.Log
import androidx.core.net.toUri
import java.io.File

class ModelSetupScreen : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferencesManager: PreferencesManager

    private val documentTreeLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let { selectedUri ->
            // Take persistable permission to access this URI
//            FileTransferHelper.takePersistentPermission(requireContext(), selectedUri)
        }
    }

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let { selectedUri ->
            handleDirectorySelection(selectedUri)
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

        preferencesManager = PreferencesManager(requireContext())
        setupButtons()
    }

    private fun setupButtons() {
        // Folder Selection Button
        binding.phi3Button.setOnClickListener {
            if (!hasStoragePermissions(requireContext())) {
                requestAllPermissions(requireActivity())
            } else {
                openDocumentTreePicker()
            }
        }

        // Local File Selection Button
        binding.localFileButton.setOnClickListener {
            openFilePicker()
        }

        // Back Button
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    private fun openDocumentTreePicker() {
        Toast.makeText(requireContext(), "Select folder to copy to internal storage...", Toast.LENGTH_SHORT).show()
        val intent = FileTransferHelper.createDirectoryPickerIntent()
        documentTreeLauncher.launch(null)
    }

    private fun openFilePicker() {
        Toast.makeText(requireContext(), "Select model directory containing .gguf files...", Toast.LENGTH_SHORT).show()
        val intent = FileTransferHelper.createDirectoryPickerIntent()
        filePickerLauncher.launch(null)
    }

    private fun handleDirectorySelection(uri: Uri) {
        try {
            Toast.makeText(requireContext(), "Selected model directory", Toast.LENGTH_SHORT).show()
            
            // Take permission for the directory and copy to internal cache
            FileTransferHelper.takePersistentPermission(requireContext(), uri)
            
            // Copy entire directory to internal cache
            Thread {
                try {
                    Log.d("ModelSetupScreen", "Starting copy from: $uri")
                    
                    val copiedFiles = FileTransferHelper.copyDirectory(requireContext(), uri)
                    
                    if (copiedFiles != null) {
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "Copy complete! ${copiedFiles.size} files copied",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("ModelSetupScreen", "=== Copied Files ===")
                            copiedFiles.forEachIndexed { index, fp ->
                                Log.d("ModelSetupScreen", "File ${index + 1}: $fp")

                                if (fp.lowercase().endsWith(".gguf")) {
                                    val fileName = File(fp).name
                                    preferencesManager.saveModelInfo(
                                        fileName,
                                        fp
                                    )
                                }
                            }
                            
                            Toast.makeText(
                                requireContext(),
                                "Model files copied successfully!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        requireActivity().runOnUiThread {
                            Log.e("ModelSetupScreen", "Failed to copy model directory")
                            Toast.makeText(
                                requireContext(),
                                "Failed to copy model files",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ModelSetupScreen", "Error during copy: ${e.message}", e)
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Copy failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }.start()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to select directory: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLocalFileSelection(uri: Uri) {
        try {
            val fileName = getFileName(uri)
            val filePath = getAbsolutePath(uri)

            val modelsDirUri = File(filePath!!).parentFile!!.parentFile!!;

            // Take permission for the models directory and copy to internal cache
            FileTransferHelper.takePersistentPermission(requireContext(), modelsDirUri.toUri())

            // Copy entire models directory to internal cache
            Thread {
                try {
                    Log.d("ModelSetupScreen", "Starting copy from: $modelsDirUri")

                    val copiedFiles =
                        FileTransferHelper.copyDirectory(requireContext(), modelsDirUri.toUri())

                    if (copiedFiles != null) {
                        requireActivity().runOnUiThread {
                            Log.d("ModelSetupScreen", "=== Copied Files ===")
                            copiedFiles.forEachIndexed { index, fp ->
                                Log.d("ModelSetupScreen", "File ${index + 1}: $fp")

                                if (fp.lowercase().endsWith(".gguf")) {
                                    preferencesManager.saveModelInfo(
                                        fileName,
                                        fp
                                    )
                                }
                            }

                            Toast.makeText(
                                requireContext(),
                                "Model files copied successfully!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        requireActivity().runOnUiThread {
                            Log.e("ModelSetupScreen", "Failed to copy model directory")
                            Toast.makeText(
                                requireContext(),
                                "Failed to copy model files",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ModelSetupScreen", "Error during copy: ${e.message}", e)
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Copy failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }.start()
            Toast.makeText(requireContext(), "Selected file: $fileName", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAbsolutePath(uri: Uri): String? {
        return try {
            when {
                uri.scheme == "file" -> uri.path
                DocumentsContract.isDocumentUri(requireContext(), uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    if (docId.startsWith("primary:")) {
                        val path = docId.substring(8) // Remove "primary:" prefix
                        "/storage/emulated/0/$path"
                    } else {
                        null
                    }
                }

                else -> {
                    // Try to get path from content resolver
                    val cursor = requireContext().contentResolver.query(
                        uri,
                        arrayOf(android.provider.OpenableColumns.DISPLAY_NAME),
                        null,
                        null,
                        null
                    )
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val displayName =
                                it.getString(it.getColumnIndexOrThrow(android.provider.OpenableColumns.DISPLAY_NAME))
                            // For content URIs, we might not be able to get absolute path directly
                            // Return the URI string as fallback
                            uri.toString()
                        } else null
                    } ?: uri.toString()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            uri.toString() // Fallback to URI string
        }
    }

    private fun getFileName(uri: Uri): String {
        var fileName = "selected_model.gguf"

        // Try to get the actual filename from the URI
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName =
                    it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                fileName = it.getString(displayName) ?: "selected_model.gguf"
            }
        }

        return fileName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
