package dc.vilnius.slack.domain


import spock.lang.Specification
import spock.lang.Unroll

class SlackMessageParserAcceptanceSpec extends Specification {

    @Unroll
    def "Parses '#givenMessage' message into '#users' and '#message'"() {
        when:
        def result = CommandParser.parse(givenMessage)

        then:
        result.users() == users
        result.message() == message

        where:
        givenMessage                                                                | users                                  | message
        "<@RANDOMID|username> good work!"                                           | ["RANDOMID"]                           | "good work!"
        "<@RANDOMID1|name.surname> <@RANDOMID|username> you are awesome"            | ["RANDOMID1", "RANDOMID"]              | "you are awesome"
        "<@RANDOMID2|name>, <@RANDOMID3|surname>, <@RANDOMID|username> you rock 🤘" | ["RANDOMID2", "RANDOMID3", "RANDOMID"] | "you rock 🤘"
        "<@RANDOMID4|name.z.surname> lol 🤣"                                        | ["RANDOMID4"]                         | "lol 🤣"

    }

}
