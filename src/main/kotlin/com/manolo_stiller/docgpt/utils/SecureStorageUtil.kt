package com.manolo_stiller.docgpt.utils

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import org.jetbrains.concurrency.runAsync


class SecureStorageUtil(private val uniqueID: String) {
    private val passwordSafe: PasswordSafe = PasswordSafe.instance

    fun storeData(serviceKey: String, valueToSave: String) {
        runAsync {
            val credentialAttributes = createCredentialAttributes(serviceKey)
            PasswordSafe.instance.setPassword(credentialAttributes, valueToSave)

        }
    }

    fun retrieveData(serviceKey: String): String? {
        return passwordSafe.getPassword(createCredentialAttributes(serviceKey))
    }

    private fun createCredentialAttributes(key: String): CredentialAttributes {
        return CredentialAttributes(
            generateServiceName(uniqueID, key)
        )
    }
}