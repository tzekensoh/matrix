class Matrix(val values: Array<Array<Double>>) {
    operator fun times(m2: Matrix):Matrix
    {
        // check dimension compatibility
        val thisRows = values.count()
        val thisColumns: Int
        if (thisRows >= 1)
            thisColumns = values[0].count()
        else
            throw Exception("This matrix has no row")
        val thatRows = m2.values.count()
        val thatColumns: Int
        if (thatRows >= 1)
            thatColumns = m2.values[0].count()
        else
            throw Exception("That matrix has no row")
        if (thisColumns != thatRows)
            throw Exception("Dimension incompatible")

        var result = Matrix(Array(thisRows){Array(thatColumns){0.0}})
        for (i in 0 until result.values.count())
            for (j in 0 until result.values[i].count())
            {
                // loop through ith row in this
                // as well as the jth column in m2
                var k = 0
                while (k < values[i].count())
                {
                    result.values[i][j] += values[i][k] * m2.values[k][j]
                    k++
                }
            }
        return result
    }

    fun inverse(): Matrix
    {
        val augmentedMatrix = augment()
        // perform Gauss-Jordan
        // First loop
        for (i in 0 until augmentedMatrix.values.count())
        {
            augmentedMatrix.swapForNonZeroAt(i) // need the ith element in the ith row to be non-zero
            augmentedMatrix.reduceToOneAt(i)
        }

        // Second loop
        for (i in values.count() - 2 downTo 0)
        {
            augmentedMatrix.clearAfterOneAt(i) // need all elements after ith element to be zero
        }

        // Extract the augmented part of the matrix
        val extracted = Array(augmentedMatrix.values.count()){Array(augmentedMatrix.values.count()){0.0} }
        for (i in 0 until augmentedMatrix.values.count())
        {
            for (j in 0 until augmentedMatrix.values.count())
                extracted[i][j] = augmentedMatrix.values[i][j + augmentedMatrix.values.count()]
        }
        return Matrix(extracted)
    }

    private fun clearAfterOneAt(i: Int)
    {
        // every row after the ith row is cleared
        // use each of these row
        for (j in i + 1 until values.count())
        {
            // calculate the multiple
            val multiple = values[i][j]
            for (k in 0 until values[i].count())
            {
                values[i][k] -= values[j][k] * multiple
            }
        }
    }

    private fun reduceToOneAt(i: Int) {
        // assuming all rows before the ith has 1 at the diagonal and 0s before that
        // first subtract multiples of the previous rows to get all zero before ith
        for (j in 0 until i)
        {
            // calculate the multiple for the jth row
            val multiple = values[i][j]
            for (k in 0 until values[i].count())
            {
                values[i][k] -= values[j][k] * multiple
            }
        }

        // divide the whole row by the number at ith
        val divisor = values[i][i]
        for (m in i until values[i].count())
            values[i][m] /= divisor
    }

    private fun swapForNonZeroAt(i: Int)
    {
        // find a row with NonZero ith element to swap with the ith row starting from itself
        // if it is itself then no need to swap
        if (values[i][i] != 0.0)
            return

        // val temp: Array<Double>
        for (j in i+1 until values.count())
            if (values[j][i] != 0.0)
            {
                val temp = values[j] // this is the row
                values[j] = values[i]
                values[i] = temp
                break
            }
    }

    private fun augment(): Matrix
    {
        val (thisRows, thisColumns: Int) = checkSquare()
        val augmented = Array(thisRows){Array(0){0.0} }
        for (i in 0 until thisRows)
        {
            // augment the ith row
            val newArr = Array(thisColumns * 2){0.0}
            for (k in 0 until newArr.count()) {
                newArr[k] = when
                {
                    (k < thisColumns) -> values[i][k]
                    (k == thisColumns + i) -> 1.0
                    else -> 0.0
                }
            }
            augmented[i] = newArr
        }
        return Matrix(augmented)
    }

    private fun checkSquare(): Pair<Int, Int> {
        // check dimension compatibility
        val thisRows = values.count()
        val thisColumns: Int
        if (thisRows >= 1)
            thisColumns = values[0].count()
        else
            throw Exception("This matrix has no row")
        if (thisRows != thisColumns)
            throw Exception("Dimension incompatible")
        return Pair(thisRows, thisColumns)
    }

    override fun toString(): String {
        val sb = StringBuffer()
        for (array in values) {
            for (value in array) {
                sb.append("$value ")
            }
            sb.append("\n")
        }

        return sb.toString()
    }
}

fun main () {
    val m1 = Matrix(arrayOf(arrayOf(2.0),arrayOf(-1.0),arrayOf(7.0)))
    val m2 = Matrix(arrayOf(arrayOf(1.0,3.0,2.0), arrayOf(2.0,7.0,7.0), arrayOf(2.0,5.0,2.0)))

    println(m2.inverse() * m1)
}
