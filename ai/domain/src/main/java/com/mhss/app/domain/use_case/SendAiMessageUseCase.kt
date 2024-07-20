package com.mhss.app.domain.use_case

import com.mhss.app.di.geminiApi
import com.mhss.app.di.openaiApi
import com.mhss.app.domain.AiConstants
import com.mhss.app.domain.model.AiMessage
import com.mhss.app.domain.model.InternetError
import com.mhss.app.domain.model.InvalidToken
import com.mhss.app.domain.model.NetworkResult
import com.mhss.app.domain.model.Success
import com.mhss.app.domain.model.UnexpectedError
import com.mhss.app.domain.repository.AiApi
import com.mhss.app.preferences.domain.model.AiProvider
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

@Single
class SendAiMessageUseCase(
    @Named(openaiApi) private val openai: AiApi,
    @Named(geminiApi) private val gemini: AiApi
) {
    suspend operator fun invoke(
        messages: List<AiMessage>,
        systemPrompt: String,
        key: String,
        model: String,
        provider: AiProvider,
        baseURL: String = ""
    ): NetworkResult {
        return try {
            if (key.isBlank()) return InvalidToken
            when (provider) {
                AiProvider.OpenAI -> {
                    Success(
                        openai.sendMessage(
                            baseUrl = baseURL,
                            messages = messages,
                            systemMessage = systemPrompt,
                            model = model,
                            key = key
                        )
                    )
                }
                AiProvider.Gemini -> {
                    Success(
                        gemini.sendMessage(
                            baseUrl = AiConstants.GEMINI_BASE_URL,
                            messages = messages,
                            systemMessage = systemPrompt,
                            model = model,
                            key = key
                        )
                    )
                }

                else -> throw IllegalStateException("No AI provider is chosen")
            }
        } catch (e: IOException) {
            InternetError
        } catch (e: Exception) {
            UnexpectedError
        }
    }
}