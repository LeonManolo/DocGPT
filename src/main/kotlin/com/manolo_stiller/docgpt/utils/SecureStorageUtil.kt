package com.manolo_stiller.docgpt.utils

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe


class SecureStorageUtil(private val uniqueID: String) {
    private val passwordSafe: PasswordSafe = PasswordSafe.instance
    fun storeData(serviceKey: String, valueToSave: String) {
        val credentialAttributes = CredentialAttributes(uniqueID, serviceKey)
        val credentials = Credentials("", valueToSave)
        passwordSafe.set(credentialAttributes, credentials)
    }

    fun retrieveData(serviceKey: String): String? {
        val credentialAttributes = CredentialAttributes(uniqueID, serviceKey)
        return passwordSafe.get(credentialAttributes)?.getPasswordAsString()
    }
}