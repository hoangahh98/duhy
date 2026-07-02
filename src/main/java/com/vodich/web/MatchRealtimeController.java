package com.vodich.web;

import com.vodich.match.MatchService;
import com.vodich.match.ScoreMessage;
import com.vodich.logging.AppLogService;
import com.vodich.logging.LogLevel;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class MatchRealtimeController {
    private final MatchService matches;
    private final AppLogService logs;

    public MatchRealtimeController(MatchService matches, AppLogService logs) {
        this.matches = matches;
        this.logs = logs;
    }

    @MessageMapping("/tournaments/{tournamentId}/matches/{matchId}/score")
    public void update(@DestinationVariable Long tournamentId, @DestinationVariable Long matchId, ScoreMessage message) {
        try {
            matches.updateScore(tournamentId, matchId, message);
            logs.recordSystem(LogLevel.INFO, "WEBSOCKET", "Cập nhật điểm trận " + matchId,
                "tournamentId=" + tournamentId + ", scoreA=" + message.scoreA() + ", scoreB=" + message.scoreB(), null);
        } catch (RuntimeException ex) {
            logs.recordSystem(LogLevel.ERROR, "WEBSOCKET", "Lỗi cập nhật điểm trận " + matchId,
                "tournamentId=" + tournamentId, ex);
            throw ex;
        }
    }
}
