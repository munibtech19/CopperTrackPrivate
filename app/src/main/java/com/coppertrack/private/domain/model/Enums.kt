package com.coppertrack.private.domain.model

enum class ContactType {
    SUPPLIER,
    BUYER,
    BOTH
}

enum class DealStatus {
    DRAFT,
    STOCK_PENDING,
    QUOTED,
    PURCHASED,
    IN_TRANSIT,
    PARTIALLY_SOLD,
    COMPLETED,
    CANCELLED
}

enum class PaymentStatus {
    UNPAID,
    PARTIAL,
    PAID,
    OVERPAID
}

enum class ExpenseStatus {
    UNPAID,
    PAID,
    REIMBURSED
}

enum class PaymentMethod {
    CASH,
    BANK_TRANSFER,
    JAZZCASH,
    EASYPAISA,
    CHEQUE,
    OTHER
}

enum class PaymentDirection {
    INFLOW,   // money received (from buyer or other)
    OUTFLOW   // money paid (to supplier or expense)
}

enum class ExpenseCategory {
    TRANSPORT,
    LOADING_UNLOADING,
    LABOUR,
    WEIGHING,
    PACKAGING,
    COMMISSION,
    STORAGE,
    COMMUNICATION,
    BANK_CHARGES,
    TAXES,
    MISCELLANEOUS
}
