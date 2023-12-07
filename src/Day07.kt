data class Card(val value: Int) {
	companion object {
		val cardValues = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2') // lower is better
	}

	operator fun compareTo(other: Card): Int = other.value - this.value // because lower is better

	constructor(charValue: Char) : this(cardValues.indexOf(charValue))
}

data class Hand(val cards: List<Card>, val bid: Int) : Comparable<Hand> {
	companion object {
		val handValueLambdas = listOf<(List<Int>) -> Boolean>(
			{ it[0] == 5 }, // 5-of-a-kind
			{ it[0] == 4 }, // 4-of-a-kind
			{ it[0] == 3 && it[1] == 2 }, // full-house
			{ it[0] == 3 }, // 3-of-a-kind
			{ it[0] == 2 && it[1] == 2 }, // two pairs
			{ it[0] == 2 }, // one pair
			{ true }, // nothing
		)

		fun fromString(str: String): Hand {
			return str.split(' ').let { handStr ->
				Hand(
					handStr[0].map { Card(it) },
					handStr[1].toInt()
				)
			}
		}
	}

	private val handFrequencies = cards.groupingBy { it.value }.eachCount().values.sortedByDescending { it }
	val handValue = handValueLambdas.indexOfFirst {
		it.invoke(handFrequencies)
	} // lower is better

	private fun compareHandValue(other: Hand, cardIndex: Int): Int {
		if (cardIndex > cards.lastIndex) throw Exception("Two hands of the same value?")
		this.cards[cardIndex].compareTo(other.cards[cardIndex]).let {
			if (it != 0) return it
			else return compareHandValue(other, cardIndex + 1)
		}
	}

	override operator fun compareTo(other: Hand): Int {
		return if (other.handValue - this.handValue == 0)
			compareHandValue(other, 0)
		else other.handValue - this.handValue
	}
}

fun main() {
	fun part1(input: List<String>) = input.map { Hand.fromString(it) }
		.sorted().mapIndexed { index, hand ->
			(index + 1) * hand.bid
		}.sum()

	fun part2(input: List<String>) = input.size

	val testInput = readInput("Day07_test")
	check(part1(testInput).also { it.println() } == 6446)
//	check(part2(testInput).also { it.println() } == 71503)

	val input = readInput("Day07")
	part1(input).println()
//	part2(input).println()
}
