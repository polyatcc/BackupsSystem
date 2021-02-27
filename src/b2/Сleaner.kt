package backup.clean

import backup.RestorePointChain
import java.lang.Integer.max
import java.time.Instant
import kotlin.math.min

sealed class Cleaner {

    abstract fun countRelevant(restorePointChains: List<RestorePointChain>): Int

    infix fun and(c2: Cleaner) = Both(this, c2)

    infix fun or(c2: Cleaner) = OneOf(this, c2)

}

class LongerThan(private val limit: Int) : Cleaner() {

    override fun countRelevant(restorePointChains: List<RestorePointChain>): Int {
        var count = 0
        var sum = 0
        while (count < restorePointChains.size) {
            val add = restorePointChains[count].size
            if (sum + add > limit) {
                break
            }
            sum += add
            count++
        }
        return count
    }

}

class OlderThan(private val limit: Instant) : Cleaner() {

    override fun countRelevant(restorePointChains: List<RestorePointChain>): Int {
        var count = 0
        while (count < restorePointChains.size && restorePointChains[count][0].creationTime >= limit) {
            count++
        }
        return count
    }

}

class LargerThan(private val limit: Long) : Cleaner() {

    override fun countRelevant(restorePointChains: List<RestorePointChain>): Int {
        var count = 0
        var sum = 0L
        while (count < restorePointChains.size) {
            val add = restorePointChains[count].map { it.size }.sum()
            if (sum + add > limit) {
                break
            }
            sum += add
            count++
        }
        return count
    }

}

class OneOf(private val c1: Cleaner, private val c2: Cleaner) : Cleaner() {

    override fun countRelevant(restorePointChains: List<RestorePointChain>): Int {
        return min(c1.countRelevant(restorePointChains), c2.countRelevant(restorePointChains))
    }

}

class Both(private val c1: Cleaner, private val c2: Cleaner) : Cleaner() {

    override fun countRelevant(restorePointChains: List<RestorePointChain>): Int {
        return max(c1.countRelevant(restorePointChains), c2.countRelevant(restorePointChains))
    }

}