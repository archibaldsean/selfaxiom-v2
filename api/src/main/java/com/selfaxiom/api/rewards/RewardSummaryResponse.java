package com.selfaxiom.api.rewards;

public class RewardSummaryResponse {

  private final int pointsBalance;
  private final long totalEarned;

  public RewardSummaryResponse(int pointsBalance, long totalEarned) {
    this.pointsBalance = pointsBalance;
    this.totalEarned = totalEarned;
  }

  public int getPointsBalance() {
    return pointsBalance;
  }

  public long getTotalEarned() {
    return totalEarned;
  }
}
