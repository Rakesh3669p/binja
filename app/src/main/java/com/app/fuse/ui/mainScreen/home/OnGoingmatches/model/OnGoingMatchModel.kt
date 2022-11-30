package com.app.fuse.ui.mainScreen.home.OnGoingmatches.model

data class OnGoingMatchModel(
    val `data`: List<OnGoingMatchList>,
    val msg: String,
    val status: Boolean
)


data class OnGoingMatchList(
    val age: Int,
    val created_at: String,
    val designation: String,
    val email: String,
    val id: Int,
    val is_online: Boolean,
    val match_games_result: MatchGamesResult,
    val match_in_progress: Int,
    val match_with: Int,
    val profile: Any?,
    val socket_id: String,
    val updated_at: String,
    val username: String
)



data class MatchGamesResult(
    val created_at: String,
    val filter: String,
    val from_user_id: Int,
    val id: Int,
    val status: Int,
    val to_user_id: Int,
    val updated_at: String
)
