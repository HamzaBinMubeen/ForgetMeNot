package com.odnovolov.forgetmenot.domain.feature.parser

import java.lang.Exception

class IllegalCardFormatException(override val message: String) : Exception(message)