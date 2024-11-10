package com.stevenmassaro.matrix_commander_rest;

import com.stevenmassaro.matrix_commander_rest.model.MessageRequest;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
class MessageController {

    @Value("#{'${ROOMS}'.split(';')}")
    private List<String> rooms;


    @RequestMapping(path = "/message/{room}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public int sendMessageForm(
            @PathVariable("room") String room,
            @RequestParam MultiValueMap<String, String> params
    ) throws IOException {
        String message = params.getFirst("message");
        return sendMessageInternal(room, message);
    }

    @RequestMapping(path = "/message/{room}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public int sendMessageJson(
            @PathVariable("room") String room,
            @RequestBody MessageRequest messageRequest
    ) throws IOException {
        String message = messageRequest.getMessage();
        return sendMessageInternal(room, message);
    }

    private int sendMessageInternal(String room, String message) throws IOException {
        if (message == null || message.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message cannot be empty.");
        }

        String roomId = getRoomIdFromName(room);
        if (roomId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found.");
        }
        String command = "/bin/python3 /app/matrix_commander/matrix-commander -s /data/store -c /data/credentials.json -r " + roomId + " -m";

        CommandLine cmdLine = CommandLine.parse(command);
        cmdLine.addArgument(message, false);
        DefaultExecutor executor = DefaultExecutor.builder().get();
        return executor.execute(cmdLine);
    }

    private String getRoomIdFromName(String roomName) {
        for (String room : rooms) {
            String name = room.split(",")[0];
            if (roomName.equals(name)) {
                return room.split(",")[1];
            }
        }
        return null;
    }
}
