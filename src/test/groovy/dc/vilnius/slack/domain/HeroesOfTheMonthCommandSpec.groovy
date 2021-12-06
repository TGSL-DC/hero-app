package dc.vilnius.slack.domain

import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.Month

class HeroesOfTheMonthCommandSpec extends Specification {

    @Unroll
    def "Should reveal heroes at the end of the month: #dateTime"() {
        expect:
        HeroesOfTheMonthDateUtil.isAllowedToRevealHeroesLeaderboard(dateTime)

        where:
        dateTime << [
                LocalDate.of(2021, Month.SEPTEMBER, 26),
                LocalDate.of(2021, Month.SEPTEMBER, 27),
                LocalDate.of(2021, Month.SEPTEMBER, 28),
                LocalDate.of(2021, Month.SEPTEMBER, 29),
                LocalDate.of(2021, Month.SEPTEMBER, 30)
        ]
    }

    @Unroll
    def "Should not reveal heroes at the end of the month: #dateTime"() {
        expect:
        !HeroesOfTheMonthDateUtil.isAllowedToRevealHeroesLeaderboard(dateTime)

        where:
        dateTime << [
                LocalDate.of(2021, Month.SEPTEMBER, 1),
                LocalDate.of(2021, Month.SEPTEMBER, 25),
                LocalDate.of(2021, Month.OCTOBER, 1),
        ]
    }
}
