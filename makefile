manual/RISK_AGENT_GUIDE.pdf: manual/RISK_AGENT_GUIDE.typ manual/template.typ manual/versions.yaml
	typst compile $< $@

manual/RULESET.pdf: manual/RULESET.typ manual/template.typ
	typst compile $< $@

manual/versions.yaml: build.gradle
	./gradlew -q :generateVersionsYaml
