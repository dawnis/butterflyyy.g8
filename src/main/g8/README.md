# butterflyy
Reusable Code Base for a Scala API &amp; Server

## IDE Setup Notes

For IDE setup, please see the [SBT IDE Setup Guide](https://www.scala-sbt.org/1.x/docs/IDE.html)

### Intellij
Using the BSP integration seems to work better than a plain ipmort. See the [latest documentation](https://www.jetbrains.com/help/idea/bsp-support.html#import_bsp) for how to open an sbt project from the BSP model. 

### Metals
It's possible to configure the Language Server Protocol using the package `Metals`. This would be compatible with NeoVim's LSP integration in Lua or VSCode. 

## Tech Stack

* Scala
* SBT
* AkkaHTTTP
* Postgres/Slick
* Flyway

## Flyway
Flyway is used to create a series of SQL scripts for database migrations. See this [blog post](https://blog.nashtechglobal.com/how-to-use-flyway-with-scala-application/) for details. 

## How this was built

We used `sbt new akka/akka-http-quickstart-scala.g8` to build using Akka's conveniently provided quick start. See [Akka's documentation](https://doc.akka.io/docs/akka-http/current/index.html) for details

For Slick, the getting started tutorial is [here](https://scala-slick.org/doc/prerelease/gettingstarted.html)

Both tutorials were added independently in `QuickstartApp.scala` and then integrated incrementally.

For an example walkthrough of a project leveraging Slick 1st and AkkaHTTP, see this [tutorial](https://youtu.be/bLrRbImsL1c?si=82_RFfCgJqxTOqrV)

Another [nice article](https://medium.com/@ciobanunicu.boris/scala-service-combined-with-postgresql-flyway-doobie-ciris-io-cats-effects-http4s-e78d75248118) detailing a similar setup.

