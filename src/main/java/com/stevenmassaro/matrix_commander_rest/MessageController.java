package com.stevenmassaro.matrix_commander_rest;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
class MessageController {

    @Value("#{'${ROOMS}'.split(';')}")
    private List<String> rooms;

    @RequestMapping(path = "/message/{room}", method = RequestMethod.POST)
    int sendMessage(@PathVariable("room") String room, @RequestBody String message) throws IOException {
        String roomId = getRoomIdFromName(room);
        if (roomId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found.");
        }
        String command = "/bin/python3 /app/matrix_commander/matrix-commander -s /data/store -c /data/credentials.json -r " + roomId + " -m " + message;

        CommandLine cmdLine = CommandLine.parse(command);
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
