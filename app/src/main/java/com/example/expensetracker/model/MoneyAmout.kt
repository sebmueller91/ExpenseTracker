package com.example.expensetracker.model

data class MoneyAmout(
    val amount: Double,
    val currency: Currency
) {
    operator fun plus(other: MoneyAmout): MoneyAmout {
        return MoneyAmout(amount = this.amount + other.amount, currency = this.currency) // TODO: Währung umrechnen
    }

    operator fun minus(other: MoneyAmout): MoneyAmout {
        return MoneyAmout(amount = this.amount - other.amount, currency = this.currency) // TODO: Währung umrechnen
    }

    operator fun div(divisor: Double): MoneyAmout {
        return MoneyAmout(amount = this.amount / divisor, currency = this.currency)
    }
}