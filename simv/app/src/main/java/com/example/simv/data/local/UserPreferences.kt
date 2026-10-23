package com.example.simv.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Abstração sobre [SharedPreferences] para armazenar configurações simples do usuário.
 *
 * Dados persistidos:
 * - [lastOperatorId]: último ID de operador usado (auto-preenchimento na próxima sessão).
 * - [maxPhotos]: quantidade máxima de fotos por sessão (padrão: 20).
 */
class UserPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    var lastOperatorId: String
        get() = prefs.getString(KEY_OPERATOR_ID, "") ?: ""
        set(value) = prefs.edit().putString(KEY_OPERATOR_ID, value).apply()

    var maxPhotos: Int
        get() = prefs.getInt(KEY_MAX_PHOTOS, DEFAULT_MAX_PHOTOS)
        set(value) = prefs.edit().putInt(KEY_MAX_PHOTOS, value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "simv_user_prefs"
        private const val KEY_OPERATOR_ID = "last_operator_id"
        private const val KEY_MAX_PHOTOS = "max_photos"
        const val DEFAULT_MAX_PHOTOS = 20
    }
}
