package r2.adam.bashful

import android.animation.Animator
import android.view.ViewPropertyAnimator


/**
 * Add an action which will be invoked when the animation has ended.
 *
 * @return the [Animator.AnimatorListener] added to the Animator
 * @see Animator.end
 */
inline fun ViewPropertyAnimator.doOnEnd(crossinline action: (animator: Animator) -> Unit) =
    addListener(onEnd = action)

/**
 * Add an action which will be invoked when the animation has started.
 *
 * @return the [Animator.AnimatorListener] added to the Animator
 * @see Animator.start
 */
inline fun ViewPropertyAnimator.doOnStart(crossinline action: (animator: Animator) -> Unit) =
    addListener(onStart = action)

/**
 * Add an action which will be invoked when the animation has been cancelled.
 *
 * @return the [Animator.AnimatorListener] added to the Animator
 * @see Animator.cancel
 */
inline fun ViewPropertyAnimator.doOnCancel(crossinline action: (animator: Animator) -> Unit) =
    addListener(onCancel = action)

/**
 * Add an action which will be invoked when the animation has repeated.
 *
 * @return the [Animator.AnimatorListener] added to the Animator
 */
inline fun ViewPropertyAnimator.doOnRepeat(crossinline action: (animator: Animator) -> Unit) =
    addListener(onRepeat = action)


/**
 * Add a listener to this Animator using the provided actions.
 *
 * @return the [Animator.AnimatorListener] added to the Animator
 */
inline fun ViewPropertyAnimator.addListener(
    crossinline onEnd: (animator: Animator) -> Unit = {},
    crossinline onStart: (animator: Animator) -> Unit = {},
    crossinline onCancel: (animator: Animator) -> Unit = {},
    crossinline onRepeat: (animator: Animator) -> Unit = {}
): ViewPropertyAnimator {
    val listener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animator: Animator) = onRepeat(animator)
        override fun onAnimationEnd(animator: Animator) = onEnd(animator)
        override fun onAnimationCancel(animator: Animator) = onCancel(animator)
        override fun onAnimationStart(animator: Animator) = onStart(animator)
    }
    setListener(listener)
    return this
}