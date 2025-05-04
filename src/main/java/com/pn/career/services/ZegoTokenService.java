package com.pn.career.services;

import com.pn.career.utils.TokenServerAssistant;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class ZegoTokenService {

    private static final long APP_ID = 1509815072;
    private static final String SERVER_SECRET = "40ef81546a6d06ea6661f3072d8544be";
public String generateToken(Long userId, Number roomId) {
    int effectiveTimeInSeconds = 3600; // 1 hour

    JSONObject payloadData = new JSONObject();
    payloadData.put("room_id", roomId.toString());

    JSONObject privilege = new JSONObject();
    privilege.put(TokenServerAssistant.PrivilegeKeyLogin, TokenServerAssistant.PrivilegeEnable);
    privilege.put(TokenServerAssistant.PrivilegeKeyPublish, TokenServerAssistant.PrivilegeEnable);
    payloadData.put("privilege", privilege);
    payloadData.put("stream_id_list", null);

    String payload = payloadData.toJSONString();
    TokenServerAssistant.TokenInfo token = TokenServerAssistant.generateToken04(
            APP_ID, userId.toString(), SERVER_SECRET, effectiveTimeInSeconds, payload
    );

    if (token.error != null && token.error.code != TokenServerAssistant.ErrorCode.SUCCESS) {
        throw new RuntimeException("Failed to generate token: " + token.error.message);
    }

    return token.data;
}
}
