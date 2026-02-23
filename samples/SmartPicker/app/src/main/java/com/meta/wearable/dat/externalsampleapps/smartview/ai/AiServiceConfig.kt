// AiServiceConfig - Local configuration for AI services
// Copy this file to ai_service_config.properties and fill in your API key
// This file is ignored by git

package com.meta.wearable.dat.externalsampleapps.smartview.ai

data class AiServiceConfig(
    val huggingFaceApiKey: String = "YOUR_HUGGINGFACE_API_KEY_HERE"
) {
    companion object {
        val DEFAULT = AiServiceConfig()
        
        fun fromLocalProperties(): AiServiceConfig {
            // This would read from a local properties file
            // For now, return default
            return DEFAULT
        }
    }
}
