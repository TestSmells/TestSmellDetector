package testsmell

import com.github.javaparser.ast.CompilationUnit
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import testsmell.smell.AssertionRoulette
import testsmell.smell.EagerTest
import testsmell.Util;
import thresholds.DefaultThresholds
import thresholds.SpadiniThresholds

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestDetectionCorrectness {

    private lateinit var testCompilationUnit: CompilationUnit
    private lateinit var productionCompilationUnit: CompilationUnit
    private lateinit var testFile: TestFile
    private val booleanGranularity: ((AbstractSmell) -> Any) = { it.hasSmell() }
    private val numericGranularity: ((AbstractSmell) -> Any) = { it.numberOfSmellyTests }

    @BeforeEach
    fun setup() {
        testCompilationUnit = Util.parseJava(fractionTest)
        productionCompilationUnit = Util.parseJava(fractionSource)
        testFile = mock(TestFile::class.java)
        `when`(testFile.testFileNameWithoutExtension).thenReturn("fake/path")
        `when`(testFile.productionFileNameWithoutExtension).thenReturn("fake/path")
    }

    @Test
    fun `Test assertion roulette boolean`() {
        val smell = AssertionRoulette(DefaultThresholds())
        smell.runAnalysis(testCompilationUnit, productionCompilationUnit,
                testFile.testFileNameWithoutExtension, testFile.productionFileNameWithoutExtension)
        `when`(testFile.testSmells).thenReturn(listOf(smell))
        val values = testFile.testSmells.map { booleanGranularity.invoke(it) }
        Assertions.assertTrue(values[0] as Boolean)
    }

    @Test
    fun `Test assertion roulette granular`() {
        val smell = AssertionRoulette(DefaultThresholds())
        smell.runAnalysis(testCompilationUnit, productionCompilationUnit,
                testFile.testFileNameWithoutExtension, testFile.productionFileNameWithoutExtension)
        `when`(testFile.testSmells).thenReturn(listOf(smell))
        val values = testFile.testSmells.map { numericGranularity.invoke(it) }
        Assertions.assertEquals(24, values[0] as Int)
    }

    @Test
    fun `Test assertion roulette granular with Spadini`() {
        val smell = AssertionRoulette(SpadiniThresholds())
        smell.runAnalysis(testCompilationUnit, productionCompilationUnit,
                testFile.testFileNameWithoutExtension, testFile.productionFileNameWithoutExtension)
        `when`(testFile.testSmells).thenReturn(listOf(smell))
        val values = testFile.testSmells.map { numericGranularity.invoke(it) }
        Assertions.assertEquals(23, values[0] as Int)
    }

    @Test
    fun `Test eager test boolean`() {
        val smell = EagerTest(DefaultThresholds())
        smell.runAnalysis(testCompilationUnit, productionCompilationUnit,
                testFile.testFileNameWithoutExtension, testFile.productionFileNameWithoutExtension)
        `when`(testFile.testSmells).thenReturn(listOf(smell))
        val values = testFile.testSmells.map { booleanGranularity.invoke(it) }
        Assertions.assertTrue(values[0] as Boolean)
    }

    @Test
    fun `Test eager test granular`() {
        val smell = EagerTest(DefaultThresholds())
        smell.runAnalysis(testCompilationUnit, productionCompilationUnit,
                testFile.testFileNameWithoutExtension, testFile.productionFileNameWithoutExtension)
        `when`(testFile.testSmells).thenReturn(listOf(smell))
        val values = testFile.testSmells.map { numericGranularity.invoke(it) }
        Assertions.assertEquals(24, values[0] as Int)
    }

    @Test
    fun `Test eager test granular spadini threshold`() {
        val smell = EagerTest(SpadiniThresholds())
        smell.runAnalysis(testCompilationUnit, productionCompilationUnit,
                testFile.testFileNameWithoutExtension, testFile.productionFileNameWithoutExtension)
        `when`(testFile.testSmells).thenReturn(listOf(smell))
        val values = testFile.testSmells.map { numericGranularity.invoke(it) }
        Assertions.assertEquals(24, values[0] as Int)
    }

    @Test
    fun `Test number of methods detected`() {
        val declaration = testCompilationUnit.types[0]
        Assertions.assertEquals(25, declaration.methods.size)
    }

    private val fractionSource = """
        /*
         * Licensed to the Apache Software Foundation (ASF) under one or more
         * contributor license agreements.  See the NOTICE file distributed with
         * this work for additional information regarding copyright ownership.
         * The ASF licenses this file to You under the Apache License, Version 2.0
         * (the "License"); you may not use this file except in compliance with
         * the License.  You may obtain a copy of the License at
         *
         *      http://www.apache.org/licenses/LICENSE-2.0
         *
         * Unless required by applicable law or agreed to in writing, software
         * distributed under the License is distributed on an "AS IS" BASIS,
         * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
         * See the License for the specific language governing permissions and
         * limitations under the License.
         */
        package org.apache.commons.lang3.math;

        import java.math.BigInteger;

        import org.apache.commons.lang3.Validate;

        /**
         * <p>{@code Fraction} is a {@code Number} implementation that
         * stores fractions accurately.</p>
         *
         * <p>This class is immutable, and interoperable with most methods that accept
         * a {@code Number}.</p>
         *
         * <p>Note that this class is intended for common use cases, it is <i>int</i>
         * based and thus suffers from various overflow issues. For a BigInteger based
         * equivalent, please see the Commons Math BigFraction class. </p>
         *
         * @since 2.0
         */
        public final class Fraction extends Number implements Comparable<Fraction> {

            /**
             * Required for serialization support. Lang version 2.0.
             *
             * @see java.io.Serializable
             */
            private static final long serialVersionUID = 65382027393090L;

            /**
             * {@code Fraction} representation of 0.
             */
            public static final Fraction ZERO = new Fraction(0, 1);
            /**
             * {@code Fraction} representation of 1.
             */
            public static final Fraction ONE = new Fraction(1, 1);
            /**
             * {@code Fraction} representation of 1/2.
             */
            public static final Fraction ONE_HALF = new Fraction(1, 2);
            /**
             * {@code Fraction} representation of 1/3.
             */
            public static final Fraction ONE_THIRD = new Fraction(1, 3);
            /**
             * {@code Fraction} representation of 2/3.
             */
            public static final Fraction TWO_THIRDS = new Fraction(2, 3);
            /**
             * {@code Fraction} representation of 1/4.
             */
            public static final Fraction ONE_QUARTER = new Fraction(1, 4);
            /**
             * {@code Fraction} representation of 2/4.
             */
            public static final Fraction TWO_QUARTERS = new Fraction(2, 4);
            /**
             * {@code Fraction} representation of 3/4.
             */
            public static final Fraction THREE_QUARTERS = new Fraction(3, 4);
            /**
             * {@code Fraction} representation of 1/5.
             */
            public static final Fraction ONE_FIFTH = new Fraction(1, 5);
            /**
             * {@code Fraction} representation of 2/5.
             */
            public static final Fraction TWO_FIFTHS = new Fraction(2, 5);
            /**
             * {@code Fraction} representation of 3/5.
             */
            public static final Fraction THREE_FIFTHS = new Fraction(3, 5);
            /**
             * {@code Fraction} representation of 4/5.
             */
            public static final Fraction FOUR_FIFTHS = new Fraction(4, 5);


            /**
             * The numerator number part of the fraction (the three in three sevenths).
             */
            private final int numerator;
            /**
             * The denominator number part of the fraction (the seven in three sevenths).
             */
            private final int denominator;

            /**
             * Cached output hashCode (class is immutable).
             */
            private transient int hashCode = 0;
            /**
             * Cached output toString (class is immutable).
             */
            private transient String toString = null;
            /**
             * Cached output toProperString (class is immutable).
             */
            private transient String toProperString = null;

            /**
             * <p>Constructs a {@code Fraction} instance with the 2 parts
             * of a fraction Y/Z.</p>
             *
             * @param numerator  the numerator, for example the three in 'three sevenths'
             * @param denominator  the denominator, for example the seven in 'three sevenths'
             */
            private Fraction(final int numerator, final int denominator) {
                this.numerator = numerator;
                this.denominator = denominator;
            }

            /**
             * <p>Creates a {@code Fraction} instance with the 2 parts
             * of a fraction Y/Z.</p>
             *
             * <p>Any negative signs are resolved to be on the numerator.</p>
             *
             * @param numerator  the numerator, for example the three in 'three sevenths'
             * @param denominator  the denominator, for example the seven in 'three sevenths'
             * @return a new fraction instance
             * @throws ArithmeticException if the denominator is {@code zero}
             * or the denominator is {@code negative} and the numerator is {@code Integer#MIN_VALUE}
             */
            public static Fraction getFraction(int numerator, int denominator) {
                if (denominator == 0) {
                    throw new ArithmeticException("The denominator must not be zero");
                }
                if (denominator < 0) {
                    if (numerator == Integer.MIN_VALUE || denominator == Integer.MIN_VALUE) {
                        throw new ArithmeticException("overflow: can't negate");
                    }
                    numerator = -numerator;
                    denominator = -denominator;
                }
                return new Fraction(numerator, denominator);
            }

            /**
             * <p>Creates a {@code Fraction} instance with the 3 parts
             * of a fraction X Y/Z.</p>
             *
             * <p>The negative sign must be passed in on the whole number part.</p>
             *
             * @param whole  the whole number, for example the one in 'one and three sevenths'
             * @param numerator  the numerator, for example the three in 'one and three sevenths'
             * @param denominator  the denominator, for example the seven in 'one and three sevenths'
             * @return a new fraction instance
             * @throws ArithmeticException if the denominator is {@code zero}
             * @throws ArithmeticException if the denominator is negative
             * @throws ArithmeticException if the numerator is negative
             * @throws ArithmeticException if the resulting numerator exceeds
             *  {@code Integer.MAX_VALUE}
             */
            public static Fraction getFraction(final int whole, final int numerator, final int denominator) {
                if (denominator == 0) {
                    throw new ArithmeticException("The denominator must not be zero");
                }
                if (denominator < 0) {
                    throw new ArithmeticException("The denominator must not be negative");
                }
                if (numerator < 0) {
                    throw new ArithmeticException("The numerator must not be negative");
                }
                long numeratorValue;
                if (whole < 0) {
                    numeratorValue = whole * (long) denominator - numerator;
                } else {
                    numeratorValue = whole * (long) denominator + numerator;
                }
                if (numeratorValue < Integer.MIN_VALUE || numeratorValue > Integer.MAX_VALUE) {
                    throw new ArithmeticException("Numerator too large to represent as an Integer.");
                }
                return new Fraction((int) numeratorValue, denominator);
            }

            /**
             * <p>Creates a reduced {@code Fraction} instance with the 2 parts
             * of a fraction Y/Z.</p>
             *
             * <p>For example, if the input parameters represent 2/4, then the created
             * fraction will be 1/2.</p>
             *
             * <p>Any negative signs are resolved to be on the numerator.</p>
             *
             * @param numerator  the numerator, for example the three in 'three sevenths'
             * @param denominator  the denominator, for example the seven in 'three sevenths'
             * @return a new fraction instance, with the numerator and denominator reduced
             * @throws ArithmeticException if the denominator is {@code zero}
             */
            public static Fraction getReducedFraction(int numerator, int denominator) {
                if (denominator == 0) {
                    throw new ArithmeticException("The denominator must not be zero");
                }
                if (numerator == 0) {
                    return ZERO; // normalize zero.
                }
                // allow 2^k/-2^31 as a valid fraction (where k>0)
                if (denominator == Integer.MIN_VALUE && (numerator & 1) == 0) {
                    numerator /= 2;
                    denominator /= 2;
                }
                if (denominator < 0) {
                    if (numerator == Integer.MIN_VALUE || denominator == Integer.MIN_VALUE) {
                        throw new ArithmeticException("overflow: can't negate");
                    }
                    numerator = -numerator;
                    denominator = -denominator;
                }
                // simplify fraction.
                final int gcd = greatestCommonDivisor(numerator, denominator);
                numerator /= gcd;
                denominator /= gcd;
                return new Fraction(numerator, denominator);
            }

            /**
             * <p>Creates a {@code Fraction} instance from a {@code double} value.</p>
             *
             * <p>This method uses the <a href="http://archives.math.utk.edu/articles/atuyl/confrac/">
             *  continued fraction algorithm</a>, computing a maximum of
             *  25 convergents and bounding the denominator by 10,000.</p>
             *
             * @param value  the double value to convert
             * @return a new fraction instance that is close to the value
             * @throws ArithmeticException if {@code |value| &gt; Integer.MAX_VALUE}
             *  or {@code value = NaN}
             * @throws ArithmeticException if the calculated denominator is {@code zero}
             * @throws ArithmeticException if the algorithm does not converge
             */
            public static Fraction getFraction(double value) {
                final int sign = value < 0 ? -1 : 1;
                value = Math.abs(value);
                if (value > Integer.MAX_VALUE || Double.isNaN(value)) {
                    throw new ArithmeticException("The value must not be greater than Integer.MAX_VALUE or NaN");
                }
                final int wholeNumber = (int) value;
                value -= wholeNumber;

                int numer0 = 0; // the pre-previous
                int denom0 = 1; // the pre-previous
                int numer1 = 1; // the previous
                int denom1 = 0; // the previous
                int numer2 = 0; // the current, setup in calculation
                int denom2 = 0; // the current, setup in calculation
                int a1 = (int) value;
                int a2 = 0;
                double x1 = 1;
                double x2 = 0;
                double y1 = value - a1;
                double y2 = 0;
                double delta1, delta2 = Double.MAX_VALUE;
                double fraction;
                int i = 1;
                do {
                    delta1 = delta2;
                    a2 = (int) (x1 / y1);
                    x2 = y1;
                    y2 = x1 - a2 * y1;
                    numer2 = a1 * numer1 + numer0;
                    denom2 = a1 * denom1 + denom0;
                    fraction = (double) numer2 / (double) denom2;
                    delta2 = Math.abs(value - fraction);
                    a1 = a2;
                    x1 = x2;
                    y1 = y2;
                    numer0 = numer1;
                    denom0 = denom1;
                    numer1 = numer2;
                    denom1 = denom2;
                    i++;
                } while (delta1 > delta2 && denom2 <= 10000 && denom2 > 0 && i < 25);
                if (i == 25) {
                    throw new ArithmeticException("Unable to convert double to fraction");
                }
                return getReducedFraction((numer0 + wholeNumber * denom0) * sign, denom0);
            }

            /**
             * <p>Creates a Fraction from a {@code String}.</p>
             *
             * <p>The formats accepted are:</p>
             *
             * <ol>
             *  <li>{@code double} String containing a dot</li>
             *  <li>'X Y/Z'</li>
             *  <li>'Y/Z'</li>
             *  <li>'X' (a simple whole number)</li>
             * </ol>
             * <p>and a .</p>
             *
             * @param str  the string to parse, must not be {@code null}
             * @return the new {@code Fraction} instance
             * @throws NullPointerException if the string is {@code null}
             * @throws NumberFormatException if the number format is invalid
             */
            public static Fraction getFraction(String str) {
                Validate.notNull(str, "str");
                // parse double format
                int pos = str.indexOf('.');
                if (pos >= 0) {
                    return getFraction(Double.parseDouble(str));
                }

                // parse X Y/Z format
                pos = str.indexOf(' ');
                if (pos > 0) {
                    final int whole = Integer.parseInt(str.substring(0, pos));
                    str = str.substring(pos + 1);
                    pos = str.indexOf('/');
                    if (pos < 0) {
                        throw new NumberFormatException("The fraction could not be parsed as the format X Y/Z");
                    }
                    final int numer = Integer.parseInt(str.substring(0, pos));
                    final int denom = Integer.parseInt(str.substring(pos + 1));
                    return getFraction(whole, numer, denom);
                }

                // parse Y/Z format
                pos = str.indexOf('/');
                if (pos < 0) {
                    // simple whole number
                    return getFraction(Integer.parseInt(str), 1);
                }
                final int numer = Integer.parseInt(str.substring(0, pos));
                final int denom = Integer.parseInt(str.substring(pos + 1));
                return getFraction(numer, denom);
            }

            // Accessors
            //-------------------------------------------------------------------

            /**
             * <p>Gets the numerator part of the fraction.</p>
             *
             * <p>This method may return a value greater than the denominator, an
             * improper fraction, such as the seven in 7/4.</p>
             *
             * @return the numerator fraction part
             */
            public int getNumerator() {
                return numerator;
            }

            /**
             * <p>Gets the denominator part of the fraction.</p>
             *
             * @return the denominator fraction part
             */
            public int getDenominator() {
                return denominator;
            }

            /**
             * <p>Gets the proper numerator, always positive.</p>
             *
             * <p>An improper fraction 7/4 can be resolved into a proper one, 1 3/4.
             * This method returns the 3 from the proper fraction.</p>
             *
             * <p>If the fraction is negative such as -7/4, it can be resolved into
             * -1 3/4, so this method returns the positive proper numerator, 3.</p>
             *
             * @return the numerator fraction part of a proper fraction, always positive
             */
            public int getProperNumerator() {
                return Math.abs(numerator % denominator);
            }

            /**
             * <p>Gets the proper whole part of the fraction.</p>
             *
             * <p>An improper fraction 7/4 can be resolved into a proper one, 1 3/4.
             * This method returns the 1 from the proper fraction.</p>
             *
             * <p>If the fraction is negative such as -7/4, it can be resolved into
             * -1 3/4, so this method returns the positive whole part -1.</p>
             *
             * @return the whole fraction part of a proper fraction, that includes the sign
             */
            public int getProperWhole() {
                return numerator / denominator;
            }

            // Number methods
            //-------------------------------------------------------------------

            /**
             * <p>Gets the fraction as an {@code int}. This returns the whole number
             * part of the fraction.</p>
             *
             * @return the whole number fraction part
             */
            @Override
            public int intValue() {
                return numerator / denominator;
            }

            /**
             * <p>Gets the fraction as a {@code long}. This returns the whole number
             * part of the fraction.</p>
             *
             * @return the whole number fraction part
             */
            @Override
            public long longValue() {
                return (long) numerator / denominator;
            }

            /**
             * <p>Gets the fraction as a {@code float}. This calculates the fraction
             * as the numerator divided by denominator.</p>
             *
             * @return the fraction as a {@code float}
             */
            @Override
            public float floatValue() {
                return (float) numerator / (float) denominator;
            }

            /**
             * <p>Gets the fraction as a {@code double}. This calculates the fraction
             * as the numerator divided by denominator.</p>
             *
             * @return the fraction as a {@code double}
             */
            @Override
            public double doubleValue() {
                return (double) numerator / (double) denominator;
            }

            // Calculations
            //-------------------------------------------------------------------

            /**
             * <p>Reduce the fraction to the smallest values for the numerator and
             * denominator, returning the result.</p>
             *
             * <p>For example, if this fraction represents 2/4, then the result
             * will be 1/2.</p>
             *
             * @return a new reduced fraction instance, or this if no simplification possible
             */
            public Fraction reduce() {
                if (numerator == 0) {
                    return equals(ZERO) ? this : ZERO;
                }
                final int gcd = greatestCommonDivisor(Math.abs(numerator), denominator);
                if (gcd == 1) {
                    return this;
                }
                return getFraction(numerator / gcd, denominator / gcd);
            }

            /**
             * <p>Gets a fraction that is the inverse (1/fraction) of this one.</p>
             *
             * <p>The returned fraction is not reduced.</p>
             *
             * @return a new fraction instance with the numerator and denominator
             *         inverted.
             * @throws ArithmeticException if the fraction represents zero.
             */
            public Fraction invert() {
                if (numerator == 0) {
                    throw new ArithmeticException("Unable to invert zero.");
                }
                if (numerator==Integer.MIN_VALUE) {
                    throw new ArithmeticException("overflow: can't negate numerator");
                }
                if (numerator<0) {
                    return new Fraction(-denominator, -numerator);
                }
                return new Fraction(denominator, numerator);
            }

            /**
             * <p>Gets a fraction that is the negative (-fraction) of this one.</p>
             *
             * <p>The returned fraction is not reduced.</p>
             *
             * @return a new fraction instance with the opposite signed numerator
             */
            public Fraction negate() {
                // the positive range is one smaller than the negative range of an int.
                if (numerator==Integer.MIN_VALUE) {
                    throw new ArithmeticException("overflow: too large to negate");
                }
                return new Fraction(-numerator, denominator);
            }

            /**
             * <p>Gets a fraction that is the positive equivalent of this one.</p>
             * <p>More precisely: {@code (fraction &gt;= 0 ? this : -fraction)}</p>
             *
             * <p>The returned fraction is not reduced.</p>
             *
             * @return {@code this} if it is positive, or a new positive fraction
             *  instance with the opposite signed numerator
             */
            public Fraction abs() {
                if (numerator >= 0) {
                    return this;
                }
                return negate();
            }

            /**
             * <p>Gets a fraction that is raised to the passed in power.</p>
             *
             * <p>The returned fraction is in reduced form.</p>
             *
             * @param power  the power to raise the fraction to
             * @return {@code this} if the power is one, {@code ONE} if the power
             * is zero (even if the fraction equals ZERO) or a new fraction instance
             * raised to the appropriate power
             * @throws ArithmeticException if the resulting numerator or denominator exceeds
             *  {@code Integer.MAX_VALUE}
             */
            public Fraction pow(final int power) {
                if (power == 1) {
                    return this;
                } else if (power == 0) {
                    return ONE;
                } else if (power < 0) {
                    if (power == Integer.MIN_VALUE) { // MIN_VALUE can't be negated.
                        return this.invert().pow(2).pow(-(power / 2));
                    }
                    return this.invert().pow(-power);
                } else {
                    final Fraction f = this.multiplyBy(this);
                    if (power % 2 == 0) { // if even...
                        return f.pow(power / 2);
                    }
                    return f.pow(power / 2).multiplyBy(this);
                }
            }

            /**
             * <p>Gets the greatest common divisor of the absolute value of
             * two numbers, using the "binary gcd" method which avoids
             * division and modulo operations.  See Knuth 4.5.2 algorithm B.
             * This algorithm is due to Josef Stein (1961).</p>
             *
             * @param u  a non-zero number
             * @param v  a non-zero number
             * @return the greatest common divisor, never zero
             */
            private static int greatestCommonDivisor(int u, int v) {
                // From Commons Math:
                if (u == 0 || v == 0) {
                    if (u == Integer.MIN_VALUE || v == Integer.MIN_VALUE) {
                        throw new ArithmeticException("overflow: gcd is 2^31");
                    }
                    return Math.abs(u) + Math.abs(v);
                }
                // if either operand is abs 1, return 1:
                if (Math.abs(u) == 1 || Math.abs(v) == 1) {
                    return 1;
                }
                // keep u and v negative, as negative integers range down to
                // -2^31, while positive numbers can only be as large as 2^31-1
                // (i.e. we can't necessarily negate a negative number without
                // overflow)
                if (u > 0) {
                    u = -u;
                } // make u negative
                if (v > 0) {
                    v = -v;
                } // make v negative
                // B1. [Find power of 2]
                int k = 0;
                while ((u & 1) == 0 && (v & 1) == 0 && k < 31) { // while u and v are both even...
                    u /= 2;
                    v /= 2;
                    k++; // cast out twos.
                }
                if (k == 31) {
                    throw new ArithmeticException("overflow: gcd is 2^31");
                }
                // B2. Initialize: u and v have been divided by 2^k and at least
                // one is odd.
                int t = (u & 1) == 1 ? v : -(u / 2)/* B3 */;
                // t negative: u was odd, v may be even (t replaces v)
                // t positive: u was even, v is odd (t replaces u)
                do {
                    /* assert u<0 && v<0; */
                    // B4/B3: cast out twos from t.
                    while ((t & 1) == 0) { // while t is even..
                        t /= 2; // cast out twos
                    }
                    // B5 [reset max(u,v)]
                    if (t > 0) {
                        u = -t;
                    } else {
                        v = t;
                    }
                    // B6/B3. at this point both u and v should be odd.
                    t = (v - u) / 2;
                    // |u| larger: t positive (replace u)
                    // |v| larger: t negative (replace v)
                } while (t != 0);
                return -u * (1 << k); // gcd is u*2^k
            }

            // Arithmetic
            //-------------------------------------------------------------------

            /**
             * Multiply two integers, checking for overflow.
             *
             * @param x a factor
             * @param y a factor
             * @return the product {@code x*y}
             * @throws ArithmeticException if the result can not be represented as
             *                             an int
             */
            private static int mulAndCheck(final int x, final int y) {
                final long m = (long) x * (long) y;
                if (m < Integer.MIN_VALUE || m > Integer.MAX_VALUE) {
                    throw new ArithmeticException("overflow: mul");
                }
                return (int) m;
            }

            /**
             *  Multiply two non-negative integers, checking for overflow.
             *
             * @param x a non-negative factor
             * @param y a non-negative factor
             * @return the product {@code x*y}
             * @throws ArithmeticException if the result can not be represented as
             * an int
             */
            private static int mulPosAndCheck(final int x, final int y) {
                /* assert x>=0 && y>=0; */
                final long m = (long) x * (long) y;
                if (m > Integer.MAX_VALUE) {
                    throw new ArithmeticException("overflow: mulPos");
                }
                return (int) m;
            }

            /**
             * Add two integers, checking for overflow.
             *
             * @param x an addend
             * @param y an addend
             * @return the sum {@code x+y}
             * @throws ArithmeticException if the result can not be represented as
             * an int
             */
            private static int addAndCheck(final int x, final int y) {
                final long s = (long) x + (long) y;
                if (s < Integer.MIN_VALUE || s > Integer.MAX_VALUE) {
                    throw new ArithmeticException("overflow: add");
                }
                return (int) s;
            }

            /**
             * Subtract two integers, checking for overflow.
             *
             * @param x the minuend
             * @param y the subtrahend
             * @return the difference {@code x-y}
             * @throws ArithmeticException if the result can not be represented as
             * an int
             */
            private static int subAndCheck(final int x, final int y) {
                final long s = (long) x - (long) y;
                if (s < Integer.MIN_VALUE || s > Integer.MAX_VALUE) {
                    throw new ArithmeticException("overflow: add");
                }
                return (int) s;
            }

            /**
             * <p>Adds the value of this fraction to another, returning the result in reduced form.
             * The algorithm follows Knuth, 4.5.1.</p>
             *
             * @param fraction  the fraction to add, must not be {@code null}
             * @return a {@code Fraction} instance with the resulting values
             * @throws IllegalArgumentException if the fraction is {@code null}
             * @throws ArithmeticException if the resulting numerator or denominator exceeds
             *  {@code Integer.MAX_VALUE}
             */
            public Fraction add(final Fraction fraction) {
                return addSub(fraction, true /* add */);
            }

            /**
             * <p>Subtracts the value of another fraction from the value of this one,
             * returning the result in reduced form.</p>
             *
             * @param fraction  the fraction to subtract, must not be {@code null}
             * @return a {@code Fraction} instance with the resulting values
             * @throws IllegalArgumentException if the fraction is {@code null}
             * @throws ArithmeticException if the resulting numerator or denominator
             *   cannot be represented in an {@code int}.
             */
            public Fraction subtract(final Fraction fraction) {
                return addSub(fraction, false /* subtract */);
            }

            /**
             * Implement add and subtract using algorithm described in Knuth 4.5.1.
             *
             * @param fraction the fraction to subtract, must not be {@code null}
             * @param isAdd true to add, false to subtract
             * @return a {@code Fraction} instance with the resulting values
             * @throws IllegalArgumentException if the fraction is {@code null}
             * @throws ArithmeticException if the resulting numerator or denominator
             *   cannot be represented in an {@code int}.
             */
            private Fraction addSub(final Fraction fraction, final boolean isAdd) {
                Validate.notNull(fraction, "fraction");
                // zero is identity for addition.
                if (numerator == 0) {
                    return isAdd ? fraction : fraction.negate();
                }
                if (fraction.numerator == 0) {
                    return this;
                }
                // if denominators are randomly distributed, d1 will be 1 about 61%
                // of the time.
                final int d1 = greatestCommonDivisor(denominator, fraction.denominator);
                if (d1 == 1) {
                    // result is ( (u*v' +/- u'v) / u'v')
                    final int uvp = mulAndCheck(numerator, fraction.denominator);
                    final int upv = mulAndCheck(fraction.numerator, denominator);
                    return new Fraction(isAdd ? addAndCheck(uvp, upv) : subAndCheck(uvp, upv), mulPosAndCheck(denominator,
                            fraction.denominator));
                }
                // the quantity 't' requires 65 bits of precision; see knuth 4.5.1
                // exercise 7. we're going to use a BigInteger.
                // t = u(v'/d1) +/- v(u'/d1)
                final BigInteger uvp = BigInteger.valueOf(numerator).multiply(BigInteger.valueOf(fraction.denominator / d1));
                final BigInteger upv = BigInteger.valueOf(fraction.numerator).multiply(BigInteger.valueOf(denominator / d1));
                final BigInteger t = isAdd ? uvp.add(upv) : uvp.subtract(upv);
                // but d2 doesn't need extra precision because
                // d2 = gcd(t,d1) = gcd(t mod d1, d1)
                final int tmodd1 = t.mod(BigInteger.valueOf(d1)).intValue();
                final int d2 = tmodd1 == 0 ? d1 : greatestCommonDivisor(tmodd1, d1);

                // result is (t/d2) / (u'/d1)(v'/d2)
                final BigInteger w = t.divide(BigInteger.valueOf(d2));
                if (w.bitLength() > 31) {
                    throw new ArithmeticException("overflow: numerator too large after multiply");
                }
                return new Fraction(w.intValue(), mulPosAndCheck(denominator / d1, fraction.denominator / d2));
            }

            /**
             * <p>Multiplies the value of this fraction by another, returning the
             * result in reduced form.</p>
             *
             * @param fraction  the fraction to multiply by, must not be {@code null}
             * @return a {@code Fraction} instance with the resulting values
             * @throws NullPointerException if the fraction is {@code null}
             * @throws ArithmeticException if the resulting numerator or denominator exceeds
             *  {@code Integer.MAX_VALUE}
             */
            public Fraction multiplyBy(final Fraction fraction) {
                Validate.notNull(fraction, "fraction");
                if (numerator == 0 || fraction.numerator == 0) {
                    return ZERO;
                }
                // knuth 4.5.1
                // make sure we don't overflow unless the result *must* overflow.
                final int d1 = greatestCommonDivisor(numerator, fraction.denominator);
                final int d2 = greatestCommonDivisor(fraction.numerator, denominator);
                return getReducedFraction(mulAndCheck(numerator / d1, fraction.numerator / d2),
                        mulPosAndCheck(denominator / d2, fraction.denominator / d1));
            }

            /**
             * <p>Divide the value of this fraction by another.</p>
             *
             * @param fraction  the fraction to divide by, must not be {@code null}
             * @return a {@code Fraction} instance with the resulting values
             * @throws NullPointerException if the fraction is {@code null}
             * @throws ArithmeticException if the fraction to divide by is zero
             * @throws ArithmeticException if the resulting numerator or denominator exceeds
             *  {@code Integer.MAX_VALUE}
             */
            public Fraction divideBy(final Fraction fraction) {
                Validate.notNull(fraction, "fraction");
                if (fraction.numerator == 0) {
                    throw new ArithmeticException("The fraction to divide by must not be zero");
                }
                return multiplyBy(fraction.invert());
            }

            // Basics
            //-------------------------------------------------------------------

            /**
             * <p>Compares this fraction to another object to test if they are equal.</p>.
             *
             * <p>To be equal, both values must be equal. Thus 2/4 is not equal to 1/2.</p>
             *
             * @param obj the reference object with which to compare
             * @return {@code true} if this object is equal
             */
            @Override
            public boolean equals(final Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof Fraction)) {
                    return false;
                }
                final Fraction other = (Fraction) obj;
                return getNumerator() == other.getNumerator() && getDenominator() == other.getDenominator();
            }

            /**
             * <p>Gets a hashCode for the fraction.</p>
             *
             * @return a hash code value for this object
             */
            @Override
            public int hashCode() {
                if (hashCode == 0) {
                    // hash code update should be atomic.
                    hashCode = 37 * (37 * 17 + getNumerator()) + getDenominator();
                }
                return hashCode;
            }

            /**
             * <p>Compares this object to another based on size.</p>
             *
             * <p>Note: this class has a natural ordering that is inconsistent
             * with equals, because, for example, equals treats 1/2 and 2/4 as
             * different, whereas compareTo treats them as equal.
             *
             * @param other  the object to compare to
             * @return -1 if this is less, 0 if equal, +1 if greater
             * @throws ClassCastException if the object is not a {@code Fraction}
             * @throws NullPointerException if the object is {@code null}
             */
            @Override
            public int compareTo(final Fraction other) {
                if (this == other) {
                    return 0;
                }
                if (numerator == other.numerator && denominator == other.denominator) {
                    return 0;
                }

                // otherwise see which is less
                final long first = (long) numerator * (long) other.denominator;
                final long second = (long) other.numerator * (long) denominator;
                return Long.compare(first, second);
            }

            /**
             * <p>Gets the fraction as a {@code String}.</p>
             *
             * <p>The format used is '<i>numerator</i>/<i>denominator</i>' always.
             *
             * @return a {@code String} form of the fraction
             */
            @Override
            public String toString() {
                if (toString == null) {
                    toString = getNumerator() + "/" + getDenominator();
                }
                return toString;
            }

            /**
             * <p>Gets the fraction as a proper {@code String} in the format X Y/Z.</p>
             *
             * <p>The format used in '<i>wholeNumber</i> <i>numerator</i>/<i>denominator</i>'.
             * If the whole number is zero it will be omitted. If the numerator is zero,
             * only the whole number is returned.</p>
             *
             * @return a {@code String} form of the fraction
             */
            public String toProperString() {
                if (toProperString == null) {
                    if (numerator == 0) {
                        toProperString = "0";
                    } else if (numerator == denominator) {
                        toProperString = "1";
                    } else if (numerator == -1 * denominator) {
                        toProperString = "-1";
                    } else if ((numerator > 0 ? -numerator : numerator) < -denominator) {
                        // note that we do the magnitude comparison test above with
                        // NEGATIVE (not positive) numbers, since negative numbers
                        // have a larger range. otherwise numerator==Integer.MIN_VALUE
                        // is handled incorrectly.
                        final int properNumerator = getProperNumerator();
                        if (properNumerator == 0) {
                            toProperString = Integer.toString(getProperWhole());
                        } else {
                            toProperString = getProperWhole() + " " + properNumerator + "/" + getDenominator();
                        }
                    } else {
                        toProperString = getNumerator() + "/" + getDenominator();
                    }
                }
                return toProperString;
            }
        }
    """.trimIndent()

    private val fractionTest = """
        /*
         * Licensed to the Apache Software Foundation (ASF) under one
         * or more contributor license agreements.  See the NOTICE file
         * distributed with this work for additional information
         * regarding copyright ownership.  The ASF licenses this file
         * to you under the Apache License, Version 2.0 (the
         * "License"); you may not use this file except in compliance
         * with the License.  You may obtain a copy of the License at
         *
         * http://www.apache.org/licenses/LICENSE-2.0
         *
         * Unless required by applicable law or agreed to in writing,
         * software distributed under the License is distributed on an
         * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
         * KIND, either express or implied.  See the License for the
         * specific language governing permissions and limitations
         * under the License.
         */
        package org.apache.commons.lang3.math;

        import static org.junit.jupiter.api.Assertions.assertEquals;
        import static org.junit.jupiter.api.Assertions.assertNotEquals;
        import static org.junit.jupiter.api.Assertions.assertSame;
        import static org.junit.jupiter.api.Assertions.assertThrows;
        import static org.junit.jupiter.api.Assertions.assertTrue;

        import org.junit.jupiter.api.Test;

        /**
         * Test cases for the {@link Fraction} class
         */
        public class FractionTest  {

            private static final int SKIP = 500;  //53

            //--------------------------------------------------------------------------
            @Test
            public void testConstants() {
                assertEquals(0, Fraction.ZERO.getNumerator());
                assertEquals(1, Fraction.ZERO.getDenominator());

                assertEquals(1, Fraction.ONE.getNumerator());
                assertEquals(1, Fraction.ONE.getDenominator());

                assertEquals(1, Fraction.ONE_HALF.getNumerator());
                assertEquals(2, Fraction.ONE_HALF.getDenominator());

                assertEquals(1, Fraction.ONE_THIRD.getNumerator());
                assertEquals(3, Fraction.ONE_THIRD.getDenominator());

                assertEquals(2, Fraction.TWO_THIRDS.getNumerator());
                assertEquals(3, Fraction.TWO_THIRDS.getDenominator());

                assertEquals(1, Fraction.ONE_QUARTER.getNumerator());
                assertEquals(4, Fraction.ONE_QUARTER.getDenominator());

                assertEquals(2, Fraction.TWO_QUARTERS.getNumerator());
                assertEquals(4, Fraction.TWO_QUARTERS.getDenominator());

                assertEquals(3, Fraction.THREE_QUARTERS.getNumerator());
                assertEquals(4, Fraction.THREE_QUARTERS.getDenominator());

                assertEquals(1, Fraction.ONE_FIFTH.getNumerator());
                assertEquals(5, Fraction.ONE_FIFTH.getDenominator());

                assertEquals(2, Fraction.TWO_FIFTHS.getNumerator());
                assertEquals(5, Fraction.TWO_FIFTHS.getDenominator());

                assertEquals(3, Fraction.THREE_FIFTHS.getNumerator());
                assertEquals(5, Fraction.THREE_FIFTHS.getDenominator());

                assertEquals(4, Fraction.FOUR_FIFTHS.getNumerator());
                assertEquals(5, Fraction.FOUR_FIFTHS.getDenominator());
            }

            @Test
            public void testFactory_int_int() {
                Fraction f;

                // zero
                f = Fraction.getFraction(0, 1);
                assertEquals(0, f.getNumerator());
                assertEquals(1, f.getDenominator());

                f = Fraction.getFraction(0, 2);
                assertEquals(0, f.getNumerator());
                assertEquals(2, f.getDenominator());

                // normal
                f = Fraction.getFraction(1, 1);
                assertEquals(1, f.getNumerator());
                assertEquals(1, f.getDenominator());

                f = Fraction.getFraction(2, 1);
                assertEquals(2, f.getNumerator());
                assertEquals(1, f.getDenominator());

                f = Fraction.getFraction(23, 345);
                assertEquals(23, f.getNumerator());
                assertEquals(345, f.getDenominator());

                // improper
                f = Fraction.getFraction(22, 7);
                assertEquals(22, f.getNumerator());
                assertEquals(7, f.getDenominator());

                // negatives
                f = Fraction.getFraction(-6, 10);
                assertEquals(-6, f.getNumerator());
                assertEquals(10, f.getDenominator());

                f = Fraction.getFraction(6, -10);
                assertEquals(-6, f.getNumerator());
                assertEquals(10, f.getDenominator());

                f = Fraction.getFraction(-6, -10);
                assertEquals(6, f.getNumerator());
                assertEquals(10, f.getDenominator());

                // zero denominator
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(1, 0));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(2, 0));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(-3, 0));

                // very large: can't represent as unsimplified fraction, although
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(4, Integer.MIN_VALUE));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(1, Integer.MIN_VALUE));
            }

            @Test
            public void testFactory_int_int_int() {
                Fraction f;

                // zero
                f = Fraction.getFraction(0, 0, 2);
                assertEquals(0, f.getNumerator());
                assertEquals(2, f.getDenominator());

                f = Fraction.getFraction(2, 0, 2);
                assertEquals(4, f.getNumerator());
                assertEquals(2, f.getDenominator());

                f = Fraction.getFraction(0, 1, 2);
                assertEquals(1, f.getNumerator());
                assertEquals(2, f.getDenominator());

                // normal
                f = Fraction.getFraction(1, 1, 2);
                assertEquals(3, f.getNumerator());
                assertEquals(2, f.getDenominator());

                // negatives
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(1, -6, -10));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(1, -6, -10));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(1, -6, -10));

                // negative whole
                f = Fraction.getFraction(-1, 6, 10);
                assertEquals(-16, f.getNumerator());
                assertEquals(10, f.getDenominator());

                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(-1, -6, 10));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(-1, 6, -10));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(-1, -6, -10));

                // zero denominator
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(0, 1, 0));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(1, 2, 0));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(-1, -3, 0));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MAX_VALUE, 1, 2));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(-Integer.MAX_VALUE, 1, 2));

                // very large
                f = Fraction.getFraction(-1, 0, Integer.MAX_VALUE);
                assertEquals(-Integer.MAX_VALUE, f.getNumerator());
                assertEquals(Integer.MAX_VALUE, f.getDenominator());

                // negative denominators not allowed in this constructor.
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(0, 4, Integer.MIN_VALUE));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(1, 1, Integer.MAX_VALUE));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(-1, 2, Integer.MAX_VALUE));
            }

            @Test
            public void testReducedFactory_int_int() {
                Fraction f;

                // zero
                f = Fraction.getReducedFraction(0, 1);
                assertEquals(0, f.getNumerator());
                assertEquals(1, f.getDenominator());

                // normal
                f = Fraction.getReducedFraction(1, 1);
                assertEquals(1, f.getNumerator());
                assertEquals(1, f.getDenominator());

                f = Fraction.getReducedFraction(2, 1);
                assertEquals(2, f.getNumerator());
                assertEquals(1, f.getDenominator());

                // improper
                f = Fraction.getReducedFraction(22, 7);
                assertEquals(22, f.getNumerator());
                assertEquals(7, f.getDenominator());

                // negatives
                f = Fraction.getReducedFraction(-6, 10);
                assertEquals(-3, f.getNumerator());
                assertEquals(5, f.getDenominator());

                f = Fraction.getReducedFraction(6, -10);
                assertEquals(-3, f.getNumerator());
                assertEquals(5, f.getDenominator());

                f = Fraction.getReducedFraction(-6, -10);
                assertEquals(3, f.getNumerator());
                assertEquals(5, f.getDenominator());

                // zero denominator
                assertThrows(ArithmeticException.class, () -> Fraction.getReducedFraction(1, 0));
                assertThrows(ArithmeticException.class, () -> Fraction.getReducedFraction(2, 0));
                assertThrows(ArithmeticException.class, () -> Fraction.getReducedFraction(-3, 0));

                // reduced
                f = Fraction.getReducedFraction(0, 2);
                assertEquals(0, f.getNumerator());
                assertEquals(1, f.getDenominator());

                f = Fraction.getReducedFraction(2, 2);
                assertEquals(1, f.getNumerator());
                assertEquals(1, f.getDenominator());

                f = Fraction.getReducedFraction(2, 4);
                assertEquals(1, f.getNumerator());
                assertEquals(2, f.getDenominator());

                f = Fraction.getReducedFraction(15, 10);
                assertEquals(3, f.getNumerator());
                assertEquals(2, f.getDenominator());

                f = Fraction.getReducedFraction(121, 22);
                assertEquals(11, f.getNumerator());
                assertEquals(2, f.getDenominator());

                // Extreme values
                // OK, can reduce before negating
                f = Fraction.getReducedFraction(-2, Integer.MIN_VALUE);
                assertEquals(1, f.getNumerator());
                assertEquals(-(Integer.MIN_VALUE / 2), f.getDenominator());

                // Can't reduce, negation will throw
                assertThrows(ArithmeticException.class, () -> Fraction.getReducedFraction(-7, Integer.MIN_VALUE));

                // LANG-662
                f = Fraction.getReducedFraction(Integer.MIN_VALUE, 2);
                assertEquals(Integer.MIN_VALUE / 2, f.getNumerator());
                assertEquals(1, f.getDenominator());
            }

            @Test
            public void testFactory_double() {
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Double.NaN));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Double.POSITIVE_INFINITY));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Double.NEGATIVE_INFINITY));
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction((double) Integer.MAX_VALUE + 1));

                // zero
                Fraction f = Fraction.getFraction(0.0d);
                assertEquals(0, f.getNumerator());
                assertEquals(1, f.getDenominator());

                // one
                f = Fraction.getFraction(1.0d);
                assertEquals(1, f.getNumerator());
                assertEquals(1, f.getDenominator());

                // one half
                f = Fraction.getFraction(0.5d);
                assertEquals(1, f.getNumerator());
                assertEquals(2, f.getDenominator());

                // negative
                f = Fraction.getFraction(-0.875d);
                assertEquals(-7, f.getNumerator());
                assertEquals(8, f.getDenominator());

                // over 1
                f = Fraction.getFraction(1.25d);
                assertEquals(5, f.getNumerator());
                assertEquals(4, f.getDenominator());

                // two thirds
                f = Fraction.getFraction(0.66666d);
                assertEquals(2, f.getNumerator());
                assertEquals(3, f.getDenominator());

                // small
                f = Fraction.getFraction(1.0d/10001d);
                assertEquals(0, f.getNumerator());
                assertEquals(1, f.getDenominator());

                // normal
                Fraction f2 = null;
                for (int i = 1; i <= 100; i++) {  // denominator
                    for (int j = 1; j <= i; j++) {  // numerator
                        f = Fraction.getFraction((double) j / (double) i);

                        f2 = Fraction.getReducedFraction(j, i);
                        assertEquals(f2.getNumerator(), f.getNumerator());
                        assertEquals(f2.getDenominator(), f.getDenominator());
                    }
                }
                // save time by skipping some tests!  (
                for (int i = 1001; i <= 10000; i+=SKIP) {  // denominator
                    for (int j = 1; j <= i; j++) {  // numerator
                        f = Fraction.getFraction((double) j / (double) i);
                        f2 = Fraction.getReducedFraction(j, i);
                        assertEquals(f2.getNumerator(), f.getNumerator());
                        assertEquals(f2.getDenominator(), f.getDenominator());
                    }
                }
            }

            @Test
            public void testFactory_String() {
                assertThrows(NullPointerException.class, () -> Fraction.getFraction(null));
            }


            @Test
            public void testFactory_String_double() {
                Fraction f;

                f = Fraction.getFraction("0.0");
                assertEquals(0, f.getNumerator());
                assertEquals(1, f.getDenominator());

                f = Fraction.getFraction("0.2");
                assertEquals(1, f.getNumerator());
                assertEquals(5, f.getDenominator());

                f = Fraction.getFraction("0.5");
                assertEquals(1, f.getNumerator());
                assertEquals(2, f.getDenominator());

                f = Fraction.getFraction("0.66666");
                assertEquals(2, f.getNumerator());
                assertEquals(3, f.getDenominator());

                assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2.3R"));
                assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2147483648")); // too big
                assertThrows(NumberFormatException.class, () -> Fraction.getFraction("."));
            }

            @Test
            public void testFactory_String_proper() {
                Fraction f;

                f = Fraction.getFraction("0 0/1");
                assertEquals(0, f.getNumerator());
                assertEquals(1, f.getDenominator());

                f = Fraction.getFraction("1 1/5");
                assertEquals(6, f.getNumerator());
                assertEquals(5, f.getDenominator());

                f = Fraction.getFraction("7 1/2");
                assertEquals(15, f.getNumerator());
                assertEquals(2, f.getDenominator());

                f = Fraction.getFraction("1 2/4");
                assertEquals(6, f.getNumerator());
                assertEquals(4, f.getDenominator());

                f = Fraction.getFraction("-7 1/2");
                assertEquals(-15, f.getNumerator());
                assertEquals(2, f.getDenominator());

                f = Fraction.getFraction("-1 2/4");
                assertEquals(-6, f.getNumerator());
                assertEquals(4, f.getDenominator());

                assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2 3"));
                assertThrows(NumberFormatException.class, () -> Fraction.getFraction("a 3"));
                assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2 b/4"));
                assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2 "));
                assertThrows(NumberFormatException.class, () -> Fraction.getFraction(" 3"));
                assertThrows(NumberFormatException.class, () -> Fraction.getFraction(" "));
            }

            @Test
            public void testFactory_String_improper() {
                Fraction f;

                f = Fraction.getFraction("0/1");
                assertEquals(0, f.getNumerator());
                assertEquals(1, f.getDenominator());

                f = Fraction.getFraction("1/5");
                assertEquals(1, f.getNumerator());
                assertEquals(5, f.getDenominator());

                f = Fraction.getFraction("1/2");
                assertEquals(1, f.getNumerator());
                assertEquals(2, f.getDenominator());

                f = Fraction.getFraction("2/3");
                assertEquals(2, f.getNumerator());
                assertEquals(3, f.getDenominator());

                f = Fraction.getFraction("7/3");
                assertEquals(7, f.getNumerator());
                assertEquals(3, f.getDenominator());

                f = Fraction.getFraction("2/4");
                assertEquals(2, f.getNumerator());
                assertEquals(4, f.getDenominator());

                assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2/d"));
                assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2e/3"));
                assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2/"));
                assertThrows(NumberFormatException.class, () -> Fraction.getFraction("/"));
            }

            @Test
            public void testGets() {
                Fraction f;

                f = Fraction.getFraction(3, 5, 6);
                assertEquals(23, f.getNumerator());
                assertEquals(3, f.getProperWhole());
                assertEquals(5, f.getProperNumerator());
                assertEquals(6, f.getDenominator());

                f = Fraction.getFraction(-3, 5, 6);
                assertEquals(-23, f.getNumerator());
                assertEquals(-3, f.getProperWhole());
                assertEquals(5, f.getProperNumerator());
                assertEquals(6, f.getDenominator());

                f = Fraction.getFraction(Integer.MIN_VALUE, 0, 1);
                assertEquals(Integer.MIN_VALUE, f.getNumerator());
                assertEquals(Integer.MIN_VALUE, f.getProperWhole());
                assertEquals(0, f.getProperNumerator());
                assertEquals(1, f.getDenominator());
            }

            @Test
            public void testConversions() {
                Fraction f;

                f = Fraction.getFraction(3, 7, 8);
                assertEquals(3, f.intValue());
                assertEquals(3L, f.longValue());
                assertEquals(3.875f, f.floatValue(), 0.00001f);
                assertEquals(3.875d, f.doubleValue(), 0.00001d);
            }

            @Test
            public void testReduce() {
                Fraction f;

                f = Fraction.getFraction(50, 75);
                Fraction result = f.reduce();
                assertEquals(2, result.getNumerator());
                assertEquals(3, result.getDenominator());

                f = Fraction.getFraction(-2, -3);
                result = f.reduce();
                assertEquals(2, result.getNumerator());
                assertEquals(3, result.getDenominator());

                f = Fraction.getFraction(2, -3);
                result = f.reduce();
                assertEquals(-2, result.getNumerator());
                assertEquals(3, result.getDenominator());

                f = Fraction.getFraction(-2, 3);
                result = f.reduce();
                assertEquals(-2, result.getNumerator());
                assertEquals(3, result.getDenominator());
                assertSame(f, result);

                f = Fraction.getFraction(2, 3);
                result = f.reduce();
                assertEquals(2, result.getNumerator());
                assertEquals(3, result.getDenominator());
                assertSame(f, result);

                f = Fraction.getFraction(0, 1);
                result = f.reduce();
                assertEquals(0, result.getNumerator());
                assertEquals(1, result.getDenominator());
                assertSame(f, result);

                f = Fraction.getFraction(0, 100);
                result = f.reduce();
                assertEquals(0, result.getNumerator());
                assertEquals(1, result.getDenominator());
                assertSame(result, Fraction.ZERO);

                f = Fraction.getFraction(Integer.MIN_VALUE, 2);
                result = f.reduce();
                assertEquals(Integer.MIN_VALUE / 2, result.getNumerator());
                assertEquals(1, result.getDenominator());
            }

            @Test
            public void testInvert() {
                Fraction f;

                f = Fraction.getFraction(50, 75);
                f = f.invert();
                assertEquals(75, f.getNumerator());
                assertEquals(50, f.getDenominator());

                f = Fraction.getFraction(4, 3);
                f = f.invert();
                assertEquals(3, f.getNumerator());
                assertEquals(4, f.getDenominator());

                f = Fraction.getFraction(-15, 47);
                f = f.invert();
                assertEquals(-47, f.getNumerator());
                assertEquals(15, f.getDenominator());

                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(0, 3).invert());
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MIN_VALUE, 1).invert());

                f = Fraction.getFraction(Integer.MAX_VALUE, 1);
                f = f.invert();
                assertEquals(1, f.getNumerator());
                assertEquals(Integer.MAX_VALUE, f.getDenominator());
            }

            @Test
            public void testNegate() {
                Fraction f;

                f = Fraction.getFraction(50, 75);
                f = f.negate();
                assertEquals(-50, f.getNumerator());
                assertEquals(75, f.getDenominator());

                f = Fraction.getFraction(-50, 75);
                f = f.negate();
                assertEquals(50, f.getNumerator());
                assertEquals(75, f.getDenominator());

                // large values
                f = Fraction.getFraction(Integer.MAX_VALUE-1, Integer.MAX_VALUE);
                f = f.negate();
                assertEquals(Integer.MIN_VALUE+2, f.getNumerator());
                assertEquals(Integer.MAX_VALUE, f.getDenominator());

                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MIN_VALUE, 1).negate());
            }

            @Test
            public void testAbs() {
                Fraction f;

                f = Fraction.getFraction(50, 75);
                f = f.abs();
                assertEquals(50, f.getNumerator());
                assertEquals(75, f.getDenominator());

                f = Fraction.getFraction(-50, 75);
                f = f.abs();
                assertEquals(50, f.getNumerator());
                assertEquals(75, f.getDenominator());

                f = Fraction.getFraction(Integer.MAX_VALUE, 1);
                f = f.abs();
                assertEquals(Integer.MAX_VALUE, f.getNumerator());
                assertEquals(1, f.getDenominator());

                f = Fraction.getFraction(Integer.MAX_VALUE, -1);
                f = f.abs();
                assertEquals(Integer.MAX_VALUE, f.getNumerator());
                assertEquals(1, f.getDenominator());

                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MIN_VALUE, 1).abs());
            }

            @Test
            public void testPow() {
                Fraction f;

                f = Fraction.getFraction(3, 5);
                assertEquals(Fraction.ONE, f.pow(0));

                f = Fraction.getFraction(3, 5);
                assertSame(f, f.pow(1));
                assertEquals(f, f.pow(1));

                f = Fraction.getFraction(3, 5);
                f = f.pow(2);
                assertEquals(9, f.getNumerator());
                assertEquals(25, f.getDenominator());

                f = Fraction.getFraction(3, 5);
                f = f.pow(3);
                assertEquals(27, f.getNumerator());
                assertEquals(125, f.getDenominator());

                f = Fraction.getFraction(3, 5);
                f = f.pow(-1);
                assertEquals(5, f.getNumerator());
                assertEquals(3, f.getDenominator());

                f = Fraction.getFraction(3, 5);
                f = f.pow(-2);
                assertEquals(25, f.getNumerator());
                assertEquals(9, f.getDenominator());

                // check unreduced fractions stay that way.
                f = Fraction.getFraction(6, 10);
                assertEquals(Fraction.ONE, f.pow(0));

                f = Fraction.getFraction(6, 10);
                assertEquals(f, f.pow(1));
                assertNotEquals(f.pow(1), Fraction.getFraction(3, 5));

                f = Fraction.getFraction(6, 10);
                f = f.pow(2);
                assertEquals(9, f.getNumerator());
                assertEquals(25, f.getDenominator());

                f = Fraction.getFraction(6, 10);
                f = f.pow(3);
                assertEquals(27, f.getNumerator());
                assertEquals(125, f.getDenominator());

                f = Fraction.getFraction(6, 10);
                f = f.pow(-1);
                assertEquals(10, f.getNumerator());
                assertEquals(6, f.getDenominator());

                f = Fraction.getFraction(6, 10);
                f = f.pow(-2);
                assertEquals(25, f.getNumerator());
                assertEquals(9, f.getDenominator());

                // zero to any positive power is still zero.
                f = Fraction.getFraction(0, 1231);
                f = f.pow(1);
                assertEquals(0, f.compareTo(Fraction.ZERO));
                assertEquals(0, f.getNumerator());
                assertEquals(1231, f.getDenominator());
                f = f.pow(2);
                assertEquals(0, f.compareTo(Fraction.ZERO));
                assertEquals(0, f.getNumerator());
                assertEquals(1, f.getDenominator());

                // zero to negative powers should throw an exception
                final Fraction fr = f;
                assertThrows(ArithmeticException.class, () -> fr.pow(-1));
                assertThrows(ArithmeticException.class, () -> fr.pow(Integer.MIN_VALUE));

                // one to any power is still one.
                f = Fraction.getFraction(1, 1);
                f = f.pow(0);
                assertEquals(f, Fraction.ONE);
                f = f.pow(1);
                assertEquals(f, Fraction.ONE);
                f = f.pow(-1);
                assertEquals(f, Fraction.ONE);
                f = f.pow(Integer.MAX_VALUE);
                assertEquals(f, Fraction.ONE);
                f = f.pow(Integer.MIN_VALUE);
                assertEquals(f, Fraction.ONE);

                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MAX_VALUE, 1).pow(2));

                // Numerator growing too negative during the pow operation.
                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MIN_VALUE, 1).pow(3));

                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(65536, 1).pow(2));
            }

            @Test
            public void testAdd() {
                Fraction f;
                Fraction f1;
                Fraction f2;

                f1 = Fraction.getFraction(3, 5);
                f2 = Fraction.getFraction(1, 5);
                f = f1.add(f2);
                assertEquals(4, f.getNumerator());
                assertEquals(5, f.getDenominator());

                f1 = Fraction.getFraction(3, 5);
                f2 = Fraction.getFraction(2, 5);
                f = f1.add(f2);
                assertEquals(1, f.getNumerator());
                assertEquals(1, f.getDenominator());

                f1 = Fraction.getFraction(3, 5);
                f2 = Fraction.getFraction(3, 5);
                f = f1.add(f2);
                assertEquals(6, f.getNumerator());
                assertEquals(5, f.getDenominator());

                f1 = Fraction.getFraction(3, 5);
                f2 = Fraction.getFraction(-4, 5);
                f = f1.add(f2);
                assertEquals(-1, f.getNumerator());
                assertEquals(5, f.getDenominator());

                f1 = Fraction.getFraction(Integer.MAX_VALUE - 1, 1);
                f2 = Fraction.ONE;
                f = f1.add(f2);
                assertEquals(Integer.MAX_VALUE, f.getNumerator());
                assertEquals(1, f.getDenominator());

                f1 = Fraction.getFraction(3, 5);
                f2 = Fraction.getFraction(1, 2);
                f = f1.add(f2);
                assertEquals(11, f.getNumerator());
                assertEquals(10, f.getDenominator());

                f1 = Fraction.getFraction(3, 8);
                f2 = Fraction.getFraction(1, 6);
                f = f1.add(f2);
                assertEquals(13, f.getNumerator());
                assertEquals(24, f.getDenominator());

                f1 = Fraction.getFraction(0, 5);
                f2 = Fraction.getFraction(1, 5);
                f = f1.add(f2);
                assertSame(f2, f);
                f = f2.add(f1);
                assertSame(f2, f);

                f1 = Fraction.getFraction(-1, 13*13*2*2);
                f2 = Fraction.getFraction(-2, 13*17*2);
                final Fraction fr = f1.add(f2);
                assertEquals(13*13*17*2*2, fr.getDenominator());
                assertEquals(-17 - 2*13*2, fr.getNumerator());

                assertThrows(NullPointerException.class, () -> fr.add(null));

                // if this fraction is added naively, it will overflow.
                // check that it doesn't.
                f1 = Fraction.getFraction(1, 32768*3);
                f2 = Fraction.getFraction(1, 59049);
                f = f1.add(f2);
                assertEquals(52451, f.getNumerator());
                assertEquals(1934917632, f.getDenominator());

                f1 = Fraction.getFraction(Integer.MIN_VALUE, 3);
                f2 = Fraction.ONE_THIRD;
                f = f1.add(f2);
                assertEquals(Integer.MIN_VALUE+1, f.getNumerator());
                assertEquals(3, f.getDenominator());

                f1 = Fraction.getFraction(Integer.MAX_VALUE - 1, 1);
                f2 = Fraction.ONE;
                f = f1.add(f2);
                assertEquals(Integer.MAX_VALUE, f.getNumerator());
                assertEquals(1, f.getDenominator());

                final Fraction overflower = f;
                assertThrows(ArithmeticException.class, () -> overflower.add(Fraction.ONE)); // should overflow

                // denominator should not be a multiple of 2 or 3 to trigger overflow
                assertThrows(
                        ArithmeticException.class,
                        () -> Fraction.getFraction(Integer.MIN_VALUE, 5).add(Fraction.getFraction(-1, 5)));

                final Fraction maxValue = Fraction.getFraction(-Integer.MAX_VALUE, 1);
                assertThrows(ArithmeticException.class, () -> maxValue.add(maxValue));

                final Fraction negativeMaxValue = Fraction.getFraction(-Integer.MAX_VALUE, 1);
                assertThrows(ArithmeticException.class, () -> negativeMaxValue.add(negativeMaxValue));

                final Fraction f3 = Fraction.getFraction(3, 327680);
                final Fraction f4 = Fraction.getFraction(2, 59049);
                assertThrows(ArithmeticException.class, () -> f3.add(f4)); // should overflow
            }

            @Test
            public void testSubtract() {
                Fraction f;
                Fraction f1;
                Fraction f2;

                f1 = Fraction.getFraction(3, 5);
                f2 = Fraction.getFraction(1, 5);
                f = f1.subtract(f2);
                assertEquals(2, f.getNumerator());
                assertEquals(5, f.getDenominator());

                f1 = Fraction.getFraction(7, 5);
                f2 = Fraction.getFraction(2, 5);
                f = f1.subtract(f2);
                assertEquals(1, f.getNumerator());
                assertEquals(1, f.getDenominator());

                f1 = Fraction.getFraction(3, 5);
                f2 = Fraction.getFraction(3, 5);
                f = f1.subtract(f2);
                assertEquals(0, f.getNumerator());
                assertEquals(1, f.getDenominator());

                f1 = Fraction.getFraction(3, 5);
                f2 = Fraction.getFraction(-4, 5);
                f = f1.subtract(f2);
                assertEquals(7, f.getNumerator());
                assertEquals(5, f.getDenominator());

                f1 = Fraction.getFraction(0, 5);
                f2 = Fraction.getFraction(4, 5);
                f = f1.subtract(f2);
                assertEquals(-4, f.getNumerator());
                assertEquals(5, f.getDenominator());

                f1 = Fraction.getFraction(0, 5);
                f2 = Fraction.getFraction(-4, 5);
                f = f1.subtract(f2);
                assertEquals(4, f.getNumerator());
                assertEquals(5, f.getDenominator());

                f1 = Fraction.getFraction(3, 5);
                f2 = Fraction.getFraction(1, 2);
                f = f1.subtract(f2);
                assertEquals(1, f.getNumerator());
                assertEquals(10, f.getDenominator());

                f1 = Fraction.getFraction(0, 5);
                f2 = Fraction.getFraction(1, 5);
                f = f2.subtract(f1);
                assertSame(f2, f);

                final Fraction fr = f;
                assertThrows(NullPointerException.class, () -> fr.subtract(null));

                // if this fraction is subtracted naively, it will overflow.
                // check that it doesn't.
                f1 = Fraction.getFraction(1, 32768*3);
                f2 = Fraction.getFraction(1, 59049);
                f = f1.subtract(f2);
                assertEquals(-13085, f.getNumerator());
                assertEquals(1934917632, f.getDenominator());

                f1 = Fraction.getFraction(Integer.MIN_VALUE, 3);
                f2 = Fraction.ONE_THIRD.negate();
                f = f1.subtract(f2);
                assertEquals(Integer.MIN_VALUE+1, f.getNumerator());
                assertEquals(3, f.getDenominator());

                f1 = Fraction.getFraction(Integer.MAX_VALUE, 1);
                f2 = Fraction.ONE;
                f = f1.subtract(f2);
                assertEquals(Integer.MAX_VALUE-1, f.getNumerator());
                assertEquals(1, f.getDenominator());

                // Should overflow
                assertThrows(
                        ArithmeticException.class,
                        () -> Fraction.getFraction(1, Integer.MAX_VALUE).subtract(Fraction.getFraction(1, Integer.MAX_VALUE - 1)));
                    f = f1.subtract(f2);

                // denominator should not be a multiple of 2 or 3 to trigger overflow
                assertThrows(
                        ArithmeticException.class,
                        () -> Fraction.getFraction(Integer.MIN_VALUE, 5).subtract(Fraction.getFraction(1, 5)));

                assertThrows(
                        ArithmeticException.class, () -> Fraction.getFraction(Integer.MIN_VALUE, 1).subtract(Fraction.ONE));

                assertThrows(
                        ArithmeticException.class,
                        () -> Fraction.getFraction(Integer.MAX_VALUE, 1).subtract(Fraction.ONE.negate()));

                // Should overflow
                assertThrows(
                        ArithmeticException.class,
                        () -> Fraction.getFraction(3, 327680).subtract(Fraction.getFraction(2, 59049)));
            }

            @Test
            public void testMultiply() {
                Fraction f;
                Fraction f1;
                Fraction f2;

                f1 = Fraction.getFraction(3, 5);
                f2 = Fraction.getFraction(2, 5);
                f = f1.multiplyBy(f2);
                assertEquals(6, f.getNumerator());
                assertEquals(25, f.getDenominator());

                f1 = Fraction.getFraction(6, 10);
                f2 = Fraction.getFraction(6, 10);
                f = f1.multiplyBy(f2);
                assertEquals(9, f.getNumerator());
                assertEquals(25, f.getDenominator());
                f = f.multiplyBy(f2);
                assertEquals(27, f.getNumerator());
                assertEquals(125, f.getDenominator());

                f1 = Fraction.getFraction(3, 5);
                f2 = Fraction.getFraction(-2, 5);
                f = f1.multiplyBy(f2);
                assertEquals(-6, f.getNumerator());
                assertEquals(25, f.getDenominator());

                f1 = Fraction.getFraction(-3, 5);
                f2 = Fraction.getFraction(-2, 5);
                f = f1.multiplyBy(f2);
                assertEquals(6, f.getNumerator());
                assertEquals(25, f.getDenominator());


                f1 = Fraction.getFraction(0, 5);
                f2 = Fraction.getFraction(2, 7);
                f = f1.multiplyBy(f2);
                assertSame(Fraction.ZERO, f);

                f1 = Fraction.getFraction(2, 7);
                f2 = Fraction.ONE;
                f = f1.multiplyBy(f2);
                assertEquals(2, f.getNumerator());
                assertEquals(7, f.getDenominator());

                f1 = Fraction.getFraction(Integer.MAX_VALUE, 1);
                f2 = Fraction.getFraction(Integer.MIN_VALUE, Integer.MAX_VALUE);
                f = f1.multiplyBy(f2);
                assertEquals(Integer.MIN_VALUE, f.getNumerator());
                assertEquals(1, f.getDenominator());

                final Fraction fr = f;
                assertThrows(NullPointerException.class, () -> fr.multiplyBy(null));

                final Fraction fr1 = Fraction.getFraction(1, Integer.MAX_VALUE);
                assertThrows(ArithmeticException.class, () -> fr1.multiplyBy(fr1));

                final Fraction fr2 = Fraction.getFraction(1, -Integer.MAX_VALUE);
                assertThrows(ArithmeticException.class, () -> fr2.multiplyBy(fr2));
            }

            @Test
            public void testDivide() {
                Fraction f;
                Fraction f1;
                Fraction f2;

                f1 = Fraction.getFraction(3, 5);
                f2 = Fraction.getFraction(2, 5);
                f = f1.divideBy(f2);
                assertEquals(3, f.getNumerator());
                assertEquals(2, f.getDenominator());

                assertThrows(ArithmeticException.class, () -> Fraction.getFraction(3, 5).divideBy(Fraction.ZERO));

                f1 = Fraction.getFraction(0, 5);
                f2 = Fraction.getFraction(2, 7);
                f = f1.divideBy(f2);
                assertSame(Fraction.ZERO, f);

                f1 = Fraction.getFraction(2, 7);
                f2 = Fraction.ONE;
                f = f1.divideBy(f2);
                assertEquals(2, f.getNumerator());
                assertEquals(7, f.getDenominator());

                f1 = Fraction.getFraction(1, Integer.MAX_VALUE);
                f = f1.divideBy(f1);
                assertEquals(1, f.getNumerator());
                assertEquals(1, f.getDenominator());

                f1 = Fraction.getFraction(Integer.MIN_VALUE, Integer.MAX_VALUE);
                f2 = Fraction.getFraction(1, Integer.MAX_VALUE);
                final Fraction fr = f1.divideBy(f2);
                assertEquals(Integer.MIN_VALUE, fr.getNumerator());
                assertEquals(1, fr.getDenominator());

                assertThrows(NullPointerException.class, () -> fr.divideBy(null));

                final Fraction smallest = Fraction.getFraction(1, Integer.MAX_VALUE);
                assertThrows(ArithmeticException.class, () -> smallest.divideBy(smallest.invert())); // Should overflow

                final Fraction negative = Fraction.getFraction(1, -Integer.MAX_VALUE);
                assertThrows(ArithmeticException.class, () -> negative.divideBy(negative.invert())); // Should overflow
            }

            @Test
            public void testEquals() {
                Fraction f1;
                Fraction f2;

                f1 = Fraction.getFraction(3, 5);
                assertNotEquals(null, f1);
                assertNotEquals(f1, new Object());
                assertNotEquals(f1, Integer.valueOf(6));

                f1 = Fraction.getFraction(3, 5);
                f2 = Fraction.getFraction(2, 5);
                assertNotEquals(f1, f2);
                assertEquals(f1, f1);
                assertEquals(f2, f2);

                f2 = Fraction.getFraction(3, 5);
                assertEquals(f1, f2);

                f2 = Fraction.getFraction(6, 10);
                assertNotEquals(f1, f2);
            }

            @Test
            public void testHashCode() {
                final Fraction f1 = Fraction.getFraction(3, 5);
                Fraction f2 = Fraction.getFraction(3, 5);

                assertEquals(f1.hashCode(), f2.hashCode());

                f2 = Fraction.getFraction(2, 5);
                assertTrue(f1.hashCode() != f2.hashCode());

                f2 = Fraction.getFraction(6, 10);
                assertTrue(f1.hashCode() != f2.hashCode());
            }

            @Test
            public void testCompareTo() {
                Fraction f1;
                Fraction f2;

                f1 = Fraction.getFraction(3, 5);
                assertEquals(0, f1.compareTo(f1));

                final Fraction fr = f1;
                assertThrows(NullPointerException.class, () -> fr.compareTo(null));

                f2 = Fraction.getFraction(2, 5);
                assertTrue(f1.compareTo(f2) > 0);
                assertEquals(0, f2.compareTo(f2));

                f2 = Fraction.getFraction(4, 5);
                assertTrue(f1.compareTo(f2) < 0);
                assertEquals(0, f2.compareTo(f2));

                f2 = Fraction.getFraction(3, 5);
                assertEquals(0, f1.compareTo(f2));
                assertEquals(0, f2.compareTo(f2));

                f2 = Fraction.getFraction(6, 10);
                assertEquals(0, f1.compareTo(f2));
                assertEquals(0, f2.compareTo(f2));

                f2 = Fraction.getFraction(-1, 1, Integer.MAX_VALUE);
                assertTrue(f1.compareTo(f2) > 0);
                assertEquals(0, f2.compareTo(f2));

            }

            @Test
            public void testToString() {
                Fraction f;

                f = Fraction.getFraction(3, 5);
                final String str = f.toString();
                assertEquals("3/5", str);
                assertSame(str, f.toString());

                f = Fraction.getFraction(7, 5);
                assertEquals("7/5", f.toString());

                f = Fraction.getFraction(4, 2);
                assertEquals("4/2", f.toString());

                f = Fraction.getFraction(0, 2);
                assertEquals("0/2", f.toString());

                f = Fraction.getFraction(2, 2);
                assertEquals("2/2", f.toString());

                f = Fraction.getFraction(Integer.MIN_VALUE, 0, 1);
                assertEquals("-2147483648/1", f.toString());

                f = Fraction.getFraction(-1, 1, Integer.MAX_VALUE);
                assertEquals("-2147483648/2147483647", f.toString());
            }

            @Test
            public void testToProperString() {
                Fraction f;

                f = Fraction.getFraction(3, 5);
                final String str = f.toProperString();
                assertEquals("3/5", str);
                assertSame(str, f.toProperString());

                f = Fraction.getFraction(7, 5);
                assertEquals("1 2/5", f.toProperString());

                f = Fraction.getFraction(14, 10);
                assertEquals("1 4/10", f.toProperString());

                f = Fraction.getFraction(4, 2);
                assertEquals("2", f.toProperString());

                f = Fraction.getFraction(0, 2);
                assertEquals("0", f.toProperString());

                f = Fraction.getFraction(2, 2);
                assertEquals("1", f.toProperString());

                f = Fraction.getFraction(-7, 5);
                assertEquals("-1 2/5", f.toProperString());

                f = Fraction.getFraction(Integer.MIN_VALUE, 0, 1);
                assertEquals("-2147483648", f.toProperString());

                f = Fraction.getFraction(-1, 1, Integer.MAX_VALUE);
                assertEquals("-1 1/2147483647", f.toProperString());

                assertEquals("-1", Fraction.getFraction(-1).toProperString());
            }
        }
    """.trimIndent()
}
