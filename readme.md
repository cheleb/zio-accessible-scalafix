# Scalafix rules for ZIO 2.x Module pattern.

To use:
```
sbt "scalafix dependency:ZIOAccessible@dev.cheleb::zio-module-pattern:0.0.2"
```

To develop rule:
```
cd scalafix
sbt ~tests/test
# edit rules/src/main/scala/fix/ZIOAccessible.scala
```
