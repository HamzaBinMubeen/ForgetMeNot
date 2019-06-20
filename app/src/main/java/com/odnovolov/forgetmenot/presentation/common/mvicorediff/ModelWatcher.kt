package com.odnovolov.forgetmenot.presentation.common.mvicorediff

class ModelWatcher<T> private constructor(
    private val watchers: List<Watcher<T, Any?>>
) {
    private var model: T? = null

    operator fun invoke(newModel: T) {
        val oldModel = model
        watchers.forEach { element ->
            val getter = element.accessor
            val new = getter(newModel)
            if (oldModel == null || element.diffStrategy(getter(oldModel), new)) {
                element.callback(new)
            }
        }

        model = newModel
    }

    private class Watcher<T, R>(
        val accessor: (T) -> R,
        val callback: (R) -> Unit,
        val diffStrategy: DiffStrategy<R>
    )

    class Builder<T> @PublishedApi internal constructor() {
        private val watchers = mutableListOf<Watcher<T, Any?>>()

        fun <R> watch(
            accessor: (T) -> R,
            diff: DiffStrategy<R> = byValue(),
            callback: (R) -> Unit
        ) {
            watchers += Watcher(
                accessor,
                callback,
                diff
            ) as Watcher<T, Any?>
        }

        /*
         * Syntactic sugar around watch (scoped inside the builder)
         */

        operator fun <R> ((T) -> R).invoke(callback: (R) -> Unit) {
            watch(this, callback = callback)
        }

        infix fun <R> ((T) -> R).using(pair: Pair<DiffStrategy<R>, (R) -> Unit>) {
            watch(this, pair.first, pair.second)
        }

        operator fun <R> (DiffStrategy<R>).invoke(callback: (R) -> Unit) =
            this to callback

        @PublishedApi
        internal fun build(): ModelWatcher<T> =
            ModelWatcher(watchers)
    }
}

inline fun <T> modelWatcher(init: ModelWatcher.Builder<T>.() -> Unit): ModelWatcher<T> =
    ModelWatcher.Builder<T>()
        .apply(init)
        .build()
