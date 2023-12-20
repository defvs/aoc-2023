@file:Suppress("unused")

import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.max
import kotlin.math.min

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
	.toString(16)
	.padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun String.substringBetween(after: Char, before: Char) = this.substringAfter(after).substringBefore(before)

/**
 * Given a list of lists (ie a matrix), transpose it
 */
fun <T> List<List<T>>.transpose(): List<List<T>> {
	return (this[0].indices).map { i -> (this.indices).map { j -> this[j][i] } }
}

/**
 * Given an array of arrays (ie a matrix), transpose it
 */
inline fun <reified T> Array<Array<T>>.transpose(): Array<Array<T>> {
	return (this[0].indices).map { i -> (this.indices).map { j -> this[j][i] }.toTypedArray() }.toTypedArray()
}

fun IntRange.intersect(other: IntRange) = IntRange(max(this.first, other.first), min(this.last, other.last))