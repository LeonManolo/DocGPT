package com.manolo_stiller.docgpt.ui.components

data class ComboBoxItem(val id: String, val text: String) {
    override fun toString(): String {
        return this.text
    }
}