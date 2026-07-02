package com.duhy.tournament;

import com.duhy.shared.Money;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "tournament")
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String venue;
    private LocalDateTime startTime;
    private int courtCount;
    private int expectedPlayers;
    @Enumerated(EnumType.STRING)
    private PlayType playType;
    @Enumerated(EnumType.STRING)
    private TournamentFormat format;
    private int touchScore;
    private int maxScore;
    private BigDecimal courtCost;
    private BigDecimal foodCost;
    private BigDecimal prizeCost;
    private BigDecimal otherCost;
    private BigDecimal prizeRate1;
    private BigDecimal prizeRate2;
    private BigDecimal prizeRate3;
    private boolean externalRegistrationEnabled;

    protected Tournament() {
    }

    public Tournament(String name) {
        this.name = name;
        this.venue = "";
        this.courtCount = 1;
        this.expectedPlayers = 10;
        this.playType = PlayType.SINGLES;
        this.format = TournamentFormat.ROUND_ROBIN;
        this.touchScore = 11;
        this.maxScore = 15;
        this.courtCost = BigDecimal.ZERO;
        this.foodCost = BigDecimal.ZERO;
        this.prizeCost = BigDecimal.ZERO;
        this.otherCost = BigDecimal.ZERO;
        this.prizeRate1 = BigDecimal.valueOf(50);
        this.prizeRate2 = BigDecimal.valueOf(30);
        this.prizeRate3 = BigDecimal.valueOf(20);
    }

    public BigDecimal minimumFeePerPlayer() {
        BigDecimal total = courtCost.add(foodCost).add(prizeCost).add(otherCost);
        BigDecimal shared = expectedPlayers > 0
            ? total.divide(BigDecimal.valueOf(expectedPlayers), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        return Money.roundUpToStep(shared, 50_000);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getVenue() { return venue; }
    public LocalDateTime getStartTime() { return startTime; }
    public int getCourtCount() { return courtCount; }
    public int getExpectedPlayers() { return expectedPlayers; }
    public PlayType getPlayType() { return playType; }
    public TournamentFormat getFormat() { return format; }
    public int getTouchScore() { return touchScore; }
    public int getMaxScore() { return maxScore; }
    public BigDecimal getCourtCost() { return courtCost; }
    public BigDecimal getFoodCost() { return foodCost; }
    public BigDecimal getPrizeCost() { return prizeCost; }
    public BigDecimal getOtherCost() { return otherCost; }
    public boolean isExternalRegistrationEnabled() { return externalRegistrationEnabled; }

    public void update(TournamentCommand command) {
        this.name = command.name();
        this.venue = command.venue();
        this.startTime = command.startTime();
        this.courtCount = command.courtCount();
        this.expectedPlayers = command.expectedPlayers();
        this.playType = command.playType();
        this.format = command.format();
        this.touchScore = command.touchScore();
        this.maxScore = command.maxScore();
        this.courtCost = command.courtCost();
        this.foodCost = command.foodCost();
        this.prizeCost = command.prizeCost();
        this.otherCost = command.otherCost();
        this.externalRegistrationEnabled = command.externalRegistrationEnabled();
    }
}
