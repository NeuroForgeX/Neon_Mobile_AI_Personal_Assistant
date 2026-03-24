package com.forge.bright.ai

enum class Models(name: String, url: String, description: String) {
    PHI3K_2GB(
        "PHI-3K",
        "https://huggingface.co/microsoft/Phi-3-mini-4k-instruct-gguf/resolve/main/Phi-3-mini-4k-instruct-q4.gguf",
        "2GB"
    ),
    PHI3K_7GB("PHI-3K", "", "7GB"),
    LOCAL("Local", "", "In Device")
}
