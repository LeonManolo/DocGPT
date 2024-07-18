package com.manolo_stiller.docgpt.utils
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.awt.RelativePoint

// https://www.plugin-dev.com/intellij/general/notifications/
class NotificationUtils {
    private val balloonNotification: NotificationGroup =
        NotificationGroupManager.getInstance().getNotificationGroup("DocGPT_balloon")
    private val toolWindowNotification: NotificationGroup =
        NotificationGroupManager.getInstance().getNotificationGroup("DocGPT_tool_window")

    fun sendNotification(
        project: Project,
        content: String,
        notificationType: NotificationType = NotificationType.INFORMATION
    ) {
        balloonNotification.createNotification(content, notificationType).notify(project)
    }

    fun sendToolWindowNotification(
        project: Project,
        content: String,
        notificationType: NotificationType = NotificationType.INFORMATION
    ) {
        toolWindowNotification.createNotification(content, notificationType).notify(project)
    }

    fun sendSmallNotification(
        project: Project,
        content: String,
        messageType: MessageType = MessageType.INFO,
        fadeoutTime: Long = 3000
    ) {
        val statusBar = WindowManager.getInstance().getStatusBar(project)
        val toolWindowManager = ToolWindowManager.getInstance(project)
        if (statusBar != null) {
            JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(content, messageType, null)
                .setFadeoutTime(fadeoutTime)
                .createBalloon()
                .show(statusBar.component?.let { RelativePoint.getNorthEastOf(it) }, Balloon.Position.below)
        }
        //JBPopupFactory.getInstance().createMessage("HALLO")
    }
}