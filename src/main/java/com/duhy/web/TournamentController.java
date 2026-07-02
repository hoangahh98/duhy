package com.duhy.web;

import com.duhy.auth.AppFeature;
import com.duhy.auth.AuthSession;
import com.duhy.auth.CurrentUser;
import com.duhy.auth.PermissionService;
import com.duhy.match.MatchService;
import com.duhy.player.PlayerRepository;
import com.duhy.tournament.PaymentStatus;
import com.duhy.tournament.Tournament;
import com.duhy.tournament.TournamentForm;
import com.duhy.tournament.TournamentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TournamentController {
    private final TournamentService tournaments;
    private final MatchService matches;
    private final PlayerRepository players;
    private final AuthSession auth;
    private final PermissionService permissions;

    public TournamentController(TournamentService tournaments, MatchService matches, PlayerRepository players, AuthSession auth, PermissionService permissions) {
        this.tournaments = tournaments;
        this.matches = matches;
        this.players = players;
        this.auth = auth;
        this.permissions = permissions;
    }

    @GetMapping("/tournaments")
    public String index(HttpSession session, Model model) {
        CurrentUser user = require(session, AppFeature.TOURNAMENTS);
        model.addAttribute("tournaments", tournaments.summariesFor(user));
        return "tournament/index";
    }

    @GetMapping("/tournaments/new")
    public String create(HttpSession session, Model model) {
        requireAdmin(session, AppFeature.TOURNAMENTS);
        model.addAttribute("form", TournamentForm.defaults());
        return "tournament/form";
    }

    @PostMapping("/tournaments")
    public String store(HttpSession session, @ModelAttribute TournamentForm form) {
        requireAdmin(session, AppFeature.TOURNAMENTS);
        Tournament tournament = tournaments.save(tournaments.commandFromForm(form));
        return "redirect:/tournaments/" + tournament.getId() + "/players";
    }

    @GetMapping("/tournaments/{id}/{section}")
    public String detail(HttpSession session, @PathVariable Long id, @PathVariable String section, Model model, HttpServletRequest request) {
        CurrentUser user = require(session, AppFeature.TOURNAMENTS);
        if (!tournaments.canView(user, id)) {
            throw new IllegalStateException("Khong co quyen");
        }
        Tournament tournament = tournaments.get(id);
        model.addAttribute("tournament", tournament);
        model.addAttribute("section", section);
        model.addAttribute("registrations", tournaments.activeRegistrations(id));
        model.addAttribute("withdrawnRegistrations", tournaments.withdrawnRegistrations(id));
        model.addAttribute("players", players.findAll());
        model.addAttribute("matches", matches.byTournament(id));
        model.addAttribute("externalLink", request.getScheme() + "://" + request.getServerName() + (request.getServerPort() > 0 ? ":" + request.getServerPort() : "") + "/external-register/" + id);
        return "tournament/detail";
    }

    @PostMapping("/tournaments/{id}/registrations")
    public String register(HttpSession session, @PathVariable Long id, @RequestParam Long playerId) {
        requireAdmin(session, AppFeature.TOURNAMENTS);
        tournaments.registerPlayer(id, playerId);
        return "redirect:/tournaments/" + id + "/players";
    }

    @PostMapping("/registrations/{id}/withdraw")
    public String withdraw(HttpSession session, @PathVariable Long id, @RequestParam Long tournamentId) {
        requireAdmin(session, AppFeature.TOURNAMENTS);
        tournaments.withdraw(id);
        return "redirect:/tournaments/" + tournamentId + "/players";
    }

    @PostMapping("/registrations/{id}/restore")
    public String restore(HttpSession session, @PathVariable Long id, @RequestParam Long tournamentId) {
        requireAdmin(session, AppFeature.TOURNAMENTS);
        tournaments.restore(id);
        return "redirect:/tournaments/" + tournamentId + "/players";
    }

    @PostMapping("/registrations/{id}/payment")
    public String payment(HttpSession session, @PathVariable Long id, @RequestParam Long tournamentId, @RequestParam String amount, @RequestParam PaymentStatus status) {
        requireAdmin(session, AppFeature.TOURNAMENTS);
        tournaments.updatePayment(id, amount, status);
        return "redirect:/tournaments/" + tournamentId + "/fees";
    }

    @PostMapping("/tournaments/{id}/generate-schedule")
    public String generate(HttpSession session, @PathVariable Long id) {
        requireAdmin(session, AppFeature.TOURNAMENTS);
        matches.generateRoundRobin(id);
        return "redirect:/tournaments/" + id + "/schedule";
    }

    private CurrentUser require(HttpSession session, AppFeature feature) {
        CurrentUser user = auth.current(session).orElseThrow();
        if (!permissions.can(user, feature)) {
            throw new IllegalStateException("Khong co quyen");
        }
        return user;
    }

    private void requireAdmin(HttpSession session, AppFeature feature) {
        CurrentUser user = auth.current(session).orElseThrow();
        if (!user.admin() || !permissions.can(user, feature)) {
            throw new IllegalStateException("Khong co quyen");
        }
    }
}
