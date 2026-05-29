package com.feupsplaza.chat.shared.protocol;

/**
 * All available Operations that the server can perform
 */
public enum Operation {
    LOGIN,
    REGISTER,
    JOIN_ROOM,
    LIST_ROOMS,
    SEND_MESSAGE,
    CHAT_BROADCAST,
    LEAVE_ROOM,
    LIST_ROOM_USERS,
    ERROR,
    RECONNECT,
    GET_HISTORY,
    CREATE_AI_ROOM,
    
}
