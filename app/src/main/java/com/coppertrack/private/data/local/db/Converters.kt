package com.coppertrack.private.data.local.db

import androidx.room.TypeConverter
import com.coppertrack.private.domain.model.*

class Converters {
    @TypeConverter
    fun fromContactType(value: ContactType): String = value.name
    @TypeConverter
    fun toContactType(value: String): ContactType = ContactType.valueOf(value)

    @TypeConverter
    fun fromDealStatus(value: DealStatus): String = value.name
    @TypeConverter
    fun toDealStatus(value: String): DealStatus = DealStatus.valueOf(value)

    @TypeConverter
    fun fromPaymentStatus(value: PaymentStatus): String = value.name
    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus = PaymentStatus.valueOf(value)

    @TypeConverter
    fun fromExpenseStatus(value: ExpenseStatus): String = value.name
    @TypeConverter
    fun toExpenseStatus(value: String): ExpenseStatus = ExpenseStatus.valueOf(value)

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod): String = value.name
    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod = PaymentMethod.valueOf(value)

    @TypeConverter
    fun fromPaymentDirection(value: PaymentDirection): String = value.name
    @TypeConverter
    fun toPaymentDirection(value: String): PaymentDirection = PaymentDirection.valueOf(value)

    @TypeConverter
    fun fromExpenseCategory(value: ExpenseCategory): String = value.name
    @TypeConverter
    fun toExpenseCategory(value: String): ExpenseCategory = ExpenseCategory.valueOf(value)
}
