package eternal.future.efmodloader.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface Screen {
    val id: String
    val extraData: List<Map<String, Any?>>
}

data class DefaultScreen(
    override val id: String,
    override val extraData: List<Map<String, Any?>> = emptyList()
) : Screen


object ScreenRegistry {
    val screens = mutableMapOf<String, Screen>()

    fun register(screen: Screen) {
        screens[screen.id] = screen
    }

    fun getScreen(id: String): Screen? = screens[id]
}

data class AnimationSpecs(
    val enter: EnterTransition = fadeIn(),
    val exit: ExitTransition = fadeOut()
)

enum class BackMode {
    ONE_BY_ONE,
    DIRECT,
    TO_BOTTOM,
    TO_DEFAULT
}

class NavigationViewModel : ViewModel() {
    private val _defaultScreen = mutableStateOf("")
    private val _navigationStack = mutableListOf<Screen>()
    private val _currentScreen = MutableStateFlow<Pair<Screen?, AnimationSpecs>>(Pair(null, defaultAnimation()))
    val currentScreen: StateFlow<Pair<Screen?, AnimationSpecs>> get() = _currentScreen.asStateFlow()

    fun navigateTo(
        screenId: String,
        extraData: List<Map<String, Any?>> = emptyList(),
        animationSpecs: AnimationSpecs = defaultAnimation()
    ) {
        viewModelScope.launch {
            clearPreviousExtraData()

            val screen = ScreenRegistry.getScreen(screenId)?.let {
                DefaultScreen(it.id, extraData)
            }
            if (screen != null) {
                _navigationStack.add(screen)
                _currentScreen.emit(Pair(screen, animationSpecs))
            }
        }
    }

    fun navigateBack(mode: BackMode, targetScreenId: String? = null, animationSpecs: AnimationSpecs = defaultAnimation()) {
        viewModelScope.launch {
            when (mode) {
                BackMode.ONE_BY_ONE -> {
                    if (_navigationStack.size > 1) {
                        _navigationStack.removeLastOrNull()
                        val previousScreen = _navigationStack.lastOrNull()
                        _currentScreen.emit(Pair(previousScreen, animationSpecs))
                    } else {
                        _currentScreen.emit(_currentScreen.value)
                    }
                }
                BackMode.DIRECT -> {
                    if (targetScreenId != null) {
                        val targetScreen = ScreenRegistry.getScreen(targetScreenId)
                        if (targetScreen != null) {
                            _navigationStack.clear()
                            _navigationStack.add(targetScreen)
                            _currentScreen.emit(Pair(targetScreen, animationSpecs))
                        }
                    }
                }
                BackMode.TO_BOTTOM -> {
                    if (_navigationStack.isNotEmpty()) {
                        val bottomScreen = _navigationStack.firstOrNull()
                        _navigationStack.clear()
                        if (bottomScreen != null) {
                            _navigationStack.add(bottomScreen)
                            _currentScreen.emit(Pair(bottomScreen, animationSpecs))
                        } else {
                            _currentScreen.emit(Pair(null, animationSpecs))
                        }
                    }
                }
                BackMode.TO_DEFAULT -> {
                    val defaultScreenId = _defaultScreen.value
                    val defaultScreen = ScreenRegistry.getScreen(defaultScreenId)
                    if (defaultScreen != null) {
                        _navigationStack.clear()
                        _navigationStack.add(defaultScreen)
                        _currentScreen.emit(Pair(defaultScreen, animationSpecs))
                    }
                }
            }
        }
    }

    fun setInitialScreen(screenId: String, animationSpecs: AnimationSpecs = defaultAnimation()) {
        _defaultScreen.value = screenId
        navigateTo(screenId = screenId, animationSpecs = animationSpecs)
    }

    fun refreshCurrentScreen(animationSpecs: AnimationSpecs = defaultAnimation()) {
        viewModelScope.launch {
            val current = _navigationStack.lastOrNull()
            if (current != null) {
                val refreshedScreen = DefaultScreen(
                    id = current.id,
                    extraData = listOf(mapOf("refreshTimestamp" to System.currentTimeMillis()))
                )
                _currentScreen.emit(Pair(refreshedScreen, animationSpecs))
            }
        }
    }

    fun removeScreen(screenId: String) {
        viewModelScope.launch {
            val indexToRemove = _navigationStack.indexOfFirst { it.id == screenId }
            if (indexToRemove != -1) {
                val removedScreen = _navigationStack.removeAt(indexToRemove)
                ScreenRegistry.screens.remove(removedScreen.id)
                if (_navigationStack.lastOrNull()?.id == screenId) {
                    val newCurrent = if (_navigationStack.isNotEmpty()) _navigationStack.last() else null
                    _currentScreen.emit(Pair(newCurrent, defaultAnimation()))
                }
            }
        }
    }

    fun removeCurrentScreen() {
        viewModelScope.launch {
            if (_navigationStack.isNotEmpty()) {
                val removedScreen = _navigationStack.removeLastOrNull()
                if (removedScreen != null) {
                    ScreenRegistry.screens.remove(removedScreen.id)
                    val newCurrent = if (_navigationStack.isNotEmpty()) _navigationStack.last() else null
                    _currentScreen.emit(Pair(newCurrent, defaultAnimation()))
                }
            }
        }
    }

    fun addExtraData(extraData: Map<String, Any?>) {
        viewModelScope.launch {
            val current = _navigationStack.lastOrNull() as? DefaultScreen ?: return@launch
            val updatedScreen = current.copy(
                extraData = current.extraData + listOf(extraData)
            )
            val index = _navigationStack.indexOfLast { it.id == current.id }
            if (index != -1) {
                _navigationStack[index] = updatedScreen
                _currentScreen.emit(Pair(updatedScreen, defaultAnimation()))
            }
        }
    }

    fun getExtraData(key: String? = null): List<Map<String, Any?>> {
        return _currentScreen.value.first?.extraData.orEmpty().let { data ->
            if (key == null) data else data.filter { it.containsKey(key) }
        }
    }

    private fun clearPreviousExtraData() {
        val lastScreen = _navigationStack.takeIf { it.isNotEmpty() }?.lastOrNull()
        _navigationStack.clear()
        lastScreen?.let { _navigationStack.add(it) }
    }

    private fun defaultAnimation(): AnimationSpecs {
        return AnimationSpecs(
            enter = fadeIn(animationSpec = tween(durationMillis = 500)),
            exit = fadeOut(animationSpec = tween(durationMillis = 500))
        )
    }
}