package top.yukonga.mishka.service

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import top.yukonga.mishka.platform.ProxyServiceController

class VpnPermissionActivity : Activity() {

    private val subscriptionId: String?
        get() = intent.getStringExtra(EXTRA_SUBSCRIPTION_ID)?.ifEmpty { null }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = subscriptionId ?: run {
            finish()
            return
        }
        val permissionIntent = VpnService.prepare(this)
        if (permissionIntent == null) {
            startVpnAndFinish(id)
        } else {
            @Suppress("DEPRECATION")
            startActivityForResult(permissionIntent, VPN_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val id = subscriptionId
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK && id != null) {
            ProxyServiceController(this).start(id)
        }
        finish()
    }

    private fun startVpnAndFinish(subscriptionId: String) {
        ProxyServiceController(this).start(subscriptionId)
        finish()
    }

    companion object {
        const val EXTRA_SUBSCRIPTION_ID = "subscription_id"
        private const val VPN_REQUEST_CODE = 1002
    }
}
