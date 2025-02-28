#let manual(
  title: none,
  authors: (),
  lastedit: datetime.today(),
  doc,
) = {
  set page(
    paper: "a4",
    numbering: "1/1",
    header: [#title #h(1fr) #authors.first().name],
  )
  set align(center)
  text(17pt, title)
  linebreak()
  [last edited on #lastedit.display()]

  let count = authors.len()
  let ncols = calc.min(count, 3)
  grid(
    columns: (1fr,) * ncols,
    row-gutter: 24pt,
    ..authors.map(author => [
      #author.name \
      #author.affiliation \
      #link("mailto:" + author.email)
    ]),
  )
  set align(left)
  doc
}
