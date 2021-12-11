package com.itmo.microservices.demo.payment.impl.util

import java.lang.StringBuilder

class PaymentServiceMeta {

    companion object {

        private fun getExternalServiceUrl() : String {
            return "http://77.234.215.138:30027"
        }

        fun makeTransactionUri() : String {

            val sb = StringBuilder()

            sb.append(getExternalServiceUrl())
            sb.append("/transactions")

            return sb.toString()
        }
    }
}