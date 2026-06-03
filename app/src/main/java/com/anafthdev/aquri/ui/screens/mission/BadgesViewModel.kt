package com.anafthdev.aquri.ui.screens.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.aquri.data.badge.DefaultBadgeCatalog
import com.anafthdev.aquri.data.model.entity.BadgeEntity
import com.anafthdev.aquri.data.repository.BadgeRepository
import com.anafthdev.aquri.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BadgesViewModel @Inject constructor(
    private val badgeRepository: BadgeRepository,
    private val userRepository: UserRepository,
    private val defaultBadgeCatalog: DefaultBadgeCatalog
) : ViewModel() {

    val uiState = userRepository.getUser()
        .flatMapLatest { user ->
            if (user == null) {
                badgeRepository.getAllBadges().map { storedBadges ->
                    BadgesUiState(
                        badges = mergeAvailableBadges(storedBadges).map { badge ->
                            BadgeUiModel(
                                badge = badge,
                                isEarned = false
                            )
                        },
                        isLoading = false
                    )
                }
            } else {
                combine(
                    badgeRepository.getAllBadges(),
                    badgeRepository.getUserBadges(user.id)
                ) { storedBadges, userBadges ->
                    val earnedIds = userBadges.map { it.badgeId }.toSet()
                    val earnedNames = storedBadges
                        .filter { it.id in earnedIds }
                        .map { it.name.normalizedBadgeName() }
                        .toSet()

                    BadgesUiState(
                        badges = mergeAvailableBadges(storedBadges).map { badge ->
                            BadgeUiModel(
                                badge = badge,
                                isEarned = badge.id in earnedIds || badge.name.normalizedBadgeName() in earnedNames
                            )
                        },
                        isLoading = false
                    )
                }
            }
        }
        .catch { throwable ->
            emit(
                BadgesUiState(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Failed to load badges."
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BadgesUiState()
        )

    private fun mergeAvailableBadges(storedBadges: List<BadgeEntity>): List<BadgeEntity> {
        val byName = linkedMapOf<String, BadgeEntity>()
        defaultBadgeCatalog.badges().forEach { badge ->
            byName[badge.name.normalizedBadgeName()] = badge
        }
        storedBadges.forEach { badge ->
            byName[badge.name.normalizedBadgeName()] = badge
        }

        return byName.values.sortedWith(
            compareBy<BadgeEntity> { it.category.ordinal }
                .thenBy { it.rarity.ordinal }
                .thenBy { it.name }
        )
    }
}

data class BadgesUiState(
    val badges: List<BadgeUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class BadgeUiModel(
    val badge: BadgeEntity,
    val isEarned: Boolean
)

private fun String.normalizedBadgeName(): String {
    return trim().lowercase()
}
