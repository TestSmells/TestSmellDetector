package thresholds

abstract class Thresholds {

    abstract val eagerTest: Int
    abstract val assertionRoulette: Int
    abstract val verboseTest: Int
    abstract val conditionalTestLogic: Int
    abstract val magicNumberTest: Int
    abstract val generalFixture: Int
    abstract val mysteryGuest: Int
    abstract val resourceOptimism: Int
    abstract val sleepyTest: Int
    abstract val emptyTest: Int
    abstract val exceptionCatchingThrowing: Int
    abstract val printStatement: Int
    abstract val redundantAssertion: Int
    abstract val sensitiveEquality: Int
}

/** Default thresholds as the original interpretation on Van Deursen et.atl
 *
 */
open class DefaultThresholds : Thresholds() {
    override val eagerTest: Int
        get() = 1
    override val assertionRoulette: Int
        get() = 1
    override val verboseTest: Int
        get() = 1
    override val conditionalTestLogic: Int
        get() = 0
    override val magicNumberTest: Int
        get() = 0
    override val generalFixture: Int
        get() = 0
    override val mysteryGuest: Int
        get() = 0
    override val resourceOptimism: Int
        get() = 0
    override val sleepyTest: Int
        get() = 0
    override val emptyTest: Int
        get() = 0
    override val exceptionCatchingThrowing: Int
        get() = 0
    override val printStatement: Int
        get() = 0
    override val redundantAssertion: Int
        get() = 0
    override val sensitiveEquality: Int
        get() = 0
}

/**
 * Thresholds for the test smell detection proposed by Spadini at.al. in the paper
 * "Investigating severity thresholds for test smells"
 */
class SpadiniThresholds : DefaultThresholds() {
    override val eagerTest: Int
        get() = 4
    override val assertionRoulette: Int
        get() = 3
    override val verboseTest: Int
        get() = 13
}

