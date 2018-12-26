package io.github.uditkarode.updater

import android.annotation.SuppressLint
import com.topjohnwu.superuser.Shell
import org.json.JSONObject
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

enum class RequestStatus { SUCCESSFUL, FAILED }

fun Int.toBoolean() = this > 0

class UpdateChecker {
    companion object {
        private var reqSuccess: Boolean = false
        private var joResponse: JSONObject = JSONObject()
    }

    fun setJson(json: JSONObject) {
        reqSuccess = true
        joResponse = json
    }

    fun getStatus(): RequestStatus {
        return if(reqSuccess) RequestStatus.SUCCESSFUL else RequestStatus.FAILED
    }

    @SuppressLint("SimpleDateFormat")
    fun updateAvailable(): Boolean {
        joResponse = joResponse.getJSONObject(Constants.DEVICE_CODENAME).getJSONObject(Constants.ROM_NAME)
        return if (joResponse.getString("type") == "OTA")
            SimpleDateFormat("dd/MM/yyyy").parse(joResponse.getString("date")).compareTo(
                SimpleDateFormat("dd/MM/yyyy").parse(getProp("2"))).toBoolean()
        else
            SimpleDateFormat("dd/MM/yyyy").parse(joResponse.getString("date")).compareTo(Date(Timestamp(getProp("ro.build.date.utc").toLong()).time)).toBoolean()
    }

    fun getDirectLink(): String {
        return joResponse.getString("link")
    }

    fun getBuildMD5Hash(): String {
        return joResponse.getString("hash")
    }

    private fun getProp(key: String): String {
        return Shell.sh("getprop $key").exec().out[0]
    }
}