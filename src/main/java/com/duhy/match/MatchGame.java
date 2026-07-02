package com.duhy.match;

import com.duhy.tournament.Tournament;
import jakarta.persistence.*;

@Entity
@Table(name = "match_game")
public class MatchGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;
    private String teamA;
    private String teamB;
    private int scoreA;
    private int scoreB;
    @Enumerated(EnumType.STRING)
    private MatchStatus status;
    private int courtNumber;
    private int roundNumber;
    private String servingTeam;
    private int scoreOrder;

    protected MatchGame() {
    }

    public MatchGame(Tournament tournament, String teamA, String teamB, int courtNumber, int roundNumber) {
        this.tournament = tournament;
        this.teamA = teamA;
        this.teamB = teamB;
        this.courtNumber = courtNumber;
        this.roundNumber = roundNumber;
        this.status = MatchStatus.SCHEDULED;
        this.servingTeam = "A";
        this.scoreOrder = 2;
    }

    public void updateScore(int scoreA, int scoreB, String servingTeam, int scoreOrder) {
        this.scoreA = Math.max(0, scoreA);
        this.scoreB = Math.max(0, scoreB);
        this.servingTeam = "B".equals(servingTeam) ? "B" : "A";
        this.scoreOrder = scoreOrder == 1 ? 1 : 2;
        int high = Math.max(this.scoreA, this.scoreB);
        int diff = Math.abs(this.scoreA - this.scoreB);
        this.status = high >= tournament.getMaxScore() || (high >= tournament.getTouchScore() && diff >= 2)
            ? MatchStatus.FINISHED
            : MatchStatus.PLAYING;
    }

    public Long getId() { return id; }
    public Tournament getTournament() { return tournament; }
    public String getTeamA() { return teamA; }
    public String getTeamB() { return teamB; }
    public int getScoreA() { return scoreA; }
    public int getScoreB() { return scoreB; }
    public MatchStatus getStatus() { return status; }
    public int getCourtNumber() { return courtNumber; }
    public int getRoundNumber() { return roundNumber; }
    public String getServingTeam() { return servingTeam; }
    public int getScoreOrder() { return scoreOrder; }
}
