@file:Suppress("DuplicatedCode")

data class Card2(val value: Int) {
	companion object {
		val cardValues = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J') // lower is better
	}

	operator fun compareTo(other: Card2): Int = other.value - this.value // because lower is better

	constructor(charValue: Char) : this(cardValues.indexOf(charValue))
}

data class Hand2(val cards: List<Card2>, val bid: Int) : Comparable<Hand2> {
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

		fun fromString(str: String): Hand2 {
			return str.split(' ').let { handStr ->
				Hand2(
					handStr[0].map { Card2(it) },
					handStr[1].toInt()
				)
			}
		}
	}

	private val handFrequencies = cards.groupingBy { it.value }.eachCount().let {
		val jockers = it.getOrDefault(12, 0)
		if (jockers == 5) return@let listOf(Pair(12, 5))
		val list = it.filterKeys{ it != 12 }.toList().sortedByDescending { it.second }.toMutableList()
		list[0] = Pair(list[0].first, list[0].second + jockers)
		list.toList()
	}.map { it.second }
	val handValue = handValueLambdas.indexOfFirst {
		it.invoke(handFrequencies)
	} // lower is better

	private fun compareHandValue(other: Hand2, cardIndex: Int): Int {
		if (cardIndex > cards.lastIndex) throw Exception("Two hands of the same value?")
		this.cards[cardIndex].compareTo(other.cards[cardIndex]).let {
			if (it != 0) return it
			else return compareHandValue(other, cardIndex + 1)
		}
	}

	override operator fun compareTo(other: Hand2): Int {
		return if (other.handValue - this.handValue == 0)
			compareHandValue(other, 0)
		else other.handValue - this.handValue
	}
}

fun main() {
	fun part2(input: List<String>) = input.map { Hand2.fromString(it) }
		.sorted().mapIndexed { index, hand ->
			(index + 1) * hand.bid
		}.sum()

	val testInput = readInput("Day07_test")
	check(part2(testInput).also { it.println() } == 5905)

	val input = readInput("Day07")
	part2(input).println()
}
