package com.selfaxiom.api.rewards;

import com.selfaxiom.api.auth.AuthModels.AuthenticatedUser;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rewards")
public class RewardController {

  private final RewardService rewardService;

  public RewardController(RewardService rewardService) {
    this.rewardService = rewardService;
  }

  @GetMapping("/summary")
  public ResponseEntity<RewardSummaryResponse> summary(@AuthenticationPrincipal AuthenticatedUser currentUser) {
    return ResponseEntity.ok(rewardService.summary(currentUser.id()));
  }

  @GetMapping("/history")
  public ResponseEntity<List<RewardEventResponse>> history(@AuthenticationPrincipal AuthenticatedUser currentUser) {
    return ResponseEntity.ok(rewardService.history(currentUser.id()));
  }
}
