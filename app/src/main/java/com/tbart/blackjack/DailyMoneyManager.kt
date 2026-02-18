package com.tbart.blackjack

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

data class DailyRecord(
    val date: String,
    val money: Int // Gains du jour (peut être négatif)
)

class DailyMoneyManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("blackjack_prefs", Context.MODE_PRIVATE)

    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    @RequiresApi(Build.VERSION_CODES.O)
    val today = LocalDate.now().toString()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    private val firestore = FirebaseFirestore.getInstance()


    companion object {
        private const val KEY_CURRENT_MONEY = "current_money"
        private const val KEY_LAST_RESET_DATE = "last_reset_date"
        private const val KEY_DAILY_HISTORY = "daily_history"
        private const val STARTING_MONEY = 1000
        private const val TAG = "BLACKJACK_DEBUG"
    }

    // Récupère l'argent actuel
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentMoney(): Int {
        Log.d(TAG, "📞 getCurrentMoney() appelé")
        checkAndResetIfNewDay()
        val money = prefs.getInt(KEY_CURRENT_MONEY, STARTING_MONEY)
        Log.d(TAG, "💰 Argent récupéré: $money")
        return money
    }

    // Sauvegarde l'argent actuel
    fun saveCurrentMoney(amount: Int) {
        Log.d(TAG, "💾 Sauvegarde argent: $amount")
        prefs.edit().putInt(KEY_CURRENT_MONEY, amount).apply()

        // Sauvegarde Firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .set(
                    mapOf("currentMoney" to amount),
                    com.google.firebase.firestore.SetOptions.merge()
                )

            Log.d(TAG, "☁️ Argent courant synchronisé sur Firestore")
        }
    }


    // Vérifie si on est un nouveau jour et reset si nécessaire
    @RequiresApi(Build.VERSION_CODES.O)
    fun checkAndResetIfNewDay() {
        val today = dateFormat.format(Date())
        val lastResetDate = prefs.getString(KEY_LAST_RESET_DATE, "")

        Log.d(TAG, "========================================")
        Log.d(TAG, "🔍 VÉRIFICATION NOUVEAU JOUR")
        Log.d(TAG, "📅 Date aujourd'hui: $today")
        Log.d(TAG, "📅 Dernière date enregistrée: '$lastResetDate'")
        Log.d(TAG, "========================================")

        if (lastResetDate != today) {
            val currentMoney = prefs.getInt(KEY_CURRENT_MONEY, STARTING_MONEY)

            if (lastResetDate?.isNotEmpty() == true) {
                saveDailyRecord(lastResetDate, currentMoney)
            }

            // Reset l'argent
            prefs.edit()
                .putInt(KEY_CURRENT_MONEY, STARTING_MONEY)
                .putString(KEY_LAST_RESET_DATE, today)
                .apply()

            Log.d(TAG, "🔄 Argent reset à $STARTING_MONEY")
            Log.d(TAG, "🔄 Date mise à jour: $today")
        } else {
            Log.d(TAG, "❌ Même jour ($today), pas de reset")
        }
    }

    // Sauvegarde un record quotidien
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveDailyRecord(date: String, earnings: Int) {
        val history = getDailyHistory().toMutableList()

        history.add(0, DailyRecord(date, earnings))

        if (history.size > 30) {
            history.removeAt(history.size - 1)
        }

        val json = gson.toJson(history)

        prefs.edit().putString(KEY_DAILY_HISTORY, json).apply()

        // Sauvegarde Firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val dailyData = hashMapOf(
                "money" to earnings,
                "timestamp" to FieldValue.serverTimestamp()
            )

            firestore.collection("users")
                .document(userId)
                .collection("dailyGains")
                .document(date)
                .set(dailyData)

            Log.d(TAG, "DailyRecord sauvegardé sur Firestore")
        } else {
            Log.d(TAG, "Utilisateur non connecté, Firestore ignoré")
        }

    }

    // Récupère l'historique quotidien
    fun getDailyHistory(): List<DailyRecord> {
        val json = prefs.getString(KEY_DAILY_HISTORY, null)

        if (json == null) {
            return emptyList()
        }

        val type = object : TypeToken<List<DailyRecord>>() {}.type
        val history: List<DailyRecord> = gson.fromJson(json, type)

        history.forEachIndexed { index, record ->
            Log.d(TAG, "  [$index] date=${record.date}, money=${record.money}")
        }

        return history
    }

    // Force un reset manuel (pour tester)
    fun forceReset() {
        val today = dateFormat.format(Date())
        Log.d(TAG, "🔧 forceReset() appelé")
        prefs.edit()
            .putInt(KEY_CURRENT_MONEY, STARTING_MONEY)
            .putString(KEY_LAST_RESET_DATE, today)
            .apply()
        Log.d(TAG, "✅ Reset forcé effectué")
    }

    fun syncFromFirestore(onComplete: (() -> Unit)? = null) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val money = document.getLong("currentMoney")?.toInt()
                    if (money != null) {
                        prefs.edit().putInt(KEY_CURRENT_MONEY, money).apply()
                    }
                }
                onComplete?.invoke()
            }
    }


}