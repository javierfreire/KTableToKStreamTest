build-jar:
	@./gradlew clean build

local-env:
	@x-terminal-emulator -e docker-compose -f etc/docker-compose.yml up

test-0-commit:
	@x-terminal-emulator -e java -jar build/libs/kTableToKStreamTest.jar start -c 0
	@java -jar build/libs/kTableToKStreamTest.jar test -w 4000

test-1000-commit:
	@x-terminal-emulator -e java -jar build/libs/kTableToKStreamTest.jar start -c 1000
	@java -jar build/libs/kTableToKStreamTest.jar test -w 4000