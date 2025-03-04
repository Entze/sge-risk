#import "template.typ": manual

#show: manual.with(
  title: "Risk Ruleset",
  authors: (
    (name: "Lukas Grassauer", affiliation: "TU Wien", email: "Lukas Grassauer"),
  ),
)

Please note that you can view the manual which this document is based
upon under
#link("https://www.hasbro.com/common/instruct/Risk1963.PDF")[#link("https://www.hasbro.com/common/instruct/Risk1963.PDF");]

= Objective

The object of the game is to occupy every territory on the board and, in
so doing, eliminate all but one player.

= Cards

There are four different kinds of cards, foot soldier or infantry,
horseman or cavalry, and cannon or artillery as well as the joker. There
are two jokers and 42 cards other cards with each exactly one territory
associated with them, while the jokers do not have any association with
territories.

= Board

There are 42 territories where some are symmetrically connected. These
territories roughly resemble the Earth. There are six continents which
are composed of several territories.

== Continents

#context {
  let id-width = calc.max(measure([9]).width, measure([*ID*]).width) + 2em
  let continent-width = (
    calc.max(measure([North America]).width, measure([*Continent*]).width) + 2em
  )
  let bonus-width = calc.max(measure([9]).width, measure([*Bonus*]).width) + 2em

  figure(
    align(center)[
      #table(
        columns: (id-width, continent-width, bonus-width),
        [*ID*], [*Continent*], [*Bonus*],
        [0], [North America], [5],
        [1], [South America], [2],
        [2], [Europe], [5],
        [3], [Africa], [3],
        [4], [Asia], [7],
        [5], [Australia], [2],
      )],
  )
}

#context [
  #let id-width = calc.max(measure([99]).width, measure([*ID*]).width) + 2em
  #let territory-width = (
    calc.max(
      measure([Eastern United States]).width,
      measure([*Territory*]).width,
    )
      + 2em
  )
  #let card-width = (
    calc.max(measure([Artillery]).width, measure([*Card Type*]).width) + 2em
  )
  #let connected-width = (
    calc.max(
      measure([99, 99, 99, 99, 99, 99]).width,
      measure([*Connected To*]).width,
    )
      + 2em
  )

  == North America

  #figure(
    align(center)[
      #table(
        columns: (id-width, territory-width, card-width, connected-width),
        [*ID*], [*Territory*], [*Card Type*], [*Connected To*],
        [0], [Alaska], [Artillery], [5, 31],
        [1], [Alberta], [Artillery], [0, 6, 8],
        [2], [Central America], [Infantry], [3, 8, 12],
        [3], [Eastern United States], [Infantry], [2, 6, 7, 8],
        [4], [Greenland], [Cavalry], [5, 6, 7, 14],
        [5], [Northwest Territory], [Cavalry], [0, 1, 4, 6, 7],
        [6], [Ontario], [Artillery], [1, 3, 4, 5, 7, 8],
        [7], [Quebec], [Artillery], [3, 4, 5, 6],
        [8], [Western United States], [Artillery], [1, 2, 3, 6],
      )],
  )

  == South America

  #figure(
    align(center)[
      #table(
        columns: (id-width, territory-width, card-width, connected-width),
        [*ID*], [*Territory*], [*Card Type*], [*Connected To*],
        [9], [Argentina], [Infantry], [10, 11],
        [10], [Brazil], [Infantry], [9, 11, 24],
        [11], [Peru], [Cavalry], [9, 10],
        [12], [Venezuela], [Cavalry], [2, 10, 11],
      )
    ],
  )

  == Europe

  #figure(
    align(center)[
      #table(
        columns: (id-width, territory-width, card-width, connected-width),
        [*ID*], [*Territory*], [*Card Type*], [*Connected To*],
        [13], [Great Britain], [Infantry], [12, 15, 16, 19],
        [14], [Iceland], [Cavalry], [4, 12, 16],
        [15], [Northern Europe], [Cavalry], [13, 16, 17, 18, 19],
        [16], [Scandinavia], [Infantry], [13, 14, 15, 18],
        [17], [Southern Europe], [Infantry], [15, 18, 19, 22, 24],
        [18], [Ukraine], [Infantry], [15, 16, 17, 26, 32, 36],
        [19], [Western Europe], [Infantry], [13, 15, 17, 24],
      )
    ],
  )

  == Africa

  #figure(
    align(center)[

      #table(
        columns: (id-width, territory-width, card-width, connected-width),
        [*ID*], [*Territory*], [*Card Type*], [*Connected To*],
        [20], [Central Africa], [Artillery], [21, 25],
        [21], [East Africa], [Infantry], [20, 22, 23, 25, 32],
        [22], [Egypt], [Cavalry], [17, 21, 24, 32],
        [23], [Madagascar], [Cavalry], [21, 25],
        [24], [North Africa], [Infantry], [10, 17, 19, 20, 22],
        [25], [South Africa], [Artillery], [20, 21, 23],
      )
    ],
  )

  == Asia

  #figure(
    align(center)[
      #table(
        columns: (id-width, territory-width, card-width, connected-width),
        [*ID*], [*Territory*], [*Card Type*], [*Connected To*],
        [26], [Afghanistan], [Infantry], [18, 27, 32, 36],
        [27], [China], [Artillery], [26, 33, 34, 35, 36],
        [28], [India], [Cavalry], [26, 27, 32, 34],
        [29], [Irkutsk], [Artillery], [31, 33, 35, 37],
        [30], [Japan], [Cavalry], [31, 33],
        [31], [Kamchatka], [Artillery], [0, 29, 30, 33, 37],
        [32], [Middle East], [Artillery], [18, 21, 22, 26, 28],
        [33], [Mongolia], [Cavalry], [27, 29, 30, 31, 35],
        [34], [Siam], [Cavalry], [27, 28, 34],
        [35], [Siberia], [Infantry], [27, 29, 33, 36, 37],
        [36], [Ural], [Infantry], [18, 26, 27, 35],
        [37], [Yakutsk], [Artillery], [29, 31, 35],
      )
    ],
  )

  == Australia

  #figure(
    align(center)[
      #table(
        columns: (id-width, territory-width, card-width, connected-width),
        [*ID*], [*Territory*], [*Card Type*], [*Connected To*],
        [38], [Eastern Australia], [Cavalry], [40, 41],
        [39], [Indonesia], [Infantry], [34, 40, 41],
        [40], [New Guinea], [Cavalry], [38, 39],
        [41], [Western Australia], [Artillery], [38, 39],
      )
    ],
  )
]

= Set-Up

Each player receives the same amount of initial reinforcements,
according to the following table

#figure(
  align(center)[#table(
      columns: 2,
      align: (col, row) => (auto, auto).at(col),
      inset: 6pt,
      [*Nr. of Players*], [*Armies*],
      [2], [50],
      [3], [35],
      [4], [30],
      [5], [25],
      [6], [20],
    )],
)



The first player selects one of the 42 territories. One army will be
placed on this territory and the number of armies will be decreased by
one. The next players do the same for any of the remaining free
territories until every territory has exactly one army in it.

After the first player the last player is to play, and the one before
after that.

As an example: If four players are enumerated starting from zero, the
order of play would be 0 3 2 1.

Once each of the territories is occupied by a single army, the remaining
armies will be placed one at an action onto the players territories.

= Play

Note: the number of mobile armies is equal to the number of armies in a
territory minus one.

+ Accumulation of Armies:

  At the start of a players move or turn they are entitled to add
  reinforcements to their territories. The number of additional armies
  to which they are entitled is equal to the number of territories
  divided by three rounded down. However, it is at least three. Should
  the player occupy every territory of a continent they are also
  entitled to that continent bonus which is added to the other
  additional armies they receive. Should a player trade in a set the set
  bonus is also added to this number.

  The player who picked their first territory last \(i.e. player number
  one) will be the first to start their regular turn. The order of play
  reverses after the initial select and reinforce phase.

  For example: If four players are enumerated starting from zero the
  order of play would be: 1 2 3 0.

+ Placing of armies:

  Once the additional armies are determined the player can put any
  number of armies in any territory they occupy. However once a certain
  number is placed in a territory they cannot change this number. If all
  but one territory are reinforced and there are still reinforcements
  left all the remaining reinforcements have to be placed into this
  territory.

  After the reinforcement phase the attack phase starts.

+ How to attack:

  The purpose of an attack is to eliminate opponents\' armies from
  adjacent territories and to occupy these territories with their own
  armies.

  A player is never forced to attack, and after collecting and placing
  the extra armies to which they are entitled, may end the attack phase.
  To attack a territory with more than one army in it has to be
  selected. Then any number of armies such that one army is not part of
  the attack however three at most can be used to attack any adjacent
  territory which is occupied by another player. The outcome of an
  attack is determined by dice throw. The number of dice used is
  determined by the number of armies used in the attack. Each army
  grants one die on the attacker side and the defender side, however the
  attacker is limited to a maximum of three dice while the defender can
  only use two at most. The attacker\'s and the defender\'s dice are
  rolled and then sorted in a descending manner, according to their face
  value. The first die of the attacker is then compared to the first of
  the defender. If and only if the attackers die\'s face value is
  greater than the one of the defender, the defender loses one army in
  their territory, otherwise the attacker loses one. If dice remain they
  are also compared. The losses are added up and then subtracted from
  the respecting territories. Should armies remain in the defenders
  territory the attacker can start an attack on any territory, including
  to the one they just attacked. They also can attack from another
  territory. Should the defending territory no longer have any armies to
  defend, the attacker has occupied this territory. The attacking player
  now has to determine a number of armies to occupy this territory with.
  At least one, but at most the number of mobile armies left in the
  attacking territory. Consequently, one army always has to remain in
  the attacking territory. The determined number is then added to the
  occupied territory while the attacking territory\'s armies are reduced
  by that number.

  As long as the attacking player has a territory with more than one
  army they can attack until they specifically end the attack phase.

+ Fortifying territories

  After the attack phase the player may choose any territory, now called
  the fortifying territory, and a number between one and the number
  mobile of armies in this territory. Consequently, one army has to be
  left behind in the fortifying territory. Any adjacent friendly
  territory, now called the fortified territory, may now be chosen. The
  chosen number is now added to the number of armies in the fortified
  territory and the number of armies in the fortifying territory is
  subtracted by this number. The player may also choose to not fortify
  any country and end their turn or move immediately after the attacking
  phase.

  Should the attacker have captured at least one territory, exactly one
  card is added to their cards. The other players do not know which card
  that is.

+ The cards

  A player may trade in a set of cards in the reinforcement phase. A
  player must trade in a set of cards in any phase if they hold five or
  more cards. A set of cards is either one of each non-joker type, three
  of a non-joker type or any two non-joker cards with a joker.

  Trading a set of cards awards additional armies. Regardless of player
  each turned in set adds extra armies according to the following table
  to the bonus:

  #figure(
    align(center)[#table(
        columns: 2,
        align: (col, row) => (auto, auto).at(col),
        inset: 6pt,
        [*Nr. of set*], [*Award*],
        [1], [4],
        [2], [6],
        [3], [8],
        [4], [10],
        [5], [12],
        [6], [15],
        [7], [20],
        [8], [25],
        [9], [30],
        […], [\+ 5],
      )],
  )

  after the ninth set the number of armies is always increased by five.

  If a player trades in a set after they have already placed territories
  they are also allowed to place the additional armies in those
  territories.

  Should a player capture the last territory of another player, their
  remaining cards are awarded to the eliminating player, which they can
  immediately combine with their cards to trade in sets. If the player
  has five or more cards they have to turn in sets until they hold four
  or fewer cards, however they may also turn in more if their cards
  allow it.

  Should a player turn in a set which holds cards that are associated
  with territories that they occupy, additional two armies are awarded.
  However, the player must reinforce each associated territory with at
  least two armies.

  Immediately after trading in a set the cards are added to the discard
  pile, which is reshuffled and added to the deck of cards should it
  ever deplete.
