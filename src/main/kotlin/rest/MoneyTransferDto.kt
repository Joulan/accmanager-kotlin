package rest


class MoneyTransferDto {
    var accountFrom: String
    var accountTo: String
    var moneyValue: Double

    // for Jackson
    constructor() {
        this.accountFrom = ""
        this.accountTo = ""
        this.moneyValue = 0.0
    }

    constructor(accountFrom: String, accountTo: String, moneyValue: Double) {
        this.accountFrom = accountFrom
        this.accountTo = accountTo
        this.moneyValue = moneyValue
    }
}