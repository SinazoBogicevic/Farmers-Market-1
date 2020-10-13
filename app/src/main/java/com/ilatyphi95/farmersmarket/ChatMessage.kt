package com.ilatyphi95.farmersmarket

import com.ilatyphi95.farmersmarket.utils.toDate

/**
 * @param chatId not null
 * @param mediaThumbUrl empty string when not specified
 * @param mediaUrl empty string when not specified
 * @param msg message content
 * @param msgId same as node id
 * @param msgType can assume value "TEXT", "IMAGE" etc.
 * @param senderId not null
 * @param timeStamp UNIX base timestamp
 */
data class ChatMessage @JvmOverloads constructor(
    val chatId: String,
    val mediaThumbUrl: String = "",
    val mediaUrl: String = "",
    val msg: String = "",
    val msgId: String = "",
    val msgType: String = "",
    val senderId: String,
    val timeStamp: Long
) {
    val time: String
        get() = toDate(timeStamp)
}